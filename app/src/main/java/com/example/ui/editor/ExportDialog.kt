package com.example.ui.editor

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.domain.models.CanvasElement
import com.example.domain.models.ExportQuality
import com.example.domain.models.PosterBackground
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight
import com.example.utils.CanvasUtils
import com.example.utils.ExportFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ExportDialog(
    posterTitle: String,
    canvasWidth: Int,
    canvasHeight: Int,
    background: PosterBackground,
    elements: List<CanvasElement>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedQuality by remember { mutableStateOf(ExportQuality.HIGH) }
    var selectedFormat by remember { mutableStateOf(ExportFormat.PNG) }
    var isExporting by remember { mutableStateOf(false) }
    var exportProgressMessage by remember { mutableStateOf("") }
    var exportSuccess by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isExporting) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Export Design",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "High-definition export & instant sharing",
                            fontSize = 11.sp,
                            color = TextTertiaryLight
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isExporting,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryLight)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quality Selector
                Text(
                    text = "RESOLUTION QUALITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryLight
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportQuality.values().forEach { quality ->
                        val isSelected = selectedQuality == quality
                        val displayRes = "${(canvasWidth * quality.scaleFactor).toInt()}x${(canvasHeight * quality.scaleFactor).toInt()}"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AccentBlue else BorderLight,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedQuality = quality },
                            color = if (isSelected) AccentBlue.copy(alpha = 0.08f) else Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = quality.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AccentBlue else TextPrimaryLight
                                )
                                Text(
                                    text = displayRes,
                                    fontSize = 9.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Format Selector
                Text(
                    text = "FILE FORMAT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryLight
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportFormat.values().forEach { format ->
                        val isSelected = selectedFormat == format
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PrimaryBlack else BorderLight,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedFormat = format },
                            color = if (isSelected) PrimaryBlack.copy(alpha = 0.06f) else Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = format.extension.uppercase(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PrimaryBlack else TextSecondaryLight
                                )
                                Text(
                                    text = when (format) {
                                        ExportFormat.PNG -> "Crisp HD"
                                        ExportFormat.JPEG -> "Compact"
                                        ExportFormat.PDF -> "Vector Doc"
                                    },
                                    fontSize = 9.sp,
                                    color = TextTertiaryLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Indicator or Action Buttons
                if (isExporting) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = AccentBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = exportProgressMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondaryLight
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Save to Gallery Button
                        Button(
                            onClick = {
                                isExporting = true
                                exportProgressMessage = "Rendering in ${selectedQuality.label}..."
                                scope.launch {
                                    val bitmap = withContext(Dispatchers.Default) {
                                        CanvasUtils.renderPosterBitmap(
                                            width = canvasWidth,
                                            height = canvasHeight,
                                            background = background,
                                            elements = elements,
                                            context = context,
                                            quality = selectedQuality
                                        )
                                    }

                                    if (selectedFormat == ExportFormat.PDF) {
                                        exportProgressMessage = "Compiling PDF Document..."
                                        val pdfFile = withContext(Dispatchers.IO) {
                                            CanvasUtils.exportPosterToPdf(context, bitmap, posterTitle)
                                        }
                                        isExporting = false
                                        if (pdfFile != null) {
                                            Toast.makeText(context, "PDF saved to app storage!", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        exportProgressMessage = "Saving to Gallery..."
                                        val uri = withContext(Dispatchers.IO) {
                                            CanvasUtils.exportBitmapToGallery(
                                                context = context,
                                                bitmap = bitmap,
                                                format = selectedFormat,
                                                title = posterTitle
                                            )
                                        }
                                        isExporting = false
                                        if (uri != null) {
                                            Toast.makeText(context, "Saved to Photos / Gallery! 📸", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "Export failed. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedFormat == ExportFormat.PDF) "Save as PDF Document" else "Save to Photos / Gallery",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Instant Share Button
                        OutlinedButton(
                            onClick = {
                                isExporting = true
                                exportProgressMessage = "Preparing shareable file..."
                                scope.launch {
                                    val bitmap = withContext(Dispatchers.Default) {
                                        CanvasUtils.renderPosterBitmap(
                                            width = canvasWidth,
                                            height = canvasHeight,
                                            background = background,
                                            elements = elements,
                                            context = context,
                                            quality = selectedQuality
                                        )
                                    }

                                    if (selectedFormat == ExportFormat.PDF) {
                                        val pdfFile = withContext(Dispatchers.IO) {
                                            CanvasUtils.exportPosterToPdf(context, bitmap, posterTitle)
                                        }
                                        isExporting = false
                                        if (pdfFile != null) {
                                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                                            CanvasUtils.shareImageUri(context, uri, "Share Poster PDF", mimeType = "application/pdf")
                                            onDismiss()
                                        }
                                    } else {
                                        val shareUri = withContext(Dispatchers.IO) {
                                            CanvasUtils.saveBitmapToCache(
                                                context = context,
                                                bitmap = bitmap,
                                                format = selectedFormat
                                            )
                                        }
                                        isExporting = false
                                        if (shareUri != null) {
                                            CanvasUtils.shareImageUri(context, shareUri, "Share Poster", mimeType = selectedFormat.mimeType)
                                            onDismiss()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1-Tap Quick Share",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
