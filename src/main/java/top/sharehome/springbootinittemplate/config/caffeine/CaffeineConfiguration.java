package top.sharehome.springbootinittemplate.config.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.caffeine.condition.CaffeineCondition;
import top.sharehome.springbootinittemplate.config.caffeine.properties.CaffeineProperties;
import top.sharehome.springbootinittemplate.config.redisson.condition.RedissonCondition;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(CaffeineProperties.class)
@AllArgsConstructor
@Slf4j
@Conditional(CaffeineCondition.class)
public class CaffeineConfiguration {

    private final CaffeineProperties caffeineProperties;

    @Bean("localCache")
    public Cache<String, Object> localCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(caffeineProperties.getExpired(), TimeUnit.SECONDS)
                .expireAfterAccess(caffeineProperties.getExpired(), TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(caffeineProperties.getInitCapacity())
                // 缓存的最大条数
                .maximumSize(caffeineProperties.getMaxCapacity())
                .build();
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName());
    }

}