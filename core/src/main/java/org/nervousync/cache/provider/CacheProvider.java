/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.cache.provider;

import jakarta.annotation.Nonnull;

import java.util.List;

/**
 * <h2 class="en-US">Cache provider interface</h2>
 * <h2 class="zh-CN">缓存适配器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 14, 2020 10:30:21 $
 */
@SuppressWarnings("unused")
public interface CacheProvider {

	/**
	 * <h3 class="en-US">Get the default port number of the current adapter</h3>
	 * <h3 class="zhs">获取当前适配器的默认端口号</h3>
	 *
	 * @return <span class="en-US">Default port number</span>
	 * <span class="zhs">默认端口号</span>
	 */
	int defaultPort();

	/*
	 * General operations
	 */

	/**
	 * <h3 class="en-US">Copies the value stored at the source key to the destination key</h3>
	 * <h3 class="zh-CN">将源键中存储的值复制到目标键</h3>
	 *
	 * @param source      <span class="en-US">Source key</span>
	 *                    <span class="zh-CN">源键值</span>
	 * @param destination <span class="en-US">Destination key</span>
	 *                    <span class="zh-CN">目标键值</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean copy(@Nonnull final String source, @Nonnull final String destination);

	/**
	 * <h3 class="en-US">Removes the specified keys</h3>
	 * <h3 class="zh-CN">删除指定的键</h3>
	 *
	 * @param keys <span class="en-US">The specified keys</span>
	 *             <span class="zh-CN">指定的键</span>
	 * @return <span class="en-US">Count of removed keys</span>
	 * <span class="zh-CN">删除的记录数</span>
	 */
	long del(@Nonnull final String... keys);

	/**
	 * <h3 class="en-US">Check the specified keys were existed</h3>
	 * <h3 class="zh-CN">检查指定的键是否存在</h3>
	 *
	 * @param keys <span class="en-US">The specified keys</span>
	 *             <span class="zh-CN">指定的键</span>
	 * @return <span class="en-US">Count of removed keys</span>
	 * <span class="zh-CN">删除的记录数</span>
	 */
	long exists(@Nonnull final String... keys);

	/**
	 * <h3 class="en-US">Set expire time to the new given expiry value which cache key was given</h3>
	 * <h3 class="zh-CN">将指定的缓存键值过期时间设置为指定的新值</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param expire <span class="en-US">New expire time</span>
	 *               <span class="zh-CN">新的过期时间</span>
	 */
	void expire(@Nonnull final String key, final int expire);

	/**
	 * <h3 class="en-US">Returns all keys matching the pattern</h3>
	 * <h3 class="zh-CN">使用给定的规则获取缓存键列表</h3>
	 *
	 * @param pattern <span class="en-US">Matching pattern string</span>
	 *                <span class="zh-CN">匹配规则字符串</span>
	 * @return <span class="en-US">List of matched cache keys</span>
	 * <span class="zh-CN">缓存键列表</span>
	 */
	List<String> keys(@Nonnull final String pattern);

	/**
	 * <h3 class="en-US">Remove expire time by the given cache key</h3>
	 * <h3 class="zh-CN">删除缓存键值的过期时间</h3>
	 *
	 * @param key <span class="en-US">Cache key</span>
	 *            <span class="zh-CN">缓存键值</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean persist(@Nonnull final String key);

	/**
	 * <h3 class="en-US">Rename the cache key to the new cache key</h3>
	 * <h3 class="zh-CN">将源键中存储的值改名为新键值</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param newKey <span class="en-US">New cache key</span>
	 *               <span class="zh-CN">新缓存键值</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean rename(@Nonnull final String key, @Nonnull final String newKey);

	/**
	 * <h3 class="en-US">Execute touch operate which cache key was given</h3>
	 * <h3 class="zh-CN">修改指定缓存键值的最后访问时间</h3>
	 *
	 * @param keys <span class="en-US">Cache keys array strings</span>
	 *             <span class="zh-CN">缓存键值数组</span>
	 * @return <span class="en-US">Count of touched keys</span>
	 * <span class="zh-CN">修改的记录数</span>
	 */
	long touch(@Nonnull final String... keys);

	/**
	 * <h3 class="en-US">Get the expiry time which cache key was given</h3>
	 * <h3 class="zh-CN">获取指定缓存键值的过期时间</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @return <span class="en-US">Count of touched keys</span>
	 * <span class="zh-CN">修改的记录数</span>
	 */
	long ttl(@Nonnull final String key);

	/*
	 * String operations
	 */

