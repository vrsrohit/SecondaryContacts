package com.rohit.secondarycontacts.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Int): Contact?

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchContacts(query: String): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE phoneNumber LIKE '%' || :digits || '%' ORDER BY name ASC")
    fun searchByPhone(digits: String): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteContacts(): Flow<List<Contact>>

    @Query("UPDATE contacts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM contacts WHERE `group` = :group ORDER BY name ASC")
    fun getContactsByGroup(group: String): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<Contact>)

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM contacts WHERE lastCalledAt IS NOT NULL ORDER BY lastCalledAt DESC LIMIT :limit")
    fun getRecentlyContacted(limit: Int = 10): Flow<List<Contact>>

    @Query("UPDATE contacts SET lastCalledAt = :timestamp WHERE id = :id")
    suspend fun markCalled(id: Int, timestamp: Long)
}
