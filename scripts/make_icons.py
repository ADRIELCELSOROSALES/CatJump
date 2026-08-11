"""
Genera el iconset de CatJump a partir de una unica definicion geometrica.

La cara del gato reusa las proporciones de CatSprite.kt (cabeza r=20 en un
espacio de 60u) reescaladas al viewport de 108u del adaptive icon, y
simetrizadas: el sprite del juego tiene los ojos y la nariz corridos ~1u a la
derecha, algo invisible a 60px pero que a 512px se lee como un error.

Un solo archivo produce el raster (PIL) y el pathData del vector drawable, asi
el icono adaptativo (API 26+) y los mipmap legacy (API 24-25) no divergen.
"""
import math, os
from PIL import Image, ImageDraw, ImageFont

ROOT = "/Users/adrielcelsorosales/Desktop/encode/CatJump"
RES = f"{ROOT}/app/src/main/res"
STORE = f"{ROOT}/playstore"

# ---------------------------------------------------------------- paleta
SKY_DARK, SKY_MED = (0x1A, 0x23, 0x7E), (0x39, 0x49, 0xAB)
CAT = (0xFF, 0x98, 0x00)          # CatOrange, igual que el titulo "CAT"
CAT_DARK = (0xE6, 0x51, 0x00)     # CatOrangeDark
EAR_IN = (0xFF, 0xCD, 0xD2)
NOSE = (0xD8, 0x1B, 0x60)
WHISK = (0x5D, 0x40, 0x37)
EYE = (0x21, 0x21, 0x21)
WHITE = (0xFF, 0xFF, 0xFF)
GRASS = (0x4C, 0xAF, 0x50)        # verde del titulo "JUMP"

# ------------------------------------------------------------- geometria
# Viewport 108u. Todo el contenido debe entrar en el circulo seguro
# del adaptive icon: centro (54,54), radio 33.
VIEW = 108.0
SAFE_C, SAFE_R = (54.0, 54.0), 33.0

HC = (54.0, 57.0)   # centro de la cabeza
HR = 24.0
K = HR / 20.0       # factor vs. el sprite del juego


def rel(dx, dy):
    """Coordenada absoluta desde un offset relativo al centro de la cabeza."""
    return (HC[0] + dx * K, HC[1] + dy * K)


def on_head(deg, dist=1.0):
    """Punto sobre (o mas alla de) el borde de la cabeza, en grados."""
    a = math.radians(deg)
    return (HC[0] + HR * dist * math.cos(a), HC[1] + HR * dist * math.sin(a))


def mirror(pts):
    return [(2 * HC[0] - x, y) for x, y in pts]


# Orejas ancladas al borde de la cabeza con el vertice bien afuera. El sprite
# del juego las apoya sobre el circulo y a tamano de icono desaparecian:
# la cabeza se las comia y solo asomaban dos puntitas.
EAR_BASE_A, EAR_BASE_B, EAR_TIP, EAR_TIP_D = 212.0, 262.0, 236.0, 1.32
EAR_L = [on_head(EAR_BASE_A), on_head(EAR_TIP, EAR_TIP_D), on_head(EAR_BASE_B)]
EAR_R = mirror(EAR_L)


def inner_ear(ear):
    """Triangulo interno alojado en la parte de la oreja que sobresale."""
    a, apex, b = ear
    mid = ((a[0] + b[0]) / 2, (a[1] + b[1]) / 2)
    piv = (apex[0] + 0.38 * (mid[0] - apex[0]), apex[1] + 0.38 * (mid[1] - apex[1]))
    return [(piv[0] + 0.46 * (v[0] - piv[0]), piv[1] + 0.46 * (v[1] - piv[1]))
            for v in ear]


EAR_L_IN, EAR_R_IN = inner_ear(EAR_L), inner_ear(EAR_R)

EYE_L, EYE_R = rel(-7, -3), rel(7, -3)
EYE_R_OUT = 6.2 * K
PUPIL_R = 3.5 * K
SHINE_R = 1.5 * K

# Nariz apuntando hacia abajo (el sprite la tiene invertida). La boca del
# sprite (tallo vertical + V) a este tamano se leia como un ancla pegada a la
# nariz, asi que va una sonrisa: un solo arco, despegado del hocico.
NOSE_TRI = [rel(-2.2, 5.0), rel(2.2, 5.0), rel(0, 8.6)]
MOUTH_C, MOUTH_R = rel(0, 5.8), 6.6
MOUTH_FROM, MOUTH_TO = 122.0, 58.0
MOUTH_W = 1.3 * K


