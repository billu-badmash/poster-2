package com.example.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PosterRepository
import com.example.domain.models.UserRole
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Close

@Composable
fun ProfileScreen(
    repository: PosterRepository,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCreator: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val currentUser by repository.currentUser.collectAsState()
    val isDarkMode by repository.isDarkMode.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser.name) }
    var editBio by remember { mutableStateOf(currentUser.bio) }

    val bgColor = if (isDarkMode) DarkBackground else BackgroundLight
    val surfaceColor = if (isDarkMode) DarkSurface else SurfaceLight
    val textColor = if (isDarkMode) DarkTextPrimary else TextPrimaryLight
    val textSecondary = if (isDarkMode) DarkTextSecondary else TextSecondaryLight
    val textTertiary = if (isDarkMode) DarkTextSecondary else TextTertiaryLight
    val borderColor = if (isDarkMode) Color(0xFF2D3348) else CardBorderLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SETTINGS & ACCOUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textTertiary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }

        // Profile Avatar & Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isDarkMode) Color(0xFF252A3A) else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.name.take(1),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) DarkTextPrimary else PrimaryBlack
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isEditing) {
                    // Edit mode - Name field
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name", color = textSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedContainerColor = surfaceColor,
                            unfocusedContainerColor = surfaceColor,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = borderColor,
                            cursorColor = AccentBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Edit mode - Bio field
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio", color = textSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedContainerColor = surfaceColor,
                            unfocusedContainerColor = surfaceColor,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = borderColor,
                            cursorColor = AccentBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Save / Cancel buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                editName = currentUser.name
                                editBio = currentUser.bio
                                isEditing = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel", color = textSecondary)
                        }
                        Button(
                            onClick = {
                                repository.updateUserProfile(editName, editBio)
                                isEditing = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // View mode
                    Text(
                        text = currentUser.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Text(
                        text = currentUser.email,
                        fontSize = 13.sp,
                        color = textSecondary
                    )

                    if (currentUser.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser.bio,
                            fontSize = 12.sp,
                            color = textTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Current Active Role Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryBlack)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Active Persona: ${currentUser.role.name}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Edit Profile button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentBlue.copy(alpha = 0.1f))
                            .clickable {
                                editName = currentUser.name
                                editBio = currentUser.bio
                                isEditing = true
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = AccentBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentBlue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dark Mode Toggle
        Text(
            text = "Appearance",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dark Mode",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = if (isDarkMode) "Dark theme active" else "Light theme active",
                            fontSize = 11.sp,
                            color = textSecondary
                        )
                    }
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { repository.toggleDarkMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AccentBlue,
                        checkedTrackColor = AccentBlue.copy(alpha = 0.3f),
                        uncheckedThumbColor = textSecondary,
                        uncheckedTrackColor = Color(0xFFE2E8F0)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Account Workspace & Role Management
        Text(
            text = "Workspace Access",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                RoleOptionRow(
                    title = "User Workspace",
                    subtitle = "Browse marketplace, generate posters & save to projects",
                    isSelected = currentUser.role == UserRole.USER,
                    icon = Icons.Default.Person,
                    isDarkMode = isDarkMode,
                    onClick = { repository.switchRole(UserRole.USER) }
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(borderColor))

                RoleOptionRow(
                    title = if (currentUser.isCreatorVerified || currentUser.role == UserRole.CREATOR || currentUser.role == UserRole.ADMIN) "Creator Studio" else "Creator Studio (Apply)",
                    subtitle = "Design templates, configure dynamic form fields & publish",
                    isSelected = currentUser.role == UserRole.CREATOR,
                    icon = Icons.Default.Brush,
                    isDarkMode = isDarkMode,
                    onClick = {
                        repository.switchRole(UserRole.CREATOR)
                    }
                )

                // Only show Administrator option if the current authenticated account has ADMIN role authority
                if (currentUser.role == UserRole.ADMIN || currentUser.email.contains("admin", ignoreCase = true)) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(borderColor))

                    RoleOptionRow(
                        title = "Administrator Panel",
                        subtitle = "Approve pending templates, moderate content & manage platform",
                        isSelected = currentUser.role == UserRole.ADMIN,
                        icon = Icons.Default.AdminPanelSettings,
                        isDarkMode = isDarkMode,
                        onClick = { repository.switchRole(UserRole.ADMIN) }
                    )
                }
            }
        }

        // Quick Dashboard Shortcuts
        if (currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.CREATOR) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Studio & Moderation Tools",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentUser.role == UserRole.CREATOR) {
                    ActionNavCard(
                        title = "Open Creator Studio Dashboard",
                        subtitle = "Track template analytics and status",
                        isDarkMode = isDarkMode,
                        onClick = onNavigateToCreator
                    )
                }

                if (currentUser.role == UserRole.ADMIN) {
                    ActionNavCard(
                        title = "Open Admin Moderation Panel",
                        subtitle = "Review pending queue and reports",
                        isDarkMode = isDarkMode,
                        onClick = onNavigateToAdmin
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Poster Maker v1.0.0", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text("Design Theme: Clean Minimalism", fontSize = 12.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Equipped with dynamic form generation and interactive canvas editing engine.", fontSize = 11.sp, color = textTertiary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Log Out / Switch Account Action Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    repository.logout()
                    onLogout()
                }
                .testTag("btn_logout_profile"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF2A1015) else Color(0xFFFEF2F2)),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF5C1020) else Color(0xFFFCA5A5))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Log Out",
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Log Out / Switch Account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
private fun RoleOptionRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    isDarkMode: Boolean = false,
    onClick: () -> Unit
) {
    val textColor = if (isDarkMode) DarkTextPrimary else TextPrimaryLight
    val secondaryColor = if (isDarkMode) DarkTextSecondary else TextSecondaryLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) (if (isDarkMode) Color(0xFF252A3A) else Color(0xFFF3F4F6)) else Color.Transparent)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AccentBlack else secondaryColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = secondaryColor
                )
            }
        }

        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = "Active", tint = Color(0xFF059669))
        }
    }
}

@Composable
private fun ActionNavCard(
    title: String,
    subtitle: String,
    isDarkMode: Boolean = false,
    onClick: () -> Unit
) {
    val surfaceColor = if (isDarkMode) DarkSurface else SurfaceLight
    val textColor = if (isDarkMode) DarkTextPrimary else TextPrimaryLight
    val secondaryColor = if (isDarkMode) DarkTextSecondary else TextSecondaryLight
    val borderColor = if (isDarkMode) Color(0xFF2D3348) else CardBorderLight
    val tertiaryColor = if (isDarkMode) DarkTextSecondary else TextTertiaryLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text(subtitle, fontSize = 12.sp, color = secondaryColor)
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = tertiaryColor, modifier = Modifier.size(14.dp))
        }
    }
}
