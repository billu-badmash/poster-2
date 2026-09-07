package com.example.utils

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import java.util.UUID

data class FontPreset(
    val name: String,
    val category: String,
    val fontFamily: FontFamily,
    val isBoldDefault: Boolean = false,
    val isItalicDefault: Boolean = false,
    val previewText: String = "Design"
)

data class TypographyPairing(
    val name: String,
    val heading: String,
    val subtitle: String,
    val ctaText: String? = null,
    val headingColor: String = "#FFFFFF",
    val subtitleColor: String = "#94A3B8"
)

data class MarketingCopyInspiration(
    val category: String,
    val title: String,
    val subtitle: String,
    val badge: String? = null
)

object TypographyHelper {

    val fontPresets = listOf(
        FontPreset("Modern Sans", "Sans-Serif", FontFamily.SansSerif, isBoldDefault = true),
        FontPreset("Clean Default", "Sans-Serif", FontFamily.Default, isBoldDefault = false),
        FontPreset("Luxury Serif", "Serif", FontFamily.Serif, isBoldDefault = false),
        FontPreset("Classic Editorial", "Serif", FontFamily.Serif, isBoldDefault = true, isItalicDefault = true),
        FontPreset("Cyber Monospace", "Monospace", FontFamily.Monospace, isBoldDefault = true),
        FontPreset("Tech Code", "Monospace", FontFamily.Monospace, isBoldDefault = false),
        FontPreset("Elegant Cursive", "Script", FontFamily.Cursive, isBoldDefault = false, isItalicDefault = true),
        FontPreset("Script Flow", "Script", FontFamily.Cursive, isBoldDefault = true)
    )

    val marketingInspirations = listOf(
        MarketingCopyInspiration(
            category = "🔥 Sales & Offers",
            title = "MEGA FLASH SALE",
            subtitle = "Up to 70% OFF • This Weekend Only",
            badge = "SPECIAL OFFER"
        ),
        MarketingCopyInspiration(
            category = "🔥 Sales & Offers",
            title = "SUMMER CLEARANCE",
            subtitle = "Buy 1 Get 1 Free on All Items",
            badge = "LIMITED TIME"
        ),
        MarketingCopyInspiration(
            category = "🔥 Sales & Offers",
            title = "BLACK FRIDAY",
            subtitle = "Unbeatable Deals Starting Now",
            badge = "EXCLUSIVE"
        ),
        MarketingCopyInspiration(
            category = "🎉 Events & Parties",
            title = "GRAND OPENING",
            subtitle = "Join Us for Music, Food & Free Giveaways",
            badge = "YOU'RE INVITED"
        ),
        MarketingCopyInspiration(
            category = "🎉 Events & Parties",
            title = "LIVE MUSIC NIGHT",
            subtitle = "Featuring Special Guests • Doors Open at 8 PM",
            badge = "FRIDAY NIGHT"
        ),
        MarketingCopyInspiration(
            category = "🎉 Events & Parties",
            title = "ANNUAL GALA 2026",
            subtitle = "An Evening of Elegance & Celebration",
            badge = "VIP ACCESS"
        ),
        MarketingCopyInspiration(
            category = "🚀 Business & Tech",
            title = "GROW YOUR BRAND",
            subtitle = "Smart Marketing Strategies for High Growth",
            badge = "WE ARE HIRING"
        ),
        MarketingCopyInspiration(
            category = "🚀 Business & Tech",
            title = "INNOVATE THE FUTURE",
            subtitle = "Next-Gen Solutions for Modern Enterprises",
            badge = "TECH SUMMIT"
        ),
        MarketingCopyInspiration(
            category = "🚀 Business & Tech",
            title = "CREATIVE STUDIO",
            subtitle = "Transforming Visions into Visual Realities",
            badge = "PORTFOLIO"
        ),
        MarketingCopyInspiration(
            category = "💡 Quotes & Mindset",
            title = "DREAM BIG • ACT FAST",
            subtitle = "The future belongs to those who build it today",
            badge = "DAILY WISDOM"
        ),
        MarketingCopyInspiration(
            category = "💡 Quotes & Mindset",
            title = "CREATE EVERY DAY",
            subtitle = "Consistency is the mother of mastery",
            badge = "MOTIVATION"
        ),
        MarketingCopyInspiration(
            category = "💡 Quotes & Mindset",
            title = "SIMPLICITY IS LUXURY",
            subtitle = "Design is not just what it looks like, but how it works",
            badge = "AESTHETICS"
        )
    )

    fun createInspirationElements(inspiration: MarketingCopyInspiration, startLayer: Int = 1): List<CanvasElement> {
        val result = mutableListOf<CanvasElement>()
        var layer = startLayer

        // Optional badge element
        if (!inspiration.badge.isNullOrBlank()) {
            result.add(
                CanvasElement(
                    id = "el_badge_" + UUID.randomUUID().toString().take(6),
                    type = ElementType.SHAPE,
                    fillColorHex = "#F59E0B",
                    xRatio = 0.35f,
                    yRatio = 0.22f,
                    widthRatio = 0.3f,
                    heightRatio = 0.05f,
                    shapeCornerRadiusDp = 20f,
                    layerOrder = layer++
                )
            )
            result.add(
                CanvasElement(
                    id = "el_badgetext_" + UUID.randomUUID().toString().take(6),
                    type = ElementType.TEXT,
                    text = inspiration.badge,
                    fontSizeSp = 12f,
                    fontColorHex = "#1E293B",
                    isBold = true,
                    xRatio = 0.35f,
                    yRatio = 0.22f,
                    widthRatio = 0.3f,
                    heightRatio = 0.05f,
                    layerOrder = layer++
                )
            )
        }

        // Title element
        result.add(
            CanvasElement(
                id = "el_title_" + UUID.randomUUID().toString().take(6),
                type = ElementType.TEXT,
                text = inspiration.title,
                fontSizeSp = 30f,
                fontColorHex = "#FFFFFF",
                isBold = true,
                xRatio = 0.1f,
                yRatio = 0.32f,
                widthRatio = 0.8f,
                heightRatio = 0.16f,
                layerOrder = layer++
            )
        )

        // Subtitle element
        result.add(
            CanvasElement(
                id = "el_sub_" + UUID.randomUUID().toString().take(6),
                type = ElementType.TEXT,
                text = inspiration.subtitle,
                fontSizeSp = 16f,
                fontColorHex = "#E2E8F0",
                isBold = false,
                xRatio = 0.12f,
                yRatio = 0.50f,
                widthRatio = 0.76f,
                heightRatio = 0.12f,
                layerOrder = layer++
            )
        )

        return result
    }
}
