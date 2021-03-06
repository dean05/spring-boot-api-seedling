package com.zoctan.seedling.core.config;

import com.zoctan.seedling.core.cache.MyRedisCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * Redis缓存配置
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Configuration
// 只有 application.properties 中有 spring.redis.host 时才配置
@ConditionalOnProperty(name = "spring.redis.host")
@EnableConfigurationProperties(RedisProperties.class)
// 支持 @Cacheable、@CachePut、@CacheEvict 等缓存注解
@EnableCaching(proxyTargetClass = true)
public class RedisCacheConfig extends CachingConfigurerSupport {
    private final static Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Resource
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    @Override
    public CacheManager cacheManager() {
        // 初始化一个RedisCacheWriter
        final RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(this.jedisConnectionFactory);
        // 设置默认过期时间：30分钟
        final RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                // .disableCachingNullValues()
                // 使用注解时的序列化、反序列化
                .serializeKeysWith(MyRedisCacheManager.STRING_PAIR)
                .serializeValuesWith(MyRedisCacheManager.FASTJSON_PAIR);
        // 初始化RedisCacheManager
        return new MyRedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }

    /**
     * 如果 @Cacheable、@CachePut、@CacheEvict 等注解没有配置 key，则使用这个自定义 key 生成器
     * <p>
     * 自定义缓存的 key 时，难以保证 key 的唯一性
     * 此时最好指定方法名，比如：@Cacheable(value="", key="{#root.methodName, #id}")
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            final StringBuilder sb = new StringBuilder(32);
            sb.append(o.getClass().getSimpleName());
            sb.append(".");
            sb.append(method.getName());
            if (objects.length > 0) {
                sb.append("#");
            }
            String sp = "";
            for (final Object object : objects) {
                sb.append(sp);
                if (object == null) {
                    sb.append("NULL");
                } else {
                    sb.append(object.toString());
                }
                sp = ".";
            }
            return sb.toString();
        };
    }

    /**
     * 错误处理
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(final RuntimeException e, final Cache cache, final Object key) {
                log.error("cache => {}", cache);
                log.error("key => {}", key);
                log.error("handleCacheGetError => ", e.getMessage());
                super.handleCacheGetError(e, cache, key);
            }

            @Override
            public void handleCachePutError(final RuntimeException e, final Cache cache, final Object key, final Object value) {
                log.error("cache => {}", cache);
                log.error("key => {}", key);
                log.error("value => {}", value);
                log.error("handleCachePutError => ", e.getMessage());
                super.handleCachePutError(e, cache, key, value);
            }

            @Override
            public void handleCacheEvictError(final RuntimeException e, final Cache cache, final Object key) {
                log.error("cache => {}", cache);
                log.error("key => {}", key);
                log.error("handleCacheEvictError => ", e.getMessage());
                super.handleCacheEvictError(e, cache, key);
            }

            @Override
            public void handleCacheClearError(final RuntimeException e, final Cache cache) {
                log.error("cache => {}", cache);
                log.error("handleCacheClearError => ", e.getMessage());
                super.handleCacheClearError(e, cache);
            }
        };
    }
}
