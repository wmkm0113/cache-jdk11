package org.nervousync.cache.test.core;

import org.junit.jupiter.api.*;
import org.nervousync.builder.ParentBuilder;
import org.nervousync.cache.CacheUtils;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.commons.Globals;
import org.nervousync.configs.ConfigureManager;
import org.nervousync.enumerations.logger.LogLevel;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.core.BeanUtils;
import org.nervousync.utils.core.StringUtils;
import org.nervousync.utils.logger.LoggerUtils;
import org.nervousync.utils.properties.PropertiesUtils;

import java.util.List;
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
		LoggerUtils.initLoggerConfigure(LogLevel.DEBUG);
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
		CacheConfigBuilder<ParentBuilder> configBuilder = CacheConfigBuilder.newBuilder()
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
			CacheConfigBuilder.ServerConfigBuilder<CacheConfigBuilder<ParentBuilder>> serverConfigBuilder =
					configBuilder.serverBuilder(serverAddresses[i]).weight(Integer.parseInt(serverWeights[i]));
			configBuilder = (serverPorts.length > 0)
					? serverConfigBuilder.port(Integer.parseInt(serverPorts[i])).confirm()
					: serverConfigBuilder.confirm();
		}

		CacheConfig cacheConfig =
				configBuilder.authorization(PROPERTIES.getProperty("UserName"), PROPERTIES.getProperty("PassWord"))
						.build();
		if (cacheConfig == null) {
			return;
		}
		Assertions.assertNotNull(cacheConfig);
		this.logger.info("Generated_Configure", BeanUtils.objectToString(cacheConfig));

		this.logger.info("Register_Result", CacheUtils.register("TestCache", cacheConfig));
		this.logger.info("Register_Check", "TestCache", CacheUtils.registered("TestCache"));
		Optional.ofNullable(CacheUtils.client("TestCache"))
				.ifPresent(client -> {
					this.logger.info("Operate_Result_Debug", "add", client.add("test", "Test add"));
					this.logger.info("Read_Debug", "test", client.get("test"));
					this.logger.info("Operate_Result_Debug", "append", client.append("test", " append"));
					this.logger.info("Read_Debug", "test", client.get("test"));
					this.logger.info("Read_Debug", "test", client.getRange("test", 2, 5));
					this.logger.info("Length_Debug", client.strLen("test"));
					this.logger.info("Operate_Result_Debug", "setRange", client.setRange("test", 5, " replace"));
					this.logger.info("Read_Debug", "test", client.get("test"));
					this.logger.info("Operate_Result_Debug", "copy", client.copy("test", "newTest"));
					this.logger.info("Operate_Result_Debug", "copy", client.copy("test", "testCopy"));
					this.logger.info("Read_Debug", "test", client.get("test"));
					this.logger.info("Read_Debug", "newTest", client.get("newTest"));
					this.logger.info("Read_Debug", "testCopy", client.get("testCopy"));
					this.logger.info("Exists_Debug", client.exists("test", "oldTest", "newTest"));
					this.logger.info("Exists_Debug", client.keys("tes*"));
					this.logger.info("Operate_Result_Debug", "persist", client.persist("test"));
					this.logger.info("TTL_Debug", client.ttl("test"));
					this.logger.info("Operate_Result_Debug", "rename", client.rename("test", "rename"));
					this.logger.info("Read_Debug", "test", client.get("test"));
					this.logger.info("Read_Debug", "rename", client.get("rename"));
					this.logger.info("Operate_Result_Debug", "append", client.getSet("test", "New test string"));
					this.logger.info("Read_Debug", "test", client.get("test"));
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					this.logger.info("TTL_Debug", client.ttl("test"));
					this.logger.info("TTL_Debug", client.ttl("rename"));
					this.logger.info("Operate_Result_Debug", "touch", client.touch("test", "rename"));
					this.logger.info("TTL_Debug", client.ttl("test"));
					this.logger.info("TTL_Debug", client.ttl("rename"));
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					this.logger.info("Operate_Result_Debug", "getEx", client.getEx("test"));
					this.logger.info("TTL_Debug", client.ttl("test"));
					this.logger.info("Operate_Result_Debug", "getDel", client.getDel("rename"));
					this.logger.info("TTL_Debug", client.ttl("rename"));

					this.logger.info("Operate_Result_Debug", "set", client.set("test", "Test set"));
					this.logger.info("Read_After_Debug", "test", "set", client.get("test"));
					this.logger.info("Operate_Result_Debug", "replace", client.replace("test", "Test replace"));
					this.logger.info("Read_After_Debug", "test", "replace", client.get("test"));
					client.expire("test", 1);
					this.logger.info("Read_After_Debug", "test", "expire", client.get("test"));
					this.logger.info("Operate_Result_Debug", "delete", client.del("test"));
					this.logger.info("Read_After_Debug", "test", "delete", client.get("test"));
					client.add("testNum", "10000000");
					long incrDefault = client.incr("testNum");
					this.logger.info("Read_After_Return_Debug", "testNum", "incr", client.get("testNum"), incrDefault);
					long incrReturn = client.incr("testNum", 2);
					this.logger.info("Read_After_Return_Debug", "testNum", "incr", client.get("testNum"), incrReturn);
					long decrDefault = client.decr("testNum");
					this.logger.info("Read_After_Return_Debug", "testNum", "decr", client.get("testNum"), decrDefault);
					long decrReturn = client.decr("testNum", 2);
					this.logger.info("Read_After_Return_Debug", "testNum", "decr", client.get("testNum"), decrReturn);
					double incrFloat = client.incrFloat("testNum", 2.5);
					this.logger.info("Read_After_Return_Debug", "testNum", "incrFloat", client.get("testNum"), incrFloat);
					this.logger.info("Operate_Result_Debug", "mset", client.mset("key1", "value1", "key2", "value2"));
					this.logger.info("Operate_Result_Debug", "msetnx", client.msetnx("key2", "value1", "key3", "value2"));
					this.logger.info("Read_Debug", List.of("key1", "key2"), client.mget("key1", "key2"));
					this.logger.info("Read_Debug", "test", client.lcs("key1", "key2"));
					this.logger.info("Read_Debug", "test", client.lcsLen("key1", "key2"));
				});
		CacheUtils.deregister("TestCache");
		CacheUtils.destroy();
	}
}

