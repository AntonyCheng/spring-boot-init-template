package top.sharehome.springbootinittemplate.config.mybatisplus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * MyBatis-Plus配置
 *
 * @author AntonyCheng
 */
@Configuration
@Slf4j
public class MyBatisPlusConfiguration {

    /**
     * MyBatis-Plus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 分页插件，如果有多个插件，分页插件添加在最后
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 防止全表更新与删除插件
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // 数据变动记录插件
        mybatisPlusInterceptor.addInnerInterceptor(new DataChangeRecorderInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}