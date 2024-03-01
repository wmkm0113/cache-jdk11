# 缓存工具包

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/cache-jdk11.svg)](https://github.com/wmkm0113/cache-jdk11/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

[English](README.md)
简体中文
[繁體中文](README_zh_TW.md)

为缓存操作打造的统一工具包，使用统一的程序接口，完成不同缓存服务的调用。

**Redis Client:** Jedis 5.1.1, Lettuce 6.3.1.RELEASE, Redisson 3.26.0   
**Memcached Client:** Xmemcached 2.4.8

## JDK版本：
编译：OpenJDK 11   
运行：OpenJDK 11+ 或兼容版本

## 生命周期：
**功能冻结：** 2026年12月31日   
**安全更新：** 2029年12月31日

## 使用方法
### 1、在项目中添加支持
**如果需要所有缓存客户端的支持：**
```
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-nodeps-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```
**如果需要特定缓存客户端的支持：**
```
<!-- 管理器实现类 -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-core-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Jedis客户端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-jedis-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Lettuce客户端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-lettuce-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Redisson客户端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-redisson-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Xmemcached客户端支持 -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-xmemcached-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```

### 2、初始化及获取缓存管理器实例对象
程序开发人员通过调用 org.nervousync.cache.CacheUtils 的 getInstance 静态方法，获取缓存管理器实例对象。
在获取缓存管理器实例对象时，如果缓存管理器未初始化，工具包会自动执行初始化工作，通过Java的SPI机制，寻找存在的缓存管理器实现类，
如果未找到缓存管理器实现类，则抛出异常信息。在初始化过程中，还会通过配置文件管理器读取并注册系统默认的缓存配置信息。

### 3、注册缓存服务器配置信息
程序开发人员通过调用 org.nervousync.cache.CacheUtils 实例对象的 register 方法，传入参数为缓存名称，
系统使用配置文件管理器读取给定缓存名称的缓存配置信息，并使用读取的配置信息注册、初始化缓存，register 方法返回boolean类型的注册结果。

### 4、获取缓存服务器客户端并完成数据操作
程序开发人员通过调用 org.nervousync.cache.CacheUtils 实例对象的 client 方法获取缓存服务器操作客户端，传入参数为缓存名称。
如果缓存名称未注册，则返回 null。

### 5、自定义缓存管理器
程序开发人员可以自定义缓存管理器，来实现自己需要的定制化缓存客户端管理器，具体方法为：   
1、创建缓存客户端管理器实现类，并实现 org.nervousync.cache.api.CacheManager 接口。
2、创建/META-INF/services/org.nervousync.cache.api.CacheManager文件，并在文件中写明实现类的完整名称（包名+类名）。   
**注意：** 整个工程中如果有多个缓存客户端管理器实现类，系统会根据加载顺序选择第一个实现类。

## 贡献与反馈
欢迎各位朋友将此文档及项目中的提示信息、错误信息等翻译为更多语言，以帮助更多的使用者更好地了解与使用此工具包。   
如果在使用过程中发现问题或需要改进、添加相关功能，请提交issue到本项目或发送电子邮件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
为了更好地沟通，请在提交issue或发送电子邮件时，写明如下信息：   
1、目的是：发现Bug/功能改进/添加新功能   
2、请粘贴以下信息（如果存在）：传入数据，预期结果，错误堆栈信息   
3、您认为可能是哪里的代码出现问题（如提供可以帮助我们尽快地找到并解决问题）   
如果您提交的是添加新功能的相关信息，请确保需要添加的功能是一般性的通用需求，即添加的新功能可以帮助到大多数使用者。

如果您需要添加的是定制化的特殊需求，我将收取一定的定制开发费用，具体费用金额根据定制化的特殊需求的工作量进行评估。   
定制化特殊需求请直接发送电子邮件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features)，同时请尽量在邮件中写明您可以负担的开发费用预算金额。

## 赞助与鸣谢
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>非常感谢 <a href="https://www.jetbrains.com/">JetBrains</a> 通过许可证赞助我们的开源项目。</span>
</span>