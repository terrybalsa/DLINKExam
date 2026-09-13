# DLINKExam — YouBike2.0 臺北市公共自行車即時資訊

顯示台北市 YouBike2.0 即時站點資訊的 Android App：租借站列表（依行政區分群、可過濾）與單一站點詳細資訊（含地圖標示、依比例變色的車輛數量）。

需求規格見 [`spec.md`](spec.md)，技術方案與工程決策見 [`plan.md`](plan.md)。

## 環境需求

- **JDK 21**
- **Android SDK**：compileSdk / targetSdk 37，minSdk 24
- Android Studio（建議最新版）或單純 Gradle 命令列

## 建置方式

### 使用 Android Studio

1. 開啟專案資料夾
2. 等待 Gradle sync 完成
3. 執行 `app` 這個 Run Configuration（實機或模擬器皆可，Android 7.0 / API 24 以上）

### 使用命令列

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

編譯完成的 APK 在 `app/build/outputs/apk/debug/app-debug.apk`。

### 執行測試

```bash
./gradlew test
```

## 使用方式

1. **列表頁**：開啟 App 後顯示所有租借站，依行政區分組；每 3 分鐘自動更新一次資料
2. **過濾**：點右上角放大鏡圖示，輸入行政區關鍵字（如「大安」），僅顯示符合的分組
3. **詳細頁**：點選任一站點，進入該站詳細資訊頁——含地圖標示位置、站點代號、總停車格、目前車輛數量（依剩餘比例變色：紅 <20%／黃 20%~50%／綠 >50%）、空位數量、資料更新時間；每 1 分鐘自動更新一次

## 資料來源

台北市公開資料平台 — YouBike2.0 臺北市公共自行車即時資訊
https://data.taipei/dataset/detail?id=c6bc8aed-557d-41d5-bfb1-8da24f78f2fb

## 技術棧

Kotlin、Jetpack Compose、MVVM、Hilt、Retrofit + kotlinx.serialization、Kotlin Coroutines/Flow、Navigation Compose、Coil（載入 OpenStreetMap 圖磚）。

## CI

推送至 `main` 分支會自動觸發 GitHub Actions（`.github/workflows/android-ci.yml`），執行 `./gradlew build`（編譯 + 單元測試）。
