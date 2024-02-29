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
package org.nervousync.cache.api;

/**
 * <h2 class="en-US">Cache client interface</h2>
 * <h2 class="zh-CN">缓存客户端接口</h2>
 */
public interface CacheClient {

	/**
	 * <span class="en-US">Default cache expire time</span>
	 * <span class="zh-CN">默认缓存有效时间</span>
	 */
	int DEFAULT_EXPIRE_TIME = -1;

	/**
	 * <h3 class="en-US">Set key-value to cache server, using default expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param value		<span class="en-US">Cache value</span>
	 *                  <span class="zh-CN">缓存数据</span>
	 */
	default void set(final String key, final String value) {
		this.set(key, value, DEFAULT_EXPIRE_TIME);
	}

	/**
	 * <h3 class="en-US">Set key-value to cache server and set expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param value		<span class="en-US">Cache value</span>
	 *                  <span class="zh-CN">缓存数据</span>
	 * @param expire	<span class="en-US">Expire time</span>
	 *                  <span class="zh-CN">过期时间</span>
	 */
	void set(final String key, final String value, final int expire);

	/**
	 * <h3 class="en-US">Add a new key-value to cache server, using default expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间添加缓存信息</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param value		<span class="en-US">Cache value</span>
	 *                  <span class="zh-CN">缓存数据</span>
	 */
	default void add(final String key, final String value) {
		this.add(key, value, DEFAULT_EXPIRE_TIME);
	}

	/**
	 * <h3 class="en-US">Add a new key-value to cache server and set expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间添加缓存信息</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param value		<span class="en-US">Cache value</span>
	 *                  <span class="zh-CN">缓存数据</span>
	 * @param expire	<span class="en-US">Expire time</span>
	 *                  <span class="zh-CN">过期时间</span>
	 */
	void add(final String key, final String value, final int expire);

	/**
	 * <h3 class="en-US">Replace exists value of given key by given value, using default expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间替换已存在的缓存信息</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param value		<span class="en-US">Cache value</span>
	 *                  <span class="zh-CN">缓存数据</span>
	 */
	default void replace(final String key, final String value) {
		this.replace(key, value, DEFAULT_EXPIRE_TIME);
	}

	/**
	 * <h3 class="en-US">Replace exists value of given key by given value and set expire time</h3>
	 * <h3 class="zh-CN">使用指定的过期时间替换已存在的缓存信息</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param value		<span class="en-US">Cache value</span>
	 *                  <span class="zh-CN">缓存数据</span>
	 * @param expire	<span class="en-US">Expire time</span>
	 *                  <span class="zh-CN">过期时间</span>
	 */
	void replace(final String key, final String value, final int expire);

	/**
	 * <h3 class="en-US">Set expire time to new given expire value which cache key was given</h3>
	 * <h3 class="zh-CN">将指定的缓存键值过期时间设置为指定的新值</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param expire	<span class="en-US">New expire time</span>
	 *                  <span class="zh-CN">新的过期时间</span>
	 */
	void expire(final String key, final int expire);

	/**
	 * <h3 class="en-US">Execute touch operate which cache key was given</h3>
	 * <h3 class="zh-CN">修改指定缓存键值的最后访问时间</h3>
	 *
	 * @param keys      <span class="en-US">Cache keys array strings</span>
	 *                  <span class="zh-CN">缓存键值数组</span>
	 */
	void touch(final String... keys);

	/**
	 * <h3 class="en-US">Remove cache key-value from cache server</h3>
	 * <h3 class="zh-CN">移除指定的缓存键值</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 */
	void delete(final String key);

	/**
	 * <h3 class="en-US">Read cache value from cache key which cache key was given</h3>
	 * <h3 class="zh-CN">读取指定缓存键值对应的缓存数据</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @return  <span class="en-US">Cache value or null if cache key was not exists or it was expired</span>
	 *          <span class="zh-CN">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String get(final String key);

	/**
	 * <h3 class="en-US">Increment data by given cache key and value</h3>
	 * <h3 class="zh-CN">对给定的缓存键值执行自增操作，增加值为给定的步进值</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param step      <span class="en-US">Increment step value</span>
	 *                  <span class="zh-CN">自增步进值</span>
	 * @return  <span class="en-US">Operate result</span>
	 *          <span class="zh-CN">操作结果</span>
	 */
	long incr(final String key, final long step);

	/**
	 * <h3 class="en-US">Decrement data by given cache key and value</h3>
	 * <h3 class="zh-CN">对给定的缓存键值执行自减操作，减少值为给定的步进值</h3>
	 *
	 * @param key       <span class="en-US">Cache key</span>
	 *                  <span class="zh-CN">缓存键值</span>
	 * @param step      <span class="en-US">Decrement step value</span>
	 *                  <span class="zh-CN">自减步进值</span>
	 * @return  <span class="en-US">Operate result</span>
	 *          <span class="zh-CN">操作结果</span>
	 */
	long decr(final String key, final long step);

	/**
	 * <h3 class="en-US">Destroy cache client</h3>
	 * <h3 class="zh-CN">销毁当前缓存客户端</h3>
	 */
	void destroy();
}
