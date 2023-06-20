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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nervousync.cache.builder.CacheConfigBuilder;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.commons.core.Globals;
import org.nervousync.exceptions.builder.BuilderException;
import org.nervousync.security.factory.SecureConfig;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.LoggerUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class ConfigureBuilderTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        LoggerUtils.initLoggerConfigure(Level.DEBUG);
    }

    @Test
    public void test000Config() {
        generateConfig(Globals.DEFAULT_VALUE_STRING, null);
    }

    @Test
    public void test010SecureConfig() {
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256).ifPresent(SecureFactory::initialize);
        SecureFactory.initConfig(SecureFactory.SecureAlgorithm.AES256)
                .ifPresent(secureConfig -> generateConfig("SecureCache", secureConfig));
    }

    private void generateConfig(final String secureName, SecureConfig secureConfig) {
        try {
            CacheConfig cacheConfig = CacheConfigBuilder.newBuilder()
                    .providerName(Globals.DEFAULT_VALUE_STRING)
                    .secureName(secureName)
                    .secureConfig(secureName, secureConfig)
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
            String xmlContent = cacheConfig.toXML();
            this.logger.info("Secure name: {}, generated config: {}", secureName, xmlContent);
            CacheConfig parsedConfig = StringUtils.stringToObject(xmlContent, CacheConfig.class,
                    "https://nervousync.org/schemas/cache");
            this.logger.info("Parsed config: {}", parsedConfig.toFormattedJson());
        } catch (BuilderException e) {
            this.logger.error("Generate config error! ", e);
        }
    }
}
