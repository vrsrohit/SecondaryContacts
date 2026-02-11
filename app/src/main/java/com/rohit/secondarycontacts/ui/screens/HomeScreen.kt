package com.rohit.secondarycontacts.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.rohit.secondarycontacts.viewmodel.ContactViewModel

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Dialer", Icons.Default.Dialpad),
    BottomNavItem("Contacts", Icons.Default.Contacts),
    BottomNavItem("Favorites", Icons.Default.Favorite)
)

@Composable
fun HomeScreen(
    viewModel: ContactViewModel,
    onAddContact: () -> Unit,
    onEditContact: (Int) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
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
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(durationMillis = 250),
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    0 -> DialerScreen(viewModel = viewModel)
                    1 -> ContactsScreen(
                        viewModel = viewModel,
                        onAddContact = onAddContact,
                        onEditContact = onEditContact
                    )
                    2 -> FavoritesScreen(
                        viewModel = viewModel,
                        onEditContact = onEditContact
                    )
                }
            }
        }
    }
}
