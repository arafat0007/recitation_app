# Agent Instructions for Mother's Islamic Audio App

You are helping build an Android app for elderly Bengali-speaking users.

Before suggesting architecture, coding steps, files, dependencies, screens, or implementation order, always use these project reference documents:

@./docs/Requirements.md
@./docs/WBS.md

## Project Summary

This is a simple Android-only recitation app for the user's mother.

Main modules:
- Surah
- Doa
- Owaj

Main priorities:
- Elderly-friendly UI
- Bengali-only app UI
- Large buttons
- Simple navigation
- Portrait-only Android app
- Surah and Doa audio playback
- Bengali meaning text
- Optional Arabic text
- Favorites
- Settings
- Firestore-based dynamic content
- Offline visibility for last fetched content
- YouTube-only Owaj playback in Version 1

## Required Tech Stack

Use:
- Kotlin
- Jetpack Compose
- MVVM
- Repository pattern
- Navigation Compose
- Material 3
- Media3 / ExoPlayer
- Room
- DataStore
- Firebase Firestore
- Coil for images
- YouTube IFrame Player approach for Owaj

## Development Rule

Do not jump directly into advanced features.

Follow this coding order:
1. Project setup and dependencies
2. Package structure
3. Theme and typography
4. Reusable elderly-friendly UI components
5. Navigation graph and empty screens
6. Settings with DataStore
7. Home screen
8. Favorites with Room
9. Surah screens with sample data
10. Doa screens with sample data
11. Media3 audio playback
12. Owaj screens with sample data
13. YouTube playback
14. Firestore schema and remote fetch
15. Offline fallback
16. Error and empty states
17. UX polish and testing

## Important UI Rule

All UI labels should be Bengali.
All primary buttons should be large and easy for elderly users to tap.
Avoid icon-only important actions.
Avoid complex gestures.