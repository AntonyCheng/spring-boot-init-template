package top.sharehome.springbootinittemplate.config.milvus.core;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.milvus.condition.MilvusCondition;
import top.sharehome.springbootinittemplate.config.milvus.properties.MilvusProperties;

import java.util.ArrayList;

/**
 * Milvus V1版本向量数据库
 *
 * @author AntonyCheng
 */
@Component
@Conditional({MilvusCondition.class})
@EnableConfigurationProperties(MilvusProperties.class)
@Slf4j
public class MilvusCoreV1 {

    @Bean(destroyMethod = "close")
    public MilvusServiceClient milvusServiceClient(MilvusProperties milvusProperties) {
        MilvusServiceClient milvusServiceClient = new MilvusServiceClient(ConnectParam.newBuilder()
                .withUri(milvusProperties.getUrl())
                .withAuthorization(milvusProperties.getUsername(), milvusProperties.getPassword())
                .withDatabaseName(milvusProperties.getDatabaseName())
                .build());
        if (!milvusServiceClient.hasCollection(HasCollectionParam.newBuilder()
                .withDatabaseName(milvusProperties.getDatabaseName())
                .withCollectionName(milvusProperties.getCollectionName())
                .build()).getData()) {
            milvusServiceClient.createCollection(CreateCollectionParam.newBuilder()
                    .withDatabaseName(milvusProperties.getDatabaseName())
                    .withCollectionName(milvusProperties.getCollectionName())
                    .withSchema(CollectionSchemaParam.newBuilder()
                            .withFieldTypes(new ArrayList<>() {
                                {
                                    add(FieldType.newBuilder()
                                            .withName("doc_id")
                                            .withDataType(DataType.VarChar)
                                            .withMaxLength(100)
                                            .withPrimaryKey(true)
                                            .withAutoID(false)
                                            .build());
                                    add(FieldType.newBuilder()
                                            .withName("content")
                                            .withDataType(DataType.VarChar)
                                            .withMaxLength(3000)
                                            .withPrimaryKey(false)
                                            .withAutoID(false)
                                            .build());
                                    add(FieldType.newBuilder()
                                            .withName("embedding")
                                            .withDataType(DataType.FloatVector)
                                            .withDimension(milvusProperties.getEmbeddingDimension())
                                            .withPrimaryKey(false)
                                            .withAutoID(false)
                                            .build());
                                    add(FieldType.newBuilder()
                                            .withName("metadata")
                                            .withDataType(DataType.JSON)
                                            .withPrimaryKey(false)
                                            .withAutoID(false)
                                            .build());
                                }
                            })
                            .build())
                    .build());
            milvusServiceClient.createIndex(CreateIndexParam.newBuilder()
                    .withDatabaseName(milvusProperties.getDatabaseName())
                    .withCollectionName(milvusProperties.getCollectionName())
                    .withFieldName("doc_id")
                    .withIndexType(IndexType.AUTOINDEX)
                    .withSyncMode(true)
                    .build());
            milvusServiceClient.createIndex(CreateIndexParam.newBuilder()
                    .withDatabaseName(milvusProperties.getDatabaseName())
                    .withCollectionName(milvusProperties.getCollectionName())
                    .withFieldName("embedding")
                    .withIndexType(IndexType.AUTOINDEX)
                    .withMetricType(MetricType.COSINE)
                    .withSyncMode(true)
                    .build());
        }
        milvusServiceClient.loadCollection(LoadCollectionParam.newBuilder()
                .withDatabaseName(milvusProperties.getDatabaseName())
                .withCollectionName(milvusProperties.getCollectionName())
                .build());
        return milvusServiceClient;
    }

}
