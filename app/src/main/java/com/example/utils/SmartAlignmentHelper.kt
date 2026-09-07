package com.example.utils

import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import com.example.domain.models.ShapeType
import java.util.UUID
import kotlin.math.abs

enum class GuideOrientation {
    VERTICAL,
    HORIZONTAL
}

enum class GuideType {
    CANVAS_CENTER,
    CANVAS_MARGIN,
    CANVAS_EDGE,
    ELEMENT_ALIGN_START,
    ELEMENT_ALIGN_CENTER,
    ELEMENT_ALIGN_END,
    EQUAL_SPACING
}

data class AlignmentGuide(
    val orientation: GuideOrientation,
    val positionRatio: Float,
    val guideType: GuideType = GuideType.ELEMENT_ALIGN_CENTER,
    val label: String? = null,
    val isCenterGuide: Boolean = false,
    val gapDistancePx: Int? = null
)

data class SnapResult(
    val snappedXRatio: Float,
    val snappedYRatio: Float,
    val activeGuides: List<AlignmentGuide> = emptyList(),
    val isSnapped: Boolean = false
)

object SmartAlignmentHelper {

    const val DEFAULT_SNAP_THRESHOLD: Float = 0.015f // 1.5% of canvas size
    const val DEFAULT_CANVAS_MARGIN: Float = 0.05f   // 5% margin

