# Helpdesk Android Application

A native Android client for the AI-Powered Helpdesk Ticket Management System, built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Clean Architecture**.

---

## 🏛️ Clean Architecture Structure

The project follows standard Android Clean Architecture separation:

```
android/
├── app/
│   └── src/main/java/com/helpdesk/app/
│       ├── HelpdeskApplication.kt        # Koin DI setup & App entry
│       ├── MainActivity.kt               # Single-activity Compose host
│       ├── core/                         # Network, DataStore, DI, Theme & Error Handlers
│       │   ├── di/AppModule.kt           # Koin Dependency Injection modules
│       │   ├── network/                  # Retrofit, OkHttp, SessionCookieJar, DynamicHostInterceptor
│       │   ├── datastore/SessionManager.kt # Jetpack DataStore session persistence
│       │   ├── result/                   # Resource<T> & AppError sealed types
│       │   ├── theme/                    # Material 3 Theme, Typography, Colors
│       │   └── util/DateTimeUtils.kt     # Date parsing & relative time formatters
│       ├── domain/                       # Pure Kotlin business layer (Zero Android framework dependencies)
│       │   ├── model/                    # Ticket, User, Reply, Stats, Customer, Enums
│       │   ├── repository/               # AuthRepository, TicketRepository, UserRepository interfaces
│       │   └── usecase/                  # Focused single-responsibility UseCases (operator fun invoke)
│       │       ├── auth/                 # LoginUseCase, LogoutUseCase, GetCurrentUserUseCase, etc.
│       │       ├── ticket/               # GetTicketsUseCase, CreateReplyUseCase, PolishReplyUseCase, SummarizeTicketUseCase, etc.
│       │       └── user/                 # GetUsersUseCase, CreateUserUseCase, UpdateUserUseCase, DeleteUserUseCase
│       ├── data/                         # Data layer (Remote API & Persistence implementations)
│       │   ├── remote/dto/               # Data Transfer Objects matching backend JSON
│       │   ├── mapper/Mappers.kt         # DTO ↔ Domain model mapper functions
│       │   └── repository/               # AuthRepositoryImpl, TicketRepositoryImpl, UserRepositoryImpl
│       └── presentation/                 # UI layer (Jetpack Compose & ViewModels)
│           ├── common/                   # Reusable components (StatusBadge, CategoryBadge, StatCard, etc.)
│           ├── navigation/               # AppNavigation, Routes, BottomNavBar
│           ├── auth/                     # LoginScreen & LoginViewModel (with Server Host switcher)
│           ├── dashboard/                # DashboardScreen & DashboardViewModel (KPIs & 30-Day volume chart)
│           ├── tickets/
│           │   ├── list/                 # TicketListScreen & TicketListViewModel (Search, Multi-filter, Sorting)
│           │   ├── create/               # CreateTicketBottomSheet
│           │   └── detail/               # TicketDetailScreen, AISummaryCard, ReplySection, ReplyComposer
│           └── users/                    # UsersScreen & UsersViewModel (Admin-only team directory & CRUD)
```

---

## ✨ Features

- 🔐 **Session Authentication & Persistence**:
  - Sign in with email & password.
  - Better-Auth session tokens and cookies are persisted via `SessionCookieJar` and `SessionManager` (DataStore).
  - Session auto-recovery on app launch via `GET /api/me`.
  - Dynamic Server Base URL switcher (supports Android Emulator `http://10.0.2.2:3000`, LAN IP, or Cloud).
- 📊 **Analytics Dashboard**:
  - KPI Cards for Total Tickets, Open Tickets, AI-Resolved %, and Average Resolution Time.
  - Interactive 30-Day Ticket Volume histogram chart.
  - Quick action shortcuts.
- 🎫 **Ticket Management**:
  - Search tickets by subject, description, or customer details.
  - Filter by Status (`Open`, `Resolved`, `Closed`), Category (`General Question`, `Technical`, `Refund Request`), and Source (`Web`, `Email`).
  - Sort by Date, Subject, or Status (Ascending/Descending).
  - Pull-to-refresh & Pagination.
  - Create new support tickets with customer email, subject, and description.
- 🤖 **AI Features on Mobile**:
  - **AI Ticket Summarization**: One-tap ticket summarization with Groq/LLM integration.
  - **AI Reply Polish**: Improve agent drafts into empathetic, professional responses with customer name insertion before sending.
- 💬 **Conversation Thread & Reply Composer**:
  - Chronological message history distinguishing Agent vs Customer replies.
  - Rich reply input with live preview of AI-polished suggestions.
  - Status & Assignee update sheets.
- 👥 **Team Management (Admin Only)**:
  - List all team members with roles (`Admin` / `Agent`).
  - Create, edit, and soft-delete agents.

---

## 🚀 How to Run in Android Studio

1. Open Android Studio.
2. Select **Open** and choose the `android/` directory (`D:\Code with AI\helpdesk-main\android`).
3. Allow Gradle to sync the project dependencies.
4. Run the backend server (`cd server && bun run dev` on port 3000).
5. Launch the Android app on an Emulator (points to `http://10.0.2.2:3000` by default) or Physical Device (tap the "Server" button on the login screen to enter your computer's LAN IP).
6. Sign in with the seeded credentials:
   - **Admin**: `admin@helpdesk.local` / `admin12345`
   - **Agent**: `sarah.agent@helpdesk.local` / `agent12345`
