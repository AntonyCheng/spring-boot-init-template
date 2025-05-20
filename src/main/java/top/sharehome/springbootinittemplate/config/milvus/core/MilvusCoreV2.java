package top.sharehome.springbootinittemplate.config.milvus.core;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.HasCollectionReq;
import io.milvus.v2.service.collection.request.LoadCollectionReq;
import io.milvus.v2.service.index.request.CreateIndexReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.milvus.condition.MilvusCondition;
import top.sharehome.springbootinittemplate.config.milvus.properties.MilvusProperties;

import java.util.List;

/**
 * Milvus V2版本向量数据库
 *
 * @author AntonyCheng
 */
@Component
@Conditional({MilvusCondition.class})
@EnableConfigurationProperties(MilvusProperties.class)
@Slf4j
public class MilvusCoreV2 {

    @Bean(destroyMethod = "close")
    public MilvusClientV2 milvusClientV2(MilvusProperties milvusProperties) {
        MilvusClientV2 milvusClientV2 = new MilvusClientV2(ConnectConfig.builder()
                .uri(milvusProperties.getUrl())
                .username(milvusProperties.getUsername())
                .password(milvusProperties.getPassword())
                .dbName(milvusProperties.getDatabaseName())
                .build());
        if (!milvusClientV2.hasCollection(HasCollectionReq.builder()
                .collectionName(milvusProperties.getCollectionName())
                .build())) {
            milvusClientV2.createCollection(CreateCollectionReq.builder()
                    .databaseName(milvusProperties.getDatabaseName())
                    .collectionName(milvusProperties.getCollectionName())
                    .collectionSchema(CreateCollectionReq.CollectionSchema.builder()
                            .fieldSchemaList(List.of(
                                    CreateCollectionReq.FieldSchema.builder()
                                            .name("doc_id")
                                            .dataType(DataType.VarChar)
                                            .maxLength(152)
                                            .isPrimaryKey(true)
                                            .autoID(false)
                                            .build(),
                                    CreateCollectionReq.FieldSchema.builder()
                                            .name("content")
                                            .dataType(DataType.VarChar)
                                            .isPrimaryKey(false)
                                            .autoID(false)
                                            .build(),
                                    CreateCollectionReq.FieldSchema.builder()
                                            .name("embedding")
                                            .dataType(DataType.FloatVector)
                                            .isPrimaryKey(false)
                                            .autoID(false)
                                            .build(),
                                    CreateCollectionReq.FieldSchema.builder()
                                            .name("metadata")
                                            .dataType(DataType.JSON)
                                            .isPrimaryKey(false)
                                            .autoID(false)
                                            .build()
                            ))
                            .build())
                    .build());
            milvusClientV2.createIndex(CreateIndexReq.builder()
                    .databaseName(milvusProperties.getDatabaseName())
                    .collectionName(milvusProperties.getCollectionName())
                    .indexParams(List.of(
                            IndexParam.builder()
                                    .fieldName("doc_id")
                                    .indexType(IndexParam.IndexType.AUTOINDEX)
                                    .build(),
                            IndexParam.builder()
                                    .fieldName("embedding")
                                    .indexType(IndexParam.IndexType.AUTOINDEX)
                                    .metricType(IndexParam.MetricType.COSINE)
                                    .build()
                    ))
                    .sync(true)
                    .build());
        }
        milvusClientV2.loadCollection(LoadCollectionReq.builder()
                .collectionName(milvusProperties.getCollectionName())
                .build());
        return milvusClientV2;
    }

}
