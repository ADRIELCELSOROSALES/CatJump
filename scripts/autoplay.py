"""Juega CatJump solo para conseguir una captura de gameplay con score real.

Alternar toques a ciegas dejaba al gato rebotando en la misma plataforma
(score 0). Este loop lo dirige: ubica al gato por sus pupilas (los unicos
pixeles casi negros del area de juego) y lo manda hacia la plataforma verde
mas cercana por encima.
"""
import io, subprocess, sys, os
from PIL import Image

ADB = os.path.expanduser("~/Library/Android/sdk/platform-tools/adb")
OUT = "/tmp/f3"
UI_BOTTOM = 600      # debajo de los paneles SCORE / LEVEL / BEST
SCALE = 4            # analizar a 1/4 alcanza y es 16x mas rapido


def grab():
    raw = subprocess.run([ADB, "exec-out", "screencap", "-p"],
                         capture_output=True).stdout
    return Image.open(io.BytesIO(raw)).convert("RGB")


def find_cat(px, w, h):
    """Centroide de los pixeles casi negros: las pupilas del gato."""
    xs, ys, n = 0, 0, 0
    for y in range(UI_BOTTOM // SCALE, h):
        for x in range(w):
            r, g, b = px[x, y]
            if r < 55 and g < 55 and b < 55:
                xs += x; ys += y; n += 1
    return (xs / n * SCALE, ys / n * SCALE) if n >= 3 else None


def platform_above(px, w, h, cat):
    """Plataforma verde mas cercana por encima del gato."""
    cx, cy = cat[0] / SCALE, cat[1] / SCALE
    best = None
    for y in range(int(cy) - 8, max(0, int(cy) - 90), -1):
        row = [x for x in range(w)
               if px[x, y][1] > 120 and px[x, y][0] < 130 and px[x, y][2] < 130]
        if len(row) >= 4:
            # partir la fila en tramos contiguos y quedarse con el mas cercano en x
            groups, cur = [], [row[0]]
            for x in row[1:]:
                if x - cur[-1] <= 2:
                    cur.append(x)
                else:
                    groups.append(cur)
                    cur = [x]
            groups.append(cur)
            centers = [sum(g) / len(g) for g in groups]
            best = min(centers, key=lambda gx: abs(gx - cx))
            break
    return best * SCALE if best is not None else None


def hold(x, ms):
    subprocess.run([ADB, "shell", "input", "swipe", str(int(x)), "1000",
                    str(int(x)), "1000", str(ms)], capture_output=True)


def main(ticks=45):
    os.makedirs(OUT, exist_ok=True)
    misses = 0
    for i in range(ticks):
        img = grab()
        small = img.resize((img.width // SCALE, img.height // SCALE))
        px, w, h = small.load(), small.width, small.height
        cat = find_cat(px, w, h)
        if cat is None:
            misses += 1
            if misses >= 3:
                print(f"tick {i}: sin gato (game over?)"); break
            continue
        misses = 0
        img.save(f"{OUT}/p{i:02d}.png")
        tgt = platform_above(px, w, h, cat)
        if tgt is None:
            continue
        if tgt < cat[0] - 25:
            hold(max(60, cat[0] - 300), 220)
        elif tgt > cat[0] + 25:
            hold(min(1020, cat[0] + 300), 220)
    print("frames:", len(os.listdir(OUT)))


if __name__ == "__main__":
    main(int(sys.argv[1]) if len(sys.argv) > 1 else 45)
