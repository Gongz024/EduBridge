# EduBridge Mobile (Android)

Native Android client for EduBridge, written in **Kotlin + Jetpack Compose**. It talks to the same
Spring Boot API as the web app (`backend/edubridge`, JWT auth at `/api/**`) and reuses the web app's
design tokens (colors, radii, card/label styles from `src/index.css`).

Unlike the web workspace, which is teacher-only, the mobile app supports all three roles. The screens
change based on the role of the account that signs in.

| Role | Tabs | What they can do |
|---|---|---|
| **Teacher** | Home · Classes · Alerts · Profile | Dashboard stats (same as web), create classes, enroll/remove students, publish lessons + materials, assign homework, post announcements, create family missions, take attendance by date, record grades, review homework submissions with score/feedback, review mission completions and award points |
| **Parent** | Home · Missions · Progress · Profile | Link a child by their student email, switch between children, see grades/attendance/homework status/announcements, complete family missions together (the teacher is notified) |
| **Student** | Home · Homework · Lessons · Progress · Profile | See what's due, submit or edit homework, read lessons and mark them complete, see grades, attendance, points and badges |

All roles get a notifications feed. Creating homework, an announcement or a mission sends in-app
notifications to the class's students **and** their linked parents.

## Requirements

- Android Studio Ladybug (2024.2) or newer, with JDK 17
- Android SDK 35 (installed by Android Studio)
- The EduBridge backend running (see `backend/edubridge`, port 8080, MySQL on 3307)

Pinned versions: AGP 8.7.3, Gradle 8.11.1, Kotlin 2.0.21, Compose BOM 2024.12.01, Retrofit 2.11,
OkHttp 4.12, minSdk 26, targetSdk 35.

## Run it

1. Start the backend: `cd backend/edubridge && ./mvnw spring-boot:run`
2. In Android Studio: **File → Open →** select the `mobile/` folder and let Gradle sync.
3. Pick an emulator and press **Run**.

### Pointing the app at the API

The base URL is a build setting, `edubridge.apiBaseUrl`, and must end in `/api/`.

- **Emulator (default):** `http://10.0.2.2:8080/api/`. `10.0.2.2` is how the emulator reaches your computer's `localhost`.
- **Physical phone on the same Wi-Fi:** add this line to `mobile/local.properties` (not committed):
  ```
  edubridge.apiBaseUrl=http://192.168.1.20:8080/api/
  ```
  Use your computer's LAN IP address, and allow port 8080 through the firewall.

Debug builds allow plain HTTP so they can reach the local server. Release builds don't, so use HTTPS
in production. Native apps don't use CORS, so `app.cors.allowed-origins` doesn't need changes.

The Profile screen shows which server the build is using.

## Project layout

```
app/src/main/java/com/it342g02/edubridge/mobile/
├── EduBridgeApp.kt            # Application + manual DI container
├── MainActivity.kt
├── data/
│   ├── model/Models.kt        # Kotlin mirrors of the JPA entities + auth DTOs
│   ├── remote/EduBridgeApi.kt # Retrofit mirror of src/api/index.js
│   ├── remote/ApiClient.kt    # OkHttp: Bearer token, 401 → logout, error messages
│   ├── session/               # JWT storage (DataStore) + SessionManager (like AuthProvider)
│   └── repository/            # All API orchestration (per-role aggregates)
├── ui/
│   ├── theme/Theme.kt         # Web design tokens → Material 3
│   ├── components/            # EduCard, StatCard, PageHeading, forms, loaders, dialogs
│   ├── navigation/            # Session gate, role-based bottom nav, routes
│   ├── auth/  shared/  teacher/  parent/  student/
└── util/Dates.kt              # ISO date parsing/formatting, greeting
```

## Notes on the API contract

- Dates go over the wire as ISO strings (`2026-11-24T09:30:00`, `2026-11-24`).
- To reference an existing row, the app sends only its id, e.g. `{"schoolClass": {"id": 3}}`.
- `PUT` replaces the whole row on the server, so updates send a full copy of the fetched object.
- The backend's free-text status columns are written with consistent values:
  `UNREAD/READ`, `ASSIGNED`, `SUBMITTED/GRADED`, `PUBLISHED`, `ACTIVE`, `COMPLETED`, `NORMAL/HIGH`.
- The backend doesn't yet restrict data by role or owner. Any signed-in user can call any endpoint.
  The app only shows each role its own data, but real access control has to be added server-side.

## Font

The web app uses Inter. To match it exactly, add Inter `.ttf` files to `app/src/main/res/font/` and
swap `AppFont` in `ui/theme/Theme.kt`. The app falls back to the system sans-serif font until then.
