package org.nervousync.cache.test.jedis;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.core.CacheCore;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public final class JedisTest {

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
	public void testJedis() {
		if (PROPERTIES.isEmpty()) {
			this.logger.info("Can't found authorization file, ignore...");
			return;
		}
		SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256).ifPresent(SecureFactory::initialize);
		SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
				.ifPresent(secureConfig -> SecureFactory.getInstance().register("SecureCache", secureConfig));
		CacheConfig cacheConfig = CacheConfigBuilder.builder()
				.providerName("JedisProvider")
				.secureName("SecureCache")
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

		this.logger.info("Register cache result: {}", CacheCore.registerCache("TestCache", cacheConfig));
		CacheCore.cacheAgent("TestCache")
				.ifPresent(agent -> {
					agent.add("test", "Test add");
					this.logger.info("Read key: {}, value: {}", "test", agent.get("test"));
					agent.set("test", "Test set");
					this.logger.info("Read key: {}, after set operate. Read value: {}", "test", agent.get("test"));
					agent.replace("test", "Test replace");
					this.logger.info("Read key: {}, after replace operate. Read value: {}", "test", agent.get("test"));
					agent.expire("test", 1);
					this.logger.info("Read key: {}, after expire operate. Read value: {}", "test", agent.get("test"));
					agent.delete("test");
					this.logger.info("Read key: {}, after delete operate. Read value: {}", "test", agent.get("test"));
					agent.add("testNum", "10000000");
					long incrReturn = agent.incr("testNum", 2);
					this.logger.info("Read key: {}, after incr operate. Read value: {}, return value: {}", "testNum", agent.get("testNum"), incrReturn);
					long decrReturn = agent.decr("testNum", 2);
					this.logger.info("Read key: {}, after decr operate. Read value: {}, return value: {}", "testNum", agent.get("testNum"), decrReturn);
				});

		CacheCore.destroyCache("Jedis");
		CacheCore.removeProvider("Jedis");
		CacheCore.destroy();
	}
}
