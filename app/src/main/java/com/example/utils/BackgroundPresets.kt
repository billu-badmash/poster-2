package com.example.utils

import com.example.domain.models.BackgroundType
import com.example.domain.models.PosterBackground

data class GradientPreset(
    val name: String,
    val color1: String,
    val color2: String,
    val color3: String? = null,
    val category: String = "Vibrant"
)

object BackgroundPresets {

    val gradients = listOf(
        // Modern Vibrant Gradients
        GradientPreset("Cyberpunk Glow", "#8A2387", "#E94057", "#F27121", "Vibrant"),
        GradientPreset("Sunset Horizon", "#FA709A", "#FEE140", null, "Warm"),
        GradientPreset("Oceanic Depths", "#2E3192", "#1BFFFF", null, "Cool"),
        GradientPreset("Emerald Luxury", "#0BA360", "#3CBA92", null, "Nature"),
        GradientPreset("Neon Twilight", "#4E54C8", "#8F94FB", null, "Vibrant"),
        GradientPreset("Midnight Obsidian", "#0F2027", "#203A43", "#2C5364", "Dark"),
        GradientPreset("Royal Velvet", "#3A1C71", "#D76D77", "#FFAF7B", "Luxury"),
        GradientPreset("Golden Aura", "#F7971E", "#FFD200", null, "Warm"),
        GradientPreset("Soft Lavender", "#A18CD1", "#FBC2EB", null, "Pastel"),
        GradientPreset("Cosmic Fusion", "#654EA3", "#EAAFC8", null, "Pastel"),
        GradientPreset("Deep Space", "#000428", "#004E92", null, "Dark"),
        GradientPreset("Mint Sorbet", "#11998E", "#38EF7D", null, "Nature"),
        GradientPreset("Rose Quartz", "#FF758C", "#FF7EB3", null, "Warm"),
        GradientPreset("Dark Carbon", "#141E30", "#243B55", null, "Dark")
    )

    val solidPresets = listOf(
        "#FFFFFF", "#0F172A", "#1E293B", "#3B82F6",
        "#10B981", "#F59E0B", "#EF4444", "#8B5CF6",
        "#EC4899", "#14B8A6", "#6366F1", "#F97316",
        "#84CC16", "#06B6D4", "#64748B", "#18181B"
    )

    fun applyPreset(preset: GradientPreset): PosterBackground {
        return PosterBackground(
            type = BackgroundType.GRADIENT_LINEAR,
            color1Hex = preset.color1,
            color2Hex = preset.color2
        )
    }
}
