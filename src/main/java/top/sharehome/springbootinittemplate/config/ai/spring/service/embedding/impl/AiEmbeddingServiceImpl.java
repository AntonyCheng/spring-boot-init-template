package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.embedding.Embedding;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.AiEmbeddingService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.manager.EmbeddingManager;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingResult;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.List;

/**
 * AI Embedding服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
@Slf4j
public class AiEmbeddingServiceImpl implements AiEmbeddingService {

    @Override
    public float[] embedToArray(EmbeddingModelBase model, String text) {
        if (StringUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return EmbeddingManager.getEmbeddingResponse(model, List.of(text)).getResult().getOutput();
    }

    @Override
    public List<float[]> embedToArrayList(EmbeddingModelBase model, String... text) {
        if (ArrayUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return EmbeddingManager.getEmbeddingResponse(model, List.of(text)).getResults().stream().map(Embedding::getOutput).toList();
    }

    @Override
    public List<float[]> embedToArrayList(EmbeddingModelBase model, List<String> text) {
        if (CollectionUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return EmbeddingManager.getEmbeddingResponse(model, text).getResults().stream().map(Embedding::getOutput).toList();
    }

    @Override
    public List<Embedding> embedToEmbeddingList(EmbeddingModelBase model, String... text) {
        if (ArrayUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return EmbeddingManager.getEmbeddingResponse(model, List.of(text)).getResults();
    }

    @Override
    public List<Embedding> embedToEmbeddingList(EmbeddingModelBase model, List<String> text) {
        if (CollectionUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return EmbeddingManager.getEmbeddingResponse(model, text).getResults();
    }

    @Override
    public EmbeddingResult embedToResult(EmbeddingModelBase model, String... text) {
        if (ArrayUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        List<String> textList = List.of(text);
        return EmbeddingResult.buildResult(EmbeddingManager.getEmbeddingResponse(model, textList), textList);
    }

    @Override
    public EmbeddingResult embedToResult(EmbeddingModelBase model, List<String> text) {
        if (CollectionUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return EmbeddingResult.buildResult(EmbeddingManager.getEmbeddingResponse(model, text), text);
    }

}
