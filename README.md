# 📱 Helpdesk Android Application

A native Android client for the AI-Powered Helpdesk Ticket Management System, built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Clean Architecture**.

---

## 🏛️ Clean Architecture Structure

The project follows standard Android Clean Architecture separation across modular layers:

```
android/
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml           # Configures networkSecurityConfig & permissions
│       │   ├── res/
│       │   │   └── xml/network_security_config.xml # Restricts cleartext traffic strictly to local dev
│       │   └── java/com/helpdesk/app/
│       │       ├── HelpdeskApplication.kt    # Koin DI setup & App entry
│       │       ├── MainActivity.kt           # Single-activity Compose host
│       │       ├── core/                     # Network, DataStore, DI, Theme & Error Handlers
│       │       │   ├── di/AppModule.kt       # Koin Dependency Injection modules
│       │       │   ├── network/              # Retrofit, OkHttp, SessionCookieJar, DynamicHostInterceptor, AuthInterceptor
│       │       │   ├── datastore/SessionManager.kt # Preferences DataStore & in-memory cached session
│       │       │   ├── result/               # Resource<T> & AppError (Validation, Server, Network, Forbidden)
│       │       │   ├── theme/                # Material 3 Theme, Typography, Colors
│       │       │   └── util/DateTimeUtils.kt # Thread-safe java.time ISO parsing & relative time formatters
│       │       ├── domain/                   # Pure Kotlin business layer (Zero Android framework dependencies)
│       │       │   ├── model/                # Ticket, User, Reply, Stats, Customer, Enums
│       │       │   ├── repository/           # AuthRepository, TicketRepository, UserRepository interfaces
│       │       │   └── usecase/              # Focused single-responsibility UseCases (operator fun invoke)
│       │       │       ├── auth/             # LoginUseCase, LogoutUseCase, GetCurrentUserUseCase, etc.
│       │       │       ├── ticket/           # GetTicketsUseCase, CreateReplyUseCase, PolishReplyUseCase, SummarizeTicketUseCase, etc.
│       │       │       └── user/             # GetUsersUseCase, CreateUserUseCase, UpdateUserUseCase, DeleteUserUseCase
│       │       ├── data/                     # Data layer (Remote API & Persistence implementations)
│       │       │   ├── remote/dto/           # Data Transfer Objects matching backend JSON
│       │       │   ├── mapper/Mappers.kt     # DTO ↔ Domain model mapper functions
│       │       │   └── repository/           # AuthRepositoryImpl, TicketRepositoryImpl, UserRepositoryImpl (safeApiCall)
│       │       └── presentation/             # UI layer (Jetpack Compose & ViewModels)
│       │           ├── common/               # Reusable components (StatusBadge, CategoryBadge, StatCard, EmptyState, Shimmer)
│       │           ├── navigation/           # AppNavigation, NavigationRoutes, BottomNavBar
│       │           ├── auth/                 # LoginScreen & LoginViewModel (with Server Host switcher)
│       │           ├── dashboard/            # DashboardScreen & DashboardViewModel (KPIs & 30-Day volume chart)
│       │           ├── tickets/
│       │           │   ├── list/             # TicketListScreen & TicketListViewModel (Search, Status Filter, Sheet)
│       │           │   ├── create/           # CreateTicketBottomSheet
│       │           │   └── detail/           # TicketDetailScreen, AISummaryCard, ReplySection, ReplyComposer
│       │           └── users/                # UsersScreen & UsersViewModel (Admin-only team directory & CRUD)
│       └── test/                             # JVM Unit Tests
│           └── java/com/helpdesk/app/
│               ├── DateTimeUtilsTest.kt      # Validates ISO parsing, formatting, relative time
│               ├── MappersTest.kt            # Validates DTO-to-Domain mapping
│               ├── AuthUseCasesTest.kt       # Validates email/password constraints
│               ├── TicketUseCasesTest.kt     # Validates ticket creation & reply limits
│               └── UserUseCasesTest.kt       # Validates user management CRUD rules
```

---

## ✨ Mobile Features

