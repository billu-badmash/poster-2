package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.domain.models.BackgroundType
import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import com.example.domain.models.PosterBackground
import com.example.domain.models.ShapeType
import com.example.utils.AlignmentGuide
import com.example.utils.GuideOrientation
import com.example.utils.GuideType
import com.example.utils.SmartAlignmentHelper
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
    smartGuidesEnabled: Boolean = true,
    onElementSelected: ((String?) -> Unit)? = null,
    onElementMoved: ((elementId: String, newXRatio: Float, newYRatio: Float) -> Unit)? = null,
    onElementResized: ((elementId: String, newWidthRatio: Float, newHeightRatio: Float) -> Unit)? = null,
    onBackgroundClick: (() -> Unit)? = null
) {
    val ratio = if (canvasHeight > 0) canvasWidth.toFloat() / canvasHeight.toFloat() else 1f
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    // Active smart alignment guides when dragging
    var activeGuides by remember { mutableStateOf<List<AlignmentGuide>>(emptyList()) }
    var wasSnapped by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(ratio)
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .testTag("poster_canvas_view")
    ) {
        val containerWidth = maxWidth
        val containerHeight = maxHeight

        // 1. Background layer (clickable to deselect and select background)
        PosterBackgroundLayer(
            background = background,
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = isInteractive) {
                    onElementSelected?.invoke(null)
                    onBackgroundClick?.invoke()
                }
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
                onDragStart = {
                    wasSnapped = false
                },
                onDrag = { dxRatio, dyRatio ->
                    if (isInteractive && !element.isLocked) {
                        val rawX = (element.xRatio + dxRatio)
                        val rawY = (element.yRatio + dyRatio)

                        if (smartGuidesEnabled) {
                            val snapResult = SmartAlignmentHelper.calculateSnap(
                                element = element,
                                targetXRatio = rawX,
                                targetYRatio = rawY,
                                otherElements = elements,
                                snapEnabled = true
                            )
                            if (snapResult.isSnapped && !wasSnapped) {
                                try {
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                } catch (e: Exception) {}
                            }
                            wasSnapped = snapResult.isSnapped
                            activeGuides = snapResult.activeGuides
                            onElementMoved?.invoke(element.id, snapResult.snappedXRatio, snapResult.snappedYRatio)
                        } else {
                            activeGuides = emptyList()
                            val clampedX = rawX.coerceIn(0f, 1f - element.widthRatio)
                            val clampedY = rawY.coerceIn(0f, 1f - element.heightRatio)
                            onElementMoved?.invoke(element.id, clampedX, clampedY)
                        }
                    }
                },
                onDragEnd = {
                    activeGuides = emptyList()
                    wasSnapped = false
                },
                onResize = { dwRatio, dhRatio ->
                    if (isInteractive && !element.isLocked) {
                        val newW = (element.widthRatio + dwRatio).coerceIn(0.1f, 1f - element.xRatio)
                        val newH = (element.heightRatio + dhRatio).coerceIn(0.04f, 1f - element.yRatio)
                        onElementResized?.invoke(element.id, newW, newH)
                    }
                }
            )
        }

        // 3. Smart Alignment Guidelines Overlay (draws real-time magnetic lines during drag)
        if (isInteractive && activeGuides.isNotEmpty()) {
            SmartGuidesOverlay(
                guides = activeGuides,
                containerWidth = containerWidth,
                containerHeight = containerHeight
            )
        }
    }
}

/**
 * High-precision visual overlay that renders vibrant smart alignment guidelines and snap markers.
 */
