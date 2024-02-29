# Cache Toolkit

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/cache-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/Cache.svg)](https://github.com/wmkm0113/Cache/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

English
[简体中文](README_zh_CN.md)

A unified toolkit created for caching operations, using a unified program interface to complete the invocation of different caching services.

**Redis Client:** Jedis 5.1.1, Lettuce 6.3.1.RELEASE, Redisson 3.26.0   
**Memcached Client:** Xmemcached 2.4.8

## Usage
```
<!-- Contains all implement providers -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-nodeps-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Choose provider to use -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-core-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Jedis -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-jedis-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Lettuce -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-lettuce-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Xmemcached -->
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>cache-xmemcached-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```

## CacheCore
**Package:** org.nervousync.cache.core

Register cache config and generate cache agent. Please execute static method CacheCore.destroy() when system shutdown.

## CacheConfigBuilder
**Package:** org.nervousync.cache.builder
Using program to generate or reconfigure the cache configure information.

## cache-config.xsd
**Package:** org.nervousync.cache.resources
Configure file XML Schemas Definition.