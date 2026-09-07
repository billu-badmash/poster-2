package com.example

import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import com.example.utils.GuideOrientation
import com.example.utils.GuideType
import com.example.utils.SmartAlignmentHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartAlignmentHelperTest {

    private val elementA = CanvasElement(
        id = "el_a",
        type = ElementType.TEXT,
        text = "Heading",
        xRatio = 0.1f,
        yRatio = 0.2f,
        widthRatio = 0.4f,
        heightRatio = 0.1f
    )

    private val elementB = CanvasElement(
        id = "el_b",
        type = ElementType.SHAPE,
        xRatio = 0.3f,
        yRatio = 0.5f,
        widthRatio = 0.4f,
        heightRatio = 0.2f
    )

    @Test
    fun testSnapToCanvasCenter() {
        // Element A width is 0.4. Center is x = 0.5 - 0.2 = 0.3
        // Proposed targetX is 0.305 (within 0.015 threshold of 0.3)
        val result = SmartAlignmentHelper.calculateSnap(
            element = elementA,
            targetXRatio = 0.305f,
            targetYRatio = 0.2f,
            otherElements = emptyList(),
            snapEnabled = true
        )

        assertEquals(0.3f, result.snappedXRatio, 0.001f)
        assertTrue(result.activeGuides.any { it.orientation == GuideOrientation.VERTICAL && it.guideType == GuideType.CANVAS_CENTER })
    }

    @Test
    fun testSnapToOtherElementLeftEdge() {
        val other = elementB.copy(xRatio = 0.2f, widthRatio = 0.3f)
        // Other left is 0.2. Dragging Element A to 0.208 -> should snap left edge to 0.2
        val result = SmartAlignmentHelper.calculateSnap(
            element = elementA,
            targetXRatio = 0.208f,
            targetYRatio = 0.2f,
            otherElements = listOf(other),
            snapEnabled = true
        )

        assertEquals(0.2f, result.snappedXRatio, 0.001f)
        assertTrue(result.activeGuides.any { it.orientation == GuideOrientation.VERTICAL && it.guideType == GuideType.ELEMENT_ALIGN_START })
    }

    @Test
    fun testSnapToOtherElementCenter() {
        // Other element center X is 0.2 + 0.1 = 0.3.
        // Element A width is 0.4, so center X alignment requires xRatio = 0.3 - 0.2 = 0.1
        val other = elementB.copy(xRatio = 0.2f, widthRatio = 0.2f)
        val result = SmartAlignmentHelper.calculateSnap(
            element = elementA,
            targetXRatio = 0.106f,
            targetYRatio = 0.2f,
            otherElements = listOf(other),
            snapEnabled = true
        )

        assertEquals(0.1f, result.snappedXRatio, 0.001f)
        assertTrue(result.activeGuides.any { it.orientation == GuideOrientation.VERTICAL && it.guideType == GuideType.ELEMENT_ALIGN_CENTER })
    }

    @Test
    fun testSnapToCanvasMargin() {
        // Canvas default margin is 0.05. Target 0.058 is within 0.015 threshold.
        val result = SmartAlignmentHelper.calculateSnap(
            element = elementA,
            targetXRatio = 0.058f,
            targetYRatio = 0.2f,
            otherElements = emptyList(),
            snapEnabled = true
        )

        assertEquals(0.05f, result.snappedXRatio, 0.001f)
        assertTrue(result.activeGuides.any { it.guideType == GuideType.CANVAS_MARGIN })
    }

    @Test
    fun testSnapDisabled() {
        val result = SmartAlignmentHelper.calculateSnap(
            element = elementA,
            targetXRatio = 0.305f,
            targetYRatio = 0.445f,
            otherElements = emptyList(),
            snapEnabled = false
        )

        assertEquals(0.305f, result.snappedXRatio, 0.0001f)
        assertEquals(0.445f, result.snappedYRatio, 0.0001f)
        assertTrue(result.activeGuides.isEmpty())
    }

    @Test
    fun testQuickAlignments() {
        val centeredH = SmartAlignmentHelper.alignCenterH(elementA)
        // 1f - 0.4f = 0.6f / 2 = 0.3f
        assertEquals(0.3f, centeredH.xRatio, 0.001f)

        val centeredV = SmartAlignmentHelper.alignCenterV(elementA)
        // 1f - 0.1f = 0.9f / 2 = 0.45f
        assertEquals(0.45f, centeredV.yRatio, 0.001f)

        val centeredBoth = SmartAlignmentHelper.centerCanvas(elementA)
        assertEquals(0.3f, centeredBoth.xRatio, 0.001f)
        assertEquals(0.45f, centeredBoth.yRatio, 0.001f)

        val alignedLeft = SmartAlignmentHelper.alignLeft(elementA, margin = 0.05f)
        assertEquals(0.05f, alignedLeft.xRatio, 0.001f)

        val alignedRight = SmartAlignmentHelper.alignRight(elementA, margin = 0.05f)
        // 1 - 0.05 - 0.4 = 0.55
        assertEquals(0.55f, alignedRight.xRatio, 0.001f)
    }

    @Test
    fun testDistributeVertically() {
        val el1 = elementA.copy(id = "1", yRatio = 0.1f, heightRatio = 0.1f)
        val el2 = elementA.copy(id = "2", yRatio = 0.3f, heightRatio = 0.1f)
        val el3 = elementA.copy(id = "3", yRatio = 0.8f, heightRatio = 0.1f)

        val distributed = SmartAlignmentHelper.distributeVertically(listOf(el1, el2, el3), topMargin = 0.1f, bottomMargin = 0.1f)
        assertEquals(3, distributed.size)
        assertEquals(0.1f, distributed[0].yRatio, 0.01f)
        // Total available height = 1 - 0.1 - 0.1 = 0.8. Total elements height = 0.3. Remaining = 0.5. Gap = 0.5/2 = 0.25
        // el2 y = 0.1 + 0.1 + 0.25 = 0.45
        assertEquals(0.45f, distributed[1].yRatio, 0.01f)
        // el3 y = 0.45 + 0.1 + 0.25 = 0.8
        assertEquals(0.8f, distributed[2].yRatio, 0.01f)
    }

    @Test
    fun testSnapEqualSpacing() {
        // e1: x = 0.1, w = 0.1 -> right is 0.2
        // e2: x = 0.3, w = 0.1 -> right is 0.4 (gap between e1 and e2 is 0.1)
        val e1 = elementA.copy(id = "e1", xRatio = 0.1f, widthRatio = 0.1f)
        val e2 = elementB.copy(id = "e2", xRatio = 0.3f, widthRatio = 0.1f)

        // Dragging element A (w = 0.1) to right of e2 with same gap 0.1 -> targetX = 0.4 + 0.1 = 0.5
        val movingEl = elementA.copy(id = "moving", widthRatio = 0.1f)
        val result = SmartAlignmentHelper.calculateSnap(
            element = movingEl,
            targetXRatio = 0.506f,
            targetYRatio = 0.2f,
            otherElements = listOf(e1, e2),
            snapEnabled = true
        )

        assertEquals(0.5f, result.snappedXRatio, 0.001f)
        assertTrue(result.activeGuides.any { it.guideType == GuideType.EQUAL_SPACING })
    }

    @Test
    fun testSmartLayoutBlueprints() {
        val hero = SmartAlignmentHelper.createHeroAnnouncementTemplate()
        assertTrue(hero.isNotEmpty())
        assertEquals(5, hero.size)

        val sale = SmartAlignmentHelper.createBigSaleTemplate()
        assertTrue(sale.isNotEmpty())
        assertEquals(6, sale.size)

        val quote = SmartAlignmentHelper.createQuoteCardTemplate()
        assertTrue(quote.isNotEmpty())
        assertEquals(4, quote.size)

        val event = SmartAlignmentHelper.createEventShowcaseTemplate()
        assertTrue(event.isNotEmpty())
        assertEquals(6, event.size)
    }

    @Test
    fun testStickerPresets() {
        val presets = com.example.utils.StickerAssetHelper.presets
        assertTrue(presets.size >= 8)
        for (preset in presets) {
            val elements = preset.elementGenerator()
            assertTrue(elements.isNotEmpty())
        }
    }
}
