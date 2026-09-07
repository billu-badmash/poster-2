package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.PosterRepository
import com.example.domain.models.UserProfile
import com.example.domain.models.UserRole
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    repository: PosterRepository,
    onLoginSuccess: (UserRole) -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.USER) }
    val focusManager = LocalFocusManager.current

    // Form inputs
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var bioInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showResetSentDialog by remember { mutableStateOf(false) }

    val presetUsers = remember { repository.getPresetUsers() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Decorative ambient gradient orbs in background
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GradStart.copy(alpha = 0.18f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = 20.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GradEnd.copy(alpha = 0.16f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Premium Glowing Logo Badge
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = AccentBlue)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradStart, GradEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Poster Studio Logo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Poster Studio",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryLight,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isSignUp) "Create your creative studio account" else "Sign in to access your creative workspace",
                fontSize = 13.sp,
                color = TextSecondaryLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Modern Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT WORKSPACE ROLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = TextTertiaryLight
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (selectedRole) {
                                        UserRole.USER -> AccentBlueBg
                                        UserRole.CREATOR -> AccentPurpleBg
                                        UserRole.ADMIN -> AccentRedBg
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = selectedRole.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (selectedRole) {
                                    UserRole.USER -> AccentBlue
                                    UserRole.CREATOR -> AccentPurple
                                    UserRole.ADMIN -> AccentRed
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vibrant Role Segmented Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceVariantLight)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        RoleTab(
                            title = "User",
                            icon = Icons.Default.Person,
                            activeColor = AccentBlue,
                            activeBg = AccentBlueBg,
                            isSelected = selectedRole == UserRole.USER,
                            onClick = {
                                selectedRole = UserRole.USER
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "role_tab_user"
                        )
                        RoleTab(
                            title = "Creator",
                            icon = Icons.Default.Brush,
                            activeColor = AccentPurple,
                            activeBg = AccentPurpleBg,
                            isSelected = selectedRole == UserRole.CREATOR,
                            onClick = {
                                selectedRole = UserRole.CREATOR
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "role_tab_creator"
                        )
                        RoleTab(
                            title = "Admin",
                            icon = Icons.Default.AdminPanelSettings,
                            activeColor = AccentRed,
                            activeBg = AccentRedBg,
                            isSelected = selectedRole == UserRole.ADMIN,
                            onClick = {
                                selectedRole = UserRole.ADMIN
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "role_tab_admin"
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Error Message Banner
                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentRedBg)
                                .border(1.dp, AccentRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AccentRed
                            )
                        }
                    }

                    // Name input (for Sign Up)
                    AnimatedVisibility(visible = isSignUp) {
                        Column {
                            Text(
                                text = if (selectedRole == UserRole.CREATOR) "Studio / Creator Name" else "Full Name",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                placeholder = { Text(if (selectedRole == UserRole.CREATOR) "e.g. Studio X Designs" else "e.g. Alex Rivera", fontSize = 13.sp, color = TextTertiaryLight) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier.fillMaxWidth().testTag("input_name"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryLight,
                                    unfocusedTextColor = TextPrimaryLight,
                                    focusedContainerColor = SurfaceLight,
                                    unfocusedContainerColor = SurfaceLight,
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = BorderLight,
                                    cursorColor = AccentBlue
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Email Input
                    Text(
                        text = "Email Address",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            errorMessage = null
                        },
                        placeholder = { Text("e.g. alex.rivera@example.com", fontSize = 13.sp, color = TextTertiaryLight) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().testTag("input_email"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimaryLight,
                            unfocusedTextColor = TextPrimaryLight,
                            focusedContainerColor = SurfaceLight,
                            unfocusedContainerColor = SurfaceLight,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = AccentBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    Text(
                        text = "Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = null
                        },
                        placeholder = { Text("••••••••", fontSize = 13.sp, color = TextTertiaryLight) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = TextTertiaryLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            executeAuth(
                                isSignUp = isSignUp,
                                nameInput = nameInput,
                                emailInput = emailInput,
                                passwordInput = passwordInput,
                                bioInput = bioInput,
                                selectedRole = selectedRole,
                                repository = repository,
                                onError = { errorMessage = it },
                                onLoginSuccess = onLoginSuccess
                            )
                        }),
                        modifier = Modifier.fillMaxWidth().testTag("input_password"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimaryLight,
                            unfocusedTextColor = TextPrimaryLight,
                            focusedContainerColor = SurfaceLight,
                            unfocusedContainerColor = SurfaceLight,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = AccentBlue
                        )
                    )

                    // Bio/Specialty Input (only for Creator Sign Up)
                    AnimatedVisibility(visible = isSignUp && selectedRole == UserRole.CREATOR) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Design Specialty",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = bioInput,
                                onValueChange = { bioInput = it },
                                placeholder = { Text("e.g. Minimalist brand & event posters", fontSize = 13.sp, color = TextTertiaryLight) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("input_bio"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimaryLight,
                                    unfocusedTextColor = TextPrimaryLight,
                                    focusedContainerColor = SurfaceLight,
                                    unfocusedContainerColor = SurfaceLight,
                                    focusedBorderColor = AccentPurple,
                                    unfocusedBorderColor = BorderLight,
                                    cursorColor = AccentPurple
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remember Me & Forgot Password
                    if (!isSignUp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { rememberMe = !rememberMe }
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = AccentBlue),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Remember me", fontSize = 12.sp, color = TextSecondaryLight)
                            }

                            Text(
                                text = "Forgot password?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue,
                                modifier = Modifier.clickable { showForgotPasswordDialog = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Main Action Button with Gradient & Press Animation
                    val submitInteraction = remember { MutableInteractionSource() }
                    val isSubmitPressed by submitInteraction.collectIsPressedAsState()
                    val submitScale by animateFloatAsState(
                        targetValue = if (isSubmitPressed) 0.97f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "submit_btn_scale"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .scale(submitScale)
                            .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = AccentBlue)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = when (selectedRole) {
                                        UserRole.USER -> listOf(GradStart, GradEnd)
                                        UserRole.CREATOR -> listOf(GradStart, AccentPink)
                                        UserRole.ADMIN -> listOf(Color(0xFFE53E3E), Color(0xFFFC7C33))
                                    }
                                )
                            )
                            .clickable(
                                interactionSource = submitInteraction,
                                indication = null
                            ) {
                                focusManager.clearFocus()
                                executeAuth(
                                    isSignUp = isSignUp,
                                    nameInput = nameInput,
                                    emailInput = emailInput,
                                    passwordInput = passwordInput,
                                    bioInput = bioInput,
                                    selectedRole = selectedRole,
                                    repository = repository,
                                    onError = { errorMessage = it },
                                    onLoginSuccess = onLoginSuccess
                                )
                            }
                            .testTag("btn_submit_auth"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isSignUp) "Create ${selectedRole.name} Account" else "Sign In as ${selectedRole.name}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Quick 1-Tap Persona Demo Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚡ QUICK 1-TAP DEMO LOGIN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = TextTertiaryLight
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetUsers.forEach { user ->
                        QuickPersonaCard(
                            user = user,
                            onClick = {
                                repository.login(user)
                                onLoginSuccess(user.role)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Sign In / Sign Up Mode Switch
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUp) "Already have an account?" else "Don't have an account?",
                    fontSize = 13.sp,
                    color = TextSecondaryLight
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSignUp) "Sign In" else "Sign Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentBlue,
                    modifier = Modifier
                        .clickable {
                            isSignUp = !isSignUp
                            errorMessage = null
                        }
                        .padding(4.dp)
                        .testTag("btn_toggle_mode")
                )
            }
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            Dialog(onDismissRequest = { showForgotPasswordDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "Reset Password",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter your email to receive a password reset link.",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            placeholder = { Text("you@example.com", fontSize = 13.sp, color = TextTertiaryLight) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimaryLight,
                                unfocusedTextColor = TextPrimaryLight,
                                focusedContainerColor = SurfaceLight,
                                unfocusedContainerColor = SurfaceLight,
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = BorderLight,
                                cursorColor = AccentBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showForgotPasswordDialog = false }) {
                                Text("Cancel", color = TextSecondaryLight, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showForgotPasswordDialog = false
                                    showResetSentDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                            ) {
                                Text("Send Link", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (showResetSentDialog) {
            Dialog(onDismissRequest = { showResetSentDialog = false }) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(AccentGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Email Sent",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Password reset instructions have been sent to your email.",
                            fontSize = 13.sp,
                            color = TextSecondaryLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { showResetSentDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("Got it", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleTab(
    title: String,
    icon: ImageVector,
    activeColor: Color,
    activeBg: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) SurfaceLight else Color.Transparent,
        animationSpec = tween(150),
        label = "role_tab_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else TextSecondaryLight,
        animationSpec = tween(150),
        label = "role_tab_color"
    )

    Row(
        modifier = modifier
            .shadow(if (isSelected) 3.dp else 0.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
private fun QuickPersonaCard(
    user: UserProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roleColor = when (user.role) {
        UserRole.USER -> AccentBlue
        UserRole.CREATOR -> AccentPurple
        UserRole.ADMIN -> AccentRed
    }
    val roleBg = when (user.role) {
        UserRole.USER -> AccentBlueBg
        UserRole.CREATOR -> AccentPurpleBg
        UserRole.ADMIN -> AccentRedBg
    }
    val roleIcon = when (user.role) {
        UserRole.USER -> Icons.Default.Person
        UserRole.CREATOR -> Icons.Default.Brush
        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
    }

    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "persona_card_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceLight)
            .border(1.dp, CardBorderLight, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interaction,
                indication = null
            ) { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .testTag("chip_persona_${user.role.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(roleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = roleIcon,
                    contentDescription = null,
                    tint = roleColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.role.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )
        }
    }
}

private fun executeAuth(
    isSignUp: Boolean,
    nameInput: String,
    emailInput: String,
    passwordInput: String,
    bioInput: String,
    selectedRole: UserRole,
    repository: PosterRepository,
    onError: (String) -> Unit,
    onLoginSuccess: (UserRole) -> Unit
) {
    if (isSignUp && nameInput.isBlank()) {
        onError("Please enter your name")
        return
    }
    if (emailInput.isBlank() || !emailInput.contains("@")) {
        onError("Please enter a valid email address")
        return
    }
    if (passwordInput.length < 4) {
        onError("Password must be at least 4 characters")
        return
    }

    if (isSignUp) {
        val user = repository.registerUser(
            name = nameInput,
            email = emailInput,
            role = selectedRole,
            bio = bioInput
        )
        onLoginSuccess(user.role)
    } else {
        val user = repository.loginWithCredentials(
            email = emailInput,
            role = selectedRole
        )
        onLoginSuccess(user.role)
    }
}

