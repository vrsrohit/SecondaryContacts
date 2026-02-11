package com.rohit.secondarycontacts.ui.screens

import android.telephony.PhoneNumberUtils
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohit.secondarycontacts.viewmodel.ContactViewModel.Companion.nameToT9
import com.rohit.secondarycontacts.ui.components.DialerButton
import com.rohit.secondarycontacts.ui.theme.Green500
import com.rohit.secondarycontacts.ui.util.rememberCallHandler
import com.rohit.secondarycontacts.viewmodel.ContactViewModel

@Composable
fun DialerScreen(viewModel: ContactViewModel) {
    var phoneNumber by remember { mutableStateOf("") }
    val view = LocalView.current
    val suggestions by viewModel.dialerSuggestions.collectAsState()
    val recentlyContacted by viewModel.recentlyContacted.collectAsState()
    val callHandler = rememberCallHandler()

    fun formatPhoneDisplay(raw: String): String {
        if (raw.isEmpty()) return " "
        val formatted = PhoneNumberUtils.formatNumber(raw, "US")
        return formatted ?: raw
    }

    val keys = listOf(
        Triple("1", "", "1"),
        Triple("2", "ABC", "2"),
        Triple("3", "DEF", "3"),
        Triple("4", "GHI", "4"),
        Triple("5", "JKL", "5"),
        Triple("6", "MNO", "6"),
        Triple("7", "PQRS", "7"),
        Triple("8", "TUV", "8"),
        Triple("9", "WXYZ", "9"),
        Triple("*", "", "*"),
        Triple("0", "+", "0"),
        Triple("#", "", "#")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Recently contacted list (fills available space above keypad)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (suggestions.isNotEmpty()) {
                // Show T9/phone suggestions when typing
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    items(suggestions) { contact ->
                        SuggestionCard(
                            contact = contact,
                            phoneNumber = phoneNumber,
                            onClick = {
                                phoneNumber = contact.phoneNumber
                                viewModel.markContactCalled(contact)
                                callHandler(phoneNumber)
                            }
                        )
                    }
                }
            } else if (phoneNumber.isEmpty()) {
                // Show recently contacted or empty state
                if (recentlyContacted.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No recent calls",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Contacts you call will appear here",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Recently contacted list — anchored to bottom of available space
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Section header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Recent",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                        }
                        LazyColumn {
                            items(recentlyContacted, key = { it.id }) { contact ->
                                RecentContactRow(
                                    contact = contact,
                                    onClick = {
                                        phoneNumber = contact.phoneNumber
                                        viewModel.markContactCalled(contact)
                                        callHandler(contact.phoneNumber)
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Phone number display
        Text(
            text = formatPhoneDisplay(phoneNumber),
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            maxLines = 1,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Keypad grid
        keys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (primary, secondary, value) ->
                    DialerButton(
                        primary = primary,
                        secondary = secondary,
                        onClick = {
                            phoneNumber += value
                            viewModel.onDialerInputChange(phoneNumber)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Call and backspace row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Empty spacer for alignment
            Spacer(modifier = Modifier.size(72.dp))

            // Call button — visually disabled when empty
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = if (phoneNumber.isNotEmpty()) Green500 else Green500.copy(alpha = 0.4f),
                onClick = {
                    if (phoneNumber.isNotEmpty()) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        callHandler(phoneNumber)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = if (phoneNumber.isNotEmpty()) "Make phone call" else "Enter a number to call",
                    tint = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (phoneNumber.isNotEmpty()) 1f else 0.5f
                    ),
                    modifier = Modifier.padding(20.dp)
                )
            }

            // Backspace
            IconButton(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    phoneNumber = phoneNumber.dropLast(1)
                    viewModel.onDialerInputChange(phoneNumber)
                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete last digit",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SuggestionCard(
    contact: com.rohit.secondarycontacts.data.Contact,
    phoneNumber: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Highlight T9-matching portion of name
                val t9 = nameToT9(contact.name)
                val matchIndex = t9.indexOf(phoneNumber)
                if (matchIndex >= 0 && phoneNumber.isNotEmpty()) {
                    // Map T9 indices back to name characters
                    var t9Pos = 0
                    var nameStart = -1
                    var nameEnd = -1
                    for (i in contact.name.indices) {
                        val ch = contact.name[i].lowercaseChar()
                        if (ch in 'a'..'z') {
                            if (t9Pos == matchIndex) nameStart = i
                            if (t9Pos == matchIndex + phoneNumber.length - 1) {
                                nameEnd = i + 1
                                break
                            }
                            t9Pos++
                        }
                    }
                    if (nameStart >= 0 && nameEnd > nameStart) {
                        Text(
                            text = buildAnnotatedString {
                                append(contact.name.substring(0, nameStart))
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                    append(contact.name.substring(nameStart, nameEnd))
                                }
                                if (nameEnd < contact.name.length) {
                                    append(contact.name.substring(nameEnd))
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Highlight matching digits in phone number
                val phoneMatchIndex = contact.phoneNumber.indexOf(phoneNumber)
                if (phoneMatchIndex >= 0 && phoneNumber.isNotEmpty()) {
                    Text(
                        text = buildAnnotatedString {
                            append(contact.phoneNumber.substring(0, phoneMatchIndex))
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                append(contact.phoneNumber.substring(phoneMatchIndex, phoneMatchIndex + phoneNumber.length))
                            }
                            if (phoneMatchIndex + phoneNumber.length < contact.phoneNumber.length) {
                                append(contact.phoneNumber.substring(phoneMatchIndex + phoneNumber.length))
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call ${contact.name}",
                tint = Green500,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RecentContactRow(
    contact: com.rohit.secondarycontacts.data.Contact,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular avatar with initial
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        // Name and phone
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
        // Call button
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Green500.copy(alpha = 0.12f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call ${contact.name}",
                tint = Green500,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
