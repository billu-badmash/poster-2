package com.example.ui.generator

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.PosterRepository
import com.example.domain.models.EditableField
import com.example.domain.models.FieldType
import com.example.domain.models.Project
import com.example.domain.models.Template
import com.example.ui.components.PosterCanvasView
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight
import com.example.utils.CanvasUtils
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicGeneratorScreen(
    templateId: String,
    repository: PosterRepository,
    onBack: () -> Unit,
    onOpenEditorWithProject: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()

    var template by remember { mutableStateOf<Template?>(null) }
    val formValues = remember { mutableStateMapOf<String, String>() }
    var projectName by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var lastSavedProjectId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(templateId) {
        val tmpl = repository.getTemplateById(templateId)
        template = tmpl
        if (tmpl != null) {
            projectName = "${tmpl.name} Custom"
            // Prepopulate form values with defaults
            tmpl.editableFields.forEach { field ->
                formValues[field.fieldId] = field.defaultValue
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Dynamic Generator", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = template?.name ?: "Loading...",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("gen_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            template?.let { tmpl ->
                                tmpl.editableFields.forEach { field ->
                                    formValues[field.fieldId] = field.defaultValue
                                }
                                Toast.makeText(context, "Reset to template defaults", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextSecondaryLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp),
                color = SurfaceLight
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save to My Projects button
                    OutlinedButton(
                        onClick = {
                            val tmpl = template ?: return@OutlinedButton
                            scope.launch {
                                isSaving = true
                                val projId = lastSavedProjectId ?: ("proj_" + UUID.randomUUID().toString().take(8))
                                val newProject = Project(
                                    projectId = projId,
                                    userId = currentUser.id,
                                    templateId = tmpl.templateId,
                                    templateName = tmpl.name,
                                    projectName = projectName.ifEmpty { "${tmpl.name} Draft" },
                                    canvasWidth = tmpl.canvasWidth,
                                    canvasHeight = tmpl.canvasHeight,
                                    background = tmpl.background,
                                    elements = tmpl.elements,
                                    fieldValues = formValues.toMap(),
                                    isDraft = false,
                                    createdAt = System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                repository.saveProject(newProject)
                                repository.incrementTemplateUsage(tmpl.templateId)
                                lastSavedProjectId = projId
                                isSaving = false
                                Toast.makeText(context, "Saved to My Projects!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_project_btn"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    // Export & Share button
                    Button(
                        onClick = { showExportDialog = true },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("export_poster_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlack)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export / Share", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        val currentTmpl = template
        if (currentTmpl == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlack)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Top Live Canvas Preview
                Text(
                    text = "LIVE POSTER PREVIEW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextTertiaryLight,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, CardBorderLight, RoundedCornerShape(20.dp))
                        .background(SurfaceLight)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PosterCanvasView(
                        modifier = Modifier.fillMaxWidth(),
                        canvasWidth = currentTmpl.canvasWidth,
                        canvasHeight = currentTmpl.canvasHeight,
                        background = currentTmpl.background,
                        elements = currentTmpl.elements,
                        fieldValues = formValues,
                        isInteractive = false
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Project Title Field
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Project Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = projectName,
                            onValueChange = { projectName = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF9FAFB),
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dynamic Form Fields Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customize Fields",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "${currentTmpl.editableFields.size} fields",
                        fontSize = 12.sp,
                        color = TextTertiaryLight
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Dynamically Render Form for each editable field
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    currentTmpl.editableFields.forEach { field ->
                        DynamicFormField(
                            field = field,
                            currentValue = formValues[field.fieldId] ?: "",
                            onValueChange = { newValue ->
                                formValues[field.fieldId] = newValue
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Edit Poster in Canvas button
                OutlinedButton(
                    onClick = {
                        // First save current draft project, then navigate to editor
                        scope.launch {
                            val projId = lastSavedProjectId ?: ("proj_" + UUID.randomUUID().toString().take(8))
                            val draftProject = Project(
                                projectId = projId,
                                userId = currentUser.id,
                                templateId = currentTmpl.templateId,
                                templateName = currentTmpl.name,
                                projectName = projectName.ifEmpty { "${currentTmpl.name} Draft" },
                                canvasWidth = currentTmpl.canvasWidth,
                                canvasHeight = currentTmpl.canvasHeight,
                                background = currentTmpl.background,
                                elements = currentTmpl.elements,
                                fieldValues = formValues.toMap(),
                                isDraft = true
                            )
                            repository.saveProject(draftProject)
                            onOpenEditorWithProject(projId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_custom_editor_btn"),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fine-tune Canvas in Full Editor", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Export & Share Dialog
    if (showExportDialog && template != null) {
        val tmpl = template!!
        Dialog(onDismissRequest = { showExportDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Export Poster", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Choose resolution & sharing options", fontSize = 12.sp, color = TextSecondaryLight)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resolution pill options
                    var selectedQuality by remember { mutableStateOf("High (1080p)") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Standard", "High (1080p)", "Max 4K").forEach { quality ->
                            val isSel = selectedQuality == quality
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) PrimaryBlack else Color(0xFFF3F4F6))
                                    .clickable { selectedQuality = quality }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = quality,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else TextPrimaryLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Direct Share Button
                    Button(
                        onClick = {
                            val bitmap = CanvasUtils.renderPosterBitmap(
                                width = tmpl.canvasWidth,
                                height = tmpl.canvasHeight,
                                background = tmpl.background,
                                elements = tmpl.elements,
                                fieldValues = formValues
                            )
                            val uri = CanvasUtils.saveBitmapToCache(context, bitmap, "poster_${System.currentTimeMillis()}.png")
                            if (uri != null) {
                                CanvasUtils.shareImageUri(context, uri, "Share Generated Poster")
                                showExportDialog = false
                            } else {
                                Toast.makeText(context, "Error saving bitmap", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("share_native_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlack)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share to Apps (WhatsApp, IG...)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Save to Gallery Button
                    OutlinedButton(
                        onClick = {
                            val bitmap = CanvasUtils.renderPosterBitmap(
                                width = tmpl.canvasWidth,
                                height = tmpl.canvasHeight,
                                background = tmpl.background,
                                elements = tmpl.elements,
                                fieldValues = formValues
                            )
                            val uri = CanvasUtils.saveBitmapToCache(context, bitmap, "poster_saved_${System.currentTimeMillis()}.png")
                            Toast.makeText(context, "Saved to device storage & gallery!", Toast.LENGTH_LONG).show()
                            showExportDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_gallery_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save PNG Image to Device", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicFormField(
    field: EditableField,
    currentValue: String,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = getFieldIcon(field.type)
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = field.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    if (field.required) {
                        Text(" *", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = field.type.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextTertiaryLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (field.type) {
                FieldType.IMAGE, FieldType.LOGO -> {
                    // Image upload mock & selector
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .clickable {
                                onValueChange("photo_user_selected_${System.currentTimeMillis()}")
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentValue.isNotEmpty()) "✓ Photo Attached (Tap to Change)" else "Tap to Upload Photo / Logo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (currentValue.isNotEmpty()) Color(0xFF059669) else Color(0xFF2563EB)
                            )
                        }
                    }
                }
                else -> {
                    val kbType = when (field.type) {
                        FieldType.PHONE -> KeyboardType.Phone
                        FieldType.EMAIL -> KeyboardType.Email
                        FieldType.PRICE -> KeyboardType.Number
                        else -> KeyboardType.Text
                    }

                    OutlinedTextField(
                        value = currentValue,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_${field.fieldId}"),
                        placeholder = { Text(field.placeholder.ifEmpty { "Enter ${field.label.lowercase()}" }, fontSize = 13.sp) },
                        singleLine = !field.label.contains("Description", ignoreCase = true) && !field.label.contains("Highlight", ignoreCase = true),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(keyboardType = kbType),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        )
                    )
                }
            }
        }
    }
}

private fun getFieldIcon(type: FieldType): ImageVector {
    return when (type) {
        FieldType.PHONE -> Icons.Default.Phone
        FieldType.EMAIL -> Icons.Default.Email
        FieldType.DATE, FieldType.TIME -> Icons.Default.DateRange
        FieldType.ADDRESS -> Icons.Default.LocationOn
        FieldType.WEBSITE -> Icons.Default.Language
        FieldType.IMAGE, FieldType.LOGO -> Icons.Default.AddPhotoAlternate
        else -> Icons.Default.Edit
    }
}
