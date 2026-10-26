package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MambaViewModel
import com.example.ui.screens.LiveMatchScreen
import com.example.ui.screens.MatchSummaryScreen
import com.example.ui.screens.SquadManagementScreen
import com.example.ui.screens.TacticalFormationScreen
import com.example.ui.theme.CardRed
import com.example.ui.theme.MambaAccentMint
import com.example.ui.theme.MambaGreenDark
import com.example.ui.theme.MambaGreenPrimary
import com.example.ui.theme.MambaGreenSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureWhite

class MainActivity : ComponentActivity() {
    private val viewModel: MambaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MambaViewModel) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val activeMatch by viewModel.activeMatch.collectAsState()

    // BackHandler to return to Tab 0 if on secondary tabs
    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    val navItems = listOf(
        NavItem("Match", Icons.Filled.SportsSoccer, Icons.Outlined.SportsSoccer, "nav_match"),
        NavItem("Tactics", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_tactics"),
        NavItem("Summary", Icons.Filled.Assessment, Icons.Outlined.Assessment, "nav_summary"),
        NavItem("Squad", Icons.Filled.Groups, Icons.Outlined.Groups, "nav_squad")
    )

    Scaffold(
        containerColor = MambaGreenDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MambaGreenDark,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, MambaAccentMint),
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(4.dp, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mamba_crest),
                                contentDescription = "FC Green Mamba Crest",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FC GREEN MAMBA",
                                    color = PureWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                if (activeMatch?.isRunning == true) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = CardRed,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "LIVE",
                                            color = PureWhite,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Tactical Match Tracker & Formation Summary",
                                color = MambaAccentMint,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MambaGreenSurface,
                    titleContentColor = PureWhite
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MambaGreenSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            if (index == 0 && activeMatch?.isRunning == true) {
                                BadgedBox(badge = {
                                    Badge(containerColor = CardRed)
                                }) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MambaGreenDark,
                            selectedTextColor = MambaAccentMint,
                            indicatorColor = MambaAccentMint,
                            unselectedIconColor = PureWhite.copy(alpha = 0.7f),
                            unselectedTextColor = PureWhite.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> LiveMatchScreen(
                    viewModel = viewModel,
                    onNavigateToSummary = { selectedTab = 2 }
                )
                1 -> TacticalFormationScreen(
                    viewModel = viewModel,
                    onNavigateToMatch = { selectedTab = 0 }
                )
                2 -> MatchSummaryScreen(
                    viewModel = viewModel
                )
                3 -> SquadManagementScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
