package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding;

import org.springframework.ai.embedding.Embedding;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingResult;

import java.util.List;

/**
 * AI Embedding服务接口
 *
 * @author AntonyCheng
 */
public interface AiEmbeddingService {

    /**
     * AI Embedding功能，返回向量数组
     *
     * @param model     embedding模型信息
     * @param text      待向量化文本
     */
    float[] embedToArray(EmbeddingModelBase model, String text);

    /**
     * AI Embedding功能，返回向量数组的列表
     *
     * @param model     embedding模型信息
     * @param text      待向量化文本数组
     */
    List<float[]> embedToArrayList(EmbeddingModelBase model, String... text);

    /**
     * AI Embedding功能，返回向量数组的列表
     *
     * @param model     embedding模型信息
     * @param text      待向量化文本列表
     */
    List<float[]> embedToArrayList(EmbeddingModelBase model, List<String> text);

    /**
     * AI Embedding功能，返回向量对象的列表
     *
     * @param model     embedding模型信息
     * @param text      待向量化文本数组
     */
    List<Embedding> embedToEmbeddingList(EmbeddingModelBase model, String... text);

    /**
     * AI Embedding功能，返回向量对象的列表
     *
     * @param model     embedding模型信息
     * @param text      待向量化文本列表
     */
    List<Embedding> embedToEmbeddingList(EmbeddingModelBase model, List<String> text);

    /**
     * AI Embedding功能，返回向量结果的列表
     *
     * @param model embedding模型信息
     * @param text  待向量化文本数组
     */
    EmbeddingResult embedToResult(EmbeddingModelBase model, String... text);

    /**
     * AI Embedding功能，返回向量结果的列表
     *
     * @param model embedding模型信息
     * @param text  待向量化文本列表
     */
    EmbeddingResult embedToResult(EmbeddingModelBase model, List<String> text);

}
