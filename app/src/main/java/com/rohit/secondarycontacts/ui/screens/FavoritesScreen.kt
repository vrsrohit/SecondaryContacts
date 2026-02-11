package com.rohit.secondarycontacts.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rohit.secondarycontacts.data.Contact
import com.rohit.secondarycontacts.ui.components.ContactItem
import com.rohit.secondarycontacts.ui.util.rememberCallHandler
import com.rohit.secondarycontacts.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: ContactViewModel,
    onEditContact: (Int) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val callHandler = rememberCallHandler()
    var lastDeletedContact by remember { mutableStateOf<Contact?>(null) }

    fun deleteWithSnackbar(contact: Contact) {
        lastDeletedContact = contact
        viewModel.deleteContact(contact)
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "${contact.name} deleted",
                actionLabel = "Undo"
            )
            if (result == SnackbarResult.ActionPerformed) {
                lastDeletedContact?.let { viewModel.importContacts(listOf(it)) }
            }
            lastDeletedContact = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Favorites") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = favorites.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No favorite contacts yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the star icon on any contact\nto add them to your favorites",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = favorites.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(favorites, key = { it.id }) { contact ->
                        ContactItem(
                            contact = contact,
                            onCall = {
                                viewModel.markContactCalled(contact)
                                callHandler(contact.phoneNumber)
                            },
                            onEdit = { onEditContact(contact.id) },
                            onDelete = { deleteWithSnackbar(contact) },
                            onToggleFavorite = { viewModel.toggleFavorite(contact) }
                        )
                    }
                }
            }
        }
    }
}
