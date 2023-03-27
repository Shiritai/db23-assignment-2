# Lab 2

## Implementation of transaction using JDBC and SP

### 小細節
* <font color="#E621FA">As2BenchTransactionType</font> 新增一個種類 `UPDATE_PRICE`
* <font color="#E621FA">As2BenchJdbcExecutor & As2BenchStoredProcFactory</font> 各別新增一個 case 對應 TxType = `UPDATE_PRICE`
* <font color="#E621FA">As2BenchmarkRte</font> 靜態生成兩種 TxExecutor，差別在於分別使用 As2ReadItemParamGen 和 As2UpdatePriceParamGen。在 *getNextTxType()* 這個函式中，根據 `READ_WRITE_TX_RATE` 這個機率決定此次 TxType 為 read or update，然後根據此結果，*getTxExecutor()* 這個函式就會回傳對應的 TxExecutor

### JDBC
<font color="#E621FA">As2UpdatePriceParamGen</font> 會生成需要的參數，內容為 [update 次數, 10 個 update id, 10 個 price raise]。而當要執行 <font color="#E621FA">UpdatePriceJdbcJob</font> 時，<font color="#E621FA">UpdatePriceProcParamHelperc</font> 會幫忙把上述傳入的參數進行解析，這樣就能知道要更新的次數以及每次操作對應的 id 與價格變化量。接著進行每筆 update時 ，利用JdbcConnection 新增 Statement，先透過 ***executeQuery*** 執行sql指令 <font color="#FA4721">SELECT i_name, i_price FROM item WHERE i_id = iid</font>, 將滿足條件的資料讀取出來，將更新後的價錢設為i_price+price raise, 但若是超過 As2BenchConstants.MAX_PRICE, 則新將價錢設為 As2BenchConstants.MIN_PRICE，最後在透過 **executeUpdate** 執行sql指令<font color="#FA4721">UPDATE item SET i_price =  newPrice WHERE i_id = iid</font>, 將更改完的資料寫回資料庫中。


### Stored Procedure
<font color="#E621FA"> UpdatePriceProcParamHelper </font> 用來記錄哪些item的price被update以及把 parameter generator產生的參數根據功能分別存在array中。呼叫 <font color="#E621FA"> UpdatePriceProc </font> 中的 method ***executesql()*** 時，從<font color="#E621FA"> UpdatePriceProcParamHelper </font> 中的 getter 把需要update的 item id、需要提升多少 price 取出，接著透過 StoredProcedureHelper 的 method ***executeQuery()*** 執行跟 JDBC 一樣的sql query <font color="#FA4721"> SELECT i_name, i_price FROM item WHERE i_id = iid </font>。接著執行 StoredProcedureHelper 的 method ***executeUpdate()*** 執行跟JDBC一樣的sql query <font color="#FA4721">UPDATE item SET i_price =  newPrice WHERE i_id = iid</font>。最後將更新過的item 的 price、name 用 <font color="#E621FA"> UpdatePriceProcParamHelper </font> 的 setter 將新的值存回 array 中

## Experiment

> with screenshot of CSV produced by `StatisticMgr`

### Environment 1

|CPU|RAM|Disk|OS|
|:-:|:-:|:-:|:-:|
|Intel Core i3-12100 3.3GHz|16GB|512GB SSD|Ubuntu 22.04|

|Ratio R/W (%)|JDBC|SP|Ratio R/W (%)|JDBC|SP|
|:-:|:-:|:-:|:-:|:-:|:-:|
|100/0|<img src="https://i.imgur.com/nhk6aPm.png"/>|<img src="https://i.imgur.com/EZJv1BZ.png"/>|25/75|<img src="https://i.imgur.com/XgOeEgI.png"/>|<img src="https://i.imgur.com/mwACEFT.png"/>|
|75/25|<img src="https://i.imgur.com/BllkC3W.png"/>|<img src="https://i.imgur.com/b6g2lEZ.png"/>|0/100|<img src="https://i.imgur.com/pxHihvp.png"/>|<img src="https://i.imgur.com/FMkjL0f.png"/>|
|50/50|<img src="https://i.imgur.com/onEPmIC.png"/>|<img src="https://i.imgur.com/J1eHgeT.png"/>|

### Environment 2

|CPU|RAM|Disk|OS|
|:-:|:-:|:-:|:-:|
|Intel Core i7-1065G7 1.3GHz|16GB|477GB SSD|Windows 10|

