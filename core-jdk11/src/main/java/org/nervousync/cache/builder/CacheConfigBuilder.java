package org.nervousync.cache.builder;

import org.nervousync.cache.commons.CacheGlobals;
import org.nervousync.cache.config.CacheConfig;
import org.nervousync.cache.config.CacheConfig.CacheServer;
import org.nervousync.cache.core.CacheCore;
import org.nervousync.commons.core.Globals;
import org.nervousync.security.factory.SecureFactory;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.StringUtils;

import java.util.List;

/**
 * <h2 class="en">Cache configure builder</h2>
 * <h2 class="zhs">缓存配置构建器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2018-12-26 17:23 $
 */
public final class CacheConfigBuilder {

    /**
     * <span class="en">Cache configure instance</span>
     * <span class="zhs">缓存配置实例</span>
     */
    private final CacheConfig cacheConfig;
    /**
     * <span class="en">Configure modify status</span>
     * <span class="zhs">配置修改状态</span>
     */
    private boolean modified = Boolean.FALSE;

    /**
     * <h3 class="en">Constructor for cache configure builder</h3>
     * <h3 class="zhs">缓存配置构造器构建方法</h3>
     *
     * @param cacheConfig   <span class="en">Current configure instance or null for generate new configure</span>
     *                      <span class="zhs">当前的缓存配置，如果传入null则生成一个新的配置</span>
     */
    private CacheConfigBuilder(final CacheConfig cacheConfig) {
        if (cacheConfig == null) {
            this.cacheConfig = new CacheConfig();
            this.modified = Boolean.TRUE;
        } else {
            this.cacheConfig = cacheConfig;
        }
    }

    public static CacheConfigBuilder builder() {
        return builder(null);
    }

    public static CacheConfigBuilder builder(final CacheConfig cacheConfig) {
        return new CacheConfigBuilder(cacheConfig);
    }

