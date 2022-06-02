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
import org.nervousync.commons.core.Globals;

/**
 * Cache server define
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Apr 25, 2017 3:18:38 PM $
 */
@XmlType(name = "cache_server")
@XmlRootElement(name = "cache_server")
@XmlAccessorType(XmlAccessType.NONE)
public final class CacheServer extends BeanObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9179968915973853412L;

	/**
	 * Server address
	 */
	@XmlElement(name = "server_address")
	private String serverAddress = Globals.DEFAULT_VALUE_STRING;
	/**
	 * Server port number
	 */
	@XmlElement(name = "server_port")
	private int serverPort = Globals.DEFAULT_VALUE_INT;
	/**
	 * Server weight
	 */
	@XmlElement(name = "server_weight")
	private int serverWeight = CacheGlobals.DEFAULT_CACHE_SERVER_WEIGHT;
	/**
	 * Is read only status
	 */
	@XmlElement(name = "read_only")
	private boolean readOnly = Boolean.FALSE;

	/**
	 * <h3 class="en">Default constructor</h3>
	 * <h3 class="zhs">默认构造方法</h3>
	 */
	public CacheServer() {
	}

	/**
	 * <h3 class="en">Match given server address/port is same as current config information</h3>
	 * <h3 class="zhs">比对指定的服务器地址/端口是否与当前配置信息一致</h3>
	 *
	 * @param serverAddress     <span class="en">Cache server address</span>
	 *                          <span class="zhs">缓存服务器地址</span>
	 * @param serverPort        <span class="en">Cache server port</span>
	 *                          <span class="zhs">缓存服务器端口号</span>
	 * @return  <span class="en">Match result</span>
	 *          <span class="en">比对结果</span>
	 */
	public boolean match(String serverAddress, int serverPort) {
		return (this.serverAddress.equalsIgnoreCase(serverAddress) && this.serverPort == serverPort);
	}

	/**
	 * <h3 class="en">Retrieve cache server address</h3>
	 * <h3 class="zhs">读取缓存服务器地址</h3>
	 *
	 * @return  <span class="en">Cache server address</span>
	 *          <span class="zhs">缓存服务器地址</span>
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * <h3 class="en">Configure cache server address</h3>
	 * <h3 class="zhs">设置缓存服务器地址</h3>
	 *
	 * @param serverAddress     <span class="en">Cache server address</span>
	 *                          <span class="zhs">缓存服务器地址</span>
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	/**
	 * <h3 class="en">Retrieve cache server port</h3>
	 * <h3 class="zhs">读取缓存服务器端口号</h3>
	 *
	 * @return  <span class="en">Cache server port</span>
	 *          <span class="zhs">缓存服务器端口号</span>
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * <h3 class="en">Configure cache server port</h3>
	 * <h3 class="zhs">设置缓存服务器端口</h3>
	 *
	 * @param serverPort        <span class="en">Cache server port</span>
	 *                          <span class="zhs">缓存服务器端口号</span>
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * <h3 class="en">Retrieve cache server weight</h3>
	 * <h3 class="zhs">获取缓存服务器权重值</h3>
	 *
	 * @return  <span class="en">Cache server weight</span>
	 *          <span class="zhs">缓存服务器权重值</span>
	 */
	public int getServerWeight() {
		return serverWeight;
	}

	/**
	 * <h3 class="en">Configure cache server weight</h3>
	 * <h3 class="zhs">设置缓存服务器权重值</h3>
	 *
	 * @param serverWeight  <span class="en">Cache server weight</span>
	 *                      <span class="zhs">缓存服务器权重值</span>
	 */
	public void setServerWeight(int serverWeight) {
		this.serverWeight = serverWeight;
	}

	/**
	 * <h3 class="en">Retrieve cache server read only status</h3>
	 * <h3 class="zhs">获取缓存服务器只读状态</h3>
	 *
	 * @return  <span class="en">Cache server read only status</span>
	 *          <span class="zhs">缓存服务器只读状态</span>
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * <h3 class="en">Configure cache server read only status</h3>
	 * <h3 class="zhs">设置缓存服务器只读状态</h3>
	 *
	 * @param readOnly  <span class="en">Cache server read only status</span>
	 *                  <span class="zhs">缓存服务器只读状态</span>
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}
