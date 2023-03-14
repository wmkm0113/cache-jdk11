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
package org.nervousync.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2 class="en">Cache provider annotation</h2>
 * <h2 class="zh-CN">缓存适配器注解</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2018-12-26 17:23 $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CacheProvider {

	/**
	 * <h3 class="en">Provider name. Configure file using current name to identify provider</h3>
	 * <h3 class="zh-CN">适配器名称，在配置文件中使用此名称识别适配器</h3>
	 *
	 * @return  <span class="en">Provider name</span>
	 *          <span class="zh-CN">适配器名称</span>
	 */
	String name();

	/**
	 * <h3 class="en">Default service port number. System using this value to connect cache server if configure file setting server_port is -1</h3>
	 * <h3 class="zh-CN">默认服务端口号，如果配置文件中的服务器端口设置为-1，则系统使用此端口连接缓存服务器</h3>
	 *
	 * @return  <span class="en">Service port number</span>
	 *          <span class="zh-CN">默认服务端口号</span>
	 */
	int defaultPort();
}
