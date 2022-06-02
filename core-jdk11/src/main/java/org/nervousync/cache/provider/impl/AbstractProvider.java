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
package org.nervousync.cache.provider.impl;

import java.util.List;

import org.nervousync.cache.annotation.CacheProvider;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.Provider;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.config.CacheServer;
import org.nervousync.commons.core.Globals;

/**
 * <h2 class="en">Abstract provider class, all providers must extend this class</h2>
 * <h2 class="zhs">缓存适配器抽象类，所有缓存适配器实现类必须继承本抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 3:01:30 PM $
 */
public abstract class AbstractProvider implements Provider {

	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zhs">日志实例</span>
	 */
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * <span class="en">Default port number</span>
	 * <span class="zhs">默认端口号</span>
	 */
	private final int defaultPort;
	
	/**
	 * <span class="en">Server connect timeout</span>
	 * <span class="zhs">缓存服务器的连接超时时间</span>
	 */
	private int connectTimeout = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en">Client pool size</span>
	 * <span class="zhs">连接池大小</span>
	 */
	private int clientPoolSize = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en">Limit size of generated client instance</span>
	 * <span class="zhs">客户端实例阈值</span>
	 */
	private int maximumClient = Globals.DEFAULT_VALUE_INT;
	/**
	 * <span class="en">Default expire time</span>
	 * <span class="zhs">默认过期时间</span>
	 */
	private int expireTime = Globals.DEFAULT_VALUE_INT;
	
	/**
	 * Default constructor
	 *
	 * @throws CacheException If class not annotation by org.nervousync.cache.annotation.CacheProvider
	 * @see CacheProvider
	 */
	protected AbstractProvider() throws CacheException {
		if (this.getClass().isAnnotationPresent(CacheProvider.class)) {
			this.defaultPort = this.getClass().getAnnotation(CacheProvider.class).defaultPort();
		} else {
			throw new CacheException("Provider implement class must annotation with " + CacheProvider.class.getName());
		}
	}
	
	/**
	 * <h3 class="en">Initialize cache agent</h3>
	 * <h3 class="zhs">初始化缓存实例</h3>
	 *
	 * @param cacheConfig   <span class="en">Cache config instance</span>
	 *                      <span class="zhs">缓存配置实例</span>
	 */
	public void initialize(CacheConfig cacheConfig) {
		this.connectTimeout = cacheConfig.getConnectTimeout();
		this.clientPoolSize = cacheConfig.getClientPoolSize();
		this.maximumClient = cacheConfig.getMaximumClient();
		this.expireTime = cacheConfig.getExpireTime();
		String passWord = cacheConfig.getPassWord();
		if (StringUtils.notBlank(passWord) && StringUtils.notBlank(cacheConfig.getSecureName())
				&& SecureFactory.getInstance().registeredConfig(cacheConfig.getSecureName())) {
			byte[] decryptData =
					SecureFactory.getInstance().decrypt(cacheConfig.getSecureName(), StringUtils.base64Decode(passWord));
			passWord = ConvertUtils.convertToString(decryptData);
		}
		this.initializeConnection(cacheConfig.getServerConfigList(), cacheConfig.getUserName(), passWord);
	}

	/**
	 * <h3 class="en">Retrieve default port</h3>
	 * <h3 class="zhs">读取缓存服务器的默认端口号</h3>
	 *
	 * @return  <span class="en">Default port number</span>
	 *          <span class="zhs">默认端口号</span>
	 */
	public int getDefaultPort() {
		return defaultPort;
	}

	/**
	 * <h3 class="en">Retrieve server connect timeout</h3>
	 * <h3 class="zhs">读取缓存服务器的连接超时时间</h3>
	 *
	 * @return  <span class="en">Connect timeout</span>
	 *          <span class="zhs">连接超时时间</span>
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * <h3 class="en">Retrieve connect client pool size</h3>
	 * <h3 class="zhs">读取客户端连接池的大小</h3>
	 *
	 * @return  <span class="en">Client pool size</span>
	 *          <span class="zhs">连接池大小</span>
	 */
	public int getClientPoolSize() {
		return clientPoolSize;
	}

	/**
	 * <h3 class="en">Retrieve limit size of generated client instance</h3>
	 * <h3 class="zhs">读取允许创建的客户端实例阈值</h3>
	 *
	 * @return  <span class="en">Limit size of generated client instance</span>
	 *          <span class="zhs">客户端实例阈值</span>
	 */
	public int getMaximumClient() {
		return maximumClient;
	}

	/**
	 * <h3 class="en">Retrieve default expire time</h3>
	 * <h3 class="zhs">读取缓存的默认过期时间</h3>
	 *
	 * @return  <span class="en">Default expire time</span>
	 *          <span class="zhs">默认过期时间</span>
	 */
	public int getExpireTime() {
		return expireTime;
	}

	/**
	 * <h3 class="en">Initialize cache server connections</h3>
	 * <h3 class="zhs">初始化缓存服务器连接池</h3>
	 *
	 * @param serverConfigList  cache server list
	 */
	protected abstract void initializeConnection(final List<CacheServer> serverConfigList,
	                                             final String userName, final String passWord);

	/**
	 * <h3 class="en">Set key-value to cache server by default expire time</h3>
	 * <h3 class="zhs">使用默认的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	public final void set(String key, String value) {
		this.set(key, value, this.expireTime);
	}

	/**
	 * <h3 class="en">Add a new key-value to cache server by default expire time</h3>
	 * <h3 class="zhs">使用默认的过期时间添加缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	public final void add(String key, String value) {
		this.add(key, value, this.expireTime);
	}

	/**
	 * <h3 class="en">Replace exists value of given key by given value by default expire time</h3>
	 * <h3 class="zhs">使用默认的过期时间替换已存在的缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	public final void replace(String key, String value) {
		this.replace(key, value, this.expireTime);
	}

	/**
	 * <h3 class="en">Set expire time to new given expire value which cache key was given</h3>
	 * <h3 class="zhs">将指定的缓存键值过期时间设置为指定的新值</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param expire	<span class="en">New expire time</span>
	 *                  <span class="zhs">新的过期时间</span>
	 */
	public abstract void expire(String key, int expire);

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
	public abstract void set(String key, String value, int expiry);

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
	public abstract void add(String key, String value, int expire);

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
	public abstract void replace(String key, String value, int expire);

	/**
	 * Operate touch to given keys
	 * @param keys      Keys
	 */
	public abstract void touch(String... keys);

	/**
	 * <h3 class="en">Remove cache key-value from cache server</h3>
	 * <h3 class="zhs">移除指定的缓存键值</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 */
	public abstract void delete(String key);

	/**
	 * <h3 class="en">Read cache value from cache key which cache key was given</h3>
	 * <h3 class="zhs">读取指定缓存键值对应的缓存数据</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @return  <span class="en">Cache value or null if cache key was not exists or it was expired</span>
	 *          <span class="zhs">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	public abstract String get(String key);

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
	public abstract long incr(String key, long step);

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
	public abstract long decr(String key, long step);

	/**
	 * <h3 class="en">Destroy agent instance</h3>
	 * <h3 class="zhs">销毁缓存对象</h3>
	 */
	public abstract void destroy();
}
