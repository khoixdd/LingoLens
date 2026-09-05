# LingoLens

LingoLens is an Android vocabulary-learning app that helps learners turn English they encounter in everyday life into vocabulary they can save, review, and practice.

The app combines OCR, vocabulary management, flashcards, quizzes, translation, progress tracking, and gamification in one learning experience.

> **See → Scan → Save → Review → Master**

## Highlights

- Scan English text with the camera using OCR
- Import text from gallery images
- Save and organize vocabulary in a personal notebook
- View definitions, pronunciation, examples, and word details
- Practice with flashcards and multiple-choice quizzes
- Translate between English and Vietnamese
- Track daily goals, streaks, XP, levels, and weekly activity
- Unlock achievements and receive celebration feedback
- Customize a profile with preset avatars
- Compare progress through a community leaderboard
- Configure study reminders and notifications

## Screens

LingoLens is organized around five main areas:

- **Home** — daily goal, streak, XP, weekly activity, and quick actions
- **Scan** — camera and gallery OCR for capturing vocabulary
- **Learn** — notebook, review, quiz, mastery status, and statistics
- **Community** — XP-based learner leaderboard
- **Profile** — personalization, achievements, translator, notifications, and settings

## Core Learning Flow

```text
Real-world text
      ↓
Camera / Gallery
      ↓
OCR
      ↓
Vocabulary enrichment
      ↓
Save to Notebook
      ↓
Review / Quiz
      ↓
Mastery & progress
```

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, Unidirectional Data Flow |
| Navigation | Jetpack Navigation 3 |
| Dependency Injection | Hilt |
| Local Database | Room |
| Authentication | Firebase Authentication |
| Cloud Data | Cloud Firestore |
| Camera | CameraX |
| OCR | Google ML Kit Text Recognition |
| Language Identification | Google ML Kit |
| Translation | Google ML Kit Translation |
| Networking | Retrofit, OkHttp |
| Background Tasks | WorkManager |
| State Management | Coroutines, Flow, StateFlow |
| Text-to-Speech | Android TextToSpeech |

## Architecture

The application uses a layered architecture that separates UI, business rules, and data access.

```text
Navigation Destination
        ↓
       Route
        ↓
     ViewModel
        ↓
   StateFlow<UiState>
        ↓
      Screen
```

User actions flow back through the ViewModel to repository interfaces and data sources:

```text
Screen
  ↓
Action
  ↓
ViewModel
  ↓
Repository
  ↓
Room / Firebase / APIs / Android services
```

Main package structure:

```text
com.example.lingolens/
├── feature/       # Screens, routes, ViewModels, UI state and actions
├── navigation/    # Navigation destinations and app navigation
├── domain/        # Models, repository contracts and shared logic
├── data/          # Repository implementations and data sources
├── di/            # Hilt dependency modules
├── notification/  # Reminder scheduling and notifications
└── ui/            # Theme and reusable UI components
```

## Main Features

### Scan and OCR

LingoLens supports both live camera capture and gallery images. Recognized English text can be processed into vocabulary entries and enriched with dictionary information before being saved.

### Vocabulary Notebook

Saved words are stored locally and can be searched, filtered, favorited, reviewed, and opened for detailed information including pronunciation and examples.

### Review and Quiz

Flashcards support repeated front/back flipping and self-assessment through **Again**, **Hard**, **Good**, and **Easy** ratings.

Quiz mode generates multiple-choice questions from saved vocabulary and provides immediate feedback and a result screen.

### Progress and Gamification

Learning activity is reflected through:

- Daily vocabulary goals
- XP and levels
- Learning streaks
- Weekly activity
- Vocabulary mastery
- Statistics
- Achievements
- Celebration effects for milestones

### Profile and Community

Users can edit their display name, select from preset avatars, view personal learning statistics, and compare XP with other learners on the leaderboard.

### Translator

A dedicated translator provides English–Vietnamese translation separately from the OCR vocabulary-saving workflow.

## Requirements

- Android Studio
- Android SDK
- Android 7.0 / API 24 or newer
- Internet connection for cloud and online features

## Getting Started

Clone the repository:

```bash
git clone https://github.com/khoixdd/LingoLens.git
cd LingoLens
```

Open the project in Android Studio, allow Gradle to sync, then run the app on an emulator or Android device.

To build from the command line on Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

To run unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

## Design

LingoLens uses a light educational visual style built around:

- green and mint accents
- white card surfaces
- rounded components
- restrained elevation
- clear information hierarchy
- consistent reusable Compose components
- visual feedback for progress and achievements

The Scan interface intentionally uses a darker camera-focused presentation for better contrast over the live preview.

## Team

Developed by students from the **Advanced Program in Computer Science** at the **University of Science, VNU-HCM** for the course **Mobile Device Application Development**.

| Member | Student ID |
|---|---:|
| Nguyen Chanh Chuong | 24125005 |
| Huynh Nguyen Khanh Duy | 24125007 |
| Dang Tran Tuan Khoi | 24125034 |
| Vo Nguyen Minh Triet | 24125082 |

## Project Status

LingoLens was developed as a university final project. The repository demonstrates a complete Android vocabulary-learning workflow integrating device capabilities, local persistence, cloud services, and modern Jetpack Compose architecture.
