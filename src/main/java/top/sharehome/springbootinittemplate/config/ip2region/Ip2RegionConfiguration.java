package top.sharehome.springbootinittemplate.config.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import top.sharehome.springbootinittemplate.config.ip2region.condition.Ip2RegionCondition;
import top.sharehome.springbootinittemplate.config.ip2region.properties.Ip2RegionProperties;
import top.sharehome.springbootinittemplate.config.ip2region.properties.enums.LoadType;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 离线IP库配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(Ip2RegionProperties.class)
@Slf4j
@Conditional(Ip2RegionCondition.class)
public class Ip2RegionConfiguration {

    @javax.annotation.Resource
    private Ip2RegionProperties ip2RegionProperties;

    @javax.annotation.Resource
    private ResourceLoader resourceLoader;

    private Searcher searcher = null;

    /**
     * 初始化配置类
     */
    @Bean(destroyMethod = "destroy")
    @Order(1)
    public Ip2RegionConfiguration initConfiguration() {
        LoadType loadType = ip2RegionProperties.getLoadType();
        Resource resource = resourceLoader.getResource("classpath:ip2region/ip2region.xdb");
        String path = null;
        try {
            path = resource.getFile().getPath();
            if (LoadType.FILE.equals(loadType)) {
                searcher = Searcher.newWithFileOnly(path);
            } else if (LoadType.INDEX.equals(loadType)) {
                byte[] vIndex = Searcher.loadVectorIndexFromFile(path);
                searcher = Searcher.newWithVectorIndex(path, vIndex);
            } else if (LoadType.MEMORY.equals(loadType)) {
                byte[] cBuff = Searcher.loadContentFromFile(path);
                searcher = Searcher.newWithBuffer(cBuff);
            }
            if (searcher == null) {
                byte[] cBuff = Searcher.loadContentFromFile(path);
                searcher = Searcher.newWithBuffer(cBuff);
            }
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将Searcher纳入Spring IOC容器中
     */
    @Bean
    @Order(2)
    public Searcher getSearcher() {
        return searcher;
    }

    /**
     * 在Bean销毁时关闭searcher
     */
    private void destroy() {
        try {
            if (searcher != null) {
                searcher.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}
