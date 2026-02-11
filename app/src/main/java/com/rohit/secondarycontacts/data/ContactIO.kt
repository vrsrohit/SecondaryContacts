package com.rohit.secondarycontacts.data

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

object ContactIO {

    fun exportToCsv(contacts: List<Contact>, outputStream: OutputStream) {
        outputStream.bufferedWriter().use { writer ->
            writer.write("Name,PhoneNumber,Group,IsFavorite")
            writer.newLine()
            contacts.forEach { contact ->
                val name = contact.name.replace("\"", "\"\"")
                val phone = contact.phoneNumber.replace("\"", "\"\"")
                val group = contact.group.replace("\"", "\"\"")
                val fav = if (contact.isFavorite) "1" else "0"
                writer.write("\"$name\",\"$phone\",\"$group\",$fav")
                writer.newLine()
            }
        }
    }

    fun importFromCsv(inputStream: InputStream): List<Contact> {
        val contacts = mutableListOf<Contact>()
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            // Skip header
            reader.readLine()
            var line = reader.readLine()
            while (line != null) {
                val parts = parseCsvLine(line)
                if (parts.size >= 2) {
                    contacts.add(
                        Contact(
                            name = parts[0],
                            phoneNumber = parts[1],
                            group = parts.getOrElse(2) { "" },
                            isFavorite = parts.getOrElse(3) { "0" } == "1"
                        )
                    )
                }
                line = reader.readLine()
            }
        }
        return contacts
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = false
                    }
                } else {
                    current.append(c)
                }
            } else {
                when (c) {
                    '"' -> inQuotes = true
                    ',' -> {
                        result.add(current.toString())
                        current = StringBuilder()
                    }
                    else -> current.append(c)
                }
            }
            i++
        }
        result.add(current.toString())
        return result
    }

    fun exportToVCard(contacts: List<Contact>, outputStream: OutputStream) {
        outputStream.bufferedWriter().use { writer ->
            contacts.forEach { contact ->
                writer.write("BEGIN:VCARD")
                writer.newLine()
                writer.write("VERSION:3.0")
                writer.newLine()
                writer.write("FN:${contact.name}")
                writer.newLine()
                writer.write("TEL;TYPE=CELL:${contact.phoneNumber}")
                writer.newLine()
                if (contact.group.isNotEmpty()) {
                    writer.write("CATEGORIES:${contact.group}")
                    writer.newLine()
                }
                writer.write("END:VCARD")
                writer.newLine()
            }
        }
    }

    fun importFromVCard(inputStream: InputStream): List<Contact> {
        val contacts = mutableListOf<Contact>()
        var name = ""
        var phone = ""
        var group = ""

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line = reader.readLine()
            while (line != null) {
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("FN:") -> name = trimmed.removePrefix("FN:")
                    trimmed.startsWith("TEL") && trimmed.contains(":") -> {
                        phone = trimmed.substringAfter(":")
                    }
                    trimmed.startsWith("CATEGORIES:") -> group = trimmed.removePrefix("CATEGORIES:")
                    trimmed == "END:VCARD" -> {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            contacts.add(Contact(name = name, phoneNumber = phone, group = group))
                        }
                        name = ""
                        phone = ""
                        group = ""
                    }
                }
                line = reader.readLine()
            }
        }
        return contacts
    }
}