|Ratio R/W (%)|JDBC|SP|Ratio R/W (%)|JDBC|SP|
|:-:|:-:|:-:|:-:|:-:|:-:|
|100/0|<img src="https://i.imgur.com/yh0yOOb.png" />|<img src="https://i.imgur.com/hqdp9au.png" />|25/75|<img src="https://i.imgur.com/UDNhoWT.png" />|<img src="https://i.imgur.com/VfTT7BT.png" />|
|75/25|<img src="https://i.imgur.com/VxXfJGb.png" />|<img src="https://i.imgur.com/FMl6SdI.png" />|0/100|<img src="https://i.imgur.com/LcOhTMo.png" />|<img src="https://i.imgur.com/kYcg6t4.png" />|
|50/50|<img src="https://i.imgur.com/7q96bbB.png" />|<img src="https://i.imgur.com/6g2DVKM.png" />|

### Environment 3

|CPU|RAM|Disk|OS|
|:-:|:-:|:-:|:-:|
|Intel Core i5-8265U 1.6GHz|20GB|931GB|Windows 10|

|Ratio R/W (%)|JDBC|SP|Ratio R/W (%)|JDBC|SP|
|:-:|:-:|:-:|:-:|:-:|:-:|
|100/0|<img src="https://i.imgur.com/36VExuL.png" />|<img src="https://i.imgur.com/1xXl5eX.png" />|25/75|<img src="https://i.imgur.com/oknUBD0.png" />|<img src="https://i.imgur.com/p2GgJmB.png" />|
|75/25|<img src="https://i.imgur.com/GKUFWFh.png" />|<img src="https://i.imgur.com/VbGn3tN.png" />|0/100|<img src="https://i.imgur.com/urskvmv.png" />|<img src="https://i.imgur.com/WgJztZH.png" />|
|50/50|<img src="https://i.imgur.com/g5uMhha.png" />|<img src="https://i.imgur.com/AoZ9MHr.png" />|

### Analysis and explanation

* JDBC v.s. SP
    
    |             JDBC over SP             |             SP over JDBC             |
    | :----------------------------------: | :----------------------------------: |
    | ![](https://i.imgur.com/Y4vJ7Ua.png) | ![](https://i.imgur.com/2zhjuFo.png) |
    
    由上分析可知 SP 在吞吐量大勝 JDBC 約 6~16 倍，在不同平台上平均延遲 (平均、25 分位數、中位數、75 分位數) 也有約 3~16 倍的差距，足見在略過呼叫 JDBC 介面的情況下，Stored Procedure 擁有遠勝於套皮後的 query/update 速度。
    細究之，以 `executeQuery` 於 JDBC 和 Stored Procedure 的行為來說...
    
    * 後者直接呼叫 `VanillaDb::newPlanner` 取得 planer 後呼叫 `Planner::createQueryPlan`，並然後呼叫 `Plan::open`，連續的呼叫一氣呵成。
    * 前者呼叫後會透過介面呼叫 `JdbcStatement::executeQuery`，接著呼叫 `RemoteStatementImpl::executeQuery`，然後建構 `RemoteResultSetImpl` 才呼叫 `VanillaDb::newPlanner` 取得 planer 後呼叫 `Planner::createQueryPlan`，並然後呼叫 `Plan::open`。還沒完，還要包成 `JdbcResultSet` 傳送回去。
    
    對前者的追蹤實際上還跳過一些處理，足見兩者 Function call 負擔的極大差距。
    
* Performance w.r.t. R/W ratio, based on 100% read
    ![](https://i.imgur.com/lNyctux.png)
    基本上吞吐量隨 write 的比率提高逐漸降低，延遲則反之，原因也十分清楚，即 Update 由於比起 Read 多進行了更新，導致執行時間較長。
    
* Platform
    ![](https://i.imgur.com/OytGx5j.png)
    不同平台下結果大相徑庭，Ubuntu server (env1) 遠勝其他兩者的原因可能有以下。
    * env2 SSD 空間不足，該 SSD 可能由於空間過少而難以使用 SLC 模擬加速，導致 IO burst 時效率遠低於其他兩者。
    * env2 讀寫速度 (R/W 1700/1400 Mb/s) 較其他兩者低 (e.g. env1 R/W 3500/2300 Mb/s)
    * Ubuntu Server (env1) 除了啟動 JVM、數個 VSCode 工作空間與相應套件之外，沒有額外的工作，讓系統可以專注服務 CPU/IO，反之 env2 同時進行 IO burst 的其他工作，這使執行時間可用的 IO 資源進一步減少。