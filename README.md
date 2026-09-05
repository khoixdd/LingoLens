# LingoLens

LingoLens is an Android vocabulary-learning application that helps learners turn real-world English text into reusable study material.

The core learning flow is:

**See → Scan → Save → Review → Master**

Users can capture text with the camera or select an image from the gallery, recognize English text using OCR, save vocabulary, review words with flashcards, practice with quizzes, and track progress through gamification features such as XP, levels, streaks, daily goals, weekly activity, and achievements.

---

## Features

### Authentication & Onboarding
- Branded splash screen on every app launch
- First-use onboarding flow
- Email/password registration and login
- Google Sign-In
- Firebase Authentication
- Persistent authentication state

### Scan & Vocabulary Capture
- CameraX live camera preview
- Camera-based OCR
- Gallery image selection
- ML Kit Text Recognition
- ML Kit Language Identification for gallery OCR
- Dictionary enrichment using the Free Dictionary API
- Save detected vocabulary to the local database

### Translator
- Dedicated English ↔ Vietnamese translator screen
- Translation implemented as a separate feature from the OCR-to-vocabulary pipeline

### Notebook
- Persistent vocabulary collection with Room
- Search, filter, and sort
- Favorite vocabulary
- Manual vocabulary entry
- Vocabulary detail screen
- Text-to-Speech pronunciation
- Delete and edit supported vocabulary data

### Review
- Flashcard-based review
- Bidirectional 3D card flip
- Ratings:
  - Again
  - Hard
  - Good
  - Easy
- Mastery and review-history updates
- Review XP rewards
- Duplicate reward protection

### Quiz
- Multiple-choice questions generated from saved vocabulary
- Correct/incorrect answer feedback
- Persistent learning-stat updates
- XP rewards
- Duplicate completion protection
- Animated Quiz Result screen
- Confetti celebration per unique quiz attempt

### Gamification
- XP system
- Level calculation
- Learning streaks
- Daily Goal
- Weekly activity
- Mastery tracking
- Statistics dashboard
- Achievement system
- Achievement unlock celebration

Current achievement set:
- Goal Getter
- On Fire
- Week Warrior
- Rising Star
- XP Hunter
- Leveling Up
- Word Collector
- Vocabulary Builder

### Profile & Community
- Edit display name
- 8 preset avatar options
- Persistent avatar selection
- XP-based leaderboard
- Current-user highlight
- Profile statistics
- Navigation to Achievements, Statistics, Notifications, Translator, and Privacy settings

### Notifications
- Per-user notification preferences
- Daily reminders
- Daily Goal reminders
- Review reminders
- Achievement notifications
- Streak alerts
- Configurable reminder time
- WorkManager scheduling
- Android 13+ runtime notification permission handling

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navigation:** Jetpack Navigation 3
- **Architecture:** MVVM + Unidirectional Data Flow
- **Dependency Injection:** Hilt
- **Local Storage:** Room
- **Authentication:** Firebase Authentication
- **Cloud Data:** Cloud Firestore
- **Camera:** CameraX
- **OCR:** Google ML Kit Text Recognition
- **Language Detection:** Google ML Kit Language Identification
- **Translation:** ML Kit translation layer
- **Networking:** Retrofit + OkHttp
- **Dictionary Data:** Free Dictionary API
- **Background Work:** WorkManager
- **Location:** Fused Location Provider
- **State Management:** Kotlin Coroutines + StateFlow

---

## Architecture

The project follows a layered architecture:

```text
com.example.lingolens/
├── feature/       # Route, Screen, ViewModel, UiState, Action
├── navigation/    # Navigation 3 destinations and back stack
├── domain/        # Models, repository contracts, shared logic
├── data/          # Room, Firebase, Retrofit implementations
├── di/            # Hilt modules
├── notification/  # Reminder scheduling and notifications
└── ui/            # Theme and reusable Compose components
```

Typical feature flow:

```text
Navigation Destination
        ↓
       Route
        ↓
   Hilt ViewModel
        ↓
 StateFlow<UiState>
        ↓
      Screen
```

User actions flow in the opposite direction:

```text
Screen
  ↓ Action
ViewModel
  ↓
Repository
  ↓
Room / Firebase / REST API / Android Services
```

