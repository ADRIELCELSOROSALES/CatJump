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
    val earInnerColor: Color = Color(0xFFFFCDD2),
    val noseColor: Color = Color(0xFFD81B60),
    val hasStripes: Boolean = false,
    val stripeColor: Color = Color.Transparent,
    val hasSpots: Boolean = false,
    val spotColor: Color = Color.Transparent,
    val hasBelly: Boolean = false,
    val bellyColor: Color = Color.White
)

enum class PatternType {
    SOLID,
    BICOLOR,
    TABBY,
    CALICO,
    SIAMESE,
    SPOTTED
}

enum class BodyScale {
    SLIM,
    NORMAL,
    CHUBBY,
    FLUFFY
}

object CatSkins {
    // 1. Naranjito - Classic orange cat
    val ORANGE = CatSkin(
        id = "orange",
        name = "Naranjito",
        primaryColor = Color(0xFFFF9800),
        secondaryColor = Color(0xFFFFB74D),
        darkColor = Color(0xFFE65100)
    )

    // 2. Medianoche - Pure black cat
    val MIDNIGHT = CatSkin(
        id = "midnight",
        name = "Medianoche",
        primaryColor = Color(0xFF1A1A1A),
        secondaryColor = Color(0xFF2D2D2D),
        darkColor = Color(0xFF000000),
        earInnerColor = Color(0xFF424242),
        noseColor = Color(0xFF37474F)
    )

    // 3. Copito - Pure white cat
    val SNOWBALL = CatSkin(
        id = "snowball",
        name = "Copito",
        primaryColor = Color(0xFFFFFFFFF),
        secondaryColor = Color(0xFFF5F5F5),
        darkColor = Color(0xFFE0E0E0),
        earInnerColor = Color(0xFFFFCDD2),
        noseColor = Color(0xFFFF8A80)
    )

    // 4. Caramelo - Cream/caramel cat
    val CARAMEL = CatSkin(
        id = "caramel",
        name = "Caramelo",
        primaryColor = Color(0xFFD4A574),
        secondaryColor = Color(0xFFE8C9A0),
        darkColor = Color(0xFFB8860B),
        noseColor = Color(0xFFCD853F)
    )

    // 5. Cenizo - Gray cat
    val ASHEN = CatSkin(
        id = "ashen",
        name = "Cenizo",
        primaryColor = Color(0xFF757575),
        secondaryColor = Color(0xFF9E9E9E),
        darkColor = Color(0xFF424242)
    )

    // 6. Tigrito - Orange tabby with stripes
    val TIGER = CatSkin(
        id = "tiger",
        name = "Tigrito",
        primaryColor = Color(0xFFFF8C00),
        secondaryColor = Color(0xFFFFAB40),
        darkColor = Color(0xFFE65100),
        patternType = PatternType.TABBY,
        hasStripes = true,
        stripeColor = Color(0xFF8B4513)
    )

    // 7. Smoky - Gray tabby
    val SMOKY = CatSkin(
        id = "smoky",
        name = "Smoky",
        primaryColor = Color(0xFF6E6E6E),
        secondaryColor = Color(0xFF8E8E8E),
        darkColor = Color(0xFF4A4A4A),
        patternType = PatternType.TABBY,
        hasStripes = true,
        stripeColor = Color(0xFF2E2E2E)
    )

    // 8. Tuxedo - Black and white formal cat
    val TUXEDO = CatSkin(
        id = "tuxedo",
        name = "Elegante",
        primaryColor = Color(0xFF1C1C1C),
        secondaryColor = Color(0xFF333333),
        darkColor = Color(0xFF000000),
        patternType = PatternType.BICOLOR,
        hasBelly = true,
        bellyColor = Color.White,
        earInnerColor = Color(0xFF4A4A4A)
    )

    // 9. Siames - Siamese pointed cat
    val SIAMESE = CatSkin(
        id = "siamese",
        name = "Siames",
        primaryColor = Color(0xFFFAF0E6),
        secondaryColor = Color(0xFF8B7355),
        darkColor = Color(0xFF5D4037),
        patternType = PatternType.SIAMESE,
        earInnerColor = Color(0xFF8B7355),
        noseColor = Color(0xFF5D4037),
        bodyScale = BodyScale.SLIM
    )

