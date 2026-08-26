package com.example.ui.admin

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.domain.models.Category
import com.example.domain.models.Template
import com.example.domain.models.TemplateReport
import com.example.domain.models.TemplateStatus
import com.example.domain.models.UserProfile
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
fun AdminDashboardScreen(
    repository: PosterRepository,
    onBack: () -> Unit,
    onPreviewTemplate: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pendingTemplates by repository.getPendingTemplates().collectAsState(initial = emptyList())
    val allTemplates by repository.getAllTemplates().collectAsState(initial = emptyList())
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    val categories by repository.categories.collectAsState()
    val reports by repository.reports.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Approvals (${pendingTemplates.size})",
        "Templates (${allTemplates.size})",
        "Users (${users.size})",
        "Reports (${reports.count { !it.isResolved }})"
    )

    var templateToReject by remember { mutableStateOf<Template?>(null) }
    var rejectionFeedback by remember { mutableStateOf("") }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryIcon by remember { mutableStateOf("🏷️") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Control Center", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceLight,
                contentColor = PrimaryBlack
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Pending Approvals Queue
                    if (pendingTemplates.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Queue Clean", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("No pending templates awaiting moderation.", fontSize = 13.sp, color = TextSecondaryLight)
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pendingTemplates, key = { it.templateId }) { template ->
                                PendingApprovalCard(
                                    template = template,
                                    onApprove = {
                                        scope.launch {
                                            repository.updateTemplateStatus(template.templateId, TemplateStatus.APPROVED)
                                            Toast.makeText(context, "Template Approved!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onReject = {
                                        templateToReject = template
                                        rejectionFeedback = "Please adjust text contrast and verify logo copyright."
                                    },
                                    onPreview = { onPreviewTemplate(template.templateId) }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // All Templates Moderation
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allTemplates, key = { it.templateId }) { template ->
                            AdminTemplateRow(
                                template = template,
                                onToggleFeatured = {
                                    scope.launch {
                                        repository.toggleTemplateFeatured(template.templateId, !template.isFeatured)
                                    }
                                },
                                onDelete = {
                                    scope.launch {
                                        repository.deleteTemplate(template.templateId)
                                        Toast.makeText(context, "Template deleted from platform", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }

                2 -> {
                    // Users Management
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(users, key = { it.id }) { user ->
                            AdminUserCard(
                                user = user,
                                onToggleSuspension = {
                                    scope.launch {
                                        repository.toggleUserSuspension(user.id, !user.isSuspended)
                                    }
                                }
                            )
                        }
                    }
                }

                3 -> {
                    // Reports Moderation
                    if (reports.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No reports filed by users.", color = TextSecondaryLight, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(reports, key = { it.reportId }) { report ->
                                ReportItemCard(
                                    report = report,
                                    onResolve = {
                                        repository.resolveReport(report.reportId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Feedback Dialog
    if (templateToReject != null) {
        val tmpl = templateToReject!!
        Dialog(onDismissRequest = { templateToReject = null }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Reject Template", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Provide feedback for the creator so they can fix it.", fontSize = 12.sp, color = TextSecondaryLight)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = rejectionFeedback,
                        onValueChange = { rejectionFeedback = it },
                        label = { Text("Rejection Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { templateToReject = null }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    repository.updateTemplateStatus(tmpl.templateId, TemplateStatus.REJECTED, rejectionFeedback)
                                    templateToReject = null
                                    Toast.makeText(context, "Template marked as rejected", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("Reject & Notify")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingApprovalCard(
    template: Template,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onPreview() }
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(template.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Text("Creator: ${template.creatorName}", fontSize = 12.sp, color = TextSecondaryLight)
                    Text("Category: ${template.categoryName}", fontSize = 11.sp, color = TextTertiaryLight)
                    Text("${template.editableFields.size} dynamic fields", fontSize = 11.sp, color = Color(0xFF2563EB))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve & Publish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminTemplateRow(
    template: Template,
    onToggleFeatured: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(template.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    if (template.isFeatured) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("FEATURED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "By ${template.creatorName} • ${template.usageCount} uses • Status: ${template.status.name}",
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFeatured) {
                    Icon(
                        imageVector = if (template.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Feature",
                        tint = if (template.isFeatured) Color(0xFFF59E0B) else TextTertiaryLight
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: UserProfile,
    onToggleSuspension: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PrimaryBlack
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (user.role.name == "ADMIN") Color(0xFFFEF2F2) else Color(0xFFEFF6FF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(user.role.name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                        }
                    }
                    Text(user.email, fontSize = 11.sp, color = TextSecondaryLight)
                }
            }

            OutlinedButton(
                onClick = onToggleSuspension,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (user.isSuspended) Color(0xFF059669) else Color(0xFFDC2626)
                ),
                modifier = Modifier.height(34.dp)
            ) {
                Text(if (user.isSuspended) "Unsuspend" else "Suspend", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ReportItemCard(
    report: TemplateReport,
    onResolve: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Report on '${report.templateName}'", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                }

                if (report.isResolved) {
                    Text("Resolved", fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Reason: ${report.reason}", fontSize = 12.sp, color = TextSecondaryLight)
            if (report.details.isNotEmpty()) {
                Text("Details: ${report.details}", fontSize = 11.sp, color = TextTertiaryLight)
            }

            if (!report.isResolved) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onResolve,
                    modifier = Modifier.align(Alignment.End).height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Text("Mark as Resolved", fontSize = 11.sp)
                }
            }
        }
    }
}