This keeps Compose screens independent from repositories, Firebase, and navigation internals.

---

## Main Navigation

The five top-level destinations are:

- Home
- Scan
- Learn
- Community
- Profile

Important child destinations include:

- Notebook
- Vocabulary Detail
- Review
- Quiz
- Quiz Result
- Statistics
- Achievements
- Edit Profile
- Notification Settings
- Translator
- Location & Privacy

---

## Startup Flow

```text
App Launch
   ↓
Splash
   ↓
Onboarding completed?
├── No  → Onboarding → Login
└── Yes
      ↓
Authenticated?
├── Yes → Home
└── No  → Login
```

The splash screen appears on every launch, while onboarding is shown only for first-time use.

---

## Data & Persistence

### Vocabulary
Room is used as the local source of truth for vocabulary.

Vocabulary data includes:
- word
- meaning
- pronunciation
- part of speech
- example
- tags
- favorite state
- mastery state
- review timestamps
- correct/incorrect counters

Vocabulary is scoped per authenticated user.

### Daily Activity
Daily Goal progress uses the composite identity:

```text
(userId, epochDay, vocabularyId)
```

This ensures the same vocabulary item counts only once per day.

### Database Migration
The current Room database uses explicit migrations:

```text
1 → 2
2 → 3
```

Destructive migration is not used in the final configuration.

---

## Gamification Rules

### Level
```text
Level = floor(XP / 200) + 1
```

### Daily Goal
```text
10 unique vocabulary items per day
```

### Review XP
```text
+5 XP per accepted review rating
```

### Quiz XP
```text
20 base XP + 10 XP × correct answers
```

---

## Build Requirements

- Android Studio
- Android SDK with API 24+
- JDK compatible with the project Gradle configuration
- Internet connection for Firebase, dictionary, and translation features

Minimum supported Android version:

```text
minSdk = 24
```

---

## Build & Run

### Windows PowerShell

Build debug APK:

```powershell
.\gradlew.bat :app:assembleDebug --no-daemon
```

Install on a connected Android device:

```powershell
.\gradlew.bat :app:installDebug
```

Run unit tests:

```powershell
.\gradlew.bat testDebugUnitTest --no-daemon
```

Run Android instrumentation tests:

```powershell
.\gradlew.bat connectedDebugAndroidTest --no-daemon
```

Run Android lint:

```powershell
.\gradlew.bat :app:lintDebug --no-daemon
```

Check Git whitespace issues:

```powershell
git diff --check
```

---

## Verification Status

Latest verified integration branch results:

- `:app:assembleDebug` — **PASS**
- `testDebugUnitTest` — **12 tests passed**
- `git diff --check` — **PASS**

Merge/integration commit:

```text
66fd077776db09051a5fb4bc38dc5ea223d7a66a
```

Instrumentation and lint results should only be reported as passed after they are run successfully on the final merged commit.

---

## UI Design

LingoLens uses a consistent light educational design system with:

- green primary color
- pale mint surfaces
- white cards
- dark green text
- rounded corners
- low elevation
- subtle borders
- reusable Compose components
- responsive layouts
- gamification feedback and celebrations

The Scan screen intentionally uses a darker camera-oriented interface for contrast over the live preview.

---

## Current Limitations

- Translator is a separate feature and is not automatically chained into the OCR-to-vocabulary saving flow.
- Nearby Learners is not part of the active Community screen.
- Location & Privacy is not a complete production-ready location-sharing experience.
- Review scheduling uses simplified intervals instead of a full adaptive spaced-repetition algorithm.

---

## Team

| Student | Student ID |
|---|---:|
| Nguyen Chanh Chuong | 24125005 |
| Huynh Nguyen Khanh Duy | 24125007 |
| Dang Tran Tuan Khoi | 24125034 |
| Vo Nguyen Minh Triet | 24125082 |

---

## Repository

```bash
git clone https://github.com/khoixdd/LingoLens.git
```

---

## Project Context

**Course:** Mobile Device Application Development  
**University:** University of Science, VNU-HCM  
**Program:** Advanced Program in Computer Science  
**Final Project:** LingoLens  
**Academic Year:** 2026
