# SPEC：YouBike2.0 臺北市公共自行車即時資訊 App

> 產品需求文件，作為後續設計 / 開發 / 驗收的唯一依據（Spec-Driven Development）。
> 本文件只定義「要做什麼」（What），不綁定實作技術（How）；技術選型與工程決策見 `plan.md`。

---

## 1. 專案概述

一個顯示台北市公共自行車 YouBike2.0 即時資訊的 Android App，共兩個畫面：

1. **租借站列表頁**（首頁）：依行政區分群列出所有站點
2. **租借站詳細頁**：顯示單一站點的完整即時資訊與地圖位置

---

## 2. 資料來源（API Contract）

- **資料集**：台北市公開資料平台 - YouBike2.0 臺北市公共自行車即時資訊
- **資料集頁面**：https://data.taipei/dataset/detail?id=c6bc8aed-557d-41d5-bfb1-8da24f78f2fb
- **API 端點（JSON）**：`https://tcgbusfs.blob.core.windows.net/dotapp/youbike/v2/youbike_immediate.json`
- **來源更新頻率**：約每 1 分鐘

### 2.1 回傳欄位對照表

已實際呼叫 API 取得即時回應並核對欄位，範例如下（單筆）：

```json
{
  "sno": "500101001",
  "sna": "YouBike2.0_捷運科技大樓站",
  "sarea": "大安區",
  "mday": "2026-09-11 17:26:17",
  "ar": "復興南路二段235號前",
  "sareaen": "Daan Dist.",
  "snaen": "YouBike2.0_MRT Technology Bldg. Sta.",
  "aren": "No.235， Sec. 2， Fuxing S. Rd.",
  "act": "1",
  "srcUpdateTime": "2026-09-11 17:26:52",
  "updateTime": "2026-09-11 17:26:52",
  "infoTime": "2026-09-11 17:26:17",
  "infoDate": "2026-09-11",
  "Quantity": 28,
  "available_rent_bikes": 23,
  "latitude": 25.02605,
  "longitude": 121.5436,
  "available_return_bikes": 4
}
```

| API 欄位 | 型別 | 說明 | 對應需求 |
|---|---|---|---|
| `sno` | String | 站點代號 | 4-b |
| `sna` | String | 場站中文名稱（含 "YouBike2.0_" 前綴） | 3-列表標題 / 4-c |
| `snaen` | String | 場站英文名稱 | - |
| `sarea` | String | 場站所屬行政區（如「大安區」） | 3-列表分群依據 |
| `sareaen` | String | 行政區英文 | - |
| `ar` | String | 地址（中文） | 3-列表地址欄位 |
| `aren` | String | 地址（英文） | - |
| `latitude` | Double | 緯度 | 4-a 地圖標示 |
| `longitude` | Double | 經度 | 4-a 地圖標示 |
| `Quantity` | Int | 場站總停車格數（**注意大寫 Q**，與其他欄位命名不一致，DTO 解析需對齊此大小寫或用 `@SerializedName` 映射） | 3-腳踏車總數 / 4-d |
| `available_rent_bikes` | Int | 目前可租借車輛數量 | 3-可租借數量 / 4-e |
| `available_return_bikes` | Int | 目前空位數量 | 4-g |
| `act` | String("0"/"1") | 場站是否啟用（1=啟用） | - |
| `mday` | String (`yyyy-MM-dd HH:mm:ss`) | 資料更新時間 | 3-更新時間 / 4-f |
| `srcUpdateTime` | String (`yyyy-MM-dd HH:mm:ss`) | YouBike2.0 系統發布資料更新時間 | 4-h |
| `infoTime` | String | 各場站來源資料更新時間 | 備用 |

> 實際查證：全台北市共 13 個分群（12個行政區 + 「臺大公館校區」），站點總數約 1800 筆。

---

## 3. 畫面一：租借站列表頁

### 3.1 功能需求
1. 呼叫 API 取得所有站點資料，**以 `sarea`（行政區）分群**顯示，群組標題為區名（如「大安區」「中正區」）
2. 每一筆站點列表項目需顯示：
   - 站名（`sna`）
   - 地址（`ar`）
   - 可租借車輛數量（`available_rent_bikes`） / 腳踏車總數（`Quantity`）— 格式如「可租借數量全部剩餘：X / Y」
   - 更新時間（`mday`，需格式化為可讀時間，如 `HH:mm`）
3. 頁面資料**每 3 分鐘自動重新整理**一次（背景輪詢，不需使用者操作）
4. 右上角放大鏡（🔍）按鈕：開啟過濾功能，使用者輸入 / 選擇行政區關鍵字，僅顯示符合的分群與站點
5. 點選任一站點項目 → Navigate 至「租借站詳細頁」，並帶入該站的 `sno`（或整筆資料）

### 3.2 UI 對應（mockup）
- Toolbar：標題「YouBike2.0臺北市公共自行車即時資訊」+ 右上角放大鏡 icon
- 內容區：分區塊（Section）的清單，每個 Section header 為區名，底下為該區站點 item
- 每個 item 為可點擊的 row，含站名（粗體）、地址（次要文字）、更新時間

### 3.3 驗收標準（Acceptance Criteria）
- [ ] 列出所有租借站，以區域分群，每站顯示地址、可租借腳踏車數量、腳踏車總數、更新時間
- [ ] 頁面每 3 分鐘自動更新
- [ ] 右上角放大鏡按鈕可依區域過濾顯示租借站
- [ ] 點選租借站可進入該站資訊頁面

---

## 4. 畫面二：租借站詳細頁

### 4.1 功能需求
顯示以下資訊：

版面配置（依 mockup，由上至下）：站名文字 → 地圖圖片 → OPEN DATA 各欄位資訊。

| # | 項目 | 資料來源 | 特殊規則 |
|---|---|---|---|
| a | 地圖標示租借站位置 | `latitude`, `longitude` | - |
| b | 站點代號 | `sno` | - |
| c | 場站中文名稱 | `sna` | - |
| d | 場站總停車格 | `Quantity` | - |
| e | 場站目前車輛數量 | `available_rent_bikes` | **依比例變色**（見 4.2） |
| f | 資料更新時間 | `mday` | 格式化為可讀時間 |
| g | 空位數量 | `available_return_bikes` | - |
| h | YouBike2.0 系統發布資料更新時間 | `srcUpdateTime` | - |

頁面**每 1 分鐘自動重新整理**一次。

### 4.2 車輛數量變色規則

以 `available_rent_bikes / Quantity` 計算比例：

| 條件 | 顏色 |
|---|---|
| 比例 < 20% | 紅色 |
| 20% ≤ 比例 ≤ 50% | 黃色 |
| 比例 > 50% | 綠色 |

### 4.3 驗收標準
- [ ] 地圖標示租借站位置
- [ ] 顯示站點代號
- [ ] 顯示場站中文名稱
- [ ] 顯示場站總停車格
- [ ] 顯示場站目前車輛數量，且依比例變色（<20% 紅／20~50% 黃／>50% 綠）
- [ ] 顯示資料更新時間
- [ ] 顯示空位數量
- [ ] 顯示 YouBike2.0 系統發布資料更新的時間
- [ ] 頁面每 1 分鐘自動更新
