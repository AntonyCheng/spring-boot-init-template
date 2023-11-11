package top.sharehome.springbootinittemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.elasticsearch.entity.UserEs;
import top.sharehome.springbootinittemplate.elasticsearch.mapper.UserEsMapper;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.oss.ali.AliUtils;
import top.sharehome.springbootinittemplate.utils.oss.minio.MinioUtils;
import top.sharehome.springbootinittemplate.utils.oss.tencent.TencentUtils;
import top.sharehome.springbootinittemplate.utils.rabbitmq.RabbitMqUtils;
import top.sharehome.springbootinittemplate.utils.rabbitmq.model.RabbitMqMessage;
import top.sharehome.springbootinittemplate.utils.redisson.CacheUtils;
import top.sharehome.springbootinittemplate.utils.redisson.RateLimitUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private UserService userService;

    /**
     * 引入属于Easy-ES的映射类，当测试ElasticSearch时将@Resource注解打开，测试其他示例代码时将其关闭
     */
    //@Resource
    private UserEsMapper userEsMapper;

    /**
     * 初始化管理员账号密码（数据库中默认内置有：admin/123456）
     */
    @Test
    void initAdmin() {
        User user = new User();
        // 设置用户账号
        user.setAccount("admin");
        // 设置用户密码
        user.setPassword("123456");
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getRole, "admin");
        if (ObjectUtils.isEmpty(userService.getOne(userLambdaQueryWrapper))) {
            userService.save(user);
            System.out.println("\n管理员身份创建成功！");
            // 创建之后切记前往数据库修改这条数据的"user_role"字段为"admin"
        } else {
            System.out.println("\n管理员身份已经存在！");
        }
    }

    /**
     * 修改管理员账号密码
     */
    @Test
    void updateAdmin() {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getRole, "admin");
        User admin = userService.getOne(userLambdaQueryWrapper);
        System.out.println(admin);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, admin.getId())
                // 修改管理员账号
                .set(User::getAccount, "admin")
                // 修改管理员密码
                .set(User::getPassword, "123456");
        userService.update(userLambdaUpdateWrapper);
        admin = userService.getOne(userLambdaQueryWrapper);
        System.out.println(admin);
    }

    /**
     * 测试缓存工具类
     */
    @Test
    void testCacheUtils() {
        // 测试字符串
        CacheUtils.putString("test", "test");
        System.out.println(CacheUtils.getString("test"));
        System.out.println(CacheUtils.existsString("test"));
        CacheUtils.deleteString("test");

        // 测试数字
        CacheUtils.putNumber("test", 9999999999L);
        System.out.println(CacheUtils.getNumberDoubleValue("test"));
        System.out.println(CacheUtils.existsNumber("test"));
        CacheUtils.deleteNumber("test");

        // 测试List
        List<String> l = new ArrayList<String>() {
            {
                add("test1");
                add("test2");
            }
        };
        CacheUtils.putList("test", l);
        System.out.println(CacheUtils.getList("test"));
        System.out.println(CacheUtils.existsList("test"));
        CacheUtils.deleteList("test");

        // 测试Set
        Set<String> s = new HashSet<String>() {
            {
                add("test1");
                add("test2");
            }
        };
        CacheUtils.putSet("test", s);
        System.out.println(CacheUtils.getSet("test"));
        System.out.println(CacheUtils.existsSet("test"));
        CacheUtils.deleteSet("test");

        // 测试Map
        Map<String, String> m = new HashMap<String, String>() {
            {
                put("test1", "test1");
                put("test2", "test2");
            }
        };
        CacheUtils.putMap("test", m);
        System.out.println(CacheUtils.getMap("test"));
        System.out.println(CacheUtils.existsMap("test"));
        CacheUtils.deleteMap("test");
    }

    /**
     * 测试限流工具类
     */
    @Test
    void testRateLimitUtils() throws InterruptedException {
        try {
            for (int i = 0; i < 5; i++) {
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }
        } catch (CustomizeReturnException e) {
            System.out.println("请求太多，请稍后");
        }
        ThreadUtils.sleep(Duration.ofSeconds(2));
        try {
            for (int i = 0; i < 10; i++) {
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }
        } catch (CustomizeReturnException e) {
            System.out.println("请求太多，请稍后");
        }
    }

    /**
     * 测试腾讯云COS工具类——上传
     */
    @Test
    void testTencentUtilsUpload() throws IOException {
        File file = new File("蓬勃.psd");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(TencentUtils.upload(multipartFile, "test/init"));
    }

    /**
     * 测试腾讯云COS工具类——删除
     */
    @Test
    void testTencentUtilsDelete() {
        TencentUtils.delete("https://test-1306588126.cos.ap-chengdu.myqcloud.com/test/init/61f122e335934970be3f2b14eeef37c8_README.md");
    }

    /**
     * 测试阿里云OSS工具类——上传
     */
    @Test
    void testAliUtilsUpload() throws IOException {
        File file = new File("蓬勃.psd");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(AliUtils.upload(multipartFile, "test/init"));
    }

    /**
     * 测试阿里云OSS工具类——删除
     */
    @Test
    void testAliUtilsDelete() {
        AliUtils.delete("https://antonychengtest.oss-cn-beijing.aliyuncs.com/test/init/33ce4679377b48e9a733d95deaf43975_README.md");
    }

    /**
     * 测试MinIO工具类——上传
     */
    @Test
    void testMinioUtilsUpload() throws IOException {
        File file = new File("蓬勃.psd");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(MinioUtils.upload(multipartFile, "test/init"));
    }

    /**
     * 测试MinIO工具类——删除
     */
    @Test
    void testMinioUtilsDelete() {
        MinioUtils.delete("http://8.219.59.31:9000/test/test/init/92a3aab57fcd491b89674273e0b87c11_README.md");
    }

    /**
     * 测试RabbitMqUtils
     */
    @Test
    void testRabbitMqUtils() throws InterruptedException {
        RabbitMqUtils.defaultSendMq("1", "1");
        RabbitMqMessage rabbitMqMessage = RabbitMqUtils.defaultReceiveMsg();
        System.out.println(rabbitMqMessage);
        System.out.println("=====1=====");

        RabbitMqUtils.defaultSendMqWithDlx("2", "22");
        RabbitMqMessage rabbitMqMessage1 = RabbitMqUtils.defaultReceiveMsgWithDlx();
        System.out.println(rabbitMqMessage1);
        System.out.println("=====2=====");

        RabbitMqUtils.defaultSendMqWithDlx("3", "333");
        ThreadUtils.sleep(Duration.ofSeconds(20));
        RabbitMqMessage rabbitMqMessage2 = RabbitMqUtils.defaultReceiveMsgWithDlx();
        System.out.println(rabbitMqMessage2);
        RabbitMqMessage rabbitMqMessage3 = RabbitMqUtils.defaultReceiveMsgWithDlxInDlx();
        System.out.println(rabbitMqMessage3);
        System.out.println("=====3=====");

        RabbitMqUtils.defaultSendMqWithDelay("4", "444");
        RabbitMqMessage rabbitMqMessage4 = RabbitMqUtils.defaultReceiveMsgWithDelay();
        System.out.println(rabbitMqMessage4);
        System.out.println("=====4=====");
    }

    /**
     * 测试Easy-ES创建索引
     * 这是测试EE其他实例的前置操作，不能重复创建已经存在的索引
     */
    @Test
    void testEasyEsCreateIndex() {
        // 首先判断索引是否存在
        if (!userEsMapper.existsIndex(UserEs.INDEX)) {
            // 测试创建索引,框架会根据实体类及字段上加的自定义注解一键帮您生成索引，需确保索引托管模式处于manual手动挡(默认处于此模式),若为自动挡则会冲突
            Boolean indexCreateSuccess = userEsMapper.createIndex(UserEs.INDEX);
            Assertions.assertTrue(indexCreateSuccess);
        }
    }

    /**
     * 测试Easy-ES删除索引
     * 这是测试EE其他实例的前置操作
     */
    @Test
    void testEasyEsDeleteIndex() {
        // 测试删除索引
        Boolean indexDeleteSuccess = userEsMapper.deleteIndex(UserEs.INDEX);
        Assertions.assertTrue(indexDeleteSuccess);
    }

    /**
     * 测试Easy-ES CRUD
     */
    @Test
    void testEasyEsCrud() {
        // 插入数据
        UserEs userEs = new UserEs()
                .setId(1L)
                .setName("antony")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("admin")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        Integer insertCount = userEsMapper.insert(userEs);
        System.out.println("insertCount = " + insertCount);

        // 根据字段查询数据
        LambdaEsQueryWrapper<UserEs> userEsLambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        userEsLambdaEsQueryWrapper.eq(UserEs::getRole, userEs.getRole());
        UserEs query1 = userEsMapper.selectOne(userEsLambdaEsQueryWrapper);
        System.out.println("query1 = " + query1);

        // 根据ID查询数据
        UserEs query2 = userEsMapper.selectById(userEs.getId());
        System.out.println("query2 = " + query2);

        // 根据字段修改数据
        LambdaEsUpdateWrapper<UserEs> userEsLambdaEsUpdateWrapper = new LambdaEsUpdateWrapper<>();
        userEsLambdaEsUpdateWrapper
                .set(UserEs::getRole, "user")
                .set(UserEs::getAccount, "user")
                .eq(UserEs::getRole, "admin")
                .eq(UserEs::getAccount, "admin");
        Integer updateCount1 = userEsMapper.update(null, userEsLambdaEsUpdateWrapper);
        System.out.println("updateCount1 = " + updateCount1);
        UserEs query3 = userEsMapper.selectById(userEs.getId());
        System.out.println("query3 = " + query3);

        // 根据ID修改数据
        userEs.setAccount("admin").setRole("admin");
        Integer updateCount2 = userEsMapper.updateById(userEs);
        System.out.println("updateCount2 = " + updateCount2);
        UserEs query4 = userEsMapper.selectById(userEs.getId());
        System.out.println("query4 = " + query4);

        // 根据ID删除数据
        Integer deleteCount = userEsMapper.deleteById(userEs.getId());
        System.out.println("deleteCount = " + deleteCount);
        UserEs query5 = userEsMapper.selectById(userEs.getId());
        System.out.println("query5 = " + query5);

    }

    /**
     * 测试Easy-ES高亮
     */
    @Test
    public void testEasyEsHighLight() {
        // 插入数据
        UserEs userEs = new UserEs()
                .setId(1L)
                .setName("antony")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("admin")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        Integer insertCount = userEsMapper.insert(userEs);
        System.out.println("insertCount = " + insertCount);
        LambdaEsQueryWrapper<UserEs> userEsLambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        // 高亮查询使用Match方法
        userEsLambdaEsQueryWrapper.match(UserEs::getAccount, userEs.getAccount());
        UserEs res = userEsMapper.selectOne(userEsLambdaEsQueryWrapper);
        System.out.println("res = " + res);
    }

    public static void main(String[] args) {
        User user1 = new User();
        user1.setId(123L);
        user1.setName("123");

        User user2 = new User();
        user2.setId(123L).setName("123");

        System.out.println(user1);
        System.out.println(user2);
    }

}