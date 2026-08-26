package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.BackgroundType
import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import com.example.domain.models.PosterBackground
import com.example.domain.models.ShapeType
import kotlin.math.roundToInt

@Composable
fun PosterCanvasView(
    modifier: Modifier = Modifier,
    canvasWidth: Int = 1080,
    canvasHeight: Int = 1080,
    background: PosterBackground,
    elements: List<CanvasElement>,
    fieldValues: Map<String, String> = emptyMap(),
    selectedElementId: String? = null,
    isInteractive: Boolean = false,
    onElementSelected: ((String?) -> Unit)? = null,
    onElementMoved: ((elementId: String, newXRatio: Float, newYRatio: Float) -> Unit)? = null
) {
    val ratio = if (canvasHeight > 0) canvasWidth.toFloat() / canvasHeight.toFloat() else 1f

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(ratio)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .testTag("poster_canvas_view")
    ) {
        val containerWidth = maxWidth
        val containerHeight = maxHeight

        // 1. Background layer
        PosterBackgroundLayer(
            background = background,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Elements layer
        val sorted = elements.filter { it.isVisible }.sortedBy { it.layerOrder }

        for (element in sorted) {
            val isSelected = isInteractive && element.id == selectedElementId
            val textContent = if (element.isEditableField && element.editableFieldId != null && fieldValues.containsKey(element.editableFieldId)) {
                fieldValues[element.editableFieldId] ?: element.text
            } else {
                element.text
            }

            ElementComposable(
                element = element,
                displayContent = textContent,
                containerWidth = containerWidth,
                containerHeight = containerHeight,
                isSelected = isSelected,
                isInteractive = isInteractive,
                onClick = {
                    if (isInteractive) {
                        onElementSelected?.invoke(element.id)
                    }
                },
                onDrag = { dxRatio, dyRatio ->
                    if (isInteractive && !element.isLocked) {
                        val newX = (element.xRatio + dxRatio).coerceIn(0f, 1f - element.widthRatio)
                        val newY = (element.yRatio + dyRatio).coerceIn(0f, 1f - element.heightRatio)
                        onElementMoved?.invoke(element.id, newX, newY)
                    }
                }
            )
        }

        // Click outside to deselect
        if (isInteractive && selectedElementId != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // Background tap handling if needed
                    }
            )
        }
    }
}

@Composable
private fun PosterBackgroundLayer(
    background: PosterBackground,
    modifier: Modifier = Modifier
) {
    val c1 = try {
        Color(android.graphics.Color.parseColor(background.color1Hex))
    } catch (e: Exception) {
        Color.White
    }
    val c2 = try {
        Color(android.graphics.Color.parseColor(background.color2Hex ?: background.color1Hex))
    } catch (e: Exception) {
        Color(0xFFF3F4F6)
    }

    when (background.type) {
        BackgroundType.SOLID -> {
            Box(modifier = modifier.background(c1))
        }
        BackgroundType.GRADIENT_LINEAR -> {
            Box(
                modifier = modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(c1, c2)
                    )
                )
            )
        }
        BackgroundType.GRADIENT_RADIAL -> {
            Box(
                modifier = modifier.background(
                    Brush.radialGradient(
                        colors = listOf(c1, c2)
                    )
                )
            )
        }
        else -> {
            Box(modifier = modifier.background(c1))
        }
    }
}

@Composable
private fun ElementComposable(
    element: CanvasElement,
    displayContent: String,
    containerWidth: Dp,
    containerHeight: Dp,
    isSelected: Boolean,
    isInteractive: Boolean,
    onClick: () -> Unit,
    onDrag: (dxRatio: Float, dyRatio: Float) -> Unit
) {
    val density = LocalDensity.current
    val xOffset = containerWidth * element.xRatio
    val yOffset = containerHeight * element.yRatio
    val elWidth = containerWidth * element.widthRatio
    val elHeight = containerHeight * element.heightRatio

    val scaleFactor = (containerWidth / 360.dp)

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    with(density) { xOffset.toPx() }.roundToInt(),
                    with(density) { yOffset.toPx() }.roundToInt()
                )
            }
            .size(elWidth, elHeight)
            .rotate(element.rotation)
            .then(
                if (isInteractive && !element.isLocked) {
                    Modifier.pointerInput(element.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val dxRatio = dragAmount.x / with(density) { containerWidth.toPx() }
                            val dyRatio = dragAmount.y / with(density) { containerHeight.toPx() }
                            onDrag(dxRatio, dyRatio)
                        }
                    }
                } else Modifier
            )
            .clickable(enabled = isInteractive) { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFF2563EB), RoundedCornerShape(4.dp))
                } else Modifier
            )
    ) {
        when (element.type) {
            ElementType.SHAPE -> {
                ShapeElementContent(element = element, width = elWidth, height = elHeight)
            }
            ElementType.TEXT -> {
                TextElementContent(element = element, text = displayContent, scaleFactor = scaleFactor)
            }
            ElementType.IMAGE -> {
                ImageElementContent(element = element)
            }
        }

        // Selection handles
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF2563EB), CircleShape)
                    .align(Alignment.TopStart)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF2563EB), CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun ShapeElementContent(
    element: CanvasElement,
    width: Dp,
    height: Dp
) {
    val fillColor = try {
        Color(android.graphics.Color.parseColor(element.fillColorHex))
    } catch (e: Exception) {
        Color.Gray
    }.copy(alpha = element.opacity)

    val strokeColor = try {
        Color(android.graphics.Color.parseColor(element.strokeColorHex))
    } catch (e: Exception) {
        Color.Transparent
    }

    when (element.shapeType) {
        ShapeType.RECTANGLE -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fillColor)
                    .then(
                        if (element.strokeWidthDp > 0) Modifier.border(element.strokeWidthDp.dp, strokeColor) else Modifier
                    )
            )
        }
        ShapeType.ROUNDED_RECT, ShapeType.BADGE -> {
            val corner = RoundedCornerShape(element.shapeCornerRadiusDp.dp)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(corner)
                    .background(fillColor)
                    .then(
                        if (element.strokeWidthDp > 0) Modifier.border(element.strokeWidthDp.dp, strokeColor, corner) else Modifier
                    )
            )
        }
        ShapeType.CIRCLE -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(fillColor)
                    .then(
                        if (element.strokeWidthDp > 0) Modifier.border(element.strokeWidthDp.dp, strokeColor, CircleShape) else Modifier
                    )
            )
        }
        ShapeType.LINE -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((element.strokeWidthDp.coerceAtLeast(2f)).dp)
                        .background(fillColor)
                )
            }
        }
    }
}

@Composable
private fun TextElementContent(
    element: CanvasElement,
    text: String,
    scaleFactor: Float
) {
    val color = try {
        Color(android.graphics.Color.parseColor(element.fontColorHex))
    } catch (e: Exception) {
        Color.Black
    }.copy(alpha = element.opacity)

    val tAlign = when (element.textAlign.uppercase()) {
        "LEFT" -> TextAlign.Left
        "RIGHT" -> TextAlign.Right
        else -> TextAlign.Center
    }

    val shadow = if (element.hasShadow) {
        val sColor = try {
            Color(android.graphics.Color.parseColor(element.shadowColorHex))
        } catch (e: Exception) {
            Color.Black.copy(alpha = 0.5f)
        }
        Shadow(color = sColor, offset = Offset(2f, 2f), blurRadius = 4f)
    } else null

    val scaledFontSize = (element.fontSizeSp * scaleFactor.coerceIn(0.5f, 2.5f)).sp

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = when (element.textAlign.uppercase()) {
            "LEFT" -> Alignment.CenterStart
            "RIGHT" -> Alignment.CenterEnd
            else -> Alignment.Center
        }
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = color,
                fontSize = scaledFontSize,
                fontWeight = if (element.isBold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (element.isItalic) FontStyle.Italic else FontStyle.Normal,
                textAlign = tAlign,
                letterSpacing = (element.letterSpacingSp).sp,
                shadow = shadow
            )
        )
    }
}

@Composable
private fun ImageElementContent(
    element: CanvasElement
) {
    val corner = RoundedCornerShape(element.cornerRadiusDp.dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(corner)
            .background(Color(0xFFE2E8F0))
            .border(
                if (element.borderWidthDp > 0) element.borderWidthDp.dp else 1.dp,
                Color(0xFFCBD5E1),
                corner
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "📷 Image / Logo",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
        )
    }
}
