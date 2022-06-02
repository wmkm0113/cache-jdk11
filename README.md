# Cache Utils
Nervousync® Cache Utils. Provider implement class loaded by Java SPI.

**Redis Client:** Jedis 4.2.3, Lettuce 6.1.8.RELEASE

**Memcached Client:** Xmemcached 2.4.7

## Usage
```
<!-- Contains all implement providers -->
<dependency>
    <groupId>org.nervousync.cache</groupId>
	<artifactId>nodeps-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Choose provider to use -->
<dependency>
    <groupId>org.nervousync.cache</groupId>
	<artifactId>core-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Jedis -->
<dependency>
    <groupId>org.nervousync.cache</groupId>
	<artifactId>jedis-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Lettuce -->
<dependency>
    <groupId>org.nervousync.cache</groupId>
	<artifactId>lettuce-jdk11</artifactId>
    <version>${version}</version>
</dependency>
<!-- Xmemcached -->
<dependency>
    <groupId>org.nervousync.cache</groupId>
	<artifactId>xmemcached-jdk11</artifactId>
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