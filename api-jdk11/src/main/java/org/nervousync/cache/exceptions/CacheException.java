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
package org.nervousync.cache.exceptions;

/**
 * <h2 class="en">Cache Exception</h2>
 * <h2 class="zh-CN">缓存异常</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 6:30:42 PM $
 */
public final class CacheException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -156775157749202954L;

    /**
     * <h3 class="en">Constructs an instance of CachedException.</h3>
     * <h3 class="zh-CN">默认构造器</h3>
     */
    public CacheException() {
    }

    /**
     * <h3 class="en">Constructs an instance of CachedException with the specified detail message.</h3>
     * <h3 class="zh-CN">使用给定的异常信息构造CachedException实例</h3>
     *
     * @param errorMessage  <span class="en">The detail message.</span>
     *                      <span class="zh-CN">异常信息详情</span>
     */
    public CacheException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * <h3 class="en">Constructs an instance of CachedException with nested exception.</h3>
     * <h3 class="zh-CN">使用给定的异常实例构造CachedException实例</h3>
     *
     * @param e     <span class="en">Nested exception.</span>
     *              <span class="zh-CN">异常实例</span>
     */
    public CacheException(Exception e) {
        super(e);
    }
}
