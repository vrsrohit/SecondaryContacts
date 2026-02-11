package com.rohit.secondarycontacts

import android.app.Application
import com.rohit.secondarycontacts.data.ContactDatabase

class SecondaryContactsApp : Application() {
    val database: ContactDatabase by lazy {
        ContactDatabase.getDatabase(this)
    }
}
