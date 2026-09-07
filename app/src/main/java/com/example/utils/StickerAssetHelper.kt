package com.example.utils

import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import com.example.domain.models.ShapeType
import java.util.UUID

data class StickerPreset(
    val id: String,
    val title: String,
    val category: StickerCategory,
    val elementGenerator: () -> List<CanvasElement>
)

enum class StickerCategory(val label: String) {
    SALE_DISCOUNT("Sale & Deals"),
    BADGES_TRUST("Badges & Trust"),
    CTA_BUTTONS("Call to Action"),
    EVENT_PARTY("Events & News")
}

object StickerAssetHelper {

    private fun newId() = "el_" + UUID.randomUUID().toString().take(6)

    val presets: List<StickerPreset> = listOf(
        // 1. SALE & DISCOUNT
        StickerPreset(
            id = "stk_50_off",
            title = "50% OFF Badge",
            category = StickerCategory.SALE_DISCOUNT,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.CIRCLE,
                        fillColorHex = "#EF4444",
                        xRatio = 0.35f,
                        yRatio = 0.35f,
                        widthRatio = 0.3f,
                        heightRatio = 0.15f,
                        strokeWidthDp = 2f,
                        strokeColorHex = "#FFFFFF"
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "50% OFF",
                        fontSizeSp = 22f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.35f,
                        yRatio = 0.38f,
                        widthRatio = 0.3f,
                        heightRatio = 0.08f
                    )
                )
            }
        ),
        StickerPreset(
            id = "stk_mega_sale",
            title = "Mega Sale Ribbon",
            category = StickerCategory.SALE_DISCOUNT,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColorHex = "#F59E0B",
                        xRatio = 0.25f,
                        yRatio = 0.4f,
                        widthRatio = 0.5f,
                        heightRatio = 0.08f,
                        shapeCornerRadiusDp = 12f
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "🔥 MEGA FLASH SALE",
                        fontSizeSp = 16f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.25f,
                        yRatio = 0.41f,
                        widthRatio = 0.5f,
                        heightRatio = 0.06f
                    )
                )
            }
        ),
        StickerPreset(
            id = "stk_bogo",
            title = "Buy 1 Get 1 Free",
            category = StickerCategory.SALE_DISCOUNT,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColorHex = "#10B981",
                        xRatio = 0.25f,
                        yRatio = 0.4f,
                        widthRatio = 0.5f,
                        heightRatio = 0.08f,
                        shapeCornerRadiusDp = 14f
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "BUY 1 GET 1 FREE",
                        fontSizeSp = 15f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.25f,
                        yRatio = 0.41f,
                        widthRatio = 0.5f,
                        heightRatio = 0.06f
                    )
                )
            }
        ),

        // 2. BADGES & TRUST
        StickerPreset(
            id = "stk_verified",
            title = "Verified Official Badge",
            category = StickerCategory.BADGES_TRUST,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.BADGE,
                        fillColorHex = "#2563EB",
                        xRatio = 0.3f,
                        yRatio = 0.4f,
                        widthRatio = 0.4f,
                        heightRatio = 0.07f,
                        shapeCornerRadiusDp = 20f
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "✓ 100% VERIFIED",
                        fontSizeSp = 13f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.3f,
                        yRatio = 0.41f,
                        widthRatio = 0.4f,
                        heightRatio = 0.05f
                    )
                )
            }
        ),
        StickerPreset(
            id = "stk_premium",
            title = "Premium Quality Seal",
            category = StickerCategory.BADGES_TRUST,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColorHex = "#0F172A",
                        xRatio = 0.28f,
                        yRatio = 0.4f,
                        widthRatio = 0.44f,
                        heightRatio = 0.07f,
                        shapeCornerRadiusDp = 10f,
                        strokeWidthDp = 1.5f,
                        strokeColorHex = "#F59E0B"
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "⭐ PREMIUM QUALITY",
                        fontSizeSp = 13f,
                        fontColorHex = "#F59E0B",
                        isBold = true,
                        xRatio = 0.28f,
                        yRatio = 0.41f,
                        widthRatio = 0.44f,
                        heightRatio = 0.05f
                    )
                )
            }
        ),

        // 3. CALL TO ACTION BUTTONS
        StickerPreset(
            id = "stk_shop_now",
            title = "Shop Now Button",
            category = StickerCategory.CTA_BUTTONS,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColorHex = "#111827",
                        xRatio = 0.3f,
                        yRatio = 0.8f,
                        widthRatio = 0.4f,
                        heightRatio = 0.08f,
                        shapeCornerRadiusDp = 16f
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "SHOP NOW →",
                        fontSizeSp = 15f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.3f,
                        yRatio = 0.81f,
                        widthRatio = 0.4f,
                        heightRatio = 0.06f
                    )
                )
            }
        ),
        StickerPreset(
            id = "stk_order_online",
            title = "Order Online Pill",
            category = StickerCategory.CTA_BUTTONS,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColorHex = "#DC2626",
                        xRatio = 0.25f,
                        yRatio = 0.8f,
                        widthRatio = 0.5f,
                        heightRatio = 0.08f,
                        shapeCornerRadiusDp = 20f
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "ORDER ONLINE TODAY",
                        fontSizeSp = 14f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.25f,
                        yRatio = 0.81f,
                        widthRatio = 0.5f,
                        heightRatio = 0.06f
                    )
                )
            }
        ),

        // 4. EVENTS & NEWS
        StickerPreset(
            id = "stk_grand_open",
            title = "Grand Opening Tag",
            category = StickerCategory.EVENT_PARTY,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColorHex = "#8B5CF6",
                        xRatio = 0.2f,
                        yRatio = 0.15f,
                        widthRatio = 0.6f,
                        heightRatio = 0.08f,
                        shapeCornerRadiusDp = 12f
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "🎉 GRAND OPENING",
                        fontSizeSp = 16f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.2f,
                        yRatio = 0.16f,
                        widthRatio = 0.6f,
                        heightRatio = 0.06f
                    )
                )
            }
        ),
        StickerPreset(
            id = "stk_live_webinar",
            title = "Live Webinar Pill",
            category = StickerCategory.EVENT_PARTY,
            elementGenerator = {
                val bgId = newId()
                val txtId = newId()
                listOf(
                    CanvasElement(
                        id = bgId,
                        type = ElementType.SHAPE,
                        shapeType = ShapeType.ROUNDED_RECT,
                        fillColorHex = "#DC2626",
                        xRatio = 0.32f,
                        yRatio = 0.12f,
                        widthRatio = 0.36f,
                        heightRatio = 0.06f,
                        shapeCornerRadiusDp = 16f
                    ),
                    CanvasElement(
                        id = txtId,
                        type = ElementType.TEXT,
                        text = "🔴 LIVE EVENT",
                        fontSizeSp = 13f,
                        fontColorHex = "#FFFFFF",
                        isBold = true,
                        xRatio = 0.32f,
                        yRatio = 0.13f,
                        widthRatio = 0.36f,
                        heightRatio = 0.04f
                    )
                )
            }
        )
    )
}
