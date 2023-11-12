package top.sharehome.springbootinittemplate.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;

/**
 * Redis配置类
 *
 * @author AntonyCheng
 */
@Configuration
@Slf4j
@ConditionalOnBean(type = {"org.springframework.data.redis.connection.RedisConnectionFactory"})
public class RedisConfiguration extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 没有改变序列化之前的序列化规则是JdkSerializationRedisSerializer();
        // 这里只写键的序列化即可，当程序获取时会将其进行对应的反序列化;
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        log.info(">>>>>>>>>>> redis config init.");
        return redisTemplate;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ redis config DI.");
    }

}