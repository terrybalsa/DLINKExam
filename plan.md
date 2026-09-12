# PLAN：技術方案與工程決策

> 對應 `spec.md`（定義要做什麼），本文件定義「怎麼做」（How）。
> 內容為討論後的決策，非單方面推測。

---

## 1. 整體架構

**MVVM**：`UI (Compose)` → `ViewModel` → `Repository` → `Remote API`

- **DI**：使用 **Hilt**（雖然專案規模小，但視為正式產品開發，需展現完整DI架構）
- **ViewModel 狀態輸出**：
  - `StateFlow`：持有持續性 UI 狀態（列表資料、目前的 loading/error 狀態）
  - `SharedFlow` / `Channel`：處理一次性事件（例如錯誤 toast）
- **UI 狀態建模**：用 `sealed interface UiState`（`Loading` / `Success` / `Error`），不用多個 Boolean flag 拼湊狀態

---

## 2. 資料夾結構

```
com.example.dlinkexam
├── data
│   ├── remote        # Retrofit API interface、DTO（API 回傳格式）
│   └── repository     # Repository 實作，負責輪詢邏輯（Flow）
├── domain
│   └── model          # UI 實際使用的 model（由 DTO 轉換而來）
├── ui
│   ├── list           # 列表頁 Composable + ViewModel
│   ├── detail         # 詳細頁 Composable + ViewModel
│   └── theme          # 既有 Compose theme
├── di                 # Hilt Module（提供 Retrofit / OkHttp / Repository）
└── navigation         # NavHost 設定
```

---

## 3. 網路層與輪詢機制

### 3.1 API
- 兩個畫面（列表頁、詳細頁）呼叫**同一支 API**：`youbike_immediate.json`
- 該 API **沒有查詢單一站點的專用端點**，僅能一次回傳全部站點（約 1800 筆）
- 詳細頁的每分鐘更新，做法是：打同一支 API 抓全部資料，再用 `sno` 從結果中找出對應那一筆

### 3.2 輪詢設計
- 輪詢邏輯放在 **Repository 層**，以 `Flow` 實作（例如 `flow { while (true) { emit(fetch()); delay(interval) } }`）
- **ViewModel 只負責 collect 該 Flow 並轉換成 UiState**，不自己管理計時器
- 列表頁 interval = 3 分鐘；詳細頁 interval = 1 分鐘，各自獨立
- **畫面離開時自動停止輪詢**：UI 層用 `collectAsStateWithLifecycle()` 收集 Flow，畫面不在前景（非 STARTED 狀態）時自動停止 collect，回到前景自動恢復
- 兩個畫面的計時器**互斥、不會同時運作**——使用者同一時間只會在其中一個畫面，離開列表頁進入詳細頁時列表頁計時器停止，反之亦然

### 3.3 過濾（Filter）
- API 不支援篩選參數，**過濾在本機記憶體中執行**：對已抓取、存於 `StateFlow` 的完整 `List<Station>` 做 `.filter { it.sarea.contains(關鍵字) }`
- 過濾動作本身**不會觸發打 API**，只有輪詢計時器會呼叫 API
- 每次輪詢抓到新資料後，若使用者當下有輸入過濾關鍵字，會用新資料重新套用同一組過濾條件
- **不使用 Room**：本地儲存的資料只是活在 ViewModel/Repository 記憶體中的變數，隨畫面生命週期存在，不寫入磁碟（因為不需要離線快取，見 3.4）

### 3.4 離線快取
- **不做**離線快取。無網路時直接顯示「無網路狀態」，不保留上次資料

### 3.5 錯誤處理（分兩塊）
- **無網路**：簡單偵測網路狀態，顯示「無網路」提示畫面
- **API 本身問題**（逾時、資料格式異常等）：另外用 error handling 機制處理（顯示錯誤訊息，不可白畫面/閃退）

---

## 4. 資料模型

- API 欄位命名不一致，**`Quantity` 開頭大寫**，需在 DTO 用 `@SerialName("Quantity")` 對齊
- 建議保留 DTO（API 回傳格式）與 Domain Model（UI 使用格式）分層，DTO → Domain 轉換時順便處理時間格式化、命名正規化

---

## 5. 畫面與導覽

- **Navigation Compose**：單一 Activity + 兩個 Composable 畫面（列表頁、詳細頁）
- 列表頁點選站點時，帶入該站的 `sno` 參數導覽至詳細頁

---

## 6. UI 實作重點

### 6.1 分組清單（列表頁）
- 用 Compose `LazyColumn` + `stickyHeader`（Foundation 內建，不需額外套件）依 `sarea` 分組
- 效果：往下捲動時目前區域的標題會固定在畫面頂端，直到下一個區域標題把它推上去
- 資料量約 1800 筆，`LazyColumn` 本身即為虛擬化清單（只渲染可見範圍內的項目），不需額外分頁機制

### 6.2 地圖（詳細頁）
- **不使用地圖 SDK**（Google Maps / OSM SDK 皆不用），避免申請 API Key / 綁定帳單的成本
- 改用 **OSM 靜態圖磚**：依真實經緯度組成圖磚 URL，以 **Coil** 載入顯示為一張靜態圖片（不可互動，不可縮放拖曳）
- 頁面版面配置（依 mockup，由上至下）：站名文字 → 地圖圖片 → OPEN DATA 各欄位資訊

### 6.3 過濾功能（列表頁）
- 右上角放大鏡按鈕開啟一個**搜尋框**
- 僅針對「區域」關鍵字過濾（對齊 PDF 原文「使用者可以針對區域做過濾」），不做全欄位搜尋

### 6.4 變色規則（詳細頁）
- 計算 `available_rent_bikes / Quantity` 比例
- `< 20%` 紅色、`20% ~ 50%`（**含頭尾**）黃色、`> 50%` 綠色

---

## 7. 其他決策

- **時區**：一律以 `Asia/Taipei` 顯示時間
- **minSdk**：沿用現有專案設定 `24`

---

## 8. 依賴套件清單（待加入 `libs.versions.toml`）

| 用途 | 套件 |
|---|---|
| 網路請求 | Retrofit, OkHttp |
| JSON 解析 | kotlinx.serialization |
| 非同步 | Kotlin Coroutines（部分 Compose 已內建） |
| 導覽 | Navigation Compose |
| 圖片載入（地圖靜態圖） | Coil |
| 依賴注入 | Hilt |