    /**
     * Calculates magnetically snapped position (xRatio, yRatio) and active smart guidelines
     * when dragging an element across the canvas.
     */
    fun calculateSnap(
        element: CanvasElement,
        targetXRatio: Float,
        targetYRatio: Float,
        otherElements: List<CanvasElement>,
        snapEnabled: Boolean = true,
        snapThreshold: Float = DEFAULT_SNAP_THRESHOLD,
        canvasMargin: Float = DEFAULT_CANVAS_MARGIN
    ): SnapResult {
        if (!snapEnabled) {
            return SnapResult(
                snappedXRatio = targetXRatio.coerceIn(0f, 1f - element.widthRatio),
                snappedYRatio = targetYRatio.coerceIn(0f, 1f - element.heightRatio),
                activeGuides = emptyList(),
                isSnapped = false
            )
        }

        val w = element.widthRatio
        val h = element.heightRatio
        val validOthers = otherElements.filter { it.isVisible && it.id != element.id }

        val activeGuides = mutableListOf<AlignmentGuide>()
        var didSnap = false

        // -------------------------
        // 1. VERTICAL GUIDES (X AXIS)
        // -------------------------
        var bestX = targetXRatio
        var minXDiff = snapThreshold
        var bestXGuide: AlignmentGuide? = null

        data class XCandidate(
            val target: Float,
            val candidateX: Float,
            val guideType: GuideType,
            val label: String,
            val isCenter: Boolean,
            val gapDist: Int? = null
        )

        val xCandidates = mutableListOf<XCandidate>()

        // Canvas Center (X = 0.5)
        xCandidates.add(XCandidate(0.5f, 0.5f - w / 2f, GuideType.CANVAS_CENTER, "Center X", true))
        // Canvas Margins
        xCandidates.add(XCandidate(canvasMargin, canvasMargin, GuideType.CANVAS_MARGIN, "Left Margin", false))
        xCandidates.add(XCandidate(1f - canvasMargin, 1f - canvasMargin - w, GuideType.CANVAS_MARGIN, "Right Margin", false))
        // Canvas Edges
        xCandidates.add(XCandidate(0f, 0f, GuideType.CANVAS_EDGE, "Left Edge", false))
        xCandidates.add(XCandidate(1f, 1f - w, GuideType.CANVAS_EDGE, "Right Edge", false))

        // Targets from other visible elements
        for (other in validOthers) {
            val otherLeft = other.xRatio
            val otherCenterX = other.xRatio + other.widthRatio / 2f
            val otherRight = other.xRatio + other.widthRatio

            // Element Left matches other Left
            xCandidates.add(XCandidate(otherLeft, otherLeft, GuideType.ELEMENT_ALIGN_START, "Align Left", false))
            // Element Center matches other Center
            xCandidates.add(XCandidate(otherCenterX, otherCenterX - w / 2f, GuideType.ELEMENT_ALIGN_CENTER, "Center X", true))
            // Element Right matches other Right
            xCandidates.add(XCandidate(otherRight, otherRight - w, GuideType.ELEMENT_ALIGN_END, "Align Right", false))
            // Element Left matches other Right (flush right)
            xCandidates.add(XCandidate(otherRight, otherRight, GuideType.ELEMENT_ALIGN_START, "Align Edge", false))
            // Element Right matches other Left (flush left)
            xCandidates.add(XCandidate(otherLeft, otherLeft - w, GuideType.ELEMENT_ALIGN_END, "Align Edge", false))
        }

        // Equal Spacing on X Axis (detect when gap between A & B matches gap between B & C)
        if (validOthers.size >= 2) {
            val sortedX = validOthers.sortedBy { it.xRatio }
            for (i in 0 until sortedX.size - 1) {
                val e1 = sortedX[i]
                val e2 = sortedX[i + 1]
                val gap = e2.xRatio - (e1.xRatio + e1.widthRatio)
                if (gap > 0.01f) {
                    val gapPx = (gap * 1000).toInt()
                    // 1. Right of e2 with same gap
                    val candidateRight = e2.xRatio + e2.widthRatio + gap
                    xCandidates.add(XCandidate(candidateRight, candidateRight, GuideType.EQUAL_SPACING, "Equal Gap", false, gapPx))
                    // 2. Left of e1 with same gap
                    val candidateLeft = e1.xRatio - gap - w
                    xCandidates.add(XCandidate(candidateLeft, candidateLeft, GuideType.EQUAL_SPACING, "Equal Gap", false, gapPx))
                }
            }
        }

        // Test each candidate against proposed position
        for (cand in xCandidates) {
            val diff = abs(targetXRatio - cand.candidateX)
            if (diff < minXDiff) {
                minXDiff = diff
                bestX = cand.candidateX
                bestXGuide = AlignmentGuide(
                    orientation = GuideOrientation.VERTICAL,
                    positionRatio = cand.target,
                    guideType = cand.guideType,
                    label = cand.label,
                    isCenterGuide = cand.isCenter,
                    gapDistancePx = cand.gapDist
                )
                didSnap = true
            }
        }

        bestXGuide?.let { activeGuides.add(it) }

        // -------------------------
        // 2. HORIZONTAL GUIDES (Y AXIS)
        // -------------------------
        var bestY = targetYRatio
        var minYDiff = snapThreshold
        var bestYGuide: AlignmentGuide? = null

        data class YCandidate(
            val target: Float,
            val candidateY: Float,
            val guideType: GuideType,
            val label: String,
            val isCenter: Boolean,
            val gapDist: Int? = null
        )

        val yCandidates = mutableListOf<YCandidate>()

        // Canvas Center (Y = 0.5)
        yCandidates.add(YCandidate(0.5f, 0.5f - h / 2f, GuideType.CANVAS_CENTER, "Center Y", true))
        // Canvas Margins
        yCandidates.add(YCandidate(canvasMargin, canvasMargin, GuideType.CANVAS_MARGIN, "Top Margin", false))
        yCandidates.add(YCandidate(1f - canvasMargin, 1f - canvasMargin - h, GuideType.CANVAS_MARGIN, "Bottom Margin", false))
        // Canvas Edges
        yCandidates.add(YCandidate(0f, 0f, GuideType.CANVAS_EDGE, "Top Edge", false))
        yCandidates.add(YCandidate(1f, 1f - h, GuideType.CANVAS_EDGE, "Bottom Edge", false))

        // Targets from other visible elements
        for (other in validOthers) {
            val otherTop = other.yRatio
            val otherCenterY = other.yRatio + other.heightRatio / 2f
            val otherBottom = other.yRatio + other.heightRatio

            // Element Top matches other Top
            yCandidates.add(YCandidate(otherTop, otherTop, GuideType.ELEMENT_ALIGN_START, "Align Top", false))
            // Element Center matches other Center
            yCandidates.add(YCandidate(otherCenterY, otherCenterY - h / 2f, GuideType.ELEMENT_ALIGN_CENTER, "Center Y", true))
            // Element Bottom matches other Bottom
            yCandidates.add(YCandidate(otherBottom, otherBottom - h, GuideType.ELEMENT_ALIGN_END, "Align Bottom", false))
            // Element Top matches other Bottom (stacked below)
            yCandidates.add(YCandidate(otherBottom, otherBottom, GuideType.ELEMENT_ALIGN_START, "Align Edge", false))
            // Element Bottom matches other Top (stacked above)
            yCandidates.add(YCandidate(otherTop, otherTop - h, GuideType.ELEMENT_ALIGN_END, "Align Edge", false))
        }

        // Equal Spacing on Y Axis (detect when vertical gap matches)
        if (validOthers.size >= 2) {
            val sortedY = validOthers.sortedBy { it.yRatio }
            for (i in 0 until sortedY.size - 1) {
                val e1 = sortedY[i]
                val e2 = sortedY[i + 1]
                val gap = e2.yRatio - (e1.yRatio + e1.heightRatio)
                if (gap > 0.01f) {
                    val gapPx = (gap * 1000).toInt()
                    // 1. Below e2 with same gap
                    val candidateBelow = e2.yRatio + e2.heightRatio + gap
                    yCandidates.add(YCandidate(candidateBelow, candidateBelow, GuideType.EQUAL_SPACING, "Equal Gap", false, gapPx))
                    // 2. Above e1 with same gap
                    val candidateAbove = e1.yRatio - gap - h
                    yCandidates.add(YCandidate(candidateAbove, candidateAbove, GuideType.EQUAL_SPACING, "Equal Gap", false, gapPx))
                }
            }
        }

        // Test each candidate against proposed position
        for (cand in yCandidates) {
            val diff = abs(targetYRatio - cand.candidateY)
            if (diff < minYDiff) {
                minYDiff = diff
                bestY = cand.candidateY
                bestYGuide = AlignmentGuide(
                    orientation = GuideOrientation.HORIZONTAL,
                    positionRatio = cand.target,
                    guideType = cand.guideType,
                    label = cand.label,
                    isCenterGuide = cand.isCenter,
                    gapDistancePx = cand.gapDist
                )
                didSnap = true
            }
        }

        bestYGuide?.let { activeGuides.add(it) }

        val finalX = bestX.coerceIn(0f, (1f - w).coerceAtLeast(0f))
        val finalY = bestY.coerceIn(0f, (1f - h).coerceAtLeast(0f))

        return SnapResult(
            snappedXRatio = finalX,
            snappedYRatio = finalY,
            activeGuides = activeGuides,
            isSnapped = didSnap
        )
    }