- 🔐 **Session Authentication & Multi-Transport Resilience**:
  - Sign in with email & password.
  - Better-Auth session tokens and cookies are persisted via `SessionCookieJar` and `SessionManager` (DataStore).
  - Automatically attaches `Authorization: Bearer <token>` for resilient cross-network authentication.
  - Dynamic Server Base URL switcher (supports Localhost `http://localhost:3000`, Emulator `http://10.0.2.2:3000`, or Cloud).
- 📊 **Analytics Dashboard**:
  - KPI Cards for Total Tickets, Open Tickets, AI-Resolved %, and Average Resolution Time.
  - Interactive 30-Day Ticket Volume histogram bar chart.
  - Metric tap navigation (e.g. tapping "Open Tickets" automatically filters the ticket list).
  - Quick action shortcuts (e.g. "New Ticket" button directly opens the ticket creation dialog).
- 🎫 **Ticket Management**:
  - Search tickets by subject, description, or customer details.
  - Filter by Status (`Open`, `Processing`, `Resolved`, `Closed`), Category (`General Question`, `Technical Question`, `Refund Request`), and Source (`Web`, `Email`).
  - Sort by Date, Subject, or Status (Ascending/Descending).
  - Pull-to-refresh & Pagination.
  - Create new support tickets with customer email, subject, and description.
- 🤖 **AI Features on Mobile**:
  - **AI Ticket Summarization**: One-tap ticket summarization powered by Groq / LLM backend integration.
  - **AI Reply Polish**: Improve agent drafts into empathetic, professional responses with customer name insertion before sending.
- 💬 **Conversation Thread & Reply Composer**:
  - Chronological message history distinguishing Agent vs Customer replies.
  - Rich reply input with live preview modal of AI-polished suggestions.
  - Role-guarded status & assignee update sheets for admins.
- 👥 **Team Management (Admin Only)**:
  - List all team members with role badges (`Admin` / `Agent`).
  - Create, edit, and soft-delete agents.
  - Unauthorized guard state for non-admin accounts.

---

## 🚀 How to Run

### Step 1: Start the Local Backend Server
Ensure the backend server is running on port 3000:
```powershell
cd server
bun dev
```

### Step 2: Connect Your Device

#### Option A: Running on Physical Android Device (over USB)
1. Enable **Developer Options** and **USB Debugging** on your Android phone.
2. Connect your phone via USB cable.
3. Run port reverse so the phone can reach `localhost:3000`:
   ```powershell
   adb reverse tcp:3000 tcp:3000
   ```
4. Install and run the app:
   ```powershell
   cd android
   .\gradlew.bat installDebug
   adb shell am start -n com.helpdesk.app/.MainActivity
   ```

#### Option B: Running on Android Studio Emulator
1. Start your Android Emulator in Android Studio.
2. The app defaults to connecting with `http://localhost:3000/` (or tap the **Server** pill on the login screen to select **Emulator** `http://10.0.2.2:3000`).
3. Click **Run ▶** in Android Studio.

---

## 🔑 Demo Seeded Credentials

| Role | Email | Password | Quick Demo Action |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@helpdesk.local` | `admin12345` | Tap **Admin** button on login screen |
| **Admin (Alt)** | `admin@example.com` | `admin12345` | Enter credentials manually |
| **Agent** | `sarah.agent@helpdesk.local` | `agent12345` | Tap **Agent** button on login screen |
| **Agent (Alt)** | `agent@example.com` | `agent12345` | Enter credentials manually |

---

## 🧪 Testing & Build Verification

### Run Unit Tests
```powershell
.\gradlew.bat testDebugUnitTest
```

### Assemble Debug APK
```powershell
.\gradlew.bat assembleDebug
```
Output APK location:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🛡️ Security Hardening

- **Cleartext Traffic Policy**: `network_security_config.xml` permits cleartext HTTP strictly on private development IPs (`10.0.2.2`, `localhost`, `127.0.0.1`, LAN IPs), enforcing HTTPS/TLS for all external domains.
- **Thread Safety**: Uses thread-safe Java 8+ `java.time` formatting to eliminate multithreading data race hazards.
- **RBAC**: Protected actions (status update, team management) prevent unauthorized requests before network dispatch.
