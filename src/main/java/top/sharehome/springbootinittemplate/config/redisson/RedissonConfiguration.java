package top.sharehome.springbootinittemplate.config.redisson;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Redisson配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@AllArgsConstructor
@Slf4j
public class RedissonConfiguration {

    private final RedissonProperties redissonProperties;

    private final ObjectMapper objectMapper;

    @Bean("singleClient")
    @ConditionalOnProperty(prefix = "redisson.singleServerConfig", name = "enableSingle", havingValue = "true")
    public RedissonClient singleClient() {
        Config config = new Config();
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setCodec(new JsonJacksonCodec(objectMapper));
        RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
        if (ObjectUtil.isNotNull(singleServerConfig)) {
            // 使用单机模式
            config.useSingleServer()
                    .setAddress(singleServerConfig.getAddress())
                    .setDatabase(singleServerConfig.getDatabase())
                    .setPassword(singleServerConfig.getPassword())
                    .setTimeout(singleServerConfig.getTimeout())
                    .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                    .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
        }
        RedissonClient redissonClient = Redisson.create(config);
        log.info(">>>>>>>>>>> redisson config init by redis standalone,address：{}.", singleServerConfig.getAddress());
        return redissonClient;
    }

    @Bean
    @ConditionalOnProperty(prefix = "redisson.clusterServersConfig", name = "enableCluster", havingValue = "true")
    @ConditionalOnMissingBean(name = {"singleClient"})
    public RedissonClient clusterClient() {
        Config config = new Config();
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setCodec(new JsonJacksonCodec(objectMapper));
        RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();
        if (ObjectUtil.isNotNull(clusterServersConfig)) {
            // 使用集群模式
            config.useClusterServers()
                    .setPassword(clusterServersConfig.getPassword())
                    .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                    .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                    .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                    .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                    .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                    .setTimeout(clusterServersConfig.getTimeout())
                    .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                    .setReadMode(ReadMode.SLAVE)
                    .setSubscriptionMode(SubscriptionMode.MASTER)
                    .setNodeAddresses(clusterServersConfig.getNodeAddresses());
        }
        RedissonClient redissonClient = Redisson.create(config);
        log.info(">>>>>>>>>>> redisson config init by redis cluster,addresses：{}.", clusterServersConfig.getNodeAddresses());
        return redissonClient;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ redisson config DI.");
    }

}