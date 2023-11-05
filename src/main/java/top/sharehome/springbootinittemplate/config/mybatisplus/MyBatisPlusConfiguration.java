package top.sharehome.springbootinittemplate.config.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
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
     * 基于AOP思想进行mybatis-plus的分页操作
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        log.info(">>>>>>>>>>> mybatis plus pagination inner config init.");
        return mybatisPlusInterceptor;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    public void initDi() {
        log.info("############ mybatis plus config DI.");
    }
}