    /**
     * <h3 class="en">Configure cache provider</h3>
     * <h3 class="zhs">设置缓存适配器</h3>
     *
     * @param providerName  <span class="en">Cache provider name</span>
     *                      <span class="zhs">缓存适配器名称</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder providerName(final String providerName) {
        if (StringUtils.notBlank(providerName) && CacheCore.registeredProvider(providerName)) {
            if (!providerName.equalsIgnoreCase(this.cacheConfig.getProviderName())) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setProviderName(providerName);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure secure name for protect password</h3>
     * <h3 class="zhs">设置用于保护密码的安全配置名称</h3>
     *
     * @param secureName    <span class="en">Secure name</span>
     *                      <span class="zhs">安全配置名称</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder secureName(final String secureName) {
        if (StringUtils.notBlank(secureName)) {
            if (!secureName.equalsIgnoreCase(this.cacheConfig.getSecureName())) {
                if (StringUtils.notBlank(this.cacheConfig.getPassWord())) {
                    String renewPassword;
                    if (StringUtils.isEmpty(this.cacheConfig.getSecureName())) {
                        renewPassword = this.encryptPassword(secureName, this.cacheConfig.getPassWord());
                    } else {
                        renewPassword = this.encryptPassword(secureName,
                                this.decryptPassword(this.cacheConfig.getSecureName(), this.cacheConfig.getPassWord()));
                    }
                    this.cacheConfig.setPassWord(renewPassword);
                }
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setSecureName(secureName);
        } else {
            if (StringUtils.notBlank(this.cacheConfig.getSecureName())) {
                this.cacheConfig.setPassWord(
                        this.decryptPassword(this.cacheConfig.getSecureName(), this.cacheConfig.getPassWord()));
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setSecureName(Globals.DEFAULT_VALUE_STRING);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure server connect timeout</h3>
     * <h3 class="zhs">设置缓存服务器的连接超时时间</h3>
     *
     * @param connectTimeout     <span class="en">Connect timeout</span>
     *                           <span class="zhs">连接超时时间</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder connectTimeout(final int connectTimeout) {
        if (connectTimeout > 0) {
            if (connectTimeout != this.cacheConfig.getConnectTimeout()) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setConnectTimeout(connectTimeout);
        } else {
            if (this.cacheConfig.getConnectTimeout() != CacheGlobals.DEFAULT_CONNECTION_TIMEOUT) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setConnectTimeout(CacheGlobals.DEFAULT_CONNECTION_TIMEOUT);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure default expire time, setting -1 for never expire</h3>
     * <h3 class="zhs">设置缓存的默认过期时间，设置为-1则永不过期</h3>
     *
     * @param expireTime    <span class="en">Default expire time</span>
     *                      <span class="zhs">默认过期时间</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder expireTime(final int expireTime) {
        if (expireTime > 0) {
            if (expireTime != this.cacheConfig.getExpireTime()) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setExpireTime(expireTime);
        } else {
            if (this.cacheConfig.getExpireTime() != CacheGlobals.DEFAULT_EXPIRE_TIME) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setExpireTime(CacheGlobals.DEFAULT_EXPIRE_TIME);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure connect client pool size</h3>
     * <h3 class="zhs">设置客户端连接池的大小</h3>
     *
     * @param clientPoolSize    <span class="en">Client pool size</span>
     *                          <span class="zhs">连接池大小</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder clientPoolSize(final int clientPoolSize) {
        if (clientPoolSize > 0) {
            if (clientPoolSize != this.cacheConfig.getClientPoolSize()) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setClientPoolSize(clientPoolSize);
        } else {
            if (this.cacheConfig.getClientPoolSize() != CacheGlobals.DEFAULT_CLIENT_POOL_SIZE) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setClientPoolSize(CacheGlobals.DEFAULT_CLIENT_POOL_SIZE);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure limit size of generated client instance</h3>
     * <h3 class="zhs">设置允许创建的客户端实例阈值</h3>
     *
     * @param maximumClient     <span class="en">Limit size of generated client instance</span>
     *                          <span class="zhs">客户端实例阈值</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder maximumClient(final int maximumClient) {
        if (maximumClient > 0) {
            if (maximumClient != this.cacheConfig.getMaximumClient()) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setMaximumClient(maximumClient);
        } else {
            if (this.cacheConfig.getMaximumClient() != CacheGlobals.DEFAULT_MAXIMUM_CLIENT) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setMaximumClient(CacheGlobals.DEFAULT_MAXIMUM_CLIENT);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure connection timeout retry count</h3>
     * <h3 class="zhs">设置连接超时后的重试次数</h3>
     *
     * @param retryCount     <span class="en">Connect retry count</span>
     *                       <span class="zhs">连接超时重试次数</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder retryCount(final int retryCount) {
        if (retryCount > 0) {
            if (retryCount != this.cacheConfig.getRetryCount()) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setRetryCount(retryCount);
        } else {
            if (this.cacheConfig.getRetryCount() != CacheGlobals.DEFAULT_RETRY_COUNT) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setRetryCount(CacheGlobals.DEFAULT_RETRY_COUNT);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure cache server authorization information</h3>
     * <h3 class="zhs">设置缓存服务器的用户名和密码</h3>
     *
     * @param userName  <span class="en">Cache server username</span>
     *                  <span class="zhs">缓存服务器用户名</span>
     * @param passWord  <span class="en">Cache server password</span>
     *                  <span class="zhs">缓存服务器密码</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder authorization(final String userName, final String passWord) {
        if (StringUtils.notBlank(userName)) {
            if (!userName.equalsIgnoreCase(this.cacheConfig.getUserName())) {
                this.modified = Boolean.TRUE;
            }
        } else {
            if (StringUtils.notBlank(this.cacheConfig.getUserName())) {
                this.modified = Boolean.TRUE;
            }
        }
        this.cacheConfig.setUserName(userName);

        if (StringUtils.notBlank(passWord)) {
            String encPassword;
            if (StringUtils.notBlank(this.cacheConfig.getSecureName())) {
                byte[] encBytes = SecureFactory.getInstance().encrypt(this.cacheConfig.getSecureName(),
                        ConvertUtils.convertToByteArray(passWord));
                encPassword = StringUtils.base64Encode(encBytes);
            } else {
                encPassword = passWord;
            }
            if (!encPassword.equalsIgnoreCase(this.cacheConfig.getPassWord())) {
                this.modified = Boolean.TRUE;
                this.cacheConfig.setPassWord(encPassword);
            }
        } else {
            if (StringUtils.notBlank(this.cacheConfig.getPassWord())) {
                this.modified = Boolean.TRUE;
            }
            this.cacheConfig.setPassWord(Globals.DEFAULT_VALUE_STRING);
        }
        return this;
    }

    /**
     * <h3 class="en">Configure cache server information</h3>
     * <h3 class="zhs">设置缓存服务器相关信息</h3>
     *
     * @param serverAddress     <span class="en">Cache server address</span>
     *                          <span class="zhs">缓存服务器地址</span>
     * @param serverPort        <span class="en">Cache server port</span>
     *                          <span class="zhs">缓存服务器端口号</span>
     * @param serverWeight  <span class="en">Cache server weight</span>
     *                      <span class="zhs">缓存服务器权重值</span>
     * @param readOnly  <span class="en">Cache server read only status</span>
     *                  <span class="zhs">缓存服务器只读状态</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder configServer(final String serverAddress, final int serverPort,
                                           final int serverWeight, final boolean readOnly) {
        List<CacheServer> cacheServerList = this.cacheConfig.getServerConfigList();
        if (cacheServerList.stream().anyMatch(serverConfig -> serverConfig.match(serverAddress, serverPort))) {
            cacheServerList.replaceAll(serverConfig -> {
                if (serverConfig.match(serverAddress, serverPort)) {
                    serverConfig.setServerWeight(serverWeight);
                    serverConfig.setReadOnly(readOnly);
                    this.modified = Boolean.TRUE;
                }
                return serverConfig;
            });
        } else {
            CacheServer serverConfig = new CacheServer();
            serverConfig.setServerAddress(serverAddress);
            serverConfig.setServerPort(serverPort);
            serverConfig.setServerWeight(serverWeight);
            serverConfig.setReadOnly(readOnly);
            cacheServerList.add(serverConfig);
            this.modified = Boolean.TRUE;
        }
        this.cacheConfig.setServerConfigList(cacheServerList);
        return this;
    }

    /**
     * <h3 class="en">Remove cache server information</h3>
     * <h3 class="zhs">删除缓存服务器信息</h3>
     *
     * @param serverAddress     <span class="en">Cache server address</span>
     *                          <span class="zhs">缓存服务器地址</span>
     * @param serverPort        <span class="en">Cache server port</span>
     *                          <span class="zhs">缓存服务器端口号</span>
     * @return  <span class="en">Current cache configure builder</span>
     *          <span class="zhs">当前缓存配置构建器</span>
     */
    public CacheConfigBuilder removeServer(final String serverAddress, final int serverPort) {
        List<CacheServer> cacheServerList = this.cacheConfig.getServerConfigList();
        if (cacheServerList.removeIf(serverConfig -> serverConfig.match(serverAddress, serverPort))) {
            this.modified = Boolean.TRUE;
            this.cacheConfig.setServerConfigList(cacheServerList);
        }
        return this;
    }

    /**
     * <h3 class="en">Confirm cache config and read cache config instance</h3>
     * <h3 class="zhs">确认配置完成并读取缓存配置实例</h3>
     *
     * @return  <span class="en">Cache config instance</span>
     *          <span class="zhs">缓存配置实例</span>
     */
    public CacheConfig confirmConfig() {
        return this.cacheConfig;
    }

    /**
     * <h3 class="en">Read configure instance modify status</h3>
     * <h3 class="zhs">读取当前缓存配置的修改状态</h3>
     *
     * @return  <span class="en">Modify status</span>
     *          <span class="zhs">缓存配置的修改状态</span>
     */
    public boolean isModified() {
        return this.modified;
    }

    private String decryptPassword(final String secureName, final String passWord) {
        byte[] decryptData = SecureFactory.getInstance().decrypt(secureName, StringUtils.base64Decode(passWord));
        return ConvertUtils.convertToString(decryptData);
    }

    private String encryptPassword(final String secureName, final String passWord) {
        byte[] encryptData = SecureFactory.getInstance().encrypt(secureName, ConvertUtils.convertToByteArray(passWord));
        return StringUtils.base64Encode(encryptData);
    }
}