def arc_pts(c, r, a0, a1, n=14):
    return [(c[0] + r * math.cos(math.radians(a0 + (a1 - a0) * i / n)),
             c[1] + r * math.sin(math.radians(a0 + (a1 - a0) * i / n)))
            for i in range(n + 1)]


MOUTH_POLY = arc_pts(MOUTH_C, MOUTH_R, MOUTH_FROM, MOUTH_TO)

# Bigotes radiales desde el borde inferior de la cabeza: quedan enteramente
# por fuera de la silueta en vez de cruzar la cara.
WHISK_W = 1.4 * K
def whiskers():
    out = []
    for deg, ln in ((168.0, 6.5), (155.0, 7.0), (142.0, 6.0)):
        for d in (deg, 180.0 - deg):
            a = math.radians(d)
            base = on_head(d)
            out.append((base, (base[0] + ln * math.cos(a), base[1] + ln * math.sin(a))))
    return out
WHISKERS = whiskers()


def check_safe():
    """Falla temprano si algo se sale del circulo seguro: a mano es invisible."""
    pts = [*EAR_L, *EAR_R, EYE_L, EYE_R, *NOSE_TRI]
    pts += [p for seg in WHISKERS for p in seg]
    pts += MOUTH_POLY
    pts += [(HC[0] + HR * math.cos(a), HC[1] + HR * math.sin(a))
            for a in [i * math.pi / 60 for i in range(120)]]
    worst = max(math.dist(p, SAFE_C) for p in pts)
    assert worst <= SAFE_R, f"contenido fuera del circulo seguro: {worst:.2f} > {SAFE_R}"
    return worst


# ------------------------------------------------------------- raster
def draw_cat(d, s, ox=0.0, oy=0.0):
    """Dibuja la cara en un ImageDraw a escala s. Mismo orden que el sprite."""
    def P(p):
        return (ox + p[0] * s, oy + p[1] * s)
    def circle(c, r, fill):
        cx, cy = P(c)
        rr = r * s
        d.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], fill=fill)
    def poly(pts, fill):
        d.polygon([P(p) for p in pts], fill=fill)
    def line(a, b, fill, w):
        d.line([P(a), P(b)], fill=fill, width=max(1, round(w * s)), joint="curve")

    circle(HC, HR, CAT)
    for outer, inner in ((EAR_L, EAR_L_IN), (EAR_R, EAR_R_IN)):
        poly(outer, CAT)
        poly(inner, EAR_IN)
    for eye in (EYE_L, EYE_R):
        pup = (eye[0], eye[1] + 1.0 * K)
        circle(eye, EYE_R_OUT, WHITE)
        circle(pup, PUPIL_R, EYE)
        # brillo contenido dentro de la pupila: pisando el borde parecia mordida
        circle((pup[0] - 1.4 * K, pup[1] - 1.4 * K), SHINE_R, WHITE)
    poly(NOSE_TRI, NOSE)
    d.line([P(p) for p in MOUTH_POLY], fill=WHISK,
           width=max(1, round(MOUTH_W * s)), joint="curve")
    for a, b in WHISKERS:
        line(a, b, WHISK, WHISK_W)


def gradient(size, top, bottom):
    w, h = size
    img = Image.new("RGB", size)
    d = ImageDraw.Draw(img)
    for y in range(h):
        t = y / max(1, h - 1)
        d.line([(0, y), (w, y)], fill=tuple(round(a + (b - a) * t) for a, b in zip(top, bottom)))
    return img


SS = 8  # supersampling


def render(px, crop=True, bg=True):
    """Compone el icono a px. crop=True recorta el central 72u (icono legacy)."""
    span = 72.0 if crop else VIEW
    off = (VIEW - span) / 2.0
    s = px * SS / span
    img = (gradient((px * SS, px * SS), SKY_DARK, SKY_MED).convert("RGBA")
           if bg else Image.new("RGBA", (px * SS, px * SS), (0, 0, 0, 0)))
    d = ImageDraw.Draw(img)
    draw_cat(d, s, -off * s, -off * s)
    return img.resize((px, px), Image.LANCZOS)


