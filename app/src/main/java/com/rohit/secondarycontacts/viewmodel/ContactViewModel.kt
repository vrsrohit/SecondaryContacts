package com.rohit.secondarycontacts.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohit.secondarycontacts.SecondaryContactsApp
import com.rohit.secondarycontacts.data.Contact
import com.rohit.secondarycontacts.data.ContactDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: ContactDao = (application as SecondaryContactsApp).database.contactDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGroup = MutableStateFlow("All")
    val selectedGroup: StateFlow<String> = _selectedGroup.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val baseContacts = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) dao.getAllContacts()
            else dao.searchContacts(query)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val contacts: StateFlow<List<Contact>> = combine(baseContacts, _selectedGroup) { contacts, group ->
        if (group == "All") contacts
        else contacts.filter { it.group == group }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<Contact>> = dao.getFavoriteContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyContacted: StateFlow<List<Contact>> = dao.getRecentlyContacted()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dialerInput = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val dialerSuggestions: StateFlow<List<Contact>> = _dialerInput
        .debounce(150)
        .flatMapLatest { digits ->
            if (digits.length < 2) flowOf(emptyList())
            else dao.getAllContacts().map { contacts ->
                contacts.filter { contact ->
                    contact.phoneNumber.contains(digits) ||
                        nameMatchesT9(contact.name, digits)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDialerInputChange(digits: String) {
        _dialerInput.value = digits
    }

    companion object {
        private val charToDigit = mapOf(
            'a' to '2', 'b' to '2', 'c' to '2',
            'd' to '3', 'e' to '3', 'f' to '3',
            'g' to '4', 'h' to '4', 'i' to '4',
            'j' to '5', 'k' to '5', 'l' to '5',
            'm' to '6', 'n' to '6', 'o' to '6',
            'p' to '7', 'q' to '7', 'r' to '7', 's' to '7',
            't' to '8', 'u' to '8', 'v' to '8',
            'w' to '9', 'x' to '9', 'y' to '9', 'z' to '9'
        )

        fun nameToT9(name: String): String =
            name.lowercase().mapNotNull { charToDigit[it] }.joinToString("")

        fun nameMatchesT9(name: String, digits: String): Boolean =
            nameToT9(name).contains(digits)

        val GROUPS = listOf("All", "Family", "Work", "Friends", "Other")
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onGroupSelected(group: String) {
        _selectedGroup.value = group
    }

    fun addContact(name: String, phoneNumber: String, group: String = "", photoUri: String? = null) {
        viewModelScope.launch {
            dao.insert(Contact(name = name, phoneNumber = phoneNumber, group = group, photoUri = photoUri))
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            dao.update(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            dao.delete(contact)
        }
    }

    fun toggleFavorite(contact: Contact) {
        viewModelScope.launch {
            dao.toggleFavorite(contact.id, !contact.isFavorite)
        }
    }

    fun markContactCalled(contact: Contact) {
        viewModelScope.launch {
            dao.markCalled(contact.id, System.currentTimeMillis())
        }
    }

    fun importContacts(contacts: List<Contact>) {
        viewModelScope.launch {
            dao.insertAll(contacts)
        }
    }

    suspend fun getContactById(id: Int): Contact? {
        return dao.getContactById(id)
    }
}