    // =======================================================
    // QUICK FORMATION & ONE-TAP ALIGNMENT UTILITIES
    // =======================================================

    fun alignLeft(element: CanvasElement, margin: Float = DEFAULT_CANVAS_MARGIN): CanvasElement {
        return element.copy(xRatio = margin.coerceIn(0f, 1f - element.widthRatio))
    }

    fun alignCenterH(element: CanvasElement): CanvasElement {
        val newX = ((1f - element.widthRatio) / 2f).coerceIn(0f, 1f - element.widthRatio)
        return element.copy(xRatio = newX)
    }

    fun alignRight(element: CanvasElement, margin: Float = DEFAULT_CANVAS_MARGIN): CanvasElement {
        val newX = (1f - margin - element.widthRatio).coerceIn(0f, 1f - element.widthRatio)
        return element.copy(xRatio = newX)
    }

    fun alignTop(element: CanvasElement, margin: Float = DEFAULT_CANVAS_MARGIN): CanvasElement {
        return element.copy(yRatio = margin.coerceIn(0f, 1f - element.heightRatio))
    }

    fun alignCenterV(element: CanvasElement): CanvasElement {
        val newY = ((1f - element.heightRatio) / 2f).coerceIn(0f, 1f - element.heightRatio)
        return element.copy(yRatio = newY)
    }

    fun alignBottom(element: CanvasElement, margin: Float = DEFAULT_CANVAS_MARGIN): CanvasElement {
        val newY = (1f - margin - element.heightRatio).coerceIn(0f, 1f - element.heightRatio)
        return element.copy(yRatio = newY)
    }

    fun centerCanvas(element: CanvasElement): CanvasElement {
        val newX = ((1f - element.widthRatio) / 2f).coerceIn(0f, 1f - element.widthRatio)
        val newY = ((1f - element.heightRatio) / 2f).coerceIn(0f, 1f - element.heightRatio)
        return element.copy(xRatio = newX, yRatio = newY)
    }

    fun distributeVertically(elements: List<CanvasElement>, topMargin: Float = 0.1f, bottomMargin: Float = 0.1f): List<CanvasElement> {
        if (elements.size <= 2) return elements
        val sorted = elements.sortedBy { it.yRatio }
        val availableHeight = 1f - topMargin - bottomMargin
        val totalElementsHeight = sorted.sumOf { it.heightRatio.toDouble() }.toFloat()
        val remainingSpace = (availableHeight - totalElementsHeight).coerceAtLeast(0f)
        val gap = remainingSpace / (sorted.size - 1)

        var currentY = topMargin
        val result = mutableListOf<CanvasElement>()
        for (el in sorted) {
            result.add(el.copy(yRatio = currentY.coerceIn(0f, 1f - el.heightRatio)))
            currentY += el.heightRatio + gap
        }
        return result
    }

