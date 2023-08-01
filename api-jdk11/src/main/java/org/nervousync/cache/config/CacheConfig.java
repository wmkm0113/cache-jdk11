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
package org.nervousync.cache.config;

import jakarta.xml.bind.annotation.*;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.enumeration.ClusterMode;
import org.nervousync.commons.Globals;
import org.nervousync.security.factory.SecureConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2 class="en">Cache configure JavaBean</h2>
 * <h2 class="zh-CN">缓存配置JavaBean</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Apr 25, 2017 3:09:14 PM $
 */
@XmlType(name = "cache_config", namespace = "https://nervousync.org/schemas/cache")
@XmlRootElement(name = "cache_config", namespace = "https://nervousync.org/schemas/cache")
@XmlAccessorType(XmlAccessType.NONE)
public final class CacheConfig extends BeanObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -6700233652090948759L;

	/**
	 * <span class="en">Cache provider name</span>
	 * <span class="zh-CN">缓存适配器名称</span>
	 */
	@XmlElement(name = "provider_name")
	private String providerName                    = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en">Secure name</span>
	 * <span class="zh-CN">安全配置名称</span>
	 */
	@XmlElement(name = "secure_name")
	private String secureName					   = Globals.DEFAULT_VALUE_STRING;
    /**
	 * <span class="en">Secure config information</span>
	 * <span class="zh-CN">安全配置定义</span>
     */
	@XmlElement(name = "secure_config", namespace = "https://nervousync.org/schemas/secure")
	private SecureConfig secureConfig;
	/**
	 * <span class="en">Connect timeout</span>
	 * <span class="zh-CN">连接超时时间</span>
	 */
	@XmlElement(name = "connect_timeout")
	private int connectTimeout						= CacheGlobals.DEFAULT_CONNECTION_TIMEOUT;
	/**
	 * <span class="en">Connect retry count</span>
	 * <span class="zh-CN">连接超时重试次数</span>
	 */
	@XmlElement(name = "retry_count")
	private int retryCount                          = CacheGlobals.DEFAULT_RETRY_COUNT;
	/**
	 * <span class="en">Default expire time</span>
	 * <span class="zh-CN">默认过期时间</span>
	 */
	@XmlElement(name = "expire_time")
	private int expireTime							= CacheGlobals.DEFAULT_EXPIRE_TIME;
	/**
	 * <span class="en">Client pool size</span>
	 * <span class="zh-CN">连接池大小</span>
	 */
	@XmlElement(name = "client_pool_size")
	private int clientPoolSize						= CacheGlobals.DEFAULT_CLIENT_POOL_SIZE;
	/**
	 * <span class="en">Limit size of generated client instance</span>
	 * <span class="zh-CN">客户端实例阈值</span>
	 */
	@XmlElement(name = "maximum_client")
	private int maximumClient						= CacheGlobals.DEFAULT_MAXIMUM_CLIENT;
	/**
	 * <span class="en">Cluster mode</span>
	 * <span class="zh-CN">集群模式</span>
	 */
	@XmlElement(name = "cluster_mode")
	private String clusterMode						= ClusterMode.Singleton.toString();
	/**
	 * <span class="en">Master name</span>
	 * <span class="zh-CN">主服务器名称</span>
	 */
	@XmlElement(name = "master_name")
	private String masterName						= Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en">Authenticate username</span>
	 * <span class="zh-CN">用于身份验证的用户名</span>
	 */
	@XmlElement(name = "username")
	private String userName = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en">Authenticate password</span>
	 * <span class="zh-CN">用于身份验证的密码</span>
	 */
	@XmlElement(name = "password")
	private String passWord = Globals.DEFAULT_VALUE_STRING;
	/**
	 * <span class="en">Cache server list</span>
	 * <span class="en">缓存服务器列表</span>
	 */
	@XmlElementWrapper(name = "server_config_list")
	@XmlElement(name = "server_config")
	private List<ServerConfig> serverConfigList;

	/**
	 * <h3 class="en">Constructor for Cache config.</h3>
	 * <h3 class="zh-CN">缓存配置JavaBean的构造器</h3>
	 */
	public CacheConfig() {
		this.serverConfigList = new ArrayList<>();
	}

	/**
	 * <h3 class="en">Retrieve cache provider name</h3>
	 * <h3 class="zh-CN">读取缓存适配器名称</h3>
	 *
	 * @return 	<span class="en">Cache provider name</span>
	 * 			<span class="zh-CN">缓存适配器名称</span>
	 */
	public String getProviderName() {
		return providerName;
	}

	/**
	 * <h3 class="en">Configure cache provider</h3>
	 * <h3 class="zh-CN">设置缓存适配器</h3>
	 *
	 * @param providerName <span class="en">Cache provider name</span>
	 *                     <span class="zh-CN">缓存适配器名称</span>
	 */
	public void setProviderName(final String providerName) {
		this.providerName = providerName;
	}

	/**
	 * <h3 class="en">Retrieve secure name</h3>
	 * <h3 class="zh-CN">读取用于保护密码的安全配置名称</h3>
	 *
	 * @return 	<span class="en">Secure name</span>
	 * 			<span class="zh-CN">安全配置名称</span>
	 */
	public String getSecureName() {
		return secureName;
	}

	/**
	 * <h3 class="en">Configure secure name for protect password</h3>
	 * <h3 class="zh-CN">设置用于保护密码的安全配置名称</h3>
	 *
	 * @param secureName <span class="en">Secure name</span>
	 *                   <span class="zh-CN">安全配置名称</span>
	 */
	public void setSecureName(final String secureName) {
		this.secureName = secureName;
	}

	/**
	 * Gets secure config.
	 *
	 * @return the secure config
	 */
	public SecureConfig getSecureConfig() {
		return secureConfig;
	}

	/**
	 * Sets secure config.
	 *
	 * @param secureConfig the secure config
	 */
	public void setSecureConfig(SecureConfig secureConfig) {
		this.secureConfig = secureConfig;
	}

	/**
	 * <h3 class="en">Retrieve cache server authorization username</h3>
	 * <h3 class="zh-CN">获取缓存服务器的用户名</h3>
	 *
	 * @return 	<span class="en">Cache server username</span>
	 * 			<span class="zh-CN">缓存服务器用户名</span>
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * <h3 class="en">Configure cache server authorization username</h3>
	 * <h3 class="zh-CN">设置缓存服务器的用户名</h3>
	 *
	 * @param userName <span class="en">Cache server username</span>
	 *                 <span class="zh-CN">缓存服务器用户名</span>
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * <h3 class="en">Retrieve cache server authorization password</h3>
	 * <h3 class="zh-CN">获取缓存服务器的密码</h3>
	 *
	 * @return 	<span class="en">Cache server password</span>
	 * 			<span class="zh-CN">缓存服务器密码</span>
	 */
	public String getPassWord() {
		return passWord;
	}

	/**
	 * <h3 class="en">Configure cache server authorization password</h3>
	 * <h3 class="zh-CN">设置缓存服务器的密码</h3>
	 *
	 * @param passWord <span class="en">Cache server password</span>
	 *                 <span class="zh-CN">缓存服务器密码</span>
	 */
	public void setPassWord(final String passWord) {
		this.passWord = passWord;
	}

	/**
	 * <h3 class="en">Retrieve cache server config list</h3>
	 * <h3 class="zh-CN">读取缓存服务器列表</h3>
	 *
	 * @return <span class="en">Cache server list</span>          <span class="en">缓存服务器列表</span>
	 */
	public List<ServerConfig> getServerConfigList() {
		return serverConfigList;
	}

	/**
	 * <h3 class="en">Configure cache server config list</h3>
	 * <h3 class="zh-CN">设置缓存服务器列表</h3>
	 *
	 * @param serverConfigList <span class="en">Cache server list</span>
	 *                         <span class="zh-CN">缓存服务器列表</span>
	 */
	public void setServerConfigList(final List<ServerConfig> serverConfigList) {
		this.serverConfigList = serverConfigList;
	}

	/**
	 * <h3 class="en">Retrieve server connect timeout</h3>
	 * <h3 class="zh-CN">读取缓存服务器的连接超时时间</h3>
	 *
	 * @return 	<span class="en">Connect timeout</span>
	 * 			<span class="zh-CN">连接超时时间</span>
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * <h3 class="en">Configure server connect timeout</h3>
	 * <h3 class="zh-CN">设置缓存服务器的连接超时时间</h3>
	 *
	 * @param connectTimeout <span class="en">Connect timeout</span>
	 *                       <span class="zh-CN">连接超时时间</span>
	 */
	public void setConnectTimeout(final int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * <h3 class="en">Retrieve server connect retry count</h3>
	 * <h3 class="zh-CN">读取缓存服务器的连接超时重试次数</h3>
	 *
	 * @return 	<span class="en">Connect retry count</span>
	 * 			<span class="zh-CN">连接超时重试次数</span>
	 */
	public int getRetryCount() {
		return retryCount;
	}

	/**
	 * <h3 class="en">Configure server connect retry count</h3>
	 * <h3 class="zh-CN">设置缓存服务器的连接超时时间</h3>
	 *
	 * @param retryCount <span class="en">Connect retry count</span>
	 *                   <span class="zh-CN">连接超时重试次数</span>
	 */
	public void setRetryCount(final int retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * <h3 class="en">Retrieve default expire time</h3>
	 * <h3 class="zh-CN">读取缓存的默认过期时间</h3>
	 *
	 * @return 	<span class="en">Default expire time</span>
	 * 			<span class="zh-CN">默认过期时间</span>
	 */
	public int getExpireTime() {
		return expireTime;
	}

	/**
	 * <h3 class="en">Configure default expire time, setting -1 for never expire</h3>
	 * <h3 class="zh-CN">设置缓存的默认过期时间，设置为-1则永不过期</h3>
	 *
	 * @param expireTime <span class="en">Default expire time</span>
	 *                   <span class="zh-CN">默认过期时间</span>
	 */
	public void setExpireTime(final int expireTime) {
		this.expireTime = expireTime;
	}

	/**
	 * <h3 class="en">Retrieve connect client pool size</h3>
	 * <h3 class="zh-CN">读取客户端连接池的大小</h3>
	 *
	 * @return 	<span class="en">Client pool size</span>
	 * 			<span class="zh-CN">连接池大小</span>
	 */
	public int getClientPoolSize() {
		return clientPoolSize;
	}

	/**
	 * <h3 class="en">Configure connect client pool size</h3>
	 * <h3 class="zh-CN">设置客户端连接池的大小</h3>
	 *
	 * @param clientPoolSize <span class="en">Client pool size</span>
	 *                       <span class="zh-CN">连接池大小</span>
	 */
	public void setClientPoolSize(final int clientPoolSize) {
		this.clientPoolSize = clientPoolSize;
	}

	/**
	 * <h3 class="en">Retrieve limit size of generated client instance</h3>
	 * <h3 class="zh-CN">读取允许创建的客户端实例阈值</h3>
	 *
	 * @return 	<span class="en">Limit size of generated client instance</span>
	 * 			<span class="zh-CN">客户端实例阈值</span>
	 */
	public int getMaximumClient() {
		return maximumClient;
	}

	/**
	 * <h3 class="en">Configure limit size of generated client instance</h3>
	 * <h3 class="zh-CN">设置允许创建的客户端实例阈值</h3>
	 *
	 * @param maximumClient <span class="en">Limit size of generated client instance</span>
	 *                      <span class="zh-CN">客户端实例阈值</span>
	 */
	public void setMaximumClient(final int maximumClient) {
		this.maximumClient = maximumClient;
	}

	/**
	 * <h3 class="en">Retrieve cluster mode</h3>
	 * <h3 class="zh-CN">读取集群类型</h3>
	 *
	 * @return 	<span class="en">Cluster mode</span>
	 * 			<span class="zh-CN">集群类型</span>
	 */
	public String getClusterMode() {
		return clusterMode;
	}

	/**
	 * <h3 class="en">Configure cluster mode</h3>
	 * <h3 class="zh-CN">设置集群类型</h3>
	 *
	 * @param clusterMode <span class="en">Cluster mode</span>
	 *                    <span class="zh-CN">集群类型</span>
	 * @see org.nervousync.cache.enumeration.ClusterMode
	 */
	public void setClusterMode(final String clusterMode) {
		this.clusterMode = clusterMode;
	}

	/**
	 * <h3 class="en">Retrieve cluster master name</h3>
	 * <h3 class="zh-CN">读取集群主服务器名称</h3>
	 *
	 * @return 	<span class="en">Master name</span>
	 * 			<span class="zh-CN">主服务器名称</span>
	 */
	public String getMasterName() {
		return masterName;
	}

	/**
	 * <h3 class="en">Configure master name</h3>
	 * <h3 class="zh-CN">设置集群主服务器名称</h3>
	 *
	 * @param masterName <span class="en">Master name</span>
	 *                   <span class="zh-CN">主服务器名称</span>
	 */
	public void setMasterName(final String masterName) {
		this.masterName = masterName;
	}

	/**
	 * <h2 class="en">Cache server configure JavaBean</h2>
	 * <h2 class="zh-CN">缓存服务器配置JavaBean</h2>
	 *
	 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
	 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 3:09:14 PM $
	 */
	@XmlType(name = "server_config", namespace = "https://nervousync.org/schemas/cache")
	@XmlRootElement(name = "server_config", namespace = "https://nervousync.org/schemas/cache")
	@XmlAccessorType(XmlAccessType.NONE)
	public static final class ServerConfig extends BeanObject {

		/**
		 *
		 */
		private static final long serialVersionUID = -9179968915973853412L;

		/**
		 * <span class="en">Server address</span>
		 * <span class="zh-CN">服务器地址</span>
		 */
		@XmlElement(name = "server_address")
		private String serverAddress;
		/**
		 * <span class="en">Server port number</span>
		 * <span class="zh-CN">服务器端口号</span>
		 */
		@XmlElement(name = "server_port")
		private int serverPort;
		/**
		 * <span class="en">Server weight</span>
		 * <span class="zh-CN">服务器权重</span>
		 */
		@XmlElement(name = "server_weight")
		private int serverWeight;

		/**
		 * <h3 class="en">Default constructor</h3>
		 * <h3 class="zh-CN">默认构造方法</h3>
		 */
		public ServerConfig() {
			this.serverAddress = Globals.DEFAULT_VALUE_STRING;
			this.serverPort = Globals.DEFAULT_VALUE_INT;
			this.serverWeight = CacheGlobals.DEFAULT_CACHE_SERVER_WEIGHT;
		}

		/**
		 * <h3 class="en">Match given server address/port is same as current config information</h3>
		 * <h3 class="zh-CN">比对指定的服务器地址/端口是否与当前配置信息一致</h3>
		 *
		 * @param serverAddress <span class="en">Cache server address</span>
		 *                      <span class="zh-CN">缓存服务器地址</span>
		 * @param serverPort    <span class="en">Cache server port</span>
		 *                      <span class="zh-CN">缓存服务器端口号</span>
		 *
		 * @return 	<span class="en">Match result</span>
		 * 			<span class="en">比对结果</span>
		 */
		public boolean match(final String serverAddress, final int serverPort) {
			return (this.serverAddress.equalsIgnoreCase(serverAddress) && this.serverPort == serverPort);
		}

		/**
		 * <h3 class="en">Match given server configure is same as current config information</h3>
		 * <h3 class="zh-CN">比对指定的服务器配置信息是否与当前配置信息一致</h3>
		 * Match boolean.
		 *
		 * @param serverConfig 	<span class="en">Cache server configure information</span>
		 *                     	<span class="zh-CN">缓存服务器配置信息</span>
		 *
		 * @return 	<span class="en">Match result</span>
		 * 			<span class="en">比对结果</span>
		 */
		public boolean match(final ServerConfig serverConfig) {
			if (serverConfig == null) {
				return Boolean.FALSE;
			}
			return this.serverAddress.equalsIgnoreCase(serverConfig.getServerAddress())
					&& this.serverPort == serverConfig.getServerPort();
		}

		/**
		 * <h3 class="en">Retrieve cache server address</h3>
		 * <h3 class="zh-CN">读取缓存服务器地址</h3>
		 *
		 * @return 	<span class="en">Cache server address</span>
		 * 			<span class="zh-CN">缓存服务器地址</span>
		 */
		public String getServerAddress() {
			return serverAddress;
		}

		/**
		 * <h3 class="en">Configure cache server address</h3>
		 * <h3 class="zh-CN">设置缓存服务器地址</h3>
		 *
		 * @param serverAddress <span class="en">Cache server address</span>
		 *                      <span class="zh-CN">缓存服务器地址</span>
		 */
		public void setServerAddress(final String serverAddress) {
			this.serverAddress = serverAddress;
		}

		/**
		 * <h3 class="en">Retrieve cache server port</h3>
		 * <h3 class="zh-CN">读取缓存服务器端口号</h3>
		 *
		 * @return 	<span class="en">Cache server port</span>
		 * 			<span class="zh-CN">缓存服务器端口号</span>
		 */
		public int getServerPort() {
			return serverPort;
		}

		/**
		 * <h3 class="en">Configure cache server port</h3>
		 * <h3 class="zh-CN">设置缓存服务器端口</h3>
		 *
		 * @param serverPort <span class="en">Cache server port</span>
		 *                   <span class="zh-CN">缓存服务器端口号</span>
		 */
		public void setServerPort(final int serverPort) {
			this.serverPort = serverPort;
		}

		/**
		 * <h3 class="en">Retrieve cache server weight</h3>
		 * <h3 class="zh-CN">获取缓存服务器权重值</h3>
		 *
		 * @return 	<span class="en">Cache server weight</span>
		 * 			<span class="zh-CN">缓存服务器权重值</span>
		 */
		public int getServerWeight() {
			return serverWeight;
		}

		/**
		 * <h3 class="en">Configure cache server weight</h3>
		 * <h3 class="zh-CN">设置缓存服务器权重值</h3>
		 *
		 * @param serverWeight <span class="en">Cache server weight</span>
		 *                     <span class="zh-CN">缓存服务器权重值</span>
		 */
		public void setServerWeight(final int serverWeight) {
			this.serverWeight = serverWeight;
		}
	}
}
