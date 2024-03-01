# Cache Toolkit

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/cache-jdk11.svg)](https://github.com/wmkm0113/cache-jdk11/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

English
[简体中文](README_zh_CN.md)
[繁體中文](README_zh_TW.md)

A unified toolkit created for caching operations, using a unified program interface to complete the invocation of different caching services.

**Redis Client:** Jedis 5.1.1, Lettuce 6.3.1.RELEASE, Redisson 3.26.0   
**Memcached Client:** Xmemcached 2.4.8

## JDK Version
Compile：OpenJDK 11   
Runtime: OpenJDK 11+ or compatible version

## End of Life

**Features Freeze:** 31, Dec, 2026   
**Secure Patch:** 31, Dec, 2029

## Usage
### 1. Add support to the project
**If developers need all client support:**
```
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-nodeps-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```
**If developers need special client support:**
```
<!-- Cache manager implements class -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-core-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Jedis client support -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-jedis-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Lettuce client support -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-lettuce-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Redisson client support -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-redisson-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Xmemcached client support -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-xmemcached-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```

### 2. Initialize and obtain the cache utility instance
Program developers obtain the cache manager instance object by calling the getInstance static method of org.nervousync.cache.CacheUtils.
When obtaining the cache manager instance object, if the cache manager has not been initialized, the toolkit will automatically perform the initialization work and search for the existing cache manager implementation class through Java's SPI mechanism. 
If the cache manager implementation class is not found, it will throw Exception information appears. During the initialization process, the system's default cache configuration information is also read and registered through the configuration file manager.

### 3. Register cache server configure information
Program developers call the register method of the org.nervousync.cache.CacheUtils instance object and pass in the cache name as the parameter. 
The system uses the configuration file manager to read the cache configuration information of the given cache name and registers it using the read configuration information. 
Initialize the cache, and the register method returns the registration result of the boolean type.

### 4. Obtain cache server client instance and operate data
Program developers obtain the cache server operation client by calling the client method of the org.nervousync.cache.CacheUtils instance object, passing in the cache name as the parameter.
Returns null if the cache name is not registered.

### 5. Customize cache client manager implements class
Program developers can customize the cache manager to implement the customized cache client manager they need. The specific method is:   
1. Create a cache client manager implementation class and implement the org.nervousync.cache.api.CacheManager interface.   
2. Create the /META-INF/services/org.nervousync.cache.api.CacheManager file and write the complete name of the implementation class (package name + class name) in the file.   
**Notice:** If there are multiple cache client manager implementation classes in the entire project, the system will select the first implementation class based on the loading order.

## Contributions and feedback
Friends are welcome to translate the prompt information, error messages, 
etc. in this document and project into more languages to help more users better understand and use this toolkit.   
If you find problems during use or need to improve or add related functions, please submit an issue to this project
or send email to [wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
For better communication, please include the following information when submitting an issue or sending an email:
1. The purpose is: discover bugs/function improvements/add new features   
2. Please paste the following information (if it exists): incoming data, expected results, error stack information   
3. Where do you think there may be a problem with the code (if provided, it can help us find and solve the problem as soon as possible)

If you are submitting information about adding new features, please ensure that the features to be added are general needs, that is, the new features can help most users.

If you need to add customized special requirements, I will charge a certain custom development fee.
The specific fee amount will be assessed based on the workload of the customized special requirements.   
For customized special features, please send an email directly to [wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features). At the same time, please try to indicate the budget amount of development cost you can afford in the email.

## Sponsorship and Thanks To
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>Many thanks to <a href="https://www.jetbrains.com/">JetBrains</a> for sponsoring our Open Source projects with a license.</span>
</span>