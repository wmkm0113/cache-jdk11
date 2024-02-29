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

import java.util.Optional;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseCacheTest {

    private transient final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());
    private static final Properties PROPERTIES;
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
        CacheUtils.initialize();
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
        boolean generateResult = CacheConfigBuilder.newBuilder("TestCache")
                .providerName(this.providerName)
                .connectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT)
                .expireTime(5)
                .clientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE)
                .maximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT)
                .serverBuilder()
                .serverConfig(PROPERTIES.getProperty("ServerAddress"), Integer.parseInt(PROPERTIES.getProperty("ServerPort")))
                .serverWeight(PROPERTIES.containsKey("ServerWeight")
                        ? Integer.parseInt(PROPERTIES.getProperty("ServerWeight"))
                        : Globals.DEFAULT_VALUE_INT)
                .confirm()
                .authorization(PROPERTIES.getProperty("UserName"), PROPERTIES.getProperty("PassWord"))
                .confirm();
        if (!generateResult) {
            return;
        }
        CacheConfig cacheConfig = ConfigureManager.getInstance().readConfigure(CacheConfig.class, "TestCache");
        Assertions.assertNotNull(cacheConfig);
        this.logger.info("Generated_Configure", cacheConfig.toXML(Boolean.TRUE));

        CacheUtils cacheUtils = CacheUtils.getInstance();
        this.logger.info("Register_Result", cacheUtils.register("TestCache", cacheConfig));
        this.logger.info("Register_Check", "TestCache", cacheUtils.registered("TestCache"));
        Optional.ofNullable(cacheUtils.client("TestCache"))
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

