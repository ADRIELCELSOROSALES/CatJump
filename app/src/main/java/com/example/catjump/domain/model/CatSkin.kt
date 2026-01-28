package com.example.catjump.domain.model

import androidx.compose.ui.graphics.Color

data class CatSkin(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val darkColor: Color,
    val patternType: PatternType = PatternType.SOLID,
    val bodyScale: BodyScale = BodyScale.NORMAL,
    val earInnerColor: Color = Color(0xFFFFCDD2), // Pink default
    val noseColor: Color = Color(0xFFD81B60),
    val hasStripes: Boolean = false,
    val stripeColor: Color = Color.Transparent,
    val hasSpots: Boolean = false,
    val spotColor: Color = Color.Transparent,
    val hasBelly: Boolean = false,
    val bellyColor: Color = Color.White
)

enum class PatternType {
    SOLID,      // Single color
    BICOLOR,    // Two colors (like tuxedo)
    TABBY,      // Striped pattern
    CALICO,     // Three colors with patches
    SIAMESE,    // Color points
    SPOTTED     // Spots pattern
}

enum class BodyScale {
    SLIM,       // Skinny cat
    NORMAL,     // Regular cat
    CHUBBY,     // Chubby cat
    FLUFFY      // Extra fluffy
}

object CatSkins {
    // Classic cats
    val ORANGE = CatSkin(
        id = "orange",
        name = "Naranjito",
        primaryColor = Color(0xFFFF9800),
        secondaryColor = Color(0xFFFFB74D),
        darkColor = Color(0xFFE65100)
    )

    val BLACK = CatSkin(
        id = "black",
        name = "Sombra",
        primaryColor = Color(0xFF212121),
        secondaryColor = Color(0xFF424242),
        darkColor = Color(0xFF000000),
        earInnerColor = Color(0xFF616161),
        noseColor = Color(0xFF37474F)
    )

    val WHITE = CatSkin(
        id = "white",
        name = "Nieve",
        primaryColor = Color(0xFFFAFAFA),
        secondaryColor = Color(0xFFFFFFFF),
        darkColor = Color(0xFFE0E0E0),
        earInnerColor = Color(0xFFFFCDD2),
        noseColor = Color(0xFFFFB6C1)
    )

    val GRAY = CatSkin(
        id = "gray",
        name = "Cenizo",
        primaryColor = Color(0xFF9E9E9E),
        secondaryColor = Color(0xFFBDBDBD),
        darkColor = Color(0xFF616161)
    )

    val CREAM = CatSkin(
        id = "cream",
        name = "Vainilla",
        primaryColor = Color(0xFFFFF8E1),
        secondaryColor = Color(0xFFFFECB3),
        darkColor = Color(0xFFFFE082),
        noseColor = Color(0xFFFFAB91)
    )

    // Patterned cats
    val TUXEDO = CatSkin(
        id = "tuxedo",
        name = "Elegante",
        primaryColor = Color(0xFF212121),
        secondaryColor = Color(0xFF424242),
        darkColor = Color(0xFF000000),
        patternType = PatternType.BICOLOR,
        hasBelly = true,
        bellyColor = Color.White,
        earInnerColor = Color(0xFF616161)
    )

    val TABBY_ORANGE = CatSkin(
        id = "tabby_orange",
        name = "Tigre",
        primaryColor = Color(0xFFFF9800),
        secondaryColor = Color(0xFFFFB74D),
        darkColor = Color(0xFFE65100),
        patternType = PatternType.TABBY,
        hasStripes = true,
        stripeColor = Color(0xFFBF360C)
    )

    val TABBY_GRAY = CatSkin(
        id = "tabby_gray",
        name = "Rayitas",
        primaryColor = Color(0xFF9E9E9E),
        secondaryColor = Color(0xFFBDBDBD),
        darkColor = Color(0xFF616161),
        patternType = PatternType.TABBY,
        hasStripes = true,
        stripeColor = Color(0xFF424242)
    )