    // 10. Tricolor - Calico cat
    val CALICO = CatSkin(
        id = "calico",
        name = "Tricolor",
        primaryColor = Color(0xFFFFFAF0),
        secondaryColor = Color(0xFFFF7F50),
        darkColor = Color(0xFF2F2F2F),
        patternType = PatternType.CALICO,
        hasSpots = true,
        spotColor = Color(0xFFFF6B35)
    )

    // 11. Bolita - Chubby orange
    val CHUBBY_ORANGE = CatSkin(
        id = "chubby_orange",
        name = "Bolita",
        primaryColor = Color(0xFFFFAA33),
        secondaryColor = Color(0xFFFFCC66),
        darkColor = Color(0xFFDD8800),
        bodyScale = BodyScale.CHUBBY,
        hasBelly = true,
        bellyColor = Color(0xFFFFE4B5)
    )

    // 12. Nubecita - Fluffy white
    val FLUFFY_WHITE = CatSkin(
        id = "fluffy_white",
        name = "Nubecita",
        primaryColor = Color(0xFFF8F8FF),
        secondaryColor = Color(0xFFFFFFFF),
        darkColor = Color(0xFFDCDCDC),
        bodyScale = BodyScale.FLUFFY,
        earInnerColor = Color(0xFFFFB6C1),
        noseColor = Color(0xFFFFB6C1)
    )

    // 13. Pantera - Slim black cat
    val PANTHER = CatSkin(
        id = "panther",
        name = "Pantera",
        primaryColor = Color(0xFF0D0D0D),
        secondaryColor = Color(0xFF1F1F1F),
        darkColor = Color(0xFF000000),
        bodyScale = BodyScale.SLIM,
        earInnerColor = Color(0xFF333333),
        noseColor = Color(0xFF1A1A1A)
    )

    // 14. Pelusa - Fluffy gray
    val FLUFFY_GRAY = CatSkin(
        id = "fluffy_gray",
        name = "Pelusa",
        primaryColor = Color(0xFF808080),
        secondaryColor = Color(0xFFA0A0A0),
        darkColor = Color(0xFF505050),
        bodyScale = BodyScale.FLUFFY
    )

    // 15. Chocolate - Brown cat
    val CHOCOLATE = CatSkin(
        id = "chocolate",
        name = "Chocolate",
        primaryColor = Color(0xFF5C4033),
        secondaryColor = Color(0xFF7B5B4C),
        darkColor = Color(0xFF3D2817),
        noseColor = Color(0xFF4A3728)
    )

    // 16. Canela - Cinnamon tabby
    val CINNAMON = CatSkin(
        id = "cinnamon",
        name = "Canela",
        primaryColor = Color(0xFFD2691E),
        secondaryColor = Color(0xFFE89050),
        darkColor = Color(0xFFA0522D),
        patternType = PatternType.TABBY,
        hasStripes = true,
        stripeColor = Color(0xFF8B4513)
    )

    // 17. Ruso - Russian blue
    val RUSSIAN = CatSkin(
        id = "russian",
        name = "Ruso",
        primaryColor = Color(0xFF5F7A8C),
        secondaryColor = Color(0xFF7A9AAE),
        darkColor = Color(0xFF445566),
        earInnerColor = Color(0xFF8FAABC),
        noseColor = Color(0xFF607080)
    )

    // 18. Doradito - Golden cat
    val GOLDEN = CatSkin(
        id = "golden",
        name = "Doradito",
        primaryColor = Color(0xFFDAA520),
        secondaryColor = Color(0xFFFFD700),
        darkColor = Color(0xFFB8860B),
        noseColor = Color(0xFFCD853F)
    )

    // 19. Manchitas - Spotted white and orange
    val SPOTTED = CatSkin(
        id = "spotted",
        name = "Manchitas",
        primaryColor = Color(0xFFFFF8DC),
        secondaryColor = Color(0xFFFF8C00),
        darkColor = Color(0xFFE07000),
        patternType = PatternType.SPOTTED,
        hasSpots = true,
        spotColor = Color(0xFFFF7F00)
    )

    // 20. Gordito Gris - Chubby gray
    val CHUBBY_GRAY = CatSkin(
        id = "chubby_gray",
        name = "Panzoncito",
        primaryColor = Color(0xFF696969),
        secondaryColor = Color(0xFF888888),
        darkColor = Color(0xFF484848),
        bodyScale = BodyScale.CHUBBY,
        hasBelly = true,
        bellyColor = Color(0xFFB8B8B8)
    )