def round_mask(img):
    px = img.size[0]
    m = Image.new("L", (px * SS, px * SS), 0)
    ImageDraw.Draw(m).ellipse([0, 0, px * SS, px * SS], fill=255)
    out = img.copy()
    out.putalpha(m.resize((px, px), Image.LANCZOS))
    return out


# ------------------------------------------------------------- vector xml
def fmt(v):
    return f"{v:.2f}".rstrip("0").rstrip(".")


def path_circle(c, r):
    return (f"M{fmt(c[0] - r)},{fmt(c[1])}"
            f"a{fmt(r)},{fmt(r)} 0 1,0 {fmt(2 * r)},0"
            f"a{fmt(r)},{fmt(r)} 0 1,0 {fmt(-2 * r)},0Z")


def path_poly(pts):
    head = f"M{fmt(pts[0][0])},{fmt(pts[0][1])}"
    return head + "".join(f"L{fmt(x)},{fmt(y)}" for x, y in pts[1:]) + "Z"


def path_line(a, b):
    return f"M{fmt(a[0])},{fmt(a[1])}L{fmt(b[0])},{fmt(b[1])}"


def fill(pd, color):
    return f'    <path android:pathData="{pd}" android:fillColor="{color}" />'


def stroke(pd, color, w):
    return (f'    <path android:pathData="{pd}" android:strokeColor="{color}"\n'
            f'        android:strokeWidth="{fmt(w)}" android:strokeLineCap="round" />')


def hexc(c):
    return "#" + "".join(f"{v:02X}" for v in c)


VEC_HEAD = ('<?xml version="1.0" encoding="utf-8"?>\n'
            '<!-- Generado desde la geometria de CatSprite.kt. No editar a mano:\n'
            '     regenerar con scripts/make_icons.py para no desincronizar los mipmap. -->\n'
            '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
            '    android:width="108dp" android:height="108dp"\n'
            '    android:viewportWidth="108" android:viewportHeight="108">\n')


def foreground_xml():
    L = [VEC_HEAD]
    L.append(fill(path_circle(HC, HR), hexc(CAT)))
    for outer, inner in ((EAR_L, EAR_L_IN), (EAR_R, EAR_R_IN)):
        L.append(fill(path_poly(outer), hexc(CAT)))
        L.append(fill(path_poly(inner), hexc(EAR_IN)))
    for eye in (EYE_L, EYE_R):
        pup = (eye[0], eye[1] + 1.0 * K)
        L.append(fill(path_circle(eye, EYE_R_OUT), "#FFFFFF"))
        L.append(fill(path_circle(pup, PUPIL_R), hexc(EYE)))
        L.append(fill(path_circle((pup[0] - 1.4 * K, pup[1] - 1.4 * K), SHINE_R), "#FFFFFF"))
    L.append(fill(path_poly(NOSE_TRI), hexc(NOSE)))
    a0, a1 = MOUTH_POLY[0], MOUTH_POLY[-1]
    L.append(stroke(f"M{fmt(a0[0])},{fmt(a0[1])}"
                    f"A{fmt(MOUTH_R)},{fmt(MOUTH_R)} 0 0,0 {fmt(a1[0])},{fmt(a1[1])}",
                    hexc(WHISK), MOUTH_W))
    for a, b in WHISKERS:
        L.append(stroke(path_line(a, b), hexc(WHISK), WHISK_W))
    L.append("</vector>\n")
    return "\n".join(L)


def background_xml():
    return (VEC_HEAD.replace('xmlns:android="http://schemas.android.com/apk/res/android"',
                             'xmlns:android="http://schemas.android.com/apk/res/android"\n'
                             '    xmlns:aapt="http://schemas.android.com/aapt"')
            + '    <path android:pathData="M0,0h108v108h-108z">\n'
              '        <aapt:attr name="android:fillColor">\n'
              '            <gradient android:type="linear"\n'
              '                android:startX="54" android:startY="0"\n'
              '                android:endX="54" android:endY="108"\n'
              f'                android:startColor="{hexc(SKY_DARK)}"\n'
              f'                android:endColor="{hexc(SKY_MED)}" />\n'
              '        </aapt:attr>\n'
              '    </path>\n</vector>\n')