    val CALICO = CatSkin(
        id = "calico",
        name = "Tricolor",
        primaryColor = Color(0xFFFAFAFA),
        secondaryColor = Color(0xFFFF9800),
        darkColor = Color(0xFF212121),
        patternType = PatternType.CALICO,
        hasSpots = true,
        spotColor = Color(0xFFFF9800)
    )

    val SIAMESE = CatSkin(
        id = "siamese",
        name = "Siames",
        primaryColor = Color(0xFFFFF8E1),
        secondaryColor = Color(0xFF8D6E63),
        darkColor = Color(0xFF5D4037),
        patternType = PatternType.SIAMESE,
        earInnerColor = Color(0xFF8D6E63),
        noseColor = Color(0xFF5D4037)
    )

    // Chubby cats
    val CHUBBY_ORANGE = CatSkin(
        id = "chubby_orange",
        name = "Gordito",
        primaryColor = Color(0xFFFF9800),
        secondaryColor = Color(0xFFFFB74D),
        darkColor = Color(0xFFE65100),
        bodyScale = BodyScale.CHUBBY,
        hasBelly = true,
        bellyColor = Color(0xFFFFE0B2)
    )

    val CHUBBY_GRAY = CatSkin(
        id = "chubby_gray",
        name = "Bolita",
        primaryColor = Color(0xFF78909C),
        secondaryColor = Color(0xFF90A4AE),
        darkColor = Color(0xFF546E7A),
        bodyScale = BodyScale.CHUBBY,
        hasBelly = true,
        bellyColor = Color(0xFFCFD8DC)
    )

    val CHUBBY_WHITE = CatSkin(
        id = "chubby_white",
        name = "Nubecita",
        primaryColor = Color(0xFFFAFAFA),
        secondaryColor = Color(0xFFFFFFFF),
        darkColor = Color(0xFFE0E0E0),
        bodyScale = BodyScale.CHUBBY,
        earInnerColor = Color(0xFFFFCDD2),
        noseColor = Color(0xFFFFB6C1)
    )

    // Slim cats
    val SLIM_BLACK = CatSkin(
        id = "slim_black",
        name = "Pantera",
        primaryColor = Color(0xFF212121),
        secondaryColor = Color(0xFF424242),
        darkColor = Color(0xFF000000),
        bodyScale = BodyScale.SLIM,
        earInnerColor = Color(0xFF616161),
        noseColor = Color(0xFF37474F)
    )

    val SLIM_SIAMESE = CatSkin(
        id = "slim_siamese",
        name = "Oriental",
        primaryColor = Color(0xFFFFF8E1),
        secondaryColor = Color(0xFF8D6E63),
        darkColor = Color(0xFF5D4037),
        bodyScale = BodyScale.SLIM,
        patternType = PatternType.SIAMESE,
        earInnerColor = Color(0xFF8D6E63),
        noseColor = Color(0xFF5D4037)
    )

    // Fluffy cats
    val FLUFFY_ORANGE = CatSkin(
        id = "fluffy_orange",
        name = "Pompom",
        primaryColor = Color(0xFFFF9800),
        secondaryColor = Color(0xFFFFB74D),
        darkColor = Color(0xFFE65100),
        bodyScale = BodyScale.FLUFFY
    )

    val FLUFFY_GRAY = CatSkin(
        id = "fluffy_gray",
        name = "Pelusa",
        primaryColor = Color(0xFF607D8B),
        secondaryColor = Color(0xFF78909C),
        darkColor = Color(0xFF455A64),
        bodyScale = BodyScale.FLUFFY
    )

    val FLUFFY_WHITE = CatSkin(
        id = "fluffy_white",
        name = "Algodón",
        primaryColor = Color(0xFFFAFAFA),
        secondaryColor = Color(0xFFFFFFFF),
        darkColor = Color(0xFFEEEEEE),
        bodyScale = BodyScale.FLUFFY,
        earInnerColor = Color(0xFFFFCDD2),
        noseColor = Color(0xFFFFB6C1)
    )

    // Special/Unique cats
    val GINGER_WHITE = CatSkin(
        id = "ginger_white",
        name = "Manchitas",
        primaryColor = Color(0xFFFFFFFF),
        secondaryColor = Color(0xFFFF9800),
        darkColor = Color(0xFFE65100),
        patternType = PatternType.SPOTTED,
        hasSpots = true,
        spotColor = Color(0xFFFF9800)
    )

