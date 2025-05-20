package top.sharehome.springbootinittemplate.config.ai.spring.etl.transformer;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.manager.ChatManager;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.List;
import java.util.Objects;

/**
 * 文档转换器
 *
 * @author AntonyCheng
 */
public class DocumentTransformer {

    /**
     * 文本Token分割转换
     *
     * @param documents             目标文本
     * @param defaultChunkSize      每个文本块的目标大小（以令牌为单位），默认为800
     * @param minChunkSizeChars     每个文本块的最小大小（以字符为单位），默认为350
     * @param minChunkLengthToEmbed 嵌入的最小块长度，默认为5
     * @param maxNumChunks          从文本中生成的最大块数，默认为10000
     * @param keepSeparator         是否在块中保留分隔符（如换行符），默认为true
     */
    public static List<Document> transformTokenText(List<Document> documents, Integer defaultChunkSize, Integer minChunkSizeChars, Integer minChunkLengthToEmbed, Integer maxNumChunks, Boolean keepSeparator) {
        if (CollectionUtils.isEmpty(documents)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[documents]不能为空");
        }
        return new TokenTextSplitter(
                Objects.isNull(defaultChunkSize) ? 800 : defaultChunkSize,
                Objects.isNull(minChunkSizeChars) ? 350 : minChunkSizeChars,
                Objects.isNull(minChunkLengthToEmbed) ? 5 : minChunkLengthToEmbed,
                Objects.isNull(maxNumChunks) ? 10000 : maxNumChunks,
                Objects.isNull(keepSeparator) || keepSeparator
        ).apply(documents);
    }

    /**
     * 文本关键词提取增强
     *
     * @param documents     目标文本
     * @param chatModel     Chat模型
     * @param keywordCount  关键词数量
     */
    public static List<Document> enrichKeywords(List<Document> documents, ChatModelBase chatModel, Integer keywordCount) {
        if (CollectionUtils.isEmpty(documents)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[documents]不能为空");
        }
        if (Objects.isNull(chatModel)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[chatModel]不能为空");
        }
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(ChatManager.getChatModel(chatModel), Objects.isNull(keywordCount) || keywordCount <= 0 ? 1 : keywordCount);
        return enricher.apply(documents);
    }

    /**
     * 文本摘要提取增强
     *
     * @param documents     目标文本
     * @param chatModel     Chat模型
     */
    public static List<Document> enrichSummary(List<Document> documents, ChatModelBase chatModel) {
        if (CollectionUtils.isEmpty(documents)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[documents]不能为空");
        }
        if (Objects.isNull(chatModel)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[chatModel]不能为空");
        }
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(ChatManager.getChatModel(chatModel), List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.NEXT));
        return enricher.apply(documents);
    }

}
