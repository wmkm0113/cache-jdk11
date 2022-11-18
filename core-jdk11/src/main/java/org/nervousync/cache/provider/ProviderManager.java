package org.nervousync.cache.provider;

import org.nervousync.cache.annotation.CacheProvider;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ServiceLoader;

public final class ProviderManager {

	/**
	 * <span class="en">Logger instance</span>
	 * <span class="zhs">日志实例</span>
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderManager.class);

	/**
	 * <span class="en">Registered cache provider map</span>
	 * <span class="zhs">注册的缓存实现类与名称对应关系</span>
	 */
	private static final Hashtable<String, Class<?>> REGISTERED_PROVIDERS = new Hashtable<>();

	static {
		//  Register all cache providers by Java SPI
		ServiceLoader.load(Provider.class).forEach(provider -> {
			if (provider.getClass().isAnnotationPresent(CacheProvider.class)) {
				registerProvider(provider.getClass());
			}
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registered cache provider number: {}", REGISTERED_PROVIDERS.size());
		}
	}

	/**
	 * <h3 class="en">Retrieve registered provider name list</h3>
	 * <h3 class="zhs">读取已注册的缓存适配器名称列表</h3>
	 *
	 * @return  <span class="en">Registered provider name list</span>
	 *          <span class="zhs">已注册的缓存适配器名称列表</span>
	 */
	public static List<String> registeredProviderNames() {
		return new ArrayList<>(REGISTERED_PROVIDERS.keySet());
	}

	/**
	 * <h3 class="en">Check register status of given provider name</h3>
	 * <h3 class="zhs">检查给定的缓存适配器名称是否已经注册</h3>
	 *
	 * @param providerName  <span class="en">Cache provider name</span>
	 *                      <span class="zhs">缓存适配器名称</span>
	 * @return  <span class="en">Register status</span>
	 *          <span class="zhs">注册状态</span>
	 */
	public static boolean registeredProvider(final String providerName) {
		if (StringUtils.isEmpty(providerName)) {
			return Boolean.FALSE;
		}
		return REGISTERED_PROVIDERS.containsKey(providerName);
	}

	/**
	 * <h3 class="en">Retrieve provider class by given provider name</h3>
	 * <h3 class="zhs">根据指定的适配器名称获取注册的适配器类</h3>
	 *
	 * @param providerName  <span class="en">Cache provider name</span>
	 *                      <span class="zhs">缓存适配器名称</span>
	 * @return  <span class="en">Register provider class</span>
	 *          <span class="zhs">注册适配器类</span>
	 */
	public static Class<?> providerClass(final String providerName) {
		if (StringUtils.isEmpty(providerName)) {
			return null;
		}
		return REGISTERED_PROVIDERS.get(providerName);
	}

	/**
	 * <h3 class="en">Register cache provider implement class manual</h3>
	 * <h3 class="zhs">注册缓存适配器</h3>
	 *
	 * @param providerClass     <span class="en">Cache provider implements class</span>
	 *                          <span class="zhs">缓存适配器实现类</span>
	 */
	private static void registerProvider(final Class<?> providerClass) {
		if (providerClass != null && providerClass.isAnnotationPresent(CacheProvider.class)) {
			CacheProvider cacheProvider = providerClass.getAnnotation(CacheProvider.class);
			if (REGISTERED_PROVIDERS.containsKey(cacheProvider.name())) {
				LOGGER.warn("Override cache provider name: {}, replace class: {}, new class: {}",
						cacheProvider.name(), REGISTERED_PROVIDERS.get(cacheProvider.name()).getName(),
						providerClass.getName());
			}
			REGISTERED_PROVIDERS.put(cacheProvider.name(), providerClass);
		}
	}
}
