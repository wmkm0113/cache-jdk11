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
package org.nervousync.cache.test.jedis;

import org.apache.logging.log4j.Level;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.nervousync.cache.CacheUtils;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.commons.Globals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.PropertiesUtils;

import java.util.Optional;
import java.util.Properties;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class JedisTest {

	private final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());
	private static Properties PROPERTIES = null;

	@BeforeClass
	public static void initialize() throws CacheException {
		LoggerUtils.initLoggerConfigure(Level.DEBUG);
		CacheUtils.initialize();
		PROPERTIES = PropertiesUtils.loadProperties("src/test/resources/authorization.xml");
	}

	@Test
	@Order(10)
	public void testJedis() throws BuilderException, CacheException {
		if (PROPERTIES.isEmpty()) {
			this.logger.info("No_Auth_File");
			return;
		}
		SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256).ifPresent(SecureFactory::initialize);
		SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
				.ifPresent(secureConfig -> SecureFactory.register("SecureCache", secureConfig));
		CacheConfig cacheConfig = CacheConfigBuilder.newBuilder()
				.providerName("JedisProvider")
				.secureName("SecureCache")
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
		Assert.assertNotNull(cacheConfig);
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

		SecureConfig secureConfig = SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
				.filter(config -> SecureFactory.register("SecureNew", config))
				.orElse(null);
		CacheConfig updateConfig = CacheConfigBuilder.newBuilder(cacheConfig)
				.secureConfig("SecureNew", secureConfig)
				.confirm();
		this.logger.info("Updatable_Configure", updateConfig.toXML(Boolean.TRUE));
	}
}
