package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.domain.models.BackgroundType
import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import com.example.domain.models.PosterBackground
import com.example.domain.models.ShapeType
import java.io.File
import java.io.FileOutputStream

object CanvasUtils {

    fun parseColor(hex: String, defaultColor: Int = android.graphics.Color.BLACK): Int {
        return try {
            if (hex.startsWith("#")) {
                android.graphics.Color.parseColor(hex)
            } else {
                android.graphics.Color.parseColor("#$hex")
            }
        } catch (e: Exception) {
            defaultColor
        }
    }

    /**
     * Renders a complete high-resolution bitmap of the poster given its background and elements.
     */
    fun renderPosterBitmap(
        width: Int = 1080,
        height: Int = 1080,
        background: PosterBackground,
        elements: List<CanvasElement>,
        fieldValues: Map<String, String> = emptyMap()
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (background.type) {
            BackgroundType.SOLID -> {
                bgPaint.color = parseColor(background.color1Hex, android.graphics.Color.WHITE)
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            }
            BackgroundType.GRADIENT_LINEAR -> {
                val c1 = parseColor(background.color1Hex, android.graphics.Color.WHITE)
                val c2 = parseColor(background.color2Hex ?: background.color1Hex, android.graphics.Color.LTGRAY)
                val shader = android.graphics.LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    c1, c2, android.graphics.Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            }
            else -> {
                bgPaint.color = parseColor(background.color1Hex, android.graphics.Color.WHITE)
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            }
        }

        // 2. Draw Elements ordered by layer
        val sortedElements = elements.filter { it.isVisible }.sortedBy { it.layerOrder }
        for (el in sortedElements) {
            val elLeft = el.xRatio * width
            val elTop = el.yRatio * height
            val elWidth = el.widthRatio * width
            val elHeight = el.heightRatio * height

            canvas.save()
            if (el.rotation != 0f) {
                canvas.rotate(el.rotation, elLeft + elWidth / 2f, elTop + elHeight / 2f)
            }

            when (el.type) {
                ElementType.SHAPE -> {
                    val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = parseColor(el.fillColorHex, android.graphics.Color.BLUE)
                        alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
                        style = Paint.Style.FILL
                    }
                    val rect = RectF(elLeft, elTop, elLeft + elWidth, elTop + elHeight)
                    when (el.shapeType) {
                        ShapeType.CIRCLE -> {
                            val radius = minOf(elWidth, elHeight) / 2f
                            canvas.drawCircle(elLeft + elWidth / 2f, elTop + elHeight / 2f, radius, shapePaint)
                        }
                        ShapeType.ROUNDED_RECT -> {
                            val corner = (el.shapeCornerRadiusDp * (width / 400f))
                            canvas.drawRoundRect(rect, corner, corner, shapePaint)
                        }
                        ShapeType.RECTANGLE -> {
                            canvas.drawRect(rect, shapePaint)
                        }
                        ShapeType.LINE -> {
                            shapePaint.strokeWidth = 4f
                            canvas.drawLine(elLeft, elTop + elHeight / 2f, elLeft + elWidth, elTop + elHeight / 2f, shapePaint)
                        }
                        ShapeType.BADGE -> {
                            val corner = (el.shapeCornerRadiusDp * (width / 400f))
                            canvas.drawRoundRect(rect, corner, corner, shapePaint)
                        }
                    }

                    // Stroke if any
                    if (el.strokeWidthDp > 0) {
                        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = parseColor(el.strokeColorHex, android.graphics.Color.WHITE)
                            style = Paint.Style.STROKE
                            strokeWidth = el.strokeWidthDp * (width / 400f)
                        }
                        canvas.drawRoundRect(rect, el.shapeCornerRadiusDp * (width / 400f), el.shapeCornerRadiusDp * (width / 400f), strokePaint)
                    }
                }

                ElementType.TEXT -> {
                    val textContent = if (el.isEditableField && el.editableFieldId != null && fieldValues.containsKey(el.editableFieldId)) {
                        fieldValues[el.editableFieldId] ?: el.text
                    } else {
                        el.text
                    }

                    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = parseColor(el.fontColorHex, android.graphics.Color.BLACK)
                        alpha = (el.opacity * 255).toInt().coerceIn(0, 255)
                        textSize = el.fontSizeSp * (width / 360f)
                        isFakeBoldText = el.isBold
                        if (el.isItalic) textSkewX = -0.25f
                        textAlign = when (el.textAlign.uppercase()) {
                            "LEFT" -> Paint.Align.LEFT
                            "RIGHT" -> Paint.Align.RIGHT
                            else -> Paint.Align.CENTER
                        }
                    }

                    val lines = textContent.split("\n")
                    val lineHeight = textPaint.textSize * 1.25f
                    val totalTextHeight = lines.size * lineHeight
                    var startY = elTop + (elHeight - totalTextHeight) / 2f + textPaint.textSize

                    val targetX = when (el.textAlign.uppercase()) {
                        "LEFT" -> elLeft
                        "RIGHT" -> elLeft + elWidth
                        else -> elLeft + elWidth / 2f
                    }

                    for (line in lines) {
                        canvas.drawText(line, targetX, startY, textPaint)
                        startY += lineHeight
                    }
                }

                ElementType.IMAGE -> {
                    // Image placeholder card or photo
                    val rect = RectF(elLeft, elTop, elLeft + elWidth, elTop + elHeight)
                    val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = parseColor("#E2E8F0", android.graphics.Color.LTGRAY)
                        style = Paint.Style.FILL
                    }
                    val corner = el.cornerRadiusDp * (width / 400f)
                    canvas.drawRoundRect(rect, corner, corner, cardPaint)

                    val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = parseColor("#64748B", android.graphics.Color.DKGRAY)
                        textSize = 28f * (width / 1080f)
                        textAlign = Paint.Align.CENTER
                    }
                    canvas.drawText("🖼 Photo Layer", elLeft + elWidth / 2f, elTop + elHeight / 2f, labelPaint)
                }
            }

            canvas.restore()
        }

        return bitmap
    }

    /**
     * Saves bitmap to app cache and returns the shareable content URI using FileProvider.
     */
    fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String = "poster_${System.currentTimeMillis()}.png"): Uri? {
        return try {
            val cacheDir = File(context.cacheDir, "shared_images")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val file = File(cacheDir, fileName)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Triggers native Android share sheet with image URI.
     */
    fun shareImageUri(context: Context, uri: Uri, title: String = "Share Poster") {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, title))
    }
}
