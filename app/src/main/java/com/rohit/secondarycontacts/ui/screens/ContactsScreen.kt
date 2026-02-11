package com.rohit.secondarycontacts.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rohit.secondarycontacts.data.Contact
import com.rohit.secondarycontacts.data.ContactIO
import com.rohit.secondarycontacts.ui.components.ContactItem
import com.rohit.secondarycontacts.ui.util.rememberCallHandler
import com.rohit.secondarycontacts.viewmodel.ContactViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ContactViewModel,
    onAddContact: () -> Unit,
    onEditContact: (Int) -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val callHandler = rememberCallHandler()
    var showMenu by remember { mutableStateOf(false) }
    var lastDeletedContact by remember { mutableStateOf<Contact?>(null) }

    fun deleteWithSnackbar(contact: Contact) {
        lastDeletedContact = contact
        viewModel.deleteContact(contact)
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "${contact.name} deleted",
                actionLabel = "Undo"
            )
            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                lastDeletedContact?.let { viewModel.importContacts(listOf(it)) }
            }
            lastDeletedContact = null
        }
    }

    // Export CSV
    val exportCsvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            scope.launch {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        ContactIO.exportToCsv(contacts, stream)
                    }
                }
                snackbarHostState.showSnackbar("${contacts.size} contacts exported to CSV")
            }
        }
    }

    // Export vCard
    val exportVCardLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/vcard")
    ) { uri ->
        uri?.let {
            scope.launch {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        ContactIO.exportToVCard(contacts, stream)
                    }
                }
                snackbarHostState.showSnackbar("${contacts.size} contacts exported to vCard")
            }
        }
    }

    // Import CSV
    val importCsvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                val imported = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        ContactIO.importFromCsv(stream)
                    } ?: emptyList()
                }
                viewModel.importContacts(imported)
                snackbarHostState.showSnackbar("${imported.size} contacts imported from CSV")
            }
        }
    }

    // Import vCard
    val importVCardLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                val imported = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        ContactIO.importFromVCard(stream)
                    } ?: emptyList()
                }
                viewModel.importContacts(imported)
                snackbarHostState.showSnackbar("${imported.size} contacts imported from vCard")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddContact) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar with overflow menu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                DockedSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = { /* no-op, results update live */ },
                    active = false,
                    onActiveChange = { /* keep docked style */ },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search contacts") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        } else {
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Export CSV") },
                                        onClick = {
                                            showMenu = false
                                            exportCsvLauncher.launch("contacts.csv")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Export vCard") },
                                        onClick = {
                                            showMenu = false
                                            exportVCardLauncher.launch("contacts.vcf")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Import CSV") },
                                        onClick = {
                                            showMenu = false
                                            importCsvLauncher.launch("text/*")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Import vCard") },
                                        onClick = {
                                            showMenu = false
                                            importVCardLauncher.launch("text/*")
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { /* no suggestions content needed */ }
            }

            // Group filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ContactViewModel.GROUPS) { group ->
                    FilterChip(
                        selected = selectedGroup == group,
                        onClick = { viewModel.onGroupSelected(group) },
                        label = { Text(group) }
                    )
                }
            }

            AnimatedVisibility(
                visible = contacts.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No results for \"$searchQuery\""
                                   else if (selectedGroup != "All") "No contacts in $selectedGroup"
                                   else "No contacts yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try a different search term or clear filters"
                                   else "Tap the + button to add your first contact",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        if (searchQuery.isEmpty() && selectedGroup == "All") {
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedButton(onClick = onAddContact) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Add Contact")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = contacts.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(contacts, key = { it.id }) { contact ->
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
