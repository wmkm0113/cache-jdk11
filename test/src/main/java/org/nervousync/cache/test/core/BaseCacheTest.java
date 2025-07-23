package org.nervousync.cache.test.core;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.*;
import org.nervousync.cache.CacheUtils;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.commons.Globals;
import org.nervousync.configs.ConfigureManager;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.PropertiesUtils;
import org.nervousync.utils.StringUtils;

import java.util.Optional;
import java.util.Properties;

/**
 * <h2 class="en-US">Abstract class of the cache test</h2>
 * <h2 class="zh-CN">缓存测试抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 23, 2020 13:43:49 $
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseCacheTest {

	/**
	 * <span class="en-US">Multilingual supported logger instance</span>
	 * <span class="zh-CN">多语言支持的日志对象</span>
	 */
	private transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());
	/**
	 * <span class="en-US">Server configure information</span>
	 * <span class="zh-CN">服务器配置信息</span>
	 */
	private static final Properties PROPERTIES;
	/**
	 * <span class="en-US">The name of the cache adapter to test</span>
	 * <span class="zh-CN">需要测试的缓存适配器名称</span>
	 */
	private final String providerName;

	static {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
		PROPERTIES = PropertiesUtils.loadProperties("src/test/resources/authorization.xml");
	}

	protected BaseCacheTest(final String providerName) {
		this.providerName = providerName;
	}

	@BeforeAll
	public static void initialize() throws CacheException {
		ConfigureManager.initialize();
	}

	@AfterAll
	public static void clear() {
		Optional.ofNullable(ConfigureManager.getInstance())
				.ifPresent(configureManager -> configureManager.removeConfigure(CacheConfig.class, "TestCache"));
	}

	@BeforeEach
	public final void init(final TestInfo testInfo) {
		this.logger.info("Execute_Begin_Test",
				testInfo.getTestClass().map(Class::getName).orElse(Globals.DEFAULT_VALUE_STRING), testInfo.getDisplayName());
	}

	@AfterEach
	public final void print(TestInfo testInfo) {
		this.logger.info("Execute_End_Test",
				testInfo.getTestClass().map(Class::getName).orElse(Globals.DEFAULT_VALUE_STRING), testInfo.getDisplayName());
	}

	@Test
	public final void test() throws BuilderException, CacheException {
		if (PROPERTIES.isEmpty()) {
			this.logger.info("No_Auth_File");
			return;
		}
		CacheConfigBuilder<?> configBuilder = CacheConfigBuilder.newBuilder()
				.providerName(this.providerName)
				.connectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT)
				.expireTime(5)
				.clientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE)
				.maximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT);
		String[] serverAddresses = StringUtils.tokenizeToStringArray(PROPERTIES.getProperty("ServerAddress"), ",");
		String[] serverPorts = StringUtils.tokenizeToStringArray(PROPERTIES.getProperty("ServerPort"), ",");
		String[] serverWeights = StringUtils.tokenizeToStringArray(PROPERTIES.getProperty("ServerWeight"), ",");
		if ((serverPorts.length != 0 && serverAddresses.length != serverPorts.length)
				|| (serverWeights.length != 0 && serverAddresses.length != serverWeights.length)) {
			throw new CacheException(0x000C00001001L);
		}
		int serverCount = serverAddresses.length;
		if (serverWeights.length == 0) {
			serverWeights = new String[serverCount];
			for (int i = 0; i < serverCount; i++) {
				serverWeights[i] = "1";
			}
		}

		for (int i = 0; i < serverCount; i++) {
			CacheConfigBuilder.ServerConfigBuilder serverConfigBuilder =
					configBuilder.serverBuilder(serverAddresses[i]).weight(Integer.parseInt(serverWeights[i]));
			if (serverPorts.length > 0) {
				serverConfigBuilder.port(Integer.parseInt(serverPorts[i]));
			}
			configBuilder = serverConfigBuilder.confirm();
		}

		CacheConfig cacheConfig =
				configBuilder.authorization(PROPERTIES.getProperty("UserName"), PROPERTIES.getProperty("PassWord"))
						.build();
		if (cacheConfig == null) {
			return;
		}
		Assertions.assertNotNull(cacheConfig);
		this.logger.info("Generated_Configure", cacheConfig.toString(StringUtils.StringType.XML));

		this.logger.info("Register_Result", CacheUtils.register("TestCache", cacheConfig));
		this.logger.info("Register_Check", "TestCache", CacheUtils.registered("TestCache"));
		Optional.ofNullable(CacheUtils.client("TestCache"))
				.ifPresent(client -> {
					client.add("test", "Test add");
					this.logger.info("Read_Debug", "test", client.get("test"));
					client.set("test", "Test set");
					this.logger.info("Read_After_Debug", "test", "set", client.get("test"));
					client.replace("test", "Test replace");
					this.logger.info("Read_After_Debug", "test", "replace", client.get("test"));
					client.expire("test", 1);
					this.logger.info("Read_After_Debug", "test", "expire", client.get("test"));
					client.delete("test");
					this.logger.info("Read_After_Debug", "test", "delete", client.get("test"));
					client.add("testNum", "10000000");
					long incrReturn = client.incr("testNum", 2);
					this.logger.info("Read_After_Return_Debug", "testNum", "incr", client.get("testNum"), incrReturn);
					long decrReturn = client.decr("testNum", 2);
					this.logger.info("Read_After_Return_Debug", "testNum", "decr", client.get("testNum"), decrReturn);
				});
		CacheUtils.deregister("TestCache");
		CacheUtils.destroy();
	}
}

