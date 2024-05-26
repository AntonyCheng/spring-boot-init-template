package top.sharehome.springbootinittemplate.elasticsearch;

import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.elasticsearch.entity.UserEs;
import top.sharehome.springbootinittemplate.elasticsearch.mapper.UserEsMapper;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 测试ElasticSearch类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class ElasticsearchTest {

    /**
     * 引入属于Easy-ES的映射类，当测试ElasticSearch时将@Resource注解打开，测试其他示例代码时将其关闭
     */
    @Resource
    private UserEsMapper userEsMapper;

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

}