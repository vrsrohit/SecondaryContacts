package com.rohit.secondarycontacts.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phoneNumber: String,
    @ColumnInfo(defaultValue = "0") val isFavorite: Boolean = false,
    @ColumnInfo(name = "group", defaultValue = "") val group: String = "",
    @ColumnInfo(defaultValue = "NULL") val photoUri: String? = null,
    @ColumnInfo(defaultValue = "NULL") val lastCalledAt: Long? = null
)
