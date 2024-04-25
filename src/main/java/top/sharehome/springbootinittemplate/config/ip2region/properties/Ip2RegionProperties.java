package top.sharehome.springbootinittemplate.config.ip2region.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.sharehome.springbootinittemplate.config.ip2region.properties.enums.LoadType;

/**
 * 离线IP库配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "ip2region")
public class Ip2RegionProperties {

    /**
     * 是否开启离线IP库
     */
    private Boolean enable = false;

    /**
     * 加载数据方式，默认为内存加载
     */
    private LoadType loadType = LoadType.MEMORY;

}