@Composable
private fun SmartGuidesOverlay(
    guides: List<AlignmentGuide>,
    containerWidth: Dp,
    containerHeight: Dp
) {
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

            for (guide in guides) {
                val lineColor = when (guide.guideType) {
                    GuideType.CANVAS_CENTER -> Color(0xFFFF007A) // Vibrant Neon Pink
                    GuideType.CANVAS_MARGIN -> Color(0xFF06B6D4) // Bright Cyan
                    GuideType.CANVAS_EDGE -> Color(0xFFF59E0B)   // Amber
                    GuideType.ELEMENT_ALIGN_CENTER -> Color(0xFF6366F1) // Indigo Center
                    GuideType.EQUAL_SPACING -> Color(0xFF10B981) // Emerald Green Equal Gap
                    else -> Color(0xFF00E5FF) // Electric Cyan
                }

                val strokeWidth = if (guide.isCenterGuide) 2.2.dp.toPx() else 1.8.dp.toPx()

                if (guide.orientation == GuideOrientation.VERTICAL) {
                    val xPx = (guide.positionRatio * w).coerceIn(0f, w)

                    // Subtle background glow for maximum contrast
                    drawLine(
                        color = lineColor.copy(alpha = 0.25f),
                        start = Offset(xPx, 0f),
                        end = Offset(xPx, h),
                        strokeWidth = strokeWidth * 2.5f
                    )

                    // Main dashed guide line
                    drawLine(
                        color = lineColor,
                        start = Offset(xPx, 0f),
                        end = Offset(xPx, h),
                        strokeWidth = strokeWidth,
                        pathEffect = dashEffect
                    )

                    // Snap anchor markers
                    drawCircle(
                        color = lineColor,
                        radius = 4.dp.toPx(),
                        center = Offset(xPx, h / 2f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(xPx, h / 2f)
                    )
                } else {
                    val yPx = (guide.positionRatio * h).coerceIn(0f, h)

                    // Subtle background glow
                    drawLine(
                        color = lineColor.copy(alpha = 0.25f),
                        start = Offset(0f, yPx),
                        end = Offset(w, yPx),
                        strokeWidth = strokeWidth * 2.5f
                    )

                    // Main dashed guide line
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, yPx),
                        end = Offset(w, yPx),
                        strokeWidth = strokeWidth,
                        pathEffect = dashEffect
                    )

                    // Snap anchor markers
                    drawCircle(
                        color = lineColor,
                        radius = 4.dp.toPx(),
                        center = Offset(w / 2f, yPx)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(w / 2f, yPx)
                    )
                }
            }
        }

        // Floating alignment badge pills for clear creator / editor feedback
        for (guide in guides) {
            val badgeText = if (guide.guideType == GuideType.EQUAL_SPACING && guide.gapDistancePx != null) {
                "⟷ ${guide.gapDistancePx}px"
            } else {
                guide.label
            }

            badgeText?.let { label ->
                val badgeColor = when (guide.guideType) {
                    GuideType.CANVAS_CENTER -> Color(0xFFFF007A)
                    GuideType.CANVAS_MARGIN -> Color(0xFF06B6D4)
                    GuideType.EQUAL_SPACING -> Color(0xFF10B981)
                    else -> Color(0xFF4361EE)
                }

                if (guide.orientation == GuideOrientation.VERTICAL) {
                    val xOffset = containerWidth * guide.positionRatio
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    with(density) { (xOffset - 32.dp).toPx() }.roundToInt(),
                                    with(density) { 8.dp.toPx() }.roundToInt()
                                )
                            }
                            .shadow(3.dp, RoundedCornerShape(8.dp))
                            .background(badgeColor, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    val yOffset = containerHeight * guide.positionRatio
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    with(density) { 8.dp.toPx() }.roundToInt(),
                                    with(density) { (yOffset - 12.dp).toPx() }.roundToInt()
                                )
                            }
                            .shadow(3.dp, RoundedCornerShape(8.dp))
                            .background(badgeColor, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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
    onDragStart: () -> Unit = {},
    onDrag: (dxRatio: Float, dyRatio: Float) -> Unit,
    onDragEnd: () -> Unit = {},
    onResize: (dwRatio: Float, dhRatio: Float) -> Unit
) {
    val density = LocalDensity.current
    val xOffset = containerWidth * element.xRatio
    val yOffset = containerHeight * element.yRatio
    val elWidth = containerWidth * element.widthRatio
    val elHeight = containerHeight * element.heightRatio
    val scaleFactor = (containerWidth / 360.dp)

    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnResize by rememberUpdatedState(onResize)
    val currentOnClick by rememberUpdatedState(onClick)

    // Visual drag state
    var isDragging by remember { mutableStateOf(false) }
    val elementScale by animateFloatAsState(
        targetValue = if (isDragging) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "element_drag_scale"
    )

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    with(density) { xOffset.toPx() }.roundToInt(),
                    with(density) { yOffset.toPx() }.roundToInt()
                )
            }
            .size(elWidth, elHeight)
            .scale(elementScale)
            .rotate(element.rotation)
            .then(
                if (isInteractive && !element.isLocked) {
                    Modifier.pointerInput(element.id) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                currentOnDragStart()
                            },
                            onDragEnd = {
                                isDragging = false
                                currentOnDragEnd()
                            },
                            onDragCancel = {
                                isDragging = false
                                currentOnDragEnd()
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            val dxRatio = dragAmount.x / with(density) { containerWidth.toPx() }
                            val dyRatio = dragAmount.y / with(density) { containerHeight.toPx() }
                            currentOnDrag(dxRatio, dyRatio)
                        }
                    }
                } else Modifier
            )
            .clickable(enabled = isInteractive) { currentOnClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, Color(0xFF4361EE), RoundedCornerShape(6.dp))
                else Modifier
            )
    ) {
        when (element.type) {
            ElementType.SHAPE -> ShapeElementContent(element = element, width = elWidth, height = elHeight)
            ElementType.TEXT -> TextElementContent(element = element, text = displayContent, scaleFactor = scaleFactor)
            ElementType.IMAGE -> ImageElementContent(element = element)
        }

        // Selection handles
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF4361EE), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .align(Alignment.TopStart)
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF4361EE), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .align(Alignment.TopEnd)
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF4361EE), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .align(Alignment.BottomStart)
            )
            // Resize handle (bottom-right)
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Color(0xFF4361EE), RoundedCornerShape(3.dp))
                    .border(1.5.dp, Color.White, RoundedCornerShape(3.dp))
                    .align(Alignment.BottomEnd)
                    .pointerInput(element.id + "_resize") {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val dwRatio = dragAmount.x / with(density) { containerWidth.toPx() }
                            val dhRatio = dragAmount.y / with(density) { containerHeight.toPx() }
                            currentOnResize(dwRatio, dhRatio)
                        }
                    }
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
    val hasImage = !element.localUri.isNullOrBlank() || !element.imageUrl.isNullOrBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(corner)
            .background(if (hasImage) Color.Transparent else Color(0xFFE2E8F0))
            .then(
                if (!hasImage) Modifier.border(
                    if (element.borderWidthDp > 0) element.borderWidthDp.dp else 1.dp,
                    Color(0xFFCBD5E1),
                    corner
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasImage) {
            val imageModel = element.localUri?.let { uriStr ->
                try {
                    android.net.Uri.parse(uriStr)
                } catch (e: Exception) {
                    null
                }
            } ?: element.imageUrl

            imageModel?.let { model ->
                AsyncImage(
                    model = model,
                    contentDescription = "User image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(corner),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            Text(
                text = "\uD83D\uDCF7 Image / Logo",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
        }
    }
}
