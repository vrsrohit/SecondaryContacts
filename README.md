# Secondary Contacts

A lightweight Android contacts app built entirely with **Jetpack Compose** and **Material 3**. Designed as a standalone, private phonebook — your contacts stay on-device in a local Room database, separate from the system contacts.

## Features

### Dialer
- Full T9 keypad with haptic feedback
- Live phone number formatting (US)
- T9 name search and phone number matching with highlighted results
- Recently contacted list with quick-call support
- Visually disabled call button when input is empty

### Contacts
- Add, edit, and delete contacts with undo support
- Search contacts by name or phone number
- Group filtering (Family, Work, Friends, Other)
- Swipe-to-call gesture on contact cards
- Import/export contacts via **CSV** and **vCard (.vcf)**

### Favorites
- Star any contact to pin it to the Favorites tab
- Same call, edit, and delete actions as the main list

### UI/UX
- Material 3 design with a branded green color palette
- Light and dark theme support
- Animated tab transitions (Crossfade)
- Empty states with guidance text and CTAs
- Snackbar feedback with undo on delete
- Contact photo support
- Accessibility content descriptions throughout

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose, Material 3 |
| Navigation | Compose Navigation |
| Database | Room (SQLite) |
| Architecture | MVVM (ViewModel + StateFlow) |
| Language | Kotlin |
| Build | Gradle (KTS), KSP |

## Project Structure

```
app/src/main/java/com/rohit/secondarycontacts/
├── data/
│   ├── Contact.kt            # Room entity
│   ├── ContactDao.kt         # Database queries
│   ├── ContactDatabase.kt    # Room database + migrations
│   └── ContactIO.kt          # CSV & vCard import/export
├── navigation/
│   └── NavGraph.kt           # Compose navigation routes
├── ui/
│   ├── components/
│   │   ├── ContactItem.kt    # Reusable contact card
│   │   └── DialerButton.kt   # Keypad button component
│   ├── screens/
│   │   ├── HomeScreen.kt     # Bottom nav + tab host
│   │   ├── DialerScreen.kt   # Dialer with T9 search & recents
│   │   ├── ContactsScreen.kt # Contact list with search & groups
│   │   ├── FavoritesScreen.kt
│   │   ├── AddContactScreen.kt
│   │   └── EditContactScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── util/
│       └── CallHandler.kt    # Shared call intent helper
├── viewmodel/
│   └── ContactViewModel.kt   # App-wide ViewModel
├── MainActivity.kt
└── SecondaryContactsApp.kt   # Application class
```

## Requirements

- Android Studio Hedgehog or newer
- Min SDK 26 (Android 8.0)
- Target SDK 34 (Android 14)

## Build & Run

```bash
# Clone the repo
git clone https://github.com/rohitvundigala/SecondaryContacts.git
cd SecondaryContacts

# Build the debug APK
./gradlew assembleDebug

# Install on a connected device or emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Permissions

| Permission | Purpose |
|-----------|---------|
| `CALL_PHONE` | Initiate phone calls from the dialer and contact cards |

## License

This project is open source and available under the [MIT License](LICENSE).
