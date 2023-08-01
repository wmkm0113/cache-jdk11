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

/**
 * <h2 class="en">Cache provider interface</h2>
 * <h2 class="zh-CN">缓存适配器接口</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 9/14/2020 10:30 AM $
 */
public interface Provider {

	/**
	 * <h3 class="en">Set key-value to cache server and set expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 * @param expiry	<span class="en">Expire time</span>
	 *                  <span class="zhs">过期时间</span>
	 */
	void set(final String key, final String value, final int expiry);

	/**
	 * <h3 class="en">Add a new key-value to cache server and set expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间添加缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 * @param expire	<span class="en">Expire time</span>
	 *                  <span class="zhs">过期时间</span>
	 */
	void add(final String key, final String value, final int expire);

	/**
	 * <h3 class="en">Replace exists value of given key by given value and set expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间替换已存在的缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 * @param expire	<span class="en">Expire time</span>
	 *                  <span class="zhs">过期时间</span>
	 */
	void replace(final String key, final String value, final int expire);

	/**
	 * <h3 class="en">Execute touch operate by given keys</h3>
	 * <h3 class="zhs">根据给定的缓存键执行touch操作</h3>
	 *
	 * @param keys 	<span class="en">Cache key array</span>
	 *              <span class="zhs">缓存键数组</span>
	 */
	void touch(final String... keys);

	/**
	 * <h3 class="en">Remove cache key-value from cache server</h3>
	 * <h3 class="zhs">移除指定的缓存键值</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 */
	void delete(final String key);

	/**
	 * <h3 class="en">Read cache value from cache key which cache key was given</h3>
	 * <h3 class="zhs">读取指定缓存键值对应的缓存数据</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @return  <span class="en">Cache value or null if cache key was not exists or it was expired</span>
	 *          <span class="zhs">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String get(final String key);

	/**
	 * <h3 class="en">Increment data by given cache key and value</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param step      <span class="en">Increment step value</span>
	 *                  <span class="zhs">自增步进值</span>
	 * @return  <span class="en">Operate result</span>
	 *          <span class="zhs">操作结果</span>
	 */
	long incr(final String key, final long step);

	/**
	 * <h3 class="en">Decrement data by given cache key and value</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param step      <span class="en">Decrement step value</span>
	 *                  <span class="zhs">自减步进值</span>
	 * @return  <span class="en">Operate result</span>
	 *          <span class="zhs">操作结果</span>
	 */
	long decr(final String key, final long step);

	/**
	 * <h3 class="en">Destroy agent instance</h3>
	 * <h3 class="zhs">销毁缓存对象</h3>
	 */
	void destroy();
}
