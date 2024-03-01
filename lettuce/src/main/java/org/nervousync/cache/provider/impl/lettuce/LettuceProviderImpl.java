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

package org.nervousync.cache.provider.impl.lettuce;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import org.nervousync.annotations.provider.Provider;
import org.nervousync.cache.config.CacheConfig.ServerConfig;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.utils.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en-US">Redis cache provider using Lettuce</h2>
 * <h2 class="zh-CN">缓存客户端适配器，使用 Lettuce 实现</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Aug 25, 2020 16:07:35 $
 */
@Provider(name = "LettuceProvider", titleKey = "lettuce.cache.provider.name")
public final class LettuceProviderImpl extends AbstractProvider {

    private AbstractRedisClient redisClient;

    private StatefulRedisClusterConnection<String, String> clusterConnection = null;

    private StatefulRedisConnection<String, String> redisConnection = null;
    private RedisClusterCommands<String, String> redisCommands = null;

    /**
     * Instantiates a new Lettuce provider.
     */
    public LettuceProviderImpl() {
    }

    @Override
    public int defaultPort() {
        return 6379;
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#set(String, String, int)
     */
    @Override
    public void set(final String key, final String value, final int expire) {
        this.process(key, value, expire);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#add(String, String, int)
     */
    @Override
    public void add(final String key, final String value, final int expire) {
        this.process(key, value, expire);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#replace(String, String, int)
     */
    @Override
    public void replace(final String key, final String value, final int expire) {
        this.process(key, value, expire);
    }

    /*
     * (non-Javadoc)
     * @see AbstractProvider#expire(String, int)
     */
    @Override
    public void expire(final String key, final int expire) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("TTL_Lettuce_Cache_Debug", this.redisCommands.ttl(key));
        }
        this.redisCommands.expire(key, expire);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#touch(String...)
     */
    @Override
    public void touch(final String... keys) {
        this.redisCommands.touch(keys);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#delete(String)
     */
    @Override
    public void delete(final String key) {
        this.redisCommands.del(key);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#get(String)
     */
    @Override
    public String get(final String key) {
        return this.redisCommands.get(key);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#incr(String, long)
     */
    @Override
    public long incr(final String key, final long step) {
        return this.redisCommands.incrby(key, step);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#decr(String, long)
     */
    @Override
    public long decr(final String key, final long step) {
        return this.redisCommands.decrby(key, step);
    }

    /*
     * (non-Javadoc)
     * @see org.nervousync.cache.provider.Provider#destroy()
     */
    @Override
    public void destroy() {
        if (this.redisConnection != null) {
            this.redisConnection.close();
            this.redisConnection = null;
        }
        if (this.clusterConnection != null) {
            this.clusterConnection.close();
            this.clusterConnection = null;
        }
        this.redisClient.close();
        this.redisClient.shutdown();
    }

    /*
     * (non-Javadoc)
     * @see AbstractProvider#singletonMode(ServerConfig, String, String)
     */
    protected void singletonMode(final ServerConfig serverConfig, final String userName, final String passWord) {
        this.redisClient = RedisClient.create(this.create(serverConfig, userName, passWord));
        this.redisConnection = ((RedisClient) this.redisClient).connect(StringCodec.UTF8);
        this.redisCommands = this.redisConnection.sync();
    }

    /*
     * (non-Javadoc)
     * @see AbstractProvider#clusterMode(List, String, String, String)
     */
    protected void clusterMode(final List<ServerConfig> serverConfigList, final String masterName,
                             final String userName, final String passWord) {
        if (serverConfigList.isEmpty()) {
            return;
        }
        if (serverConfigList.size() == 1) {
            this.singletonMode(serverConfigList.get(0), userName, passWord);
            return;
        }

        switch (this.getClusterMode()) {
            case Sentinel:
                RedisURI.Builder sentinelBuilder = this.newBuilder().withSentinelMasterId(masterName);
                serverConfigList.forEach(serverConfig ->
                        sentinelBuilder.withSentinel(this.create(serverConfig, userName, passWord)));
                this.redisClient = RedisClient.create(sentinelBuilder.build());
                this.redisConnection = ((RedisClient) this.redisClient).connect(StringCodec.UTF8);
                this.redisCommands = this.redisConnection.sync();
                break;
            case Master_Slave:
                List<RedisURI> masterList = new ArrayList<>(serverConfigList.size());
                List<RedisURI> slaveList = new ArrayList<>(serverConfigList.size());
                serverConfigList.forEach(serverConfig -> {
                    if (serverConfig.getServerAddress().equalsIgnoreCase(masterName)) {
                        masterList.add(this.create(serverConfig, userName, passWord));
                    } else {
                        slaveList.add(this.create(serverConfig, userName, passWord));
                    }
                });
                List<RedisURI> serverList = new ArrayList<>();
                serverList.addAll(masterList);
                serverList.addAll(slaveList);
                this.redisClient = RedisClient.create();
                this.redisConnection = MasterReplica.connect((RedisClient) this.redisClient, StringCodec.UTF8, serverList);
                ((StatefulRedisMasterReplicaConnection<String, String>) this.redisConnection).setReadFrom(ReadFrom.REPLICA);
                this.redisCommands = this.redisConnection.sync();
                break;
            case Cluster:
                List<RedisURI> clusterList = new ArrayList<>(serverConfigList.size());
                serverConfigList.forEach(serverConfig -> clusterList.add(this.create(serverConfig, userName, passWord)));
                this.redisClient = RedisClusterClient.create(clusterList);
                ((RedisClusterClient) this.redisClient)
                        .setOptions(ClusterClientOptions.builder().autoReconnect(Boolean.TRUE).maxRedirects(1).build());
                this.clusterConnection = ((RedisClusterClient) this.redisClient).connect(StringCodec.UTF8);
                this.redisCommands = this.clusterConnection.sync();
                break;
        }
    }

    private void process(final String key, final String value, final int expire) {
        this.redisCommands.setex(key, super.expiryTime(expire), value);
    }

    private RedisURI.Builder newBuilder() {
        return RedisURI.builder().withTimeout(Duration.ofMillis(this.getConnectTimeout() * 1000L));
    }

    private RedisURI create(final ServerConfig serverConfig, final String userName, final String passWord) {
        RedisURI.Builder serverBuilder = this.newBuilder()
                .withHost(serverConfig.getServerAddress())
                .withPort(serverConfig.getServerPort());
        if (StringUtils.notBlank(passWord)) {
            if (StringUtils.isEmpty(userName)) {
                serverBuilder.withPassword(passWord.toCharArray());
            } else {
                serverBuilder.withAuthentication(userName, passWord.toCharArray());
            }
        }
        return serverBuilder.build();
    }
}
