package top.sharehome.springbootinittemplate;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MainApplicationTests {
    @Resource
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {
        RBucket<Object> strKey = redissonClient.getBucket("strKey");
        strKey.set("hello");
        System.out.println(strKey.get());
    }

}
