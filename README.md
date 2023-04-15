# DBMS Assignment 2 實作紀錄

> NTHU CS 471000 Introduction to Database Systems 2023 Spring

## Note

本 Repository 為資料庫系統概論作業的 Mirror，該作旨在新增 Store Procedure、調整測試讀寫比率以及統計資料分析等功能，並實驗採用 JDBC 和 Store Procedure 的效能差距。

## Run the project

本專案使用純 Maven 管理套件 (不依賴如 Eclipse 等 IDE) 運行，如於 `bench` 資料夾下的終端機使用以下指令：

1. 運行 server
    ```bash
    mvn exec:exec -Pserver_startup
    ```
2. 載入 testbed
    ```bash
    mvn exec:exec -Pclient_load
    ```
3. 運行 benchmark
    ```bash
    mvn exec:exec -Pclient_bench
    ```

## Author

My teammates 109062314, 109062315 and me, thanks for their contribution :)

Please see commits for more details.
