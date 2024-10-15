package top.sharehome.springbootinittemplate.config.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import top.sharehome.springbootinittemplate.config.ip2region.properties.Ip2RegionProperties;
import top.sharehome.springbootinittemplate.config.ip2region.properties.enums.LoadType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 离线IP库配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(Ip2RegionProperties.class)
@Slf4j
public class Ip2RegionConfiguration {

    @javax.annotation.Resource
    private Ip2RegionProperties ip2RegionProperties;

    private Searcher searcher = null;

    /**
     * 初始化配置类
     * 由于打包之后Ip2Region的数据文件就不能以Path形式导入进来，所以直接在系统临时文件夹创建一个ip2region.xdb文件
     */
    @Bean(destroyMethod = "destroy")
    @Order(1)
    public Ip2RegionConfiguration initConfiguration() {
        String fileName = "ip2region" + File.separator + "ip2region.xdb";
        try (InputStream classPathFileStream = new ClassPathResource(fileName).getInputStream()) {
            LoadType loadType = ip2RegionProperties.getLoadType();
            String tempFile = (StringUtils.endsWith(FileUtils.getTempDirectoryPath(), File.separator) ? FileUtils.getTempDirectoryPath() : FileUtils.getTempDirectoryPath() + File.separator) + fileName;
            File existFile = new File(tempFile);
            if (!existFile.exists()) {
                FileUtils.copyInputStreamToFile(classPathFileStream, existFile);
            }
            String path = existFile.getPath();
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
            String fileName = "ip2region" + File.separator + "ip2region.xdb";
            String tempFile = (StringUtils.endsWith(FileUtils.getTempDirectoryPath(), File.separator) ? FileUtils.getTempDirectoryPath() : FileUtils.getTempDirectoryPath() + File.separator) + fileName;
            File existFile = new File(tempFile);
            if (existFile.exists()) {
                FileUtils.delete(existFile);
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
