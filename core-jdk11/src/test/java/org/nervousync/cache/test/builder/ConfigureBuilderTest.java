package org.nervousync.cache.test.builder;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.commons.core.Globals;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ConfigureBuilderTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void test000Config() {
		generateConfig(Globals.DEFAULT_VALUE_STRING);
	}

	@Test
	public void test010SecureConfig() {
		SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256).ifPresent(SecureFactory::initialize);
		SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
				.ifPresent(secureConfig -> SecureFactory.getInstance().register("SecureCache", secureConfig));
		generateConfig("SecureCache");
	}

	private void generateConfig(final String secureName) {
		CacheConfigBuilder cacheConfigBuilder = CacheConfigBuilder.builder()
				.providerName(Globals.DEFAULT_VALUE_STRING)
				.secureName(secureName)
				.connectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT)
				.expireTime(CacheGlobals.DEFAULT_EXPIRE_TIME)
				.clientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE)
				.maximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT)
				.configServer("192.168.166.51", 11211, CacheGlobals.DEFAULT_CACHE_SERVER_WEIGHT, Boolean.FALSE)
				.authorization("userName", "passWord");
		this.logger.info("Configure modified: {}", cacheConfigBuilder.isModified());
		String xmlContent = cacheConfigBuilder.confirmConfig().toXML();
		this.logger.info("Secure name: {}, generated config: {}", secureName, xmlContent);
		CacheConfig cacheConfig = StringUtils.stringToObject(xmlContent, CacheConfig.class);
		this.logger.info("Parsed config: {}", cacheConfig.toFormattedJson());
	}
}