    val BROWN_TABBY = CatSkin(
        id = "brown_tabby",
        name = "Chocolate",
        primaryColor = Color(0xFF8D6E63),
        secondaryColor = Color(0xFFA1887F),
        darkColor = Color(0xFF5D4037),
        patternType = PatternType.TABBY,
        hasStripes = true,
        stripeColor = Color(0xFF3E2723)
    )

    val BLUE_GRAY = CatSkin(
        id = "blue_gray",
        name = "Azulito",
        primaryColor = Color(0xFF78909C),
        secondaryColor = Color(0xFF90A4AE),
        darkColor = Color(0xFF546E7A),
        noseColor = Color(0xFF78909C)
    )

    val RUSSIAN_BLUE = CatSkin(
        id = "russian_blue",
        name = "Ruso",
        primaryColor = Color(0xFF607D8B),
        secondaryColor = Color(0xFF78909C),
        darkColor = Color(0xFF455A64),
        earInnerColor = Color(0xFF90A4AE),
        noseColor = Color(0xFF546E7A)
    )

    val PERSIAN = CatSkin(
        id = "persian",
        name = "Persa",
        primaryColor = Color(0xFFFFE0B2),
        secondaryColor = Color(0xFFFFECB3),
        darkColor = Color(0xFFFFCC80),
        bodyScale = BodyScale.FLUFFY,
        noseColor = Color(0xFFFF8A65)
    )

    val MIDNIGHT = CatSkin(
        id = "midnight",
        name = "Medianoche",
        primaryColor = Color(0xFF1A237E),
        secondaryColor = Color(0xFF283593),
        darkColor = Color(0xFF0D1642),
        earInnerColor = Color(0xFF3949AB),
        noseColor = Color(0xFF5C6BC0)
    )

    val SUNSET = CatSkin(
        id = "sunset",
        name = "Atardecer",
        primaryColor = Color(0xFFFF7043),
        secondaryColor = Color(0xFFFFAB91),
        darkColor = Color(0xFFE64A19),
        noseColor = Color(0xFFFF5722)
    )

    val LAVENDER = CatSkin(
        id = "lavender",
        name = "Lavanda",
        primaryColor = Color(0xFFCE93D8),
        secondaryColor = Color(0xFFE1BEE7),
        darkColor = Color(0xFFAB47BC),
        earInnerColor = Color(0xFFF3E5F5),
        noseColor = Color(0xFFBA68C8)
    )

    val MINT = CatSkin(
        id = "mint",
        name = "Menta",
        primaryColor = Color(0xFF80CBC4),
        secondaryColor = Color(0xFFB2DFDB),
        darkColor = Color(0xFF4DB6AC),
        earInnerColor = Color(0xFFE0F2F1),
        noseColor = Color(0xFF26A69A)
    )

    val GOLDEN = CatSkin(
        id = "golden",
        name = "Dorado",
        primaryColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFFFFE082),
        darkColor = Color(0xFFFFC107),
        noseColor = Color(0xFFFF8F00)
    )

    // All available skins
    val ALL_SKINS = listOf(
        ORANGE,
        BLACK,
        WHITE,
        GRAY,
        CREAM,
        TUXEDO,
        TABBY_ORANGE,
        TABBY_GRAY,
        CALICO,
        SIAMESE,
        CHUBBY_ORANGE,
        CHUBBY_GRAY,
        CHUBBY_WHITE,
        SLIM_BLACK,
        SLIM_SIAMESE,
        FLUFFY_ORANGE,
        FLUFFY_GRAY,
        FLUFFY_WHITE,
        GINGER_WHITE,
        BROWN_TABBY,
        BLUE_GRAY,
        RUSSIAN_BLUE,
        PERSIAN,
        MIDNIGHT,
        SUNSET,
        LAVENDER,
        MINT,
        GOLDEN
    )

    fun getById(id: String): CatSkin {
        return ALL_SKINS.find { it.id == id } ?: ORANGE
    }
}
