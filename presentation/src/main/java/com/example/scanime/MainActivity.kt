package com.example.scanime

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.scanime.model.UiMangaModel
import com.example.scanime.navigation.FaceScreen
import com.example.scanime.navigation.HomeScreen
import com.example.scanime.navigation.LoginScreen
import com.example.scanime.navigation.MangaDetails
import com.example.scanime.navigation.mangaNavType
import com.example.scanime.ui.feature.login.LoginScreen
import com.example.scanime.ui.feature.face_detect.FaceDetectorScreen
import com.example.scanime.ui.feature.home.HomeScreen
import com.example.scanime.ui.feature.login.LoginViewModel
import com.example.scanime.ui.feature.manga_description.MangaDescriptionScreen
import com.example.scanime.ui.theme.ScanimeTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import kotlin.getValue
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScanimeTheme {
                val navController = rememberNavController()
                val viewModel: LoginViewModel = koinViewModel()

                val scanimeSession: ScanimeSession by inject()

                val shouldShowBottomNav = remember{
                    mutableStateOf(false)
                }

                val startDestination = if (scanimeSession.isSignedIn())  HomeScreen  else  LoginScreen

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(visible = shouldShowBottomNav.value) {
                            BottomNavigationBar(navController)
                        }
                    },
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                    ){
                        SharedTransitionLayout {
                            NavHost(
                                navController = navController,
                                startDestination = startDestination
                            ) {
                                composable<LoginScreen> {
                                    LoginScreen(navController, viewModel)
                                    shouldShowBottomNav.value = false
                                }
                                composable<HomeScreen> {
                                    HomeScreen(navController, this)
                                    shouldShowBottomNav.value = true
                                }
                                composable<MangaDetails>(
                                    typeMap = mapOf(typeOf<UiMangaModel>() to mangaNavType)
                                ) {
                                    val mangaRoute = it.toRoute<MangaDetails>()
                                    MangaDescriptionScreen(this, mangaRoute.manga)
                                    shouldShowBottomNav.value = false
                                }
                                composable<FaceScreen> {
                                    FaceDetectorScreen()
                                    shouldShowBottomNav.value = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.Transparent
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val items = listOf(
            BottomNavItems.Home,
            BottomNavItems.Face
        )

        items.forEach { item ->
            val isSelected = currentRoute?.substringBefore("?") == item.route::class.qualifiedName
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { startRoute ->
                            popUpTo(startRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(text = item.title) },
                icon = {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(if(isSelected)MaterialTheme.colorScheme.primary else Color.Gray)
                    )
                }, colors = NavigationBarItemDefaults.colors().copy(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}

sealed class BottomNavItems(val route: Any, val title: String, val icon: Int) {
    object Home : BottomNavItems(HomeScreen, "Manga", icon = R.drawable.article)
    object Face : BottomNavItems(FaceScreen, "Face", icon = R.drawable.face)
}