# TF-IDF Calculator

這是一個使用 Trie 資料結構來計算 TF-IDF（詞頻-逆向文件頻率）的 Java 程式。

- `<文件名稱>`：包含多個文檔內容的文件。
- `<查詢測試檔案>`：包含測試關鍵字及對應查詢文檔索引的文件。

### 輸入格式
#### 文件（`<文件名稱>`）
文件中的內容會被程式切分成多個 5 行的段落，每個段落視為一個獨立的文檔。

#### 測試檔案（`<查詢測試檔案>`）
測試檔案包含兩行：
- 第一行：需要查詢的關鍵字，以空格分隔。
- 第二行：對應的文檔索引（0-based），每個索引對應第一行的關鍵字。

### 輸出
程式會產生 `output.txt`，其中包含計算出的 TF-IDF 值，每個查詢結果以空格分隔。

## 程式結構
- `TFIDFCalculator`：主程式，負責讀取檔案並計算 TF-IDF。
- `tidyUpDoc`：將原始文本清理並拆分成文檔。
- `Trie`：使用 Trie 來計算詞頻。
- `tf`：計算 TF（詞頻）。
- `idf`：計算 IDF（逆向文件頻率）。
- `tfIdfCalculate`：計算最終的 TF-IDF 值。

## 範例
### 文件內容 (`documents.txt`)
```
This is a sample document.
It contains sample words.
The text is simple and clear.
We use it for TF-IDF calculation.
Another sentence in this document.
...
```

### 測試檔案 (`queries.txt`)
```
sample document TF-IDF
0 1 2
```

### 輸出 (`output.txt`)
```
0.17609 0.30103 0.47712
```

