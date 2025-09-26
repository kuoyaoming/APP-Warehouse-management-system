## 倉儲盤點 APP / Warehouse Inventory App

<p float="left">
  <img src="./5.jpg?raw=true" width="250" />
  <img src="./4.jpg?raw=true" width="250" />
  <img src="./3.jpg?raw=true" width="250" />
  <img src="./2.jpg?raw=true" width="250" />
  <img src="./1.jpg?raw=true" width="250" />
</p>

---

### 中文介紹

**倉儲盤點 APP** 是一個以 Firebase 為後端的 Android 應用程式，協助使用者快速建立、管理並同步盤點資料。

- 使用者註冊／登入（Email/Password）
- 新增、瀏覽及更新盤點資料
- 依預設欄位填寫（品名、位置、數量、單位、備註…）
- 支援附加手機內照片
- 裝置上線時，資料自動同步至遠端（Firebase Realtime Database + Storage）

**主要技術**
- Firebase Authentication、Realtime Database、Storage
- Android（以 Android Studio 建置）

**資料儲存**
- 照片：以 JPEG 儲存於 Firebase Storage，檔名格式如 `1628241139810.jpg`
- 盤點資料：以 JSON 儲存於 Firebase Realtime Database，根節點包含 `users` 與 `posts`（使用者資料、盤點資料），結構與 [Firebase Database Quickstart] 風格一致

**專案結構**
- `database/`：Android 專案根目錄（請以此資料夾作為 Android Studio 專案開啟）
- `database/app/`：App 模組（放置 `google-services.json`）
- `internal/`：工具／輔助模組
- 根目錄放置示意截圖 `1.jpg`～`5.jpg`

**快速開始**
1. 使用 Android Studio 開啟專案資料夾：`database/`
2. 建立 Firebase 專案並啟用 Email/Password 身分驗證
3. 下載並放置 `google-services.json` 至 `database/app/`
4. 建立 Realtime Database 與 Storage（開發期間可先使用較寬鬆的規則）
5. 同步 Gradle，於真機或模擬器執行

開發用範例規則（請依實際需求調整）：

```javascript
// Realtime Database（開發用）
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

```javascript
// Storage（開發用）
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if true;
    }
  }
}
```

**展示影片**：`https://youtu.be/LMsfWJopg8k`

**預計增加功能**
- [ ] 目錄樹分類（倉庫／區／排／位置）
- [ ] 可自訂欄位模板
- [ ] 多張照片上傳
- [ ] Beta 版上架到 Google Play

---

### English

**Warehouse Inventory App** is an Android application powered by Firebase to help you create, manage, and sync inventory counts on mobile.

- Email/Password authentication
- Create, view, and update inventory posts
- Predefined fields (name, location, quantity, unit, remarks, …)
- Attach photos from device storage
- Automatic sync to Firebase Realtime Database and Storage when online

**Tech stack**
- Firebase Authentication, Realtime Database, Storage
- Android, built with Android Studio

**Project layout**
- `database/`: Android project root (open this folder in Android Studio)
- `database/app/`: App module (place your `google-services.json` here)
- `internal/`: tooling/aux modules
- Root images `1.jpg`–`5.jpg`

**Getting started**
1. Open the `database/` folder in Android Studio
2. Create a Firebase project and enable Email/Password auth
3. Download `google-services.json` and place it at `database/app/`
4. Create Realtime Database and Storage (start with permissive rules for development only)
5. Sync Gradle and run on a device/emulator

Dev-only sample rules (adapt for production):

```javascript
// Realtime Database (development)
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

```javascript
// Storage (development)
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if true;
    }
  }
}
```

**Demo video**: `https://youtu.be/LMsfWJopg8k`

**Roadmap**
- [ ] Hierarchical locations (warehouse/area/row/slot)
- [ ] Customizable field templates
- [ ] Multiple photo attachments
- [ ] Publish Beta on Google Play
