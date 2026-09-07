package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.repository.PosterRepository
import com.example.domain.models.UserRole
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.components.CleanBottomBar
import com.example.ui.creator.CreatorDashboardScreen
import com.example.ui.editor.PosterEditorScreen
import com.example.ui.favorites.FavoritesScreen
import com.example.ui.generator.DynamicGeneratorScreen
import com.example.ui.home.HomeScreen
import com.example.ui.notifications.NotificationsScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.projects.ProjectsScreen
import com.example.ui.templates.TemplateDetailScreen
import com.example.ui.templates.TemplateHistoryScreen
import com.example.ui.templates.TemplatesScreen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = PosterRepository(applicationContext)

        setContent {
            val isDarkMode by repository.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                PosterMakerApp(repository = repository)
            }
        }
    }
}

@Composable
fun PosterMakerApp(repository: PosterRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDarkMode by repository.isDarkMode.collectAsState()

    val topLevelRoutes = listOf("home", "templates", "projects", "favorites", "profile")
    val showBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (isDarkMode) com.example.ui.theme.DarkBackground else BackgroundLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.fillMaxSize()
            ) {
                // 0. Login & Role Authentication Screen
                composable("login") {
                    LoginScreen(
                        repository = repository,
                        onLoginSuccess = { role ->
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                            when (role) {
                                UserRole.ADMIN -> navController.navigate("admin")
                                UserRole.CREATOR -> navController.navigate("creator")
                                UserRole.USER -> { /* Already at home */ }
                            }
                        }
                    )
                }

                // 1. Home
                composable("home") {
                    HomeScreen(
                        repository = repository,
                        onNavigateToTemplateDetail = { templateId ->
                            navController.navigate("template_detail/$templateId")
                        },
                        onNavigateToGenerator = { templateId ->
                            navController.navigate("generator/$templateId")
                        },
                        onNavigateToEditor = { templateId, projectId ->
                            val route = when {
                                projectId != null -> "editor?projectId=$projectId"
                                templateId != null -> "editor?templateId=$templateId"
                                else -> "editor"
                            }
                            navController.navigate(route)
                        },
                        onNavigateToTemplates = {
                            navController.navigate("templates")
                        },
                        onNavigateToProjects = {
                            navController.navigate("projects")
                        },
                        onNavigateToNotifications = {
                            navController.navigate("notifications")
                        },
                        onNavigateToProfile = {
                            navController.navigate("profile")
                        },
                        onNavigateToAdmin = {
                            navController.navigate("admin")
                        },
                        onNavigateToCreator = {
                            navController.navigate("creator")
                        }
                    )
                }

                // 2. Templates Marketplace
                composable("templates") {
                    TemplatesScreen(
                        repository = repository,
                        onNavigateToTemplateDetail = { templateId ->
                            navController.navigate("template_detail/$templateId")
                        }
                    )
                }

                // 3. Projects Workspace
                composable("projects") {
                    ProjectsScreen(
                        repository = repository,
                        onOpenProjectInEditor = { projectId ->
                            navController.navigate("editor?projectId=$projectId")
                        },
                        onCreateNew = {
                            navController.navigate("editor")
                        }
                    )
                }

                // 4. Favorites
                composable("favorites") {
                    FavoritesScreen(
                        repository = repository,
                        onNavigateToTemplateDetail = { templateId ->
                            navController.navigate("template_detail/$templateId")
                        },
                        onExploreTemplates = {
                            navController.navigate("templates")
                        }
                    )
                }

                // 5. Profile
                composable("profile") {
                    ProfileScreen(
                        repository = repository,
                        onNavigateToAdmin = { navController.navigate("admin") },
                        onNavigateToCreator = { navController.navigate("creator") },
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // 6. Template Details
                composable(
                    route = "template_detail/{templateId}",
                    arguments = listOf(navArgument("templateId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val templateId = backStackEntry.arguments?.getString("templateId") ?: ""
                    TemplateDetailScreen(
                        templateId = templateId,
                        repository = repository,
                        onBack = { navController.popBackStack() },
                        onUseTemplate = { tmplId ->
                            navController.navigate("generator/$tmplId")
                        },
                        onOpenEditor = { tmplId ->
                            navController.navigate("editor?templateId=$tmplId")
                        },
                        onViewHistory = { tmplId ->
                            navController.navigate("template_history/$tmplId")
                        }
                    )
                }

                // 6b. Template Version History
                composable(
                    route = "template_history/{templateId}",
                    arguments = listOf(navArgument("templateId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val templateId = backStackEntry.arguments?.getString("templateId") ?: ""
                    TemplateHistoryScreen(
                        templateId = templateId,
                        repository = repository,
                        onBack = { navController.popBackStack() },
                        onRestored = { navController.popBackStack() }
                    )
                }

                // 7. Dynamic Form-based Poster Generator
                composable(
                    route = "generator/{templateId}",
                    arguments = listOf(navArgument("templateId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val templateId = backStackEntry.arguments?.getString("templateId") ?: ""
                    DynamicGeneratorScreen(
                        templateId = templateId,
                        repository = repository,
                        onBack = { navController.popBackStack() },
                        onOpenEditorWithProject = { projId ->
                            navController.navigate("editor?projectId=$projId")
                        }
                    )
                }

                // 8. Visual Poster Canvas Editor
                composable(
                    route = "editor?templateId={templateId}&projectId={projectId}",
                    arguments = listOf(
                        navArgument("templateId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("projectId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val templateId = backStackEntry.arguments?.getString("templateId")
                    val projectId = backStackEntry.arguments?.getString("projectId")
                    PosterEditorScreen(
                        templateId = templateId,
                        projectId = projectId,
                        repository = repository,
                        onBack = { navController.popBackStack() }
                    )
                }

                // 9. Admin Moderation Dashboard
                composable("admin") {
                    AdminDashboardScreen(
                        repository = repository,
                        onBack = { navController.popBackStack() },
                        onPreviewTemplate = { templateId ->
                            navController.navigate("template_detail/$templateId")
                        }
                    )
                }

                // 10. Creator Studio Dashboard
                composable("creator") {
                    CreatorDashboardScreen(
                        repository = repository,
                        onBack = { navController.popBackStack() },
                        onOpenEditorForTemplate = { templateId ->
                            navController.navigate("editor?templateId=$templateId")
                        }
                    )
                }

                // 11. Notifications
                composable("notifications") {
                    NotificationsScreen(
                        repository = repository,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Floating Clean Minimalism Bottom Navigation Bar
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                CleanBottomBar(
                    currentRoute = currentRoute ?: "home",
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo("home") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}
