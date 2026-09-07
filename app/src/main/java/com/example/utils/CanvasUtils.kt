package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.domain.models.BackgroundType
import com.example.domain.models.CanvasElement
import com.example.domain.models.ElementType
import com.example.domain.models.ExportQuality
import com.example.domain.models.PosterBackground
import com.example.domain.models.ShapeType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

enum class ExportFormat(val extension: String, val mimeType: String, val label: String) {
    PNG("png", "image/png", "PNG (High Quality)"),
    JPEG("jpg", "image/jpeg", "JPEG (Compressed)"),
    PDF("pdf", "application/pdf", "PDF (Print Document)")
}

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
     * @param context Optional context for loading images from URIs.
     * @param quality Export quality setting that determines the output resolution.
     */
    fun renderPosterBitmap(
        width: Int = 1080,
        height: Int = 1080,
        background: PosterBackground,
        elements: List<CanvasElement>,
        fieldValues: Map<String, String> = emptyMap(),
        context: Context? = null,
        quality: ExportQuality = ExportQuality.HIGH
    ): Bitmap {
        val outputWidth = (width * quality.scaleFactor).toInt().coerceAtLeast(100)
        val outputHeight = (height * quality.scaleFactor).toInt().coerceAtLeast(100)
        val bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (background.type) {
            BackgroundType.SOLID -> {
                bgPaint.color = parseColor(background.color1Hex, android.graphics.Color.WHITE)
                canvas.drawRect(0f, 0f, outputWidth.toFloat(), outputHeight.toFloat(), bgPaint)
            }
            BackgroundType.GRADIENT_LINEAR -> {
                val c1 = parseColor(background.color1Hex, android.graphics.Color.WHITE)
                val c2 = parseColor(background.color2Hex ?: background.color1Hex, android.graphics.Color.LTGRAY)
                val shader = android.graphics.LinearGradient(
                    0f, 0f, 0f, outputHeight.toFloat(),
                    c1, c2, android.graphics.Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                canvas.drawRect(0f, 0f, outputWidth.toFloat(), outputHeight.toFloat(), bgPaint)
            }
            BackgroundType.IMAGE -> {
                var bgLoaded = false
                if (context != null && !background.imageUrl.isNullOrBlank()) {
                    try {
                        val uri = Uri.parse(background.imageUrl)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val bmp = BitmapFactory.decodeStream(inputStream)
                            inputStream.close()
                            if (bmp != null) {
                                canvas.drawBitmap(bmp, null, RectF(0f, 0f, outputWidth.toFloat(), outputHeight.toFloat()), bgPaint)
                                bmp.recycle()
                                bgLoaded = true
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (!bgLoaded) {
                    bgPaint.color = parseColor(background.color1Hex, android.graphics.Color.DKGRAY)
                    canvas.drawRect(0f, 0f, outputWidth.toFloat(), outputHeight.toFloat(), bgPaint)
                }
            }
            else -> {
                bgPaint.color = parseColor(background.color1Hex, android.graphics.Color.WHITE)
                canvas.drawRect(0f, 0f, outputWidth.toFloat(), outputHeight.toFloat(), bgPaint)
            }
        }

        // 2. Draw Elements ordered by layer
        val sortedElements = elements.filter { it.isVisible }.sortedBy { it.layerOrder }
        for (el in sortedElements) {
            val elLeft = el.xRatio * outputWidth
            val elTop = el.yRatio * outputHeight
            val elWidth = el.widthRatio * outputWidth
            val elHeight = el.heightRatio * outputHeight

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
                            val corner = (el.shapeCornerRadiusDp * (outputWidth / 400f))
                            canvas.drawRoundRect(rect, corner, corner, shapePaint)
                        }
                        ShapeType.RECTANGLE -> {
                            canvas.drawRect(rect, shapePaint)
                        }
                        ShapeType.LINE -> {
                            shapePaint.strokeWidth = 4f * (outputWidth / 400f)
                            canvas.drawLine(elLeft, elTop + elHeight / 2f, elLeft + elWidth, elTop + elHeight / 2f, shapePaint)
                        }
                        ShapeType.BADGE -> {
                            val corner = (el.shapeCornerRadiusDp * (outputWidth / 400f))
                            canvas.drawRoundRect(rect, corner, corner, shapePaint)
                        }
                    }

                    // Stroke if any
                    if (el.strokeWidthDp > 0) {
                        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = parseColor(el.strokeColorHex, android.graphics.Color.WHITE)
                            style = Paint.Style.STROKE
                            strokeWidth = el.strokeWidthDp * (outputWidth / 400f)
                        }
                        canvas.drawRoundRect(rect, el.shapeCornerRadiusDp * (outputWidth / 400f), el.shapeCornerRadiusDp * (outputWidth / 400f), strokePaint)
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
                        textSize = el.fontSizeSp * (outputWidth / 360f)
                        isFakeBoldText = el.isBold
                        if (el.isItalic) textSkewX = -0.25f
                        textAlign = when (el.textAlign.uppercase()) {
                            "LEFT" -> Paint.Align.LEFT
                            "RIGHT" -> Paint.Align.RIGHT
                            else -> Paint.Align.CENTER
                        }
                    }

                    // Shadow effect
                    if (el.hasShadow) {
                        textPaint.setShadowLayer(
                            8f * (outputWidth / 400f),
                            2f * (outputWidth / 400f),
                            4f * (outputWidth / 400f),
                            parseColor(el.shadowColorHex, android.graphics.Color.DKGRAY)
                        )
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
                        // Optional stroke/outline
                        if (el.hasStroke && el.strokeWidthDp > 0) {
                            val strokePaint = Paint(textPaint).apply {
                                style = Paint.Style.STROKE
                                strokeWidth = el.strokeWidthDp * (outputWidth / 400f)
                                color = parseColor(el.strokeColorHex, android.graphics.Color.WHITE)
                                clearShadowLayer()
                            }
                            canvas.drawText(line, targetX, startY, strokePaint)
                        }
                        canvas.drawText(line, targetX, startY, textPaint)
                        startY += lineHeight
                    }
                }

                ElementType.IMAGE -> {
                    val rect = RectF(elLeft, elTop, elLeft + elWidth, elTop + elHeight)
                    val corner = el.cornerRadiusDp * (outputWidth / 400f)

                    var imageLoaded = false
                    if (context != null) {
                        val uriStr = el.localUri ?: el.imageUrl
                        if (!uriStr.isNullOrBlank()) {
                            try {
                                val uri = Uri.parse(uriStr)
                                val inputStream = context.contentResolver.openInputStream(uri)
                                if (inputStream != null) {
                                    val opts = BitmapFactory.Options().apply {
                                        inJustDecodeBounds = true
                                    }
                                    BitmapFactory.decodeStream(inputStream, null, opts)
                                    inputStream.close()

                                    val sampleSize = maxOf(1, maxOf(opts.outWidth / (elWidth.toInt() * 2), opts.outHeight / (elHeight.toInt() * 2)))
                                    val decodeOpts = BitmapFactory.Options().apply {
                                        inSampleSize = sampleSize
                                    }
                                    val stream2 = context.contentResolver.openInputStream(uri)
                                    if (stream2 != null) {
                                        val bmp = BitmapFactory.decodeStream(stream2, null, decodeOpts)
                                        stream2.close()
                                        if (bmp != null) {
                                            val clippedBmp = Bitmap.createBitmap(elWidth.toInt().coerceAtLeast(1), elHeight.toInt().coerceAtLeast(1), Bitmap.Config.ARGB_8888)
                                            val clipCanvas = Canvas(clippedBmp)
                                            val clipPath = android.graphics.Path().apply {
                                                addRoundRect(RectF(0f, 0f, elWidth, elHeight), corner, corner, android.graphics.Path.Direction.CW)
                                            }
                                            clipCanvas.clipPath(clipPath)
                                            clipCanvas.drawBitmap(bmp, null, RectF(0f, 0f, elWidth, elHeight), Paint(Paint.ANTI_ALIAS_FLAG))
                                            canvas.drawBitmap(clippedBmp, elLeft, elTop, null)
                                            bmp.recycle()
                                            imageLoaded = true
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    if (!imageLoaded) {
                        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = parseColor("#E2E8F0", android.graphics.Color.LTGRAY)
                            style = Paint.Style.FILL
                        }
                        canvas.drawRoundRect(rect, corner, corner, cardPaint)

                        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = parseColor("#64748B", android.graphics.Color.DKGRAY)
                            textSize = 28f * (outputWidth / 1080f)
                            textAlign = Paint.Align.CENTER
                        }
                        canvas.drawText("Photo Layer", elLeft + elWidth / 2f, elTop + elHeight / 2f, labelPaint)
                    }
                }
            }

            canvas.restore()
        }

        return bitmap
    }

    /**
     * Saves rendered poster bitmap directly to the Android MediaStore (Gallery).
     */
    fun exportBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        format: ExportFormat = ExportFormat.PNG,
        title: String = "Poster_${System.currentTimeMillis()}"
    ): Uri? {
        val cleanTitle = title.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val fileName = "$cleanTitle.${format.extension}"

        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, format.mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/PosterMaker")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (imageUri != null) {
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    val compressFormat = if (format == ExportFormat.JPEG) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG
                    val compressQuality = if (format == ExportFormat.JPEG) 92 else 100
                    bitmap.compress(compressFormat, compressQuality, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
            }
            imageUri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generates a printable PDF file from the poster bitmap.
     */
    fun exportPosterToPdf(
        context: Context,
        bitmap: Bitmap,
        title: String = "Poster_${System.currentTimeMillis()}"
    ): File? {
        return try {
            val cleanTitle = title.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
            val cacheDir = File(context.cacheDir, "exported_pdf")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val pdfFile = File(cacheDir, "$cleanTitle.pdf")

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()

            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves bitmap to app cache and returns the shareable content URI using FileProvider.
     */
    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        format: ExportFormat = ExportFormat.PNG,
        fileName: String = "poster_${System.currentTimeMillis()}.${format.extension}"
    ): Uri? {
        return try {
            val cacheDir = File(context.cacheDir, "shared_images")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val file = File(cacheDir, fileName)
            val stream = FileOutputStream(file)
            val compressFormat = if (format == ExportFormat.JPEG) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG
            bitmap.compress(compressFormat, 100, stream)
            stream.flush()
            stream.close()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): Uri? {
        return saveBitmapToCache(context, bitmap, ExportFormat.PNG, fileName)
    }

    /**
     * Triggers native Android share sheet with image URI.
     */
    fun shareImageUri(context: Context, uri: Uri, title: String = "Share Poster", mimeType: String = "image/png") {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, title))
    }

    /**
     * Extracts top dominant and vibrant hex colors from an image URI for 1-tap palette generation.
     */
    fun extractColorPalette(context: Context, imageUri: Uri, maxColors: Int = 6): List<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val fullBmp = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (fullBmp == null) return emptyList()

            val scaledBmp = Bitmap.createScaledBitmap(fullBmp, 64, 64, true)
            if (scaledBmp != fullBmp) fullBmp.recycle()

            val colorBuckets = mutableMapOf<Int, Int>()
            val width = scaledBmp.width
            val height = scaledBmp.height

            for (x in 0 until width step 2) {
                for (y in 0 until height step 2) {
                    val pixel = scaledBmp.getPixel(x, y)
                    val alpha = (pixel shr 24) and 0xFF
                    if (alpha < 128) continue

                    val r = (pixel shr 16) and 0xFF
                    val g = (pixel shr 8) and 0xFF
                    val b = pixel and 0xFF

                    val qr = (r / 32) * 32
                    val qg = (g / 32) * 32
                    val qb = (b / 32) * 32
                    val quantized = (0xFF shl 24) or (qr shl 16) or (qg shl 8) or qb

                    colorBuckets[quantized] = (colorBuckets[quantized] ?: 0) + 1
                }
            }
            scaledBmp.recycle()

            val sortedColors = colorBuckets.entries
                .sortedByDescending { it.value }
                .map { String.format("#%06X", 0xFFFFFF and it.key) }
                .distinct()
                .take(maxColors)

            if (sortedColors.isEmpty()) listOf("#111827", "#3B82F6", "#10B981", "#F59E0B") else sortedColors
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
