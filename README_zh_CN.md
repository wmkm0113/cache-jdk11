# 缓存工具包

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/Cache.svg)](https://github.com/wmkm0113/Cache/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

[English](README.md)
简体中文

为缓存操作打造的统一工具包，使用统一的程序接口，完成不同缓存服务的调用。

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
程序开发人员通过调用 org.nervousync.cache.CacheUtils 的 getInstance 静态方法，获取缓存管理器实例对象。
在获取缓存管理器实例对象时，如果缓存管理器未初始化，工具包会自动执行初始化工作，通过Java的SPI机制，寻找存在的缓存管理器实现类，
如果未找到缓存管理器实现类，则抛出异常信息。在初始化过程中，还会通过配置文件管理器读取并注册系统默认的缓存配置信息。

### 3. Register cache server configure information
程序开发人员通过调用 org.nervousync.cache.CacheUtils 实例对象的 register 方法，传入参数为缓存名称，
系统使用配置文件管理器读取给定缓存名称的缓存配置信息，并使用读取的配置信息注册、初始化缓存，register 方法返回boolean类型的注册结果。

### 4. Obtain cache server client instance and operate data
程序开发人员通过调用 org.nervousync.cache.CacheUtils 实例对象的 client 方法获取缓存服务器操作客户端，传入参数为缓存名称。
如果缓存名称未注册，则返回 null。

### 5. Customize cache client manager implements class
程序开发人员可以自定义缓存管理器，来实现自己需要的定制化缓存客户端管理器，具体方法为：   
1、创建缓存客户端管理器实现类，并实现 org.nervousync.cache.api.CacheManager 接口。
2、创建/META-INF/services/org.nervousync.cache.api.CacheManager文件，并在文件中写明实现类的完整名称（包名+类名）。   
**注意：** 整个工程中如果有多个缓存客户端管理器实现类，系统会根据加载顺序选择第一个实现类。

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