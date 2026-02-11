package com.rohit.secondarycontacts.ui.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberCallHandler(): (String) -> Unit {
    val context = LocalContext.current
    var pendingNumber by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && pendingNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$pendingNumber"))
            context.startActivity(intent)
        }
    }

    return remember(context) {
        { number: String ->
            if (number.isNotEmpty()) {
                pendingNumber = number
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
                    context.startActivity(intent)
                } else {
                    permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }
            }
        }
    }
}
