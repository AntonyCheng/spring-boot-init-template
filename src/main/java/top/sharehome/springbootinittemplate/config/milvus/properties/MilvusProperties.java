package top.sharehome.springbootinittemplate.config.milvus.properties;

import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Milvus向量数据库配置
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "milvus")
public class MilvusProperties {

    /**
     * Milvus向量数据库开关
     */
    private Boolean enable = false;

    /**
     * Milvus向量数据库主机
     */
    private String url = "http://127.0.0.1:19530";

    /**
     * Milvus向量数据库用户名称
     */
    private String username = "";

    /**
     * Milvus向量数据库密码
     */
    private String password = "";

    /**
     * Milvus向量数据库名称
     */
    private String databaseName = "default";

    /**
     * Milvus向量数据库集合名称
     */
    private String collectionName = "vector_store";

    /**
     * Milvus向量数据库嵌入维度
     */
    private Integer embeddingDimension = 1536;

}