    // 21. Flaquito - Slim cream cat
    val SLIM_CREAM = CatSkin(
        id = "slim_cream",
        name = "Flaquito",
        primaryColor = Color(0xFFFFEBCD),
        secondaryColor = Color(0xFFFFF5E6),
        darkColor = Color(0xFFDEB887),
        bodyScale = BodyScale.SLIM,
        noseColor = Color(0xFFDEB887)
    )

    // 22. Pompón - Fluffy orange
    val FLUFFY_ORANGE = CatSkin(
        id = "fluffy_orange",
        name = "Pompon",
        primaryColor = Color(0xFFFF7722),
        secondaryColor = Color(0xFFFF9955),
        darkColor = Color(0xFFDD5500),
        bodyScale = BodyScale.FLUFFY
    )

    // 23. Lavanda - Lilac/lavender cat
    val LAVENDER = CatSkin(
        id = "lavender",
        name = "Lavanda",
        primaryColor = Color(0xFFB19CD9),
        secondaryColor = Color(0xFFD4C4E8),
        darkColor = Color(0xFF8A7AB8),
        earInnerColor = Color(0xFFE6D5F2),
        noseColor = Color(0xFF9B8AC4)
    )

    // 24. Menta - Mint colored fantasy cat
    val MINT = CatSkin(
        id = "mint",
        name = "Menta",
        primaryColor = Color(0xFF98E8C1),
        secondaryColor = Color(0xFFB8F0D4),
        darkColor = Color(0xFF5FBFA0),
        earInnerColor = Color(0xFFD4F5E6),
        noseColor = Color(0xFF70C8A8)
    )

    // 25. Atardecer - Sunset orange-pink
    val SUNSET = CatSkin(
        id = "sunset",
        name = "Atardecer",
        primaryColor = Color(0xFFFF6B6B),
        secondaryColor = Color(0xFFFFAB76),
        darkColor = Color(0xFFE04545),
        noseColor = Color(0xFFFF8080),
        hasBelly = true,
        bellyColor = Color(0xFFFFD4A8)
    )

    // 26. Oceano - Blue-teal cat
    val OCEAN = CatSkin(
        id = "ocean",
        name = "Oceano",
        primaryColor = Color(0xFF4A90A4),
        secondaryColor = Color(0xFF6BB5CC),
        darkColor = Color(0xFF2E6070),
        earInnerColor = Color(0xFF8ED0E8),
        noseColor = Color(0xFF5599B0)
    )

    // 27. Rosita - Pink fantasy cat
    val ROSITA = CatSkin(
        id = "rosita",
        name = "Rosita",
        primaryColor = Color(0xFFFF9EB5),
        secondaryColor = Color(0xFFFFB8CA),
        darkColor = Color(0xFFE87A95),
        earInnerColor = Color(0xFFFFD4E0),
        noseColor = Color(0xFFFF7090)
    )

    // 28. Galaxia - Purple cosmic cat
    val GALAXY = CatSkin(
        id = "galaxy",
        name = "Galaxia",
        primaryColor = Color(0xFF6A0DAD),
        secondaryColor = Color(0xFF9B30FF),
        darkColor = Color(0xFF4B0082),
        earInnerColor = Color(0xFFDA70D6),
        noseColor = Color(0xFF8B008B),
        hasSpots = true,
        spotColor = Color(0xFFE0B0FF)
    )

    val ALL_SKINS = listOf(
        ORANGE,
        MIDNIGHT,
        SNOWBALL,
        CARAMEL,
        ASHEN,
        TIGER,
        SMOKY,
        TUXEDO,
        SIAMESE,
        CALICO,
        CHUBBY_ORANGE,
        FLUFFY_WHITE,
        PANTHER,
        FLUFFY_GRAY,
        CHOCOLATE,
        CINNAMON,
        RUSSIAN,
        GOLDEN,
        SPOTTED,
        CHUBBY_GRAY,
        SLIM_CREAM,
        FLUFFY_ORANGE,
        LAVENDER,
        MINT,
        SUNSET,
        OCEAN,
        ROSITA,
        GALAXY
    )

    fun getById(id: String): CatSkin {
        return ALL_SKINS.find { it.id == id } ?: ORANGE
    }
}
