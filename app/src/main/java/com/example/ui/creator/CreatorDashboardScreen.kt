package com.example.ui.creator

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.PosterRepository
import com.example.domain.models.CanvasAspectRatio
import com.example.domain.models.PosterBackground
import com.example.domain.models.Template
import com.example.domain.models.TemplateStatus
import com.example.ui.components.PosterCanvasView
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDashboardScreen(
    repository: PosterRepository,
    onBack: () -> Unit,
    onOpenEditorForTemplate: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()
    val myTemplates by repository.getTemplatesByCreator(currentUser.id).collectAsState(initial = emptyList())

    var showNewTemplateDialog by remember { mutableStateOf(false) }

    val publishedCount = myTemplates.count { it.status == TemplateStatus.APPROVED }
    val pendingCount = myTemplates.count { it.status == TemplateStatus.PENDING }
    val draftCount = myTemplates.count { it.status == TemplateStatus.DRAFT }
    val totalUses = myTemplates.sumOf { it.usageCount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creator Studio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metrics Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Published",
                        value = publishedCount.toString(),
                        color = Color(0xFF059669),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "In Review",
                        value = pendingCount.toString(),
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Drafts",
                        value = draftCount.toString(),
                        color = Color(0xFF4B5563),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Uses",
                        value = totalUses.toString(),
                        color = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Create New Template Button
            item {
                Button(
                    onClick = { showNewTemplateDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("creator_new_template_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlack)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Design New Template", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // My Templates Section Header
            item {
                Text(
                    text = "My Templates (${myTemplates.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
            }

            if (myTemplates.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No templates created yet. Start designing your first template!", color = TextSecondaryLight, fontSize = 13.sp)
                    }
                }
            } else {
                items(myTemplates, key = { it.templateId }) { template ->
                    CreatorTemplateItemCard(
                        template = template,
                        onEdit = { onOpenEditorForTemplate(template.templateId) },
                        onSubmitForReview = {
                            scope.launch {
                                repository.updateTemplateStatus(template.templateId, TemplateStatus.PENDING)
                                Toast.makeText(context, "Submitted for Admin Review!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDelete = {
                            scope.launch {
                                repository.deleteTemplate(template.templateId)
                                Toast.makeText(context, "Template deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    // New Template Aspect Ratio Picker Dialog
    if (showNewTemplateDialog) {
        Dialog(onDismissRequest = { showNewTemplateDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Choose Canvas Format", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select dimension preset for your template", fontSize = 12.sp, color = TextSecondaryLight)

                    Spacer(modifier = Modifier.height(16.dp))

                    CanvasAspectRatio.values().forEach { preset ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorderLight, RoundedCornerShape(12.dp))
                                .clickable {
                                    scope.launch {
                                        val newId = "tmpl_" + UUID.randomUUID().toString().take(8)
                                        val newTemplate = Template(
                                            templateId = newId,
                                            creatorId = currentUser.id,
                                            creatorName = currentUser.name,
                                            name = "New ${preset.name.lowercase().capitalize()} Template",
                                            description = "Custom poster template created in Creator Studio",
                                            categoryId = "cat_business",
                                            categoryName = "Business",
                                            canvasWidth = preset.width,
                                            canvasHeight = preset.height,
                                            background = PosterBackground(),
                                            status = TemplateStatus.DRAFT
                                        )
                                        repository.saveTemplate(newTemplate)
                                        showNewTemplateDialog = false
                                        onOpenEditorForTemplate(newId)
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(preset.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                                Text("${preset.width} x ${preset.height} px (${preset.ratioStr})", fontSize = 11.sp, color = TextSecondaryLight)
                            }
                            Icon(Icons.Default.Add, contentDescription = null, tint = TextSecondaryLight)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, fontSize = 10.sp, color = TextSecondaryLight, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun CreatorTemplateItemCard(
    template: Template,
    onEdit: () -> Unit,
    onSubmitForReview: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Mini preview box
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    PosterCanvasView(
                        modifier = Modifier.fillMaxSize(),
                        canvasWidth = template.canvasWidth,
                        canvasHeight = template.canvasHeight,
                        background = template.background,
                        elements = template.elements,
                        isInteractive = false
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info & status
                Column(modifier = Modifier.weight(1f)) {
                    Text(template.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Category: ${template.categoryName}", fontSize = 11.sp, color = TextSecondaryLight)
                    Text("Fields: ${template.editableFields.size} configured", fontSize = 11.sp, color = TextTertiaryLight)

                    Spacer(modifier = Modifier.height(6.dp))

                    // Status Badge
                    val (statusColor, statusBg, icon) = when (template.status) {
                        TemplateStatus.APPROVED -> Triple(Color(0xFF059669), Color(0xFFECFDF5), Icons.Default.CheckCircle)
                        TemplateStatus.PENDING -> Triple(Color(0xFFD97706), Color(0xFFFEF3C7), Icons.Default.HourglassEmpty)
                        TemplateStatus.DRAFT -> Triple(Color(0xFF4B5563), Color(0xFFF3F4F6), Icons.Default.Edit)
                        TemplateStatus.REJECTED -> Triple(Color(0xFFDC2626), Color(0xFFFEF2F2), Icons.Default.Warning)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(template.status.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                        }
                    }
                }
            }

            // Rejection reason notice
            if (template.status == TemplateStatus.REJECTED && !template.rejectionReason.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF2F2))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Feedback: ${template.rejectionReason}",
                        fontSize = 11.sp,
                        color = Color(0xFF991B1B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (template.status == TemplateStatus.DRAFT || template.status == TemplateStatus.REJECTED) {
                    Button(
                        onClick = onSubmitForReview,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Submit for Approval", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Canvas", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