def ear_minus_head(ear):
    """Oreja recortada al exterior del circulo, para poder unirla con evenOdd.

    El icono monocromo se tinta de un solo color: los ojos y la nariz tienen que
    ser huecos reales (evenOdd), y evenOdd convertiria en hueco cualquier
    solape oreja/cabeza. Reemplazando la base del triangulo por el arco del
    circulo, la oreja queda enteramente afuera y no hay solape.
    """
    a, apex, b = ear
    ang_a = math.atan2(a[1] - HC[1], a[0] - HC[0])
    ang_b = math.atan2(b[1] - HC[1], b[0] - HC[0])
    pa = (HC[0] + HR * math.cos(ang_a), HC[1] + HR * math.sin(ang_a))
    pb = (HC[0] + HR * math.cos(ang_b), HC[1] + HR * math.sin(ang_b))
    # arco menor de pb de vuelta a pa; sweep segun el sentido del angulo
    delta = (ang_a - ang_b) % (2 * math.pi)
    sweep = 1 if delta < math.pi else 0
    return (f"M{fmt(pa[0])},{fmt(pa[1])}L{fmt(apex[0])},{fmt(apex[1])}"
            f"L{fmt(pb[0])},{fmt(pb[1])}"
            f"A{fmt(HR)},{fmt(HR)} 0 0,{sweep} {fmt(pa[0])},{fmt(pa[1])}Z")


def monochrome_xml():
    pd = path_circle(HC, HR) + ear_minus_head(EAR_L) + ear_minus_head(EAR_R)
    for eye in (EYE_L, EYE_R):
        pd += path_circle(eye, EYE_R_OUT * 0.72)
    pd += path_poly(NOSE_TRI)
    return (VEC_HEAD
            + f'    <path android:pathData="{pd}"\n'
              '        android:fillColor="#FFFFFF" android:fillType="evenOdd" />\n</vector>\n')


# ------------------------------------------------------------- store art
def feature_graphic():
    W, H = 1024, 500
    img = gradient((W * 2, H * 2), SKY_DARK, SKY_MED).convert("RGBA")
    d = ImageDraw.Draw(img)
    # Estrellas con un LCG sembrado fijo: determinista (regenerar da el mismo
    # archivo) pero sin la grilla diagonal que dejaba i*k % ancho.
    seed = 20260810
    def rnd(n):
        nonlocal seed
        seed = (1103515245 * seed + 12345) % (1 << 31)
        return seed % n
    for _ in range(120):
        x, y, r = rnd(W * 2), rnd(H * 2), 2 + rnd(3) * 2
        d.ellipse([x - r, y - r, x + r, y + r], fill=(255, 255, 255, 70 + rnd(130)))
    cat = render(880, crop=False, bg=False)
    img.alpha_composite(cat, (110, (H * 2 - 880) // 2))
    f = ImageFont.truetype("/System/Library/Fonts/Supplemental/Arial Rounded Bold.ttf", 200)
    tx = 980
    for text, color, dy in (("CAT", CAT, 130), ("JUMP", GRASS, 350)):
        d.text((tx + 6, dy + 6), text, font=f, fill=(0, 0, 0, 90))
        d.text((tx, dy), text, font=f, fill=color)
    return img.convert("RGB").resize((W, H), Image.LANCZOS)


# ------------------------------------------------------------- salida
def main():
    worst = check_safe()
    os.makedirs(STORE, exist_ok=True)

    for name, xml in (("ic_launcher_background", background_xml()),
                      ("ic_launcher_foreground", foreground_xml()),
                      ("ic_launcher_monochrome", monochrome_xml())):
        with open(f"{RES}/drawable/{name}.xml", "w") as fh:
            fh.write(xml)

    for dpi, px in (("mdpi", 48), ("hdpi", 72), ("xhdpi", 96),
                    ("xxhdpi", 144), ("xxxhdpi", 192)):
        out = f"{RES}/mipmap-{dpi}"
        os.makedirs(out, exist_ok=True)
        icon = render(px)
        icon.convert("RGB").save(f"{out}/ic_launcher.webp", "WEBP", quality=95)
        round_mask(icon).save(f"{out}/ic_launcher_round.webp", "WEBP", quality=95)

    render(512).convert("RGB").save(f"{STORE}/icon-512.png")
    feature_graphic().save(f"{STORE}/feature-graphic-1024x500.png")
    render(1024).convert("RGB").save(f"{STORE}/icon-preview-1024.png")
    print(f"OK - punto mas lejano del centro: {worst:.2f}u (limite {SAFE_R})")


if __name__ == "__main__":
    main()