    fun distributeHorizontally(elements: List<CanvasElement>, leftMargin: Float = 0.05f, rightMargin: Float = 0.05f): List<CanvasElement> {
        if (elements.size <= 2) return elements
        val sorted = elements.sortedBy { it.xRatio }
        val availableWidth = 1f - leftMargin - rightMargin
        val totalElementsWidth = sorted.sumOf { it.widthRatio.toDouble() }.toFloat()
        val remainingSpace = (availableWidth - totalElementsWidth).coerceAtLeast(0f)
        val gap = remainingSpace / (sorted.size - 1)

        var currentX = leftMargin
        val result = mutableListOf<CanvasElement>()
        for (el in sorted) {
            result.add(el.copy(xRatio = currentX.coerceIn(0f, 1f - el.widthRatio)))
            currentX += el.widthRatio + gap
        }
        return result
    }

    fun matchCanvasWidth(element: CanvasElement, sidePaddingRatio: Float = 0.08f): CanvasElement {
        val newWidth = (1f - (sidePaddingRatio * 2)).coerceIn(0.2f, 0.96f)
        return element.copy(
            xRatio = sidePaddingRatio,
            widthRatio = newWidth
        )
    }

    // =======================================================
    // 1-TAP SMART LAYOUT BLUEPRINT GENERATORS
    // =======================================================

    fun createHeroAnnouncementTemplate(): List<CanvasElement> {
        val idGen = { "el_" + UUID.randomUUID().toString().take(6) }
        return listOf(
            CanvasElement(
                id = idGen(),
                type = ElementType.SHAPE,
                shapeType = ShapeType.BADGE,
                fillColorHex = "#2563EB",
                text = "⭐ SPECIAL ANNOUNCEMENT",
                xRatio = 0.2f,
                yRatio = 0.12f,
                widthRatio = 0.6f,
                heightRatio = 0.06f,
                layerOrder = 1
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "BIGGEST EVENT OF THE YEAR",
                fontSizeSp = 28f,
                fontColorHex = "#0F172A",
                isBold = true,
                xRatio = 0.08f,
                yRatio = 0.22f,
                widthRatio = 0.84f,
                heightRatio = 0.14f,
                layerOrder = 2
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "Join industry leaders and innovators for an exclusive masterclass live online.",
                fontSizeSp = 14f,
                fontColorHex = "#475569",
                xRatio = 0.12f,
                yRatio = 0.38f,
                widthRatio = 0.76f,
                heightRatio = 0.1f,
                layerOrder = 3
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.SHAPE,
                shapeType = ShapeType.ROUNDED_RECT,
                fillColorHex = "#0F172A",
                xRatio = 0.25f,
                yRatio = 0.82f,
                widthRatio = 0.5f,
                heightRatio = 0.08f,
                shapeCornerRadiusDp = 16f,
                layerOrder = 4
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "REGISTER NOW →",
                fontSizeSp = 15f,
                fontColorHex = "#FFFFFF",
                isBold = true,
                xRatio = 0.25f,
                yRatio = 0.83f,
                widthRatio = 0.5f,
                heightRatio = 0.06f,
                layerOrder = 5
            )
        )
    }

    fun createBigSaleTemplate(): List<CanvasElement> {
        val idGen = { "el_" + UUID.randomUUID().toString().take(6) }
        return listOf(
            CanvasElement(
                id = idGen(),
                type = ElementType.SHAPE,
                shapeType = ShapeType.CIRCLE,
                fillColorHex = "#EF4444",
                xRatio = 0.28f,
                yRatio = 0.12f,
                widthRatio = 0.44f,
                heightRatio = 0.22f,
                layerOrder = 1
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "50% OFF",
                fontSizeSp = 36f,
                fontColorHex = "#FFFFFF",
                isBold = true,
                xRatio = 0.28f,
                yRatio = 0.18f,
                widthRatio = 0.44f,
                heightRatio = 0.1f,
                layerOrder = 2
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "MEGA FLASH SALE",
                fontSizeSp = 26f,
                fontColorHex = "#111827",
                isBold = true,
                xRatio = 0.1f,
                yRatio = 0.38f,
                widthRatio = 0.8f,
                heightRatio = 0.1f,
                layerOrder = 3
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "Limited Stock Available • Free Shipping On All Orders",
                fontSizeSp = 13f,
                fontColorHex = "#64748B",
                xRatio = 0.1f,
                yRatio = 0.49f,
                widthRatio = 0.8f,
                heightRatio = 0.06f,
                layerOrder = 4
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.SHAPE,
                shapeType = ShapeType.ROUNDED_RECT,
                fillColorHex = "#EF4444",
                xRatio = 0.2f,
                yRatio = 0.8f,
                widthRatio = 0.6f,
                heightRatio = 0.09f,
                shapeCornerRadiusDp = 18f,
                layerOrder = 5
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "SHOP TODAY",
                fontSizeSp = 16f,
                fontColorHex = "#FFFFFF",
                isBold = true,
                xRatio = 0.2f,
                yRatio = 0.815f,
                widthRatio = 0.6f,
                heightRatio = 0.06f,
                layerOrder = 6
            )
        )
    }