	/**
	 * <h3 class="en-US">Add a new key-value to the cache server and set expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间添加缓存信息</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param value  <span class="en-US">Cache value</span>
	 *               <span class="zh-CN">缓存数据</span>
	 * @param expire <span class="en-US">Expire time</span>
	 *               <span class="zh-CN">过期时间</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean add(@Nonnull final String key, @Nonnull final String value, final int expire);

	/**
	 * <h3 class="en-US">Append string to the given cache key</h3>
	 * <h3 class="zh-CN">向缓存值中追加字符串</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param append <span class="en-US">Cache value</span>
	 *               <span class="zh-CN">缓存数据</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	long append(@Nonnull final String key, @Nonnull final String append);

	/**
	 * <h3 class="en-US">Decrement data by given cache key and value</h3>
	 * <h3 class="zh-CN">对给定的缓存键值执行自减操作，减少值为给定的步进值</h3>
	 *
	 * @param key  <span class="en-US">Cache key</span>
	 *             <span class="zh-CN">缓存键值</span>
	 * @param step <span class="en-US">Decrement step value</span>
	 *             <span class="zh-CN">自减步进值</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	long decr(@Nonnull final String key, final long step);

	/**
	 * <h3 class="en-US">Read cache value from the cache key which cache key was given</h3>
	 * <h3 class="zh-CN">读取指定缓存键值对应的缓存数据</h3>
	 *
	 * @param key <span class="en-US">Cache key</span>
	 *            <span class="zh-CN">缓存键值</span>
	 * @return <span class="en-US">Cache value or null if cache key did not exist, or it was expired</span>
	 * <span class="zh-CN">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String get(@Nonnull final String key);

	/**
	 * <h3 class="en-US">Read and delete the cache value from the cache key which cache key was given</h3>
	 * <h3 class="zh-CN">读取并删除指定缓存键值对应的缓存数据</h3>
	 *
	 * @param key <span class="en-US">Cache key</span>
	 *            <span class="zh-CN">缓存键值</span>
	 * @return <span class="en-US">Cache value or null if cache key did not exist, or it was expired</span>
	 * <span class="zh-CN">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String getDel(@Nonnull final String key);

	/**
	 * <h3 class="en-US">Read the cache value from the cache key which the cache key was given and set expire time by the given value</h3>
	 * <h3 class="zh-CN">读取指定缓存键值对应的缓存数据并设置过期时间为给定值</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param expire <span class="en-US">Expire time</span>
	 *               <span class="zh-CN">过期时间</span>
	 * @return <span class="en-US">Cache value or null if cache key did not exist, or it was expired</span>
	 * <span class="zh-CN">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String getEx(@Nonnull final String key, final int expire);

	/**
	 * <h3 class="en-US">Read the part of the cache value from the cache key which cache key was given</h3>
	 * <h3 class="zh-CN">读取指定缓存键值对应的缓存数据的部分字符串</h3>
	 *
	 * @param key   <span class="en-US">Cache key</span>
	 *              <span class="zh-CN">缓存键值</span>
	 * @param begin <span class="en-US">Begin position</span>
	 *              <span class="zh-CN">起始位置</span>
	 * @param end   <span class="en-US">End position</span>
	 *              <span class="zh-CN">终止位置</span>
	 * @return <span class="en-US">Cache value or null if cache key did not exist, or it was expired</span>
	 * <span class="zh-CN">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String getRange(@Nonnull final String key, final int begin, final int end);

	/**
	 * <h3 class="en-US">Read the cache value from the cache key which the cache key was given and set the new cache value</h3>
	 * <h3 class="zh-CN">读取指定缓存键值对应的缓存数据并设置新的缓存值</h3>
	 *
	 * @param key   <span class="en-US">Cache key</span>
	 *              <span class="zh-CN">缓存键值</span>
	 * @param value <span class="en-US">Cache value</span>
	 *              <span class="zh-CN">缓存数据</span>
	 * @return <span class="en-US">Cache value or null if cache key did not exist, or it was expired</span>
	 * <span class="zh-CN">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String getSet(@Nonnull final String key, @Nonnull final String value);

	/**
	 * <h3 class="en-US">Increment data by given cache key and value</h3>
	 * <h3 class="zh-CN">对给定的缓存键值执行自增操作，增加值为给定的步进值</h3>
	 *
	 * @param key  <span class="en-US">Cache key</span>
	 *             <span class="zh-CN">缓存键值</span>
	 * @param step <span class="en-US">Increment step value</span>
	 *             <span class="zh-CN">自增步进值</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	long incr(@Nonnull final String key, final long step);

	/**
	 * <h3 class="en-US">Increment data by given cache key and value</h3>
	 * <h3 class="zh-CN">对给定的缓存键值执行自增操作，增加值为给定的步进值</h3>
	 *
	 * @param key  <span class="en-US">Cache key</span>
	 *             <span class="zh-CN">缓存键值</span>
	 * @param step <span class="en-US">Increment step value</span>
	 *             <span class="zh-CN">自增步进值</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	double incrFloat(@Nonnull final String key, final double step);

	/**
	 * <h3 class="en-US">Compares two cached values for a given cache key and retrieves the cached values that contain the same string.</h3>
	 * <h3 class="zh-CN">比较两个给定缓存键的缓存值，获取缓存值中包含的相同字符串</h3>
	 *
	 * @param key1 <span class="en-US">Cache key1</span>
	 *             <span class="zh-CN">缓存键值1</span>
	 * @param key2 <span class="en-US">Cache key2</span>
	 *             <span class="zh-CN">缓存键值2</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	String lcs(@Nonnull final String key1, @Nonnull final String key2);

	/**
	 * <h3 class="en-US">Compares two cached values for a given cache key and retrieves the cached values that contain the same string length.</h3>
	 * <h3 class="zh-CN">比较两个给定缓存键的缓存值，获取缓存值中包含的相同字符串长度</h3>
	 *
	 * @param key1 <span class="en-US">Cache key1</span>
	 *             <span class="zh-CN">缓存键值1</span>
	 * @param key2 <span class="en-US">Cache key2</span>
	 *             <span class="zh-CN">缓存键值2</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	long lcsLen(@Nonnull final String key1, @Nonnull final String key2);

	/**
	 * <h3 class="en-US">Read the list of cache value from the cache keys array which cache keys array was given</h3>
	 * <h3 class="zh-CN">读取指定缓存键值数组对应的缓存数据列表</h3>
	 *
	 * @param keys <span class="en-US">Cache keys array</span>
	 *             <span class="zh-CN">缓存键值数组</span>
	 * @return <span class="en-US">Cache value list</span>
	 * <span class="zh-CN">读取的缓存数据列表</span>
	 */
	List<String> mget(@Nonnull final String... keys);

