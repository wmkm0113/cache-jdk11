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
package org.nervousync.cache.test.xmemcached;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nervousync.cache.CacheUtils;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;

public final class XmemcachedTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static Properties PROPERTIES = null;

	static {
		BasicConfigurator.configure();
		try {
			CacheUtils.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void initialize() {
		PROPERTIES = ConvertUtils.loadProperties("src/test/resources/authorization.xml");
	}

	@Test
	public void testXmemcached() throws BuilderException, CacheException {
		if (PROPERTIES.isEmpty()) {
			this.logger.info("Can't found authorization file, ignore...");
			return;
		}
		CacheConfig cacheConfig = CacheConfigBuilder.newBuilder()
				.providerName("XMemcachedProvider")
				.secureName(Globals.DEFAULT_VALUE_STRING)
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
		this.logger.info("Generated configure info: \r\n {}", cacheConfig.toXML(Boolean.TRUE));

		CacheUtils cacheUtils = CacheUtils.getInstance();
		this.logger.info("Register cache result: {}", cacheUtils.register("TestCache", cacheConfig));
		this.logger.info("Cache {} registered: {}", "TestCache", cacheUtils.registered("TestCache"));
		Optional.ofNullable(cacheUtils.client("TestCache"))
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
