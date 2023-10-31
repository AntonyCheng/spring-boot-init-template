package top.sharehome.springbootinittemplate;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.elasticsearch.client.ElasticsearchClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.exception_handler.exception.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.redis.RateLimitUtils;

import javax.annotation.Resource;

@SpringBootTest
class MainApplicationTests {
    @Resource
    UserService userService;

    @Test
    void contextLoads() {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, "admin");
        User admin = userService.getOne(userLambdaQueryWrapper);
        System.out.println(admin);
    }

}
