package org.nervousync.cache.test.lettuce;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nervousync.cache.CacheUtils;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;

public final class LettuceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Properties PROPERTIES = null;

	static {
		BasicConfigurator.configure();
	}

	@BeforeClass
	public static void initialize() {
		PROPERTIES = PropertiesUtils.loadProperties("src/test/resources/authorization.xml");
	}

	@Test
	public void testLettuce() {
		if (PROPERTIES.isEmpty()) {
			this.logger.info("Can't found authorization file, ignore...");
			return;
		}
		CacheConfig cacheConfig = CacheConfigBuilder.builder()
				.providerName("LettuceProvider")
				.secureName(Globals.DEFAULT_VALUE_STRING)
				.connectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT)
				.expireTime(5)
				.clientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE)
				.maximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT)
				.configServer(PROPERTIES.getProperty("ServerAddress"), Integer.parseInt(PROPERTIES.getProperty("ServerPort")),
						CacheGlobals.DEFAULT_CACHE_SERVER_WEIGHT,
						Boolean.parseBoolean(PROPERTIES.getProperty("ReadOnly")))
				.authorization(PROPERTIES.getProperty("UserName"), PROPERTIES.getProperty("PassWord"))
				.confirmConfig();
		Assert.assertNotNull(cacheConfig);
		this.logger.info("Generated configure: \r\n {}", cacheConfig.toXML(Boolean.TRUE));

		this.logger.info("Register cache result: {}", CacheUtils.register("TestCache", cacheConfig));
		Optional.ofNullable(CacheUtils.client("TestCache"))
				.ifPresent(client -> {
					client.add("test", "Test add");
					this.logger.info("Read key: {}, value: {}", "test", client.get("test"));
					client.set("test", "Test set");
					this.logger.info("Read key: {}, after set operate. Read value: {}", "test", client.get("test"));
					client.replace("test", "Test replace");
					this.logger.info("Read key: {}, after replace operate. Read value: {}", "test", client.get("test"));
					client.expire("test", 1);
					this.logger.info("Read key: {}, after expire operate. Read value: {}", "test", client.get("test"));
					client.delete("test");
					this.logger.info("Read key: {}, after delete operate. Read value: {}", "test", client.get("test"));
					client.add("testNum", "10000000");
					long incrReturn = client.incr("testNum", 2);
					this.logger.info("Read key: {}, after incr operate. Read value: {}, return value: {}", "testNum", client.get("testNum"), incrReturn);
					long decrReturn = client.decr("testNum", 2);
					this.logger.info("Read key: {}, after decr operate. Read value: {}, return value: {}", "testNum", client.get("testNum"), decrReturn);
				});

		CacheUtils.deregister("TestCache");
		CacheUtils.destroy();
	}
}
