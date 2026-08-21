# AGENTS.md — AI Agent Guidance for APP-Warehouse-management-system

> **Repository:** `Max97k/APP-Warehouse-management-system`  
> **Default Branch:** `main`  
> **Primary Technology Stack:** Android OS, Java 8 / Kotlin 1.4.31, Gradle 6.7.1 / AGP 4.2.2, Firebase BOM 26.6.0 (Auth, Database, Storage), FirebaseUI 8.0.0, Picasso 2.71828  
> **Visibility:** Public  

---

## 1. Project Overview & Architecture

### 1.1 Purpose & Mission
`APP-Warehouse-management-system` is an enterprise-oriented Android inventory tracking and warehouse stock management application. The system provides real-time tracking of warehouse inventory items (SKUs, stock entries, and goods metadata), multi-user authentication, photographic asset attachment to inventory records, post-detail commenting for item audit trails, and synchronized cloud storage powered by Google Firebase (Authentication, Realtime Database, Cloud Storage).

### 1.2 System Architecture & Component Diagram
The project uses a Single Activity + Jetpack Navigation Architecture with multiple Fragment destinations:

```
+--------------------------------------------------------------------+
|                APP-Warehouse-management-system                     |
|                (Root Gradle Directory: database/)                  |
+---------------------------------+----------------------------------+
                                  |
                  [Single Activity Host: MainActivity]
                                  |
                                  v
                    +---------------------------+
                    |  nav_graph_java.xml Graph |
                    +-------------+-------------+
                                  |
        +-------------------------+-------------------------+
        |                         |                         |
        v                         v                         v
+------------------+    +-------------------+    +--------------------+
|  SignInFragment  |    |   MainFragment    |    |   NewPostFragment  |
|  - Firebase Auth |    |   - Tabbed Feeds: |    |   - New SKU Record |
|    Email/Pass    |    |     Recent / Top  |    |   - Photo Upload   |
+------------------+    |   - FirebaseRecyc |    |     to Storage     |
                        |     lerAdapter    |    +--------------------+
                        +---------+---------+
                                  |
                                  v
                        +-------------------+
                        | PostDetailFragment|
                        | - Item Details    |
                        | - Comments Stream |
                        +-------------------+
```

### 1.3 Key File & Directory Map
> **⚠️ Critical Workspace Structure:** The root Android Gradle build files are located in the `database/` subfolder. All Gradle commands must be executed within `database/`.

| Path | Purpose / Description |
|---|---|
| `database/` | **Root Android Gradle Project Directory** containing all build scripts and modules. |
| `database/settings.gradle` | Root settings declaring submodules (`:app`, `:internal:lintchecks`, `:internal:lint`, `:internal:chooserx`). |
| `database/build.gradle` | Top-level Gradle configuration declaring AGP 4.2.2 and Google Maven repositories. |
| `database/app/build.gradle` | App module configuration (SDK 30, MultiDex, Firebase BOM 26.6.0, ViewBinding, Picasso). |
| `database/app/src/main/AndroidManifest.xml` | Android application manifest and permission declarations. |
| `database/app/src/main/java/com/google/firebase/quickstart/database/java/MainActivity.java` | Main host activity managing Jetpack Navigation and action bar setup. |
| `database/app/src/main/java/com/google/firebase/quickstart/database/java/SignInFragment.java` | User authentication interface with Firebase Auth email/password login and registration. |
| `database/app/src/main/java/com/google/firebase/quickstart/database/java/MainFragment.java` | Top-level inventory feed container hosting tabbed fragments (`RecentPostsFragment`, `MyPostsFragment`, `MyTopPostsFragment`). |
| `database/app/src/main/java/com/google/firebase/quickstart/database/java/NewPostFragment.java` | Form for publishing new inventory records with photo attachment upload to Firebase Storage. |
| `database/app/src/main/java/com/google/firebase/quickstart/database/java/PostDetailFragment.java` | Detailed view for individual inventory items and comment/audit streams. |
| `database/app/src/main/java/com/google/firebase/quickstart/database/java/models/` | Data model classes (`Post.java`, `User.java`, `Comment.java`). |
| `database/app/src/main/res/navigation/nav_graph_java.xml` | Android Jetpack Navigation XML graph defining fragment transitions. |
| `internal/` | Shared internal tooling and lint checks modules. |

