package com.example.ui.templates

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.PosterRepository
import com.example.domain.models.Template
import com.example.domain.models.UserRole
import com.example.ui.components.PosterCanvasView
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.theme.*
import com.example.utils.CanvasUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateDetailScreen(
    templateId: String,
    repository: PosterRepository,
    onBack: () -> Unit,
    onUseTemplate: (String) -> Unit,
    onOpenEditor: (String) -> Unit,
    onViewHistory: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()

    var template by remember { mutableStateOf<Template?>(null) }
    var isFav by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("Inappropriate Content") }
    var reportDetails by remember { mutableStateOf("") }
    var reportSubmitted by remember { mutableStateOf(false) }

    LaunchedEffect(templateId) {
        val tmpl = repository.getTemplateById(templateId)
        template = tmpl
    }

    val favorites by repository.getFavoriteTemplates(currentUser.id).collectAsState(initial = emptyList())
    isFav = favorites.any { it.templateId == templateId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Template Details", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            repository.toggleFavorite(templateId, currentUser.id)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) Color(0xFFEF4444) else TextPrimaryLight
                        )
                    }

                    IconButton(onClick = {
                        template?.let { tmpl ->
                            val bitmap = CanvasUtils.renderPosterBitmap(
                                width = tmpl.canvasWidth,
                                height = tmpl.canvasHeight,
                                background = tmpl.background,
                                elements = tmpl.elements
                            )
                            val uri = CanvasUtils.saveBitmapToCache(context, bitmap, "template_${tmpl.templateId}.png")
                            if (uri != null) {
                                CanvasUtils.shareImageUri(context, uri, "Share '${tmpl.name}'")
                            }
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }

                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.Flag, contentDescription = "Report", tint = TextTertiaryLight)
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
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onOpenEditor(templateId) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("open_canvas_editor_btn"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextPrimaryLight)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit", fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
                    }

                    Button(
                        onClick = { onUseTemplate(templateId) },
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("use_template_form_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Use Template", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                // View History button (for creators/admins)
                if (currentUser.role == UserRole.CREATOR || currentUser.role == UserRole.ADMIN) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onViewHistory(templateId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondaryLight)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Version History", fontWeight = FontWeight.SemiBold, color = TextSecondaryLight, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        val currentTmpl = template
        if (currentTmpl == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Loading template details...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Large Poster Preview - Click anywhere to open editor
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, CardBorderLight, RoundedCornerShape(24.dp))
                        .background(SurfaceLight)
                        .clickable { onOpenEditor(templateId) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PosterCanvasView(
                        modifier = Modifier.fillMaxWidth(),
                        canvasWidth = currentTmpl.canvasWidth,
                        canvasHeight = currentTmpl.canvasHeight,
                        background = currentTmpl.background,
                        elements = currentTmpl.elements,
                        isInteractive = false
                    )

                    // Touch Indicator Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "👆 Tap anywhere to edit & customize",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title, Creator & Permission Level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentTmpl.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "By ${currentTmpl.creatorName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondaryLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("Verified Creator", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = currentTmpl.categoryName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Customization Permission Level Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(currentTmpl.permissionLevel.badgeColorHex)))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentTmpl.permissionLevel.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentTmpl.permissionLevel.description,
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Bar Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Usage", fontSize = 11.sp, color = TextTertiaryLight)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${currentTmpl.usageCount}+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                        }
                        Box(modifier = Modifier.size(1.dp, 30.dp).background(CardBorderLight))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Favorites", fontSize = 11.sp, color = TextTertiaryLight)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${currentTmpl.favoriteCount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                        }
                        Box(modifier = Modifier.size(1.dp, 30.dp).background(CardBorderLight))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rating", fontSize = 11.sp, color = TextTertiaryLight)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(String.format("%.1f", currentTmpl.rating), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text("Description", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentTmpl.description,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dynamic Customizable Fields
                Text(
                    text = "Dynamic Form Fields (${currentTmpl.editableFields.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentTmpl.editableFields.forEach { field ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceLight)
                                .border(1.dp, CardBorderLight, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 10.dp)
                            ) {
                                Text(
                                    text = field.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryLight,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Type: ${field.type.name.lowercase().replaceFirstChar { it.uppercase() }} • Default: ${field.defaultValue}",
                                    fontSize = 11.sp,
                                    color = TextTertiaryLight,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentBlueBg)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Auto-Form",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }

                // Creator / Admin Actions if applicable
                if (currentUser.role == UserRole.CREATOR || currentUser.role == UserRole.ADMIN || currentTmpl.creatorId == currentUser.id) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Creator Tools", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val copy = repository.duplicateTemplate(templateId, currentUser.id, currentUser.name)
                                    if (copy != null) {
                                        onOpenEditor(copy.templateId)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Duplicate Template", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Report Dialog
    if (showReportDialog) {
        Dialog(onDismissRequest = { showReportDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Report Template", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Help us maintain safety and copyright integrity.", fontSize = 12.sp, color = TextSecondaryLight)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Reason:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val reasons = listOf("Spam", "Copyright concern", "Inappropriate content", "Misleading information", "Other")
                    reasons.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = r }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (reportReason == r) PrimaryBlack else CardBorderLight)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(r, fontSize = 13.sp, color = TextPrimaryLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reportDetails,
                        onValueChange = { reportDetails = it },
                        label = { Text("Details (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { showReportDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                template?.let { tmpl ->
                                    repository.reportTemplate(tmpl.templateId, tmpl.name, reportReason, reportDetails)
                                }
                                showReportDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("Submit Report")
                        }
                    }
                }
            }
        }
    }
}
