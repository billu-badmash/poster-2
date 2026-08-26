package com.example.ui.profile

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight

@Composable
fun ProfileScreen(
    repository: PosterRepository,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCreator: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
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
                    color = TextTertiaryLight,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
            }
        }

        // Profile Avatar & Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.name.take(1),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlack
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentUser.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )

                Text(
                    text = currentUser.email,
                    fontSize = 13.sp,
                    color = TextSecondaryLight
                )

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
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Role Persona Switcher Card
        Text(
            text = "Switch Account Persona",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryLight,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                RoleOptionRow(
                    title = "Normal User",
                    subtitle = "Browse marketplace, generate posters & save to projects",
                    isSelected = currentUser.role == UserRole.USER,
                    icon = Icons.Default.Person,
                    onClick = { repository.switchRole(UserRole.USER) }
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorderLight))

                RoleOptionRow(
                    title = "Creator",
                    subtitle = "Design templates, configure dynamic form fields & submit to marketplace",
                    isSelected = currentUser.role == UserRole.CREATOR,
                    icon = Icons.Default.Brush,
                    onClick = { repository.switchRole(UserRole.CREATOR) }
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorderLight))

                RoleOptionRow(
                    title = "Administrator",
                    subtitle = "Approve pending templates, moderate content & manage platform",
                    isSelected = currentUser.role == UserRole.ADMIN,
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = { repository.switchRole(UserRole.ADMIN) }
                )
            }
        }

        // Quick Dashboard Shortcuts
        if (currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.CREATOR) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Studio & Moderation Tools",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
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
                        onClick = onNavigateToCreator
                    )
                }

                if (currentUser.role == UserRole.ADMIN) {
                    ActionNavCard(
                        title = "Open Admin Moderation Panel",
                        subtitle = "Review pending queue and reports",
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
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Poster Maker v1.0.0", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                Text("Design Theme: Clean Minimalism", fontSize = 12.sp, color = TextSecondaryLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Equipped with dynamic form generation and interactive canvas editing engine.", fontSize = 11.sp, color = TextTertiaryLight)
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
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFF3F4F6) else Color.Transparent)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AccentBlack else TextSecondaryLight,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondaryLight
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                Text(subtitle, fontSize = 12.sp, color = TextSecondaryLight)
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TextTertiaryLight, modifier = Modifier.size(14.dp))
        }
    }
}
