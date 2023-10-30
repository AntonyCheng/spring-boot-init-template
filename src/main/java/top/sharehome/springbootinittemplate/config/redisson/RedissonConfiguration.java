package top.sharehome.springbootinittemplate.config.redisson;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Redisson配置
 *
 * @author AntonyCheng
 */
@Configuration
public class RedissonConfiguration {
    @Resource
    private RedissonProperties redissonProperties;

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public RedissonClient redissonClient() {
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
            // 这里进行数据返回是为了避免单机配置和集群配置同时打开时造成的错误，
            // 如果需要使用redis集群，那么就需要认真核对application配置中的相关项，注释掉其中不需要的部分
            return Redisson.create(config);
        }
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
        return Redisson.create(config);
    }
}
