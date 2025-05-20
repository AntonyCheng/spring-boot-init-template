package top.sharehome.springbootinittemplate.config.ai.spring.vector.milvus.impl;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.impl.AiEmbeddingServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.manager.EmbeddingManager;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.vector.milvus.MilvusVectorService;
import top.sharehome.springbootinittemplate.config.milvus.condition.MilvusCondition;
import top.sharehome.springbootinittemplate.config.milvus.properties.MilvusProperties;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.List;
import java.util.Objects;

/**
 * Milvus向量服务实现类
 *
 * @author AntonyCheng
 */
@Service
@EnableConfigurationProperties(MilvusProperties.class)
@Conditional({MilvusCondition.class})
@Slf4j
public class MilvusVectorServiceImpl implements MilvusVectorService {

    @Resource
    private MilvusServiceClient milvusServiceClient;

    @Resource
    private MilvusProperties milvusProperties;

    @Resource
    private AiEmbeddingServiceImpl aiEmbeddingService;

    @Override
    public List<Document> addTextEmbedding(EmbeddingModelBase model, List<String> text) {
        return this.addDocumentsEmbedding(model, text.stream().map(Document::new).toList());
    }

    @Override
    public List<Document> addDocumentsEmbedding(EmbeddingModelBase model, List<Document> text) {
        if (CollectionUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        this.buildVectorStore(model).doAdd(text);
        return text;
    }

    @Override
    public List<Document> searchEmbedding(EmbeddingModelBase model, String queryText) {
        return this.searchEmbedding(model, queryText, null, null);
    }

    @Override
    public List<Document> searchEmbedding(EmbeddingModelBase model, String queryText, Integer topNum) {
        return this.searchEmbedding(model, queryText, null, topNum);
    }

    @Override
    public List<Document> searchEmbedding(EmbeddingModelBase model, String queryText, Double similarityThreshold) {
        return this.searchEmbedding(model, queryText, similarityThreshold, null);
    }

    @Override
    public List<Document> searchEmbedding(EmbeddingModelBase model, String queryText, Double similarityThreshold, Integer topNum) {
        if (StringUtils.isBlank(queryText)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[queryText]不能为空");
        }
        return this.buildVectorStore(model).doSimilaritySearch(SearchRequest.builder()
                .query(queryText)
                .similarityThreshold(Objects.isNull(similarityThreshold) || similarityThreshold < 0 || similarityThreshold > 1 ? 0.0 : similarityThreshold)
                .topK(Objects.isNull(topNum) || topNum < 0 ? 3 : topNum)
                .build());
    }

    @Override
    public void deleteEmbedding(EmbeddingModelBase model, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[ids]不能为空");
        }
        this.buildVectorStore(model).doDelete(ids);
    }

    /**
     * 构建向量操作对象
     */
    private MilvusVectorStore buildVectorStore(EmbeddingModelBase model) {
        return MilvusVectorStore.builder(milvusServiceClient, EmbeddingManager.getEmbeddingModel(model))
                .collectionName(milvusProperties.getCollectionName())
                .databaseName(milvusProperties.getDatabaseName())
                .embeddingDimension(milvusProperties.getEmbeddingDimension())
                .indexType(IndexType.AUTOINDEX)
                .metricType(MetricType.COSINE)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }

}