    fun createQuoteCardTemplate(): List<CanvasElement> {
        val idGen = { "el_" + UUID.randomUUID().toString().take(6) }
        return listOf(
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "“",
                fontSizeSp = 72f,
                fontColorHex = "#3B82F6",
                isBold = true,
                xRatio = 0.1f,
                yRatio = 0.18f,
                widthRatio = 0.2f,
                heightRatio = 0.12f,
                layerOrder = 1
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "Design is not just what it looks like and feels like. Design is how it works.",
                fontSizeSp = 22f,
                fontColorHex = "#1E293B",
                isBold = true,
                isItalic = true,
                xRatio = 0.12f,
                yRatio = 0.32f,
                widthRatio = 0.76f,
                heightRatio = 0.28f,
                layerOrder = 2
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.SHAPE,
                shapeType = ShapeType.LINE,
                fillColorHex = "#CBD5E1",
                xRatio = 0.35f,
                yRatio = 0.64f,
                widthRatio = 0.3f,
                heightRatio = 0.02f,
                strokeWidthDp = 2f,
                layerOrder = 3
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "— STEVE JOBS",
                fontSizeSp = 13f,
                fontColorHex = "#64748B",
                isBold = true,
                letterSpacingSp = 2f,
                xRatio = 0.15f,
                yRatio = 0.68f,
                widthRatio = 0.7f,
                heightRatio = 0.06f,
                layerOrder = 4
            )
        )
    }

    fun createEventShowcaseTemplate(): List<CanvasElement> {
        val idGen = { "el_" + UUID.randomUUID().toString().take(6) }
        return listOf(
            CanvasElement(
                id = idGen(),
                type = ElementType.SHAPE,
                shapeType = ShapeType.ROUNDED_RECT,
                fillColorHex = "#8B5CF6",
                xRatio = 0.1f,
                yRatio = 0.1f,
                widthRatio = 0.8f,
                heightRatio = 0.07f,
                shapeCornerRadiusDp = 10f,
                layerOrder = 1
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "🗓️ SATURDAY, OCTOBER 25 • 6:00 PM",
                fontSizeSp = 12f,
                fontColorHex = "#FFFFFF",
                isBold = true,
                xRatio = 0.1f,
                yRatio = 0.11f,
                widthRatio = 0.8f,
                heightRatio = 0.05f,
                layerOrder = 2
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "CREATOR SUMMIT 2026",
                fontSizeSp = 26f,
                fontColorHex = "#0F172A",
                isBold = true,
                xRatio = 0.08f,
                yRatio = 0.22f,
                widthRatio = 0.84f,
                heightRatio = 0.14f,
                layerOrder = 3
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "Keynotes • Masterclasses • Live Q&A Networking",
                fontSizeSp = 13f,
                fontColorHex = "#64748B",
                xRatio = 0.1f,
                yRatio = 0.38f,
                widthRatio = 0.8f,
                heightRatio = 0.06f,
                layerOrder = 4
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.SHAPE,
                shapeType = ShapeType.ROUNDED_RECT,
                fillColorHex = "#8B5CF6",
                xRatio = 0.2f,
                yRatio = 0.82f,
                widthRatio = 0.6f,
                heightRatio = 0.08f,
                shapeCornerRadiusDp = 14f,
                layerOrder = 5
            ),
            CanvasElement(
                id = idGen(),
                type = ElementType.TEXT,
                text = "GET YOUR TICKET",
                fontSizeSp = 14f,
                fontColorHex = "#FFFFFF",
                isBold = true,
                xRatio = 0.2f,
                yRatio = 0.83f,
                widthRatio = 0.6f,
                heightRatio = 0.06f,
                layerOrder = 6
            )
        )
    }
}
