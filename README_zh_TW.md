# 緩存工具包

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/Cache.svg)](https://github.com/wmkm0113/Cache/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

[English](README.md)
簡體中文

為緩存操作打造的統一工具包，使用統一的程式介面，完成不同緩存服務的調用。

**Redis Client:** Jedis 5.1.1, Lettuce 6.3.1.RELEASE, Redisson 3.26.0   
**Memcached Client:** Xmemcached 2.4.8

## JDK版本：
編譯：OpenJDK 11   
運行：OpenJDK 11+ 或相容版本

## 生命週期：
**功能凍結：** 2026年12月31日   
**安全更新：** 2029年12月31日

## 使用方法
### 1、在專案中添加支持
**如果需要所有緩存用戶端的支持：**
```
<dependency>
    <groupId>org.nervousync</groupId>
    <artifactId>cache-nodeps-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```
**如果需要特定緩存用戶端的支持：**
```
<!-- 管理器實現類 -->
<dependency>
    <groupId>org.nervousync</groupId>
    <artifactId>cache-core-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Jedis用戶端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
    <artifactId>cache-jedis-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Lettuce用戶端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
    <artifactId>cache-lettuce-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Redisson用戶端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
    <artifactId>cache-redisson-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Xmemcached用戶端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
    <artifactId>cache-xmemcached-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```

### 2、初始化及獲取緩存管理器實例物件
程式開發人員通過調用 org.nervousync.cache.CacheUtils 的 getInstance 靜態方法，獲取緩存管理器實例物件。
在獲取緩存管理器實例物件時，如果緩存管理器未初始化，工具包會自動執行初始化工作，通過Java的SPI機制，尋找存在的緩存管理器實現類，
如果未找到緩存管理器實現類，則拋出異常資訊。在初始化過程中，還會通過設定檔管理員讀取並註冊系統預設的緩存配置資訊。

### 3、註冊緩存伺服器配置資訊
程式開發人員通過調用 org.nervousync.cache.CacheUtils 實例物件的 register 方法，傳入參數為緩存名稱，
系統使用設定檔管理員讀取給定緩存名稱的緩存配置資訊，並使用讀取的配置資訊註冊、初始化緩存，register 方法返回boolean類型的註冊結果。

### 4、獲取緩存伺服器用戶端並完成資料操作
程式開發人員通過調用 org.nervousync.cache.CacheUtils 實例物件的 client 方法獲取緩存伺服器操作用戶端，傳入參數為緩存名稱。
如果緩存名稱未註冊，則返回 null。

### 5、自訂緩存管理器
程式開發人員可以自訂緩存管理器，來實現自己需要的定制化緩存用戶端管理器，具體方法為：   
1、創建緩存用戶端管理器實現類，並實現 org.nervousync.cache.api.CacheManager 介面。
2、創建/META-INF/services/org.nervousync.cache.api.CacheManager檔，並在檔中寫明實現類的完整名稱（包名+類名）。   
**注意：** 整個工程中如果有多個緩存用戶端管理器實現類，系統會根據載入順序選擇第一個實現類。

## 貢獻與回饋
歡迎各位朋友將此文檔及專案中的提示資訊、錯誤資訊等翻譯為更多語言，以説明更多的使用者更好地瞭解與使用此工具包。   
如果在使用過程中發現問題或需要改進、添加相關功能，請提交issue到本專案或發送電子郵件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
為了更好地溝通，請在提交issue或發送電子郵件時，寫明如下資訊：   
1、目的是：發現Bug/功能改進/添加新功能   
2、請粘貼以下資訊（如果存在）：傳入資料，預期結果，錯誤堆疊資訊   
3、您認為可能是哪裡的代碼出現問題（如提供可以幫助我們儘快地找到並解決問題）   
如果您提交的是添加新功能的相關資訊，請確保需要添加的功能是一般性的通用需求，即添加的新功能可以幫助到大多數使用者。

如果您需要添加的是定制化的特殊需求，我將收取一定的定制開發費用，具體費用金額根據定制化的特殊需求的工作量進行評估。   
定制化特殊需求請直接發送電子郵件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features)，同時請儘量在郵件中寫明您可以負擔的開發費用預算金額。

## 贊助與鳴謝
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>非常感謝 <a href="https://www.jetbrains.com/">JetBrains</a> 通過許可證贊助我們的開源項目。</span>
</span>
