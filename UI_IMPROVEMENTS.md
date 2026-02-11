# UI Improvements Backlog

## Completed
- ~~**Delete confirmation dialog**~~ — AlertDialog before delete in ContactItem + EditContactScreen
- ~~**Form validation errors**~~ — isError + supportingText on name/phone fields in Add/Edit screens
- ~~**Material 3 SearchBar**~~ — Replaced OutlinedTextField with DockedSearchBar
- ~~**Animations**~~ — Crossfade tab transitions in HomeScreen, AnimatedVisibility for empty/list states
- ~~**Empty states with guidance**~~ — Icon + descriptive text + CTA button in ContactsScreen and FavoritesScreen
- ~~**Snackbar feedback**~~ — Delete with Undo, import/export count feedback in ContactsScreen and FavoritesScreen

## High Priority
- ~~**No haptic feedback**~~ — Added `performHapticFeedback()` to DialerButton, call button, and backspace
- ~~**Phone number not formatted**~~ — Dialer display now uses `PhoneNumberUtils.formatNumber()` for formatted output
- ~~**No unsaved changes warning**~~ — Discard confirmation dialog on back button in Add/Edit screens when form has changes
- ~~**Hardcoded colors**~~ — Replaced `Color(0xFFFFC107)` with `MaterialTheme.colorScheme.tertiary`, `Color.White` with `MaterialTheme.colorScheme.onPrimary`

## Medium Priority
- ~~**Swipe gesture not discoverable**~~ — Added "Swipe right to call" hint text below phone number in ContactItem
- ~~**4 action buttons too crowded**~~ — Moved Edit + Delete into overflow menu (3-dot), keeping Star + Call as direct buttons
- ~~**Avatar too small**~~ — Increased from 40dp to 48dp with adjusted padding
- ~~**No loading/skeleton states**~~ — Added CircularProgressIndicator while EditContactScreen loads contact data
- ~~**Accessibility gaps**~~ — Content descriptions now include contact names (e.g., "Call John Smith", "Remove John Smith from favorites")
- ~~**Dialer call button not visually disabled**~~ — Button dims to 40% opacity with faded icon when phone number is empty
- ~~**Incomplete typography scale**~~ — All 15 Material 3 text styles defined in Type.kt (display, headline, title, body, label)
- ~~**Duplicate code**~~ — Extracted `rememberCallHandler()` composable in `ui/util/CallHandler.kt`, used by all 3 screens

## Nice to Have
- ~~**Dynamic color may clash**~~ — Disabled Material You dynamic colors by default; green-branded color scheme used instead
- ~~**No long-press context menu**~~ — Long-press on contact card opens overflow menu (Edit/Delete)
- ~~**No T9 match highlighting**~~ — Matching T9 name chars and phone digits highlighted in primary color with bold
- ~~**App theme is generic**~~ — Branded with green primary color palette for light and dark themes
- ~~**FavoritesScreen missing TopAppBar**~~ — Added TopAppBar with "Favorites" title
- ~~**Dialer layout**~~ — Reverted to `Arrangement.Bottom` with keypad pinned at bottom; added recently contacted list above keypad with empty state
- ~~**Dialer button secondary text**~~ — Increased from 10sp to 12sp for readability
- ~~**Card elevation too low**~~ — Increased from 1dp to 2dp for better visual depth
- ~~**No preview functions**~~ — Added Light/Dark theme @Preview composables in Theme.kt