	/**
	 * <h3 class="en-US">Set the cache values of the given cache key-value pairs</h3>
	 * <h3 class="zh-CN">设置给定的缓存键值对</h3>
	 *
	 * @param keyvalues <span class="en-US">Cache key-value pairs array</span>
	 *                  <span class="zh-CN">缓存键值对数组</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean mset(@Nonnull final String... keyvalues);

	/**
	 * <h3 class="en-US">Override the cache values of the given cache key-value pairs</h3>
	 * <h3 class="zh-CN">覆盖给定的缓存键值对</h3>
	 *
	 * @param keyvalues <span class="en-US">Cache key-value pairs array</span>
	 *                  <span class="zh-CN">缓存键值对数组</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean msetnx(@Nonnull final String... keyvalues);

	/**
	 * <h3 class="en-US">Replace exists value of the given key by given value and set expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间替换已存在的缓存信息</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param value  <span class="en-US">Cache value</span>
	 *               <span class="zh-CN">缓存数据</span>
	 * @param expire <span class="en-US">Expire time</span>
	 *               <span class="zh-CN">过期时间</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean replace(@Nonnull final String key, @Nonnull final String value, final int expire);

	/**
	 * <h3 class="en-US">Set key-value to the cache server and set expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间设置缓存信息</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param value  <span class="en-US">Cache value</span>
	 *               <span class="zh-CN">缓存数据</span>
	 * @param expire <span class="en-US">Expire time</span>
	 *               <span class="zh-CN">过期时间</span>
	 * @return <span class="en-US">Operate result</span>
	 * <span class="zh-CN">操作结果</span>
	 */
	boolean set(@Nonnull final String key, @Nonnull final String value, final int expire);

	/**
	 * <h3 class="en-US">Overwrites part of the string stored at the key, starting at the specified offset, for the entire length of value.</h3>
	 * <h3 class="zh-CN">从指定的偏移量开始，覆盖存储在键中的字符串的一部分，直至值的整个长度。</h3>
	 *
	 * @param key    <span class="en-US">Cache key</span>
	 *               <span class="zh-CN">缓存键值</span>
	 * @param offset <span class="en-US">Offset value</span>
	 *               <span class="zh-CN">偏移量</span>
	 * @param value  <span class="en-US">Cache value</span>
	 *               <span class="zh-CN">缓存数据</span>
	 * @return <span class="en-US">The length of the string after it was modified by the command.</span>
	 * <span class="zh-CN">字符串被命令修改后的长度。</span>
	 */
	long setRange(@Nonnull final String key, final int offset, @Nonnull final String value);

	/**
	 * <h3 class="en-US">Get the length of the string which stored at the key</h3>
	 * <h3 class="zh-CN">获取给定缓存数据的字符串长度</h3>
	 *
	 * @param key <span class="en-US">Cache key</span>
	 *            <span class="zh-CN">缓存键值</span>
	 * @return <span class="en-US">The length of the string. If the cache value not string type, return -1</span>
	 * <span class="zh-CN">字符串长度。如果缓存数据非字符串，则返回 -1</span>
	 */
	long strLen(@Nonnull final String key);

	/**
	 * <h3 class="en-US">Destroy agent instance</h3>
	 * <h3 class="zhs">销毁缓存对象</h3>
	 */
	void destroy();
}
