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
package org.nervousync.cache.test.builder;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.*;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.commons.Globals;
import org.nervousync.configs.ConfigureManager;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;

import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class ConfigureBuilderTest {

    private final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());

    static {
        LoggerUtils.initLoggerConfigure(Level.DEBUG);
    }

    @BeforeAll
    public static void initialize() {
        ConfigureManager.initialize();
    }

    @Test
    @Order(10)
    public void test000Config() {
        try {
            boolean generateResult = CacheConfigBuilder.newBuilder()
                    .providerName(Globals.DEFAULT_VALUE_STRING)
                    .connectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT)
                    .expireTime(CacheGlobals.DEFAULT_EXPIRE_TIME)
                    .retryCount(3)
                    .clientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE)
                    .maximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT)
                    .masterName("ServerAddress")
                    .serverBuilder()
                    .serverConfig("onlyAddress")
                    .confirm()
                    .serverBuilder()
                    .serverConfig("ServerAddress", 11211)
                    .serverWeight(1)
                    .confirm()
                    .serverBuilder()
                    .serverConfig("ServerAddress1", 11211)
                    .serverWeight(1)
                    .confirm()
                    .serverBuilder("ServerAddress1", 11211)
                    .serverWeight(2)
                    .confirm()
                    .clusterMode(ClusterMode.Cluster)
                    .removeServer("ServerAddress1", 11211)
                    .authorization("userName", "passWord")
                    .confirm();
            if (generateResult) {
                Optional.ofNullable(ConfigureManager.getInstance().readConfigure(CacheConfig.class))
                        .ifPresent(cacheConfig -> {
                            String xmlContent = cacheConfig.toXML(Boolean.TRUE);
                            this.logger.info("Generated_Configure", xmlContent);
                            CacheConfig parsedConfig = StringUtils.stringToObject(xmlContent, CacheConfig.class,
                                    "https://nervousync.org/schemas/cache");
                            this.logger.info("Parsed_Configure", parsedConfig.toFormattedJson());
                        });
            }
        } catch (BuilderException e) {
            this.logger.error("Generated_Configure_Error", e);
        }
        ConfigureManager.getInstance().removeConfigure(CacheConfig.class);
    }
}
