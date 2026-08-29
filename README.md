# 📱 Helpdesk Android Application

A modern, native Android client for the AI-Powered Helpdesk Ticket Management System, built with **Kotlin 2.0**, **Jetpack Compose (Material 3)**, and **Clean Architecture**.

---

## 📋 Table of Contents

- [🏛️ Architecture & Project Structure](#-architecture--project-structure)
- [🛠️ Tech Stack & Dependencies](#-tech-stack--dependencies)
- [✨ Key Features](#-key-features)
- [🚀 Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Step 1: Start Backend Server](#step-1-start-the-local-backend-server)
  - [Step 2: Run Android App](#step-2-run-the-android-application)
- [🔑 Default Accounts & Access](#-default-accounts--access)
- [🧪 Build & Test Commands](#-build--test-commands)
- [🛡️ Security & Hardening](#-security--hardening)

---

## 🏛️ Architecture & Project Structure

The application strictly follows Android Clean Architecture principles, ensuring modularity, testability, and separation of concerns across distinct layers:

```
android/
├── gradlew                           # POSIX Gradle wrapper (macOS/Linux)
├── gradlew.bat                       # Windows batch Gradle wrapper
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml           # App manifest & permissions
│       │   ├── res/
│       │   │   ├── xml/network_security_config.xml # Strict TLS & local dev domain configuration
│       │   │   ├── xml/backup_rules.xml      # Excludes session secrets from cloud backup
│       │   │   ├── xml/data_extraction_rules.xml # Excludes auth data from device transfer
│       │   │   └── ...                       # Drawables, mipmaps, strings, and theme values
│       │   └── java/com/helpdesk/app/
│       │       ├── HelpdeskApplication.kt    # Application entry point & Koin DI initialization
│       │       ├── MainActivity.kt           # Single-activity Jetpack Compose host
│       │       │
│       │       ├── core/                     # Cross-cutting concerns & foundational infrastructure
│       │       │   ├── di/AppModule.kt       # Koin dependency injection module declarations
│       │       │   ├── network/              # Retrofit API, OkHttp client, CookieJar & Interceptors
│       │       │   │   ├── HelpdeskApiService.kt
│       │       │   │   ├── NetworkClient.kt
│       │       │   │   └── SessionCookieJar.kt
│       │       │   ├── datastore/SessionManager.kt # Preferences DataStore for auth & host configuration
│       │       │   ├── result/               # Resource<T> & AppError handling hierarchy
│       │       │   ├── theme/                # Material 3 Color scheme, Typography, Shapes & Theme
│       │       │   └── util/DateTimeUtils.kt # Thread-safe ISO 8601 date-time parsing & relative formatters
│       │       │
│       │       ├── domain/                   # Pure Kotlin Business Layer (Zero Android Framework imports)
│       │       │   ├── model/                # Ticket, User, Reply, Stats, Customer, Enums
│       │       │   ├── repository/           # AuthRepository, TicketRepository, UserRepository interfaces
│       │       │   └── usecase/              # Focused single-responsibility UseCases (operator fun invoke)
│       │       │       ├── auth/             # LoginUseCase, LogoutUseCase, GetCurrentUserUseCase, etc.
│       │       │       ├── ticket/           # GetTicketsUseCase, CreateReplyUseCase, PolishReplyUseCase, SummarizeTicketUseCase, etc.
│       │       │       └── user/             # GetUsersUseCase, CreateUserUseCase, UpdateUserUseCase, DeleteUserUseCase
│       │       │
│       │       ├── data/                     # Data Layer (Remote API & Local Persistence)
│       │       │   ├── remote/dto/           # Kotlinx Serialization Data Transfer Objects matching backend API
│       │       │   ├── mapper/Mappers.kt     # Bidirectional DTO ↔ Domain model mappers
│       │       │   └── repository/           # Repository implementations with SafeApiCall error handling
│       │       │       ├── AuthRepositoryImpl.kt
│       │       │       ├── TicketRepositoryImpl.kt
│       │       │       └── UserRepositoryImpl.kt
│       │       │
│       │       └── presentation/             # Presentation Layer (Jetpack Compose UI & State Management)
│       │           ├── common/               # Reusable UI components (StatusBadge, CategoryBadge, StatCard, EmptyState)
│       │           ├── navigation/           # AppNavigation graph, NavigationRoutes, and BottomNavigationBar
│       │           ├── auth/                 # LoginScreen & LoginViewModel (with Server Host selection modal)
│       │           ├── dashboard/            # DashboardScreen & DashboardViewModel (KPI metrics & 30-Day volume chart)
│       │           ├── tickets/
│       │           │   ├── list/             # TicketListScreen & TicketListViewModel (Search, Filters, Pull-to-refresh)
│       │           │   ├── create/           # CreateTicketBottomSheet dialog
│       │           │   └── detail/           # TicketDetailScreen, AISummaryCard, ReplySection, ReplyComposer modal
│       │           └── users/                # UsersScreen & UsersViewModel (Admin team directory & CRUD dialogs)
│       │
│       └── test/                             # JVM Unit Tests (Robolectric / Coroutines Test)
│           └── java/com/helpdesk/app/
│               ├── DateTimeUtilsTest.kt      # Validates ISO timestamp parsing and relative time calculations
│               ├── MappersTest.kt            # Validates DTO-to-Domain mapping consistency
│               ├── AuthUseCasesTest.kt       # Validates authentication rules and credentials formatting
│               ├── TicketUseCasesTest.kt     # Validates ticket creation, reply character limits, and status transitions
│               └── UserUseCasesTest.kt       # Validates role permissions and user CRUD rules
```

---

## 🛠️ Tech Stack & Dependencies

| Category | Technology / Library | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Kotlin | `2.0.0` | Primary language with Kotlin 2.0 Compose compiler |
| **UI Framework** | Jetpack Compose | BOM `2024.06.00` | Declarative UI toolkit |
| **Design System** | Material Design 3 | `1.2.1` | Modern Material 3 components, dynamic color, typography |
| **Navigation** | Navigation Compose | `2.7.7` | Type-safe in-app screen routing and bottom navigation |
| **Dependency Injection** | Koin | `3.5.6` | Lightweight pragmatic DI for Android & ViewModels |
| **Networking** | Retrofit 2 & OkHttp 3 | `2.11.0` / `4.12.0` | REST API communication, logging, custom cookie jar |
| **Serialization** | Kotlinx Serialization | `1.6.3` | JSON parsing & DTO serialization |
| **Async & Concurrency**| Kotlin Coroutines & Flow | `1.8.1` | Asynchronous operations and reactive state management |
| **Local Storage** | Jetpack DataStore Preferences | `1.1.1` | Asynchronous key-value session and configuration storage |
| **Testing** | JUnit 4 & Coroutines Test | `4.13.2` / `1.8.1` | Unit testing business logic, use cases, and mappers |
| **Build System** | Android Gradle Plugin (AGP) | `8.4.2` | Gradle build tools targeting SDK 34 (Min SDK 26) |

---

## ✨ Key Features

### 🔐 Multi-Transport Session Authentication
- Sign in with work email and password.
- Multi-transport session resilience: credentials persisted via **Better-Auth** session cookies (`SessionCookieJar`) and token persistence (`SessionManager` DataStore).
- Dynamic Server Host configuration allowing on-the-fly switching between **Android Emulator** (`http://10.0.2.2:3000/`), **USB ADB Reverse** (`http://127.0.0.1:3000/`), or **Cloud Production** without recompiling.

### 📊 Analytics & Insights Dashboard
- Real-time KPI summary cards: **Total Tickets**, **Open Tickets**, **AI-Resolved %**, and **Average Resolution Time**.
- Interactive **30-Day Ticket Volume** distribution bar chart.
- Quick navigation shortcuts: tap any metric card to jump directly to filtered ticket views.

### 🎫 Ticket Management & Workflow
- **Search & Filter**: Search tickets across subject, description, or customer information. Filter by Status (*Open*, *Processing*, *Resolved*, *Closed*), Category (*General Question*, *Technical Question*, *Refund Request*), and Source (*Web*, *Email*).
- **Sorting & Refresh**: Sort by date, subject, or status. Full pull-to-refresh and pagination support.
- **Ticket Creation**: Modal sheet for creating support tickets with automatic customer association.

### 🤖 AI-Powered Capabilities
- **AI Ticket Summarization**: Instant multi-point summaries generated directly from the ticket thread.
- **AI Reply Polish**: Improve agent drafts into empathetic, structured, and professional customer responses with live preview and tone adjustment.

### 💬 Conversation Thread & Reply Composer
- Chronological message history clearly distinguishing Agent responses from Customer messages.
- Rich reply composer with live AI polish previews before sending.
- Admin status and assignee management sheet.

### 👥 Team & User Management (Admin Only)
- Team directory with role badges (**Admin** vs **Agent**).
- Full user CRUD operations: invite new team members, edit roles/names, and soft-delete accounts.
- Built-in role guards protecting administrative routes.

---

## 🚀 Getting Started

### Prerequisites
- **JDK 17** installed and configured (`JAVA_HOME`).
- **Android Studio** (Koala, Ladybug, Iguana or newer).
- **Android SDK 34** (installed via Android Studio SDK Manager).
- Physical Android device (with Developer Options & USB Debugging enabled) or Android Virtual Device (AVD).

### Step 1: Start the Backend Server
Ensure the backend server is running on port `3000`:
```bash
cd server
bun dev
```

### Step 2: Run the Android Application

#### Option A: Running on Android Studio Emulator (Default)
1. Start your Android Emulator in Android Studio.
2. Build, install, and launch the application:
   ```bash
   # macOS / Linux
   ./gradlew installDebug

   # Windows
   .\gradlew.bat installDebug
   ```
3. The app defaults to `http://10.0.2.2:3000/` which connects directly to your local development backend.

#### Option B: Running on a Physical Android Device (over USB)
1. Connect your Android device via USB cable and verify connection:
   ```bash
   adb devices
   ```
2. Forward port `3000` to your device:
   ```bash
   adb reverse tcp:3000 tcp:3000
   ```
3. On the login screen, tap the **Server** pill at the bottom, select **USB Local (`http://127.0.0.1:3000/`)**, and tap **Save Configuration**.

---

## 🔑 Default Accounts & Access

### 🌐 Production Cloud Deployment (`https://help-desk-production-4340.up.railway.app/`)
*(Default backend configured in the application)*

| Role | Work Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@example.com` | `vkUSXGMOIU_27b1q` | Full administrative control (Ticket management, Assignees, Team CRUD, Analytics) |

### 💻 Local Development Server (`http://10.0.2.2:3000/` or `127.0.0.1:3000/`)
*(When running backend locally with `bun dev`)*

| Role | Default Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@helpdesk.local` | `admin12345` | Full admin privileges |
| **Agent** | `sarah.agent@helpdesk.local` | `agent12345` | Ticket triage, reply composition, AI summaries & polish |

---

## 🧪 Build & Test Commands

Run the following Gradle commands from the `android` directory:

### Run Unit Tests
```bash
# macOS / Linux
./gradlew testDebugUnitTest

# Windows
.\gradlew.bat testDebugUnitTest
```

### Assemble Debug APK
```bash
# macOS / Linux
./gradlew assembleDebug

# Windows
.\gradlew.bat assembleDebug
```
Output: `android/app/build/outputs/apk/debug/app-debug.apk`

### Assemble Release APK (Minified & Obfuscated via R8)
```bash
# macOS / Linux
./gradlew assembleRelease

# Windows
.\gradlew.bat assembleRelease
```
Output: `android/app/build/outputs/apk/release/app-release-unsigned.apk`

### Clean Build Cache
```bash
# macOS / Linux
./gradlew clean

# Windows
.\gradlew.bat clean
```

---

## 🛡️ Security & Hardening

- **Strict Network Security Configuration**: `network_security_config.xml` enforces TLS/HTTPS globally, permitting unencrypted cleartext HTTP strictly for local developer hosts (`10.0.2.2`, `localhost`, `127.0.0.1`).
- **Gated Diagnostic Logging**: `HttpLoggingInterceptor` is active only in `BuildConfig.DEBUG` builds, preventing session tokens and user credentials from leaking into production Logcat logs.
- **R8 / ProGuard Minification**: Release builds enable code shrinking, obfuscation, and unused resource removal via `isMinifyEnabled = true` and `isShrinkResources = true`.
- **Session Backup Protection**: `backup_rules.xml` and `data_extraction_rules.xml` explicitly exclude cookie stores and DataStore session tokens from automated cloud backups and device transfers.
- **Thread Safety**: Date-time parsing and formatting uses thread-safe Java 8+ `java.time` APIs.
