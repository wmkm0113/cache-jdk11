package org.nervousync.cache.client.impl;

import org.nervousync.cache.api.CacheClient;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheClientImpl implements CacheClient {

	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zhs">日志实例</span>
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * <span class="en">Current cache provider instance</span>
	 * <span class="zhs">缓存实现对象</span>
	 */
	private final AbstractProvider cacheProvider;

	/**
	 * Constructor for cache agent
	 *
	 * @param cacheConfig           <span class="en">System cache config instance</span>
	 *                              <span class="zhs">系统缓存配置实例</span>
	 * @param providerImplClass     <span class="en">Cache provider implement class</span>
	 *                              <span class="zhs">缓存适配器实现类</span>
	 * @throws CacheException       <span class="en">Generate instance of provider failed or provider implement class not extends with AbstractCacheProvider</span>
	 *                              <span class="zhs">缓存适配器实现类没有继承AbstractCacheProvider或初始化缓存适配器对象出错</span>
	 */
	public CacheClientImpl(CacheConfig cacheConfig, Class<?> providerImplClass) throws CacheException {
		if (providerImplClass != null && AbstractProvider.class.isAssignableFrom(providerImplClass)) {
			try {
				this.cacheProvider = (AbstractProvider)providerImplClass.getDeclaredConstructor().newInstance();
				this.cacheProvider.initialize(cacheConfig);
				return;
			} catch (ReflectiveOperationException e) {
				throw new CacheException(e);
			}
		}
		throw new CacheException("Provider implement class is invalid! ");
	}

	/**
	 * <h3 class="en">Set key-value to cache server by default expire time</h3>
	 * <h3 class="zhs">使用默认的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	public void set(String key, String value) {
		this.logInfo(key, value);
		this.cacheProvider.set(key, value);
	}

	/**
	 * <h3 class="en">Set key-value to cache server and set expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 * @param expire	<span class="en">Expire time</span>
	 *                  <span class="zhs">过期时间</span>
	 */
	public void set(String key, String value, int expire) {
		this.logInfo(key, value);
		this.cacheProvider.set(key, value, expire);
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
	public void add(String key, String value) {
		this.logInfo(key, value);
		this.cacheProvider.add(key, value);
	}

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
	public void add(String key, String value, int expire) {
		this.logInfo(key, value);
		this.cacheProvider.add(key, value, expire);
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
	public void replace(String key, String value) {
		this.logInfo(key, value);
		this.cacheProvider.replace(key, value);
	}

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
	public void replace(String key, String value, int expire) {
		this.logInfo(key, value);
		this.cacheProvider.replace(key, value, expire);
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
	public void expire(String key, int expire) {
		this.cacheProvider.expire(key, expire);
	}

	/**
	 * Operate touch to given keys
	 * @param keys      Keys
	 */
	public void touch(String... keys) {
		this.cacheProvider.touch(keys);
	}

	/**
	 * <h3 class="en">Remove cache key-value from cache server</h3>
	 * <h3 class="zhs">移除指定的缓存键值</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 */
	public void delete(String key) {
		this.cacheProvider.delete(key);
	}

	/**
	 * <h3 class="en">Read cache value from cache key which cache key was given</h3>
	 * <h3 class="zhs">读取指定缓存键值对应的缓存数据</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @return  <span class="en">Cache value or null if cache key was not exists or it was expired</span>
	 *          <span class="zhs">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	public String get(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		return this.cacheProvider.get(key);
	}

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
	public long incr(String key, long step) {
		if (StringUtils.isEmpty(key)) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		return this.cacheProvider.incr(key, step);
	}

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
	public long decr(String key, long step) {
		if (StringUtils.isEmpty(key)) {
			return Globals.DEFAULT_VALUE_LONG;
		}
		return this.cacheProvider.decr(key, step);
	}

	/**
	 * <h3 class="en">Destroy agent instance</h3>
	 * <h3 class="zhs">销毁缓存对象</h3>
	 */
	public void destroy() {
		this.cacheProvider.destroy();
	}

	/**
	 * <h3 class="en">Logging cache key and value when debug mode was enabled</h3>
	 * <h3 class="zhs">当调试模式开启时，在日志中输出缓存键值和数据</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	private void logInfo(String key, Object value) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Cached key: {}", key);
			this.logger.debug("Cached value: {}", value);
		}
	}
}
