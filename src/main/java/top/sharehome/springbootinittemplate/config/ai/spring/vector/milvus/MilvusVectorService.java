package top.sharehome.springbootinittemplate.config.ai.spring.vector.milvus;

import org.springframework.ai.document.Document;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;

import java.util.List;

/**
 * Milvus向量服务
 *
 * @author AntonyCheng
 */
public interface MilvusVectorService {

    /**
     * 添加嵌入向量数据
     *
     * @param model    向量模型
     * @param text     向量文本
     */
    List<Document> addTextEmbedding(EmbeddingModelBase model, List<String> text);

    /**
     * 添加嵌入向量数据
     *
     * @param model    向量模型
     * @param text     向量文本
     */
    List<Document> addDocumentsEmbedding(EmbeddingModelBase model, List<Document> text);

    /**
     * 相似度搜索向量数据
     *
     * @param model         向量模型
     * @param queryText     向量文本
     */
    List<Document> searchEmbedding(EmbeddingModelBase model, String queryText);

    /**
     * 相似度搜索向量数据
     *
     * @param model        向量模型
     * @param queryText    查询文本
     * @param topNum       查询结果最大值，默认3
     */
    List<Document> searchEmbedding(EmbeddingModelBase model, String queryText, Integer topNum);

    /**
     * 相似度搜索向量数据
     *
     * @param model                 向量模型
     * @param queryText             查询文本
     * @param similarityThreshold   相似度阈值，默认0.0，即无阈值
     */
    List<Document> searchEmbedding(EmbeddingModelBase model, String queryText, Double similarityThreshold);

    /**
     * 相似度搜索向量数据
     *
     * @param model                 向量模型
     * @param queryText             查询文本
     * @param similarityThreshold   相似度阈值，默认0.0，即无阈值
     * @param topNum                查询结果最大值，默认3
     */
    List<Document> searchEmbedding(EmbeddingModelBase model, String queryText, Double similarityThreshold, Integer topNum);

    /**
     * 删除嵌入向量数据
     *
     * @param model     向量模型
     * @param ids       向量数据ID列表
     */
    void deleteEmbedding(EmbeddingModelBase model,List<String> ids);

}