---

## 2. Development, Build & Verification Commands

### 2.1 Prerequisites & Environment Setup
- **Java Development Kit (JDK):** JDK 8 or JDK 11 (`JavaVersion.VERSION_1_8`).
- **Android SDK:** Compile SDK `30`, Min SDK `16` (Android 4.1 JellyBean), Target SDK `30`.
- **Firebase Configuration:** Valid `google-services.json` placed inside `database/app/`.
- **Gradle Version:** Gradle 6.7.1 wrapper inside `database/gradle/wrapper/`.

### 2.2 Build & Compilation Commands
*Always navigate to the `database/` directory prior to running Gradle:*

```bash
# Navigate to Android project root
cd database

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Build all modules
./gradlew build
```

*On Windows PowerShell/CMD:*
```powershell
cd database
.\gradlew.bat assembleDebug
```

### 2.3 Verification & Testing Suite
```bash
# Run unit tests across all modules
cd database && ./gradlew test

# Run app unit tests specifically
cd database && ./gradlew :app:testDebugUnitTest

# Run connected Android instrumentation tests
cd database && ./gradlew connectedAndroidTest

# Run static code analysis / Lint checks
cd database && ./gradlew lint
```

### 2.4 Clean & Reset
```bash
# Clean build artifacts
cd database && ./gradlew clean
```

---

## 3. Coding Standards & Conventions

### 3.1 Code Style & Idioms
- **Primary Language:** Java 8 with supporting Kotlin 1.4.31 components.
- **ViewBinding:** ViewBinding is active (`buildFeatures { viewBinding = true }`). Use generated binding classes (e.g. `FragmentNewPostBinding`) rather than synthetic imports or manual view casting.
- **Firebase UI Adapter:** Inventory lists leverage `FirebaseRecyclerAdapter` from `firebase-ui-database` to bind Realtime Database queries directly to `RecyclerView.ViewHolder` instances.
- **Image Loading:** Use Picasso (`com.squareup.picasso:picasso`) for asynchronous image fetching, caching, and placeholder rendering.

### 3.2 File & Module Organization
- **Package Structure:** Base package `com.google.firebase.quickstart.database.java`.
- **Model Classes:** Keep models (`Post`, `User`, `Comment`) as POJOs with public no-argument constructors and `@IgnoreExtraProperties` annotations for Firebase Database deserialization.
- **Navigation:** Maintain all screen transitions within `nav_graph_java.xml` using `NavController.navigate()`.

### 3.3 State Management & Error Handling
- **Database Schema:**
  - `/users/$uid`: User profile metadata (`username`, `email`).
  - `/posts/$postId`: Global post/inventory items.
  - `/user-posts/$uid/$postId`: Per-user indexed items.
  - `/post-comments/$postId/$commentId`: Item comments and status logs.
- **Atomic Multi-Path Updates:** When adding or updating inventory posts, write to `/posts` and `/user-posts` simultaneously using atomic `updateChildren(childUpdates)` maps to maintain database consistency.
- **Transactions:** Use `databaseReference.runTransaction()` when updating numeric counters (e.g. stock quantities, star ratings) to prevent race conditions.

---

## 4. Safety, Security & Resource Constraints

### 4.1 Secrets & Environment Management
- **Firebase Credentials (`google-services.json`):**
  - Path: `database/app/google-services.json`.
  - Do not commit production Firebase keys to public repositories.
  - Verify that Firebase Security Rules restrict write operations to authenticated users (`auth != null`).

### 4.2 Resource, Hardware & Performance Constraints
- **MultiDex Configuration:** MultiDex is enabled (`multiDexEnabled true`) due to the broad Firebase dependency surface and minimum SDK 16.
- **Network Permissions:** Declares `android.permission.INTERNET` and `android.permission.READ_EXTERNAL_STORAGE`. Ensure Scoped Storage compliance for Android 10+ (API 29+).
- **Image Compression:** Compress images before uploading to Firebase Storage to minimize bandwidth and storage quotas in warehouse environments with constrained Wi-Fi.

### 4.3 Git & Branch Workflow
- **Default Branch:** `main`.
- **Commit Conventions:** Use standard conventional commit prefixes (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`).
