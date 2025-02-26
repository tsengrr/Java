# 股票數據爬蟲與分析工具

## 介紹
本專案是一個基於 Java 的股票數據爬取與分析工具。程式透過 Jsoup 爬取指定網站的股票數據，並提供多種分析功能，如移動平均數計算、標準差分析、線性回歸等。

## 需求環境
- Java 8 或以上
- Jsoup 1.15.3 或以上

## 文件結構
```
|-- HtmlParser.java       # 主程式，負責執行爬蟲與數據分析
|-- data.csv             # 存放爬取的股票數據
|-- output.csv           # 存放分析結果
|-- README.md            # 說明文件
```

## 使用方法
### 1. 執行爬蟲
執行以下指令以啟動爬蟲，抓取最新的股票數據並存入 `data.csv`：
```sh
java HtmlParser 0
```

### 2. 進行數據分析
透過不同的 `mode` 和 `task` 參數來選擇分析功能。
```sh
java HtmlParser 1 [task] [股票名稱] [開始日期] [結束日期]
```

#### 支援的任務 (`task`)
| Task | 功能描述 |
|------|---------|
| 0 | 匯出 `data.csv` 到 `output.csv` |
| 1 | 計算指定股票的 5 日移動平均 |
| 2 | 計算指定股票的標準差 |
| 3 | 計算所有股票的標準差，並輸出前 3 名變動最大的股票 |
| 4 | 計算指定股票的線性回歸斜率與截距 |

範例：計算股票 "AAPL" 在 20240101 到 20240110 的 5 日移動平均
```sh
java HtmlParser 1 1 AAPL 20240101 20240110
```

## 程式架構
### `HtmlParser.java`
- `main` 方法負責解析輸入參數並呼叫相應的功能。
- `Crawler` 類別負責爬取股票數據。
- `TaskSolver` 類別提供數據分析功能。

### `Crawler` 類別
- `startCrawl()`：爬取網站數據並存入 `data.csv`。
- `saveToCSV(int day, ArrayList<String> nameList, ArrayList<String> stockList)`：檢查是否有重複資料，若無則儲存新數據。

### `TaskSolver` 類別
- `task0()`：將 `data.csv` 內容輸出到 `output.csv`。
- `task1()`：計算 5 日移動平均。
- `task2()`：計算標準差。
- `task3()`：計算所有股票的標準差，並輸出前三名變動最大的股票。
- `task4()`：計算線性回歸斜率與截距。

## 注意事項
- `output.csv` 可能會覆寫舊數據，請確保事先備份。
- 請確保 `data.csv` 存在，否則部分分析功能無法執行。

