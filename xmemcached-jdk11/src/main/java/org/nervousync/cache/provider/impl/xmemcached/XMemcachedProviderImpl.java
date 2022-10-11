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
package org.nervousync.cache.provider.impl.xmemcached;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.nervousync.cache.annotation.CacheProvider;
import org.nervousync.cache.config.CacheConfig.CacheServer;
import org.nervousync.cache.exceptions.CacheException;
import org.nervousync.cache.provider.impl.AbstractProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * The type X memcached provider.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 12/23/2020 13:43 PM $
 */
@CacheProvider(name = "XMemcachedProvider", defaultPort = 11211)
public class XMemcachedProviderImpl extends AbstractProvider {

	/**
	 * Memcached client object
	 */
	private MemcachedClient memcachedClient = null;

	/**
	 * Instantiates a new X memcached provider.
	 *
	 * @throws CacheException the cache exception
	 */
	public XMemcachedProviderImpl() throws CacheException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#initializeConnection(java.util.List)
	 */
	@Override
	protected void initializeConnection(final List<CacheServer> serverConfigList,
	                                    final String userName, final String passWord) {
		int[] serverWeightList = new int[serverConfigList.size()];

		int index = 0;

		StringBuilder serverAddresses = new StringBuilder();
		for (CacheServer memcachedServer : serverConfigList) {
			serverAddresses.append(" ")
					.append(memcachedServer.getServerAddress())
					.append(":")
					.append(memcachedServer.getServerPort());
			serverWeightList[index] = memcachedServer.getServerWeight();
			index++;
		}

		MemcachedClientBuilder clientBuilder =
				new XMemcachedClientBuilder(AddrUtil.getAddresses(serverAddresses.toString().trim()), serverWeightList);
		//  Using binary protocol instead of text protocol, if we use memcached 1.4.0 or later
		clientBuilder.setCommandFactory(new BinaryCommandFactory());

		if (serverConfigList.size() > 1) {
			//  Consistent Hash
			clientBuilder.setSessionLocator(new KetamaMemcachedSessionLocator());
			clientBuilder.setConnectionPoolSize(this.getClientPoolSize());
		}

		if (StringUtils.notBlank(userName) && StringUtils.notBlank(passWord)) {
			for (CacheServer memcachedServer : serverConfigList) {
				String serverAddress = memcachedServer.getServerAddress() + ":" + memcachedServer.getServerPort();
				clientBuilder.addAuthInfo(AddrUtil.getOneAddress(serverAddress),
						AuthInfo.plain(userName, passWord));
			}
		}
		try {
			this.memcachedClient = clientBuilder.build();
		} catch (IOException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Initialize memcached client error! ", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#set(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void set(String key, String value, int expiry) {
		try {
			this.memcachedClient.set(key, expiry, value);
		} catch (InterruptedException e) {
			this.logger.error("Process set data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process set data operate failed! ");
			this.printStackMessage(e);
		}
	}

	private void printStackMessage(Exception e) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Stack message: ", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#add(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void add(String key, String value, int expiry) {
		try {
			this.memcachedClient.add(key, expiry, value);
		} catch (InterruptedException e) {
			this.logger.error("Process add data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process add data operate failed! ");
			this.printStackMessage(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#replace(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void replace(String key, String value, int expiry) {
		try {
			this.memcachedClient.replace(key, expiry, value);
		} catch (InterruptedException e) {
			this.logger.error("Process replace data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process replace data operate failed! ");
			this.printStackMessage(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#expire(java.lang.String, int)
	 */
	@Override
	public void expire(String key, int expiry) {
		try {
			this.memcachedClient.touch(key, expiry);
		} catch (InterruptedException e) {
			this.logger.error("Process expire data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process expire data operate failed! ");
			this.printStackMessage(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#touch(java.lang.String)
	 */
	@Override
	public void touch(String... keys) {
		try {
			for (String key : keys) {
				this.memcachedClient.touch(key, this.getExpireTime());
			}
		} catch (InterruptedException e) {
			this.logger.error("Process touch data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process touch data operate failed! ");
			this.printStackMessage(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#delete(java.lang.String)
	 */
	@Override
	public void delete(String key) {
		try {
			this.memcachedClient.delete(key);
		} catch (InterruptedException e) {
			this.logger.error("Process delete data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process delete data operate failed! ");
			this.printStackMessage(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		try {
			return this.memcachedClient.get(key);
		} catch (InterruptedException e) {
			this.logger.error("Process get data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process get data operate failed! ");
			this.printStackMessage(e);
		}
		return null;
	}

	@Override
	public long incr(String key, long step) {
		try {
			return this.memcachedClient.incr(key, step);
		} catch (InterruptedException e) {
			this.logger.error("Process incr data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process incr data operate failed! ");
			this.printStackMessage(e);
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	@Override
	public long decr(String key, long step) {
		try {
			return this.memcachedClient.decr(key, step);
		} catch (InterruptedException e) {
			this.logger.error("Process decr data operate error! ");
			this.printStackMessage(e);
			Thread.currentThread().interrupt();
		} catch (TimeoutException | MemcachedException e) {
			this.logger.error("Process decr data operate failed! ");
			this.printStackMessage(e);
		}
		return Globals.DEFAULT_VALUE_LONG;
	}

	/*
	 * (non-Javadoc)
	 * @see com.nervousync.cache.provider.CacheProvider#destroy()
	 */
	@Override
	public void destroy() {
		if (this.memcachedClient != null && !this.memcachedClient.isShutdown()) {
			try {
				this.memcachedClient.shutdown();
			} catch (IOException e) {
				this.logger.error("Shutdown memcached client error! ");
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Stack message: ", e);
				}
			}
		}
	}
}