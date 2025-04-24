package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.impl;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.minimax.MiniMaxEmbeddingModel;
import org.springframework.ai.minimax.MiniMaxEmbeddingOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.qianfan.QianFanEmbeddingModel;
import org.springframework.ai.qianfan.QianFanEmbeddingOptions;
import org.springframework.ai.qianfan.api.QianFanApi;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.AiEmbeddingService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity.*;
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
        return this.getEmbeddingResponse(model, List.of(text)).getResult().getOutput();
    }

    @Override
    public List<float[]> embedToArrayList(EmbeddingModelBase model, String... text) {
        if (ArrayUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return this.getEmbeddingResponse(model, List.of(text)).getResults().stream().map(Embedding::getOutput).toList();
    }

    @Override
    public List<float[]> embedToArrayList(EmbeddingModelBase model, List<String> text) {
        if (CollectionUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return this.getEmbeddingResponse(model, text).getResults().stream().map(Embedding::getOutput).toList();
    }

    @Override
    public List<Embedding> embedToEmbeddingList(EmbeddingModelBase model, String... text) {
        if (ArrayUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return this.getEmbeddingResponse(model, List.of(text)).getResults();
    }

    @Override
    public List<Embedding> embedToEmbeddingList(EmbeddingModelBase model, List<String> text) {
        if (CollectionUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return this.getEmbeddingResponse(model, text).getResults();
    }

    @Override
    public EmbeddingResult embedToResult(EmbeddingModelBase model, String... text) {
        if (ArrayUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        List<String> textList = List.of(text);
        return EmbeddingResult.buildResult(this.getEmbeddingResponse(model, textList), textList);
    }

    @Override
    public EmbeddingResult embedToResult(EmbeddingModelBase model, List<String> text) {
        if (CollectionUtils.isEmpty(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        return EmbeddingResult.buildResult(this.getEmbeddingResponse(model, text), text);
    }

    /**
     * 获得向量响应对象
     */
    private EmbeddingResponse getEmbeddingResponse(EmbeddingModelBase model, List<String> text) {
        if (model instanceof OpenAiEmbeddingEntity entity) {
            return this.getOpenAiEmbeddingModel(entity).embedForResponse(text);
        } else if (model instanceof OllamaEmbeddingEntity entity) {
            return this.getOllamaEmbeddingModel(entity).embedForResponse(text);
        } else if (model instanceof ZhiPuAiEmbeddingEntity entity) {
            return this.getZhiPuAiEmbeddingModel(entity).embedForResponse(text);
        } else if (model instanceof MistralAiEmbeddingEntity entity) {
            return this.getMistralAiEmbeddingModel(entity).embedForResponse(text);
        } else if (model instanceof QianFanEmbeddingEntity entity) {
            return this.getQianFanEmbeddingModel(entity).embedForResponse(text);
        } else if (model instanceof MiniMaxEmbeddingEntity entity) {
            return this.getMiniMaxEmbeddingModel(entity).embedForResponse(text);
        } else if (model instanceof AzureOpenAiEmbeddingEntity entity) {
            return this.getAzureOpenAiEmbeddingModel(entity).embedForResponse(text);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiEmbeddingModel
     */
    private OpenAiEmbeddingModel getOpenAiEmbeddingModel(OpenAiEmbeddingEntity entity) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .build();
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, OpenAiEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取OllamaEmbeddingModel
     */
    private OllamaEmbeddingModel getOllamaEmbeddingModel(OllamaEmbeddingEntity entity) {
        OllamaApi ollamaApi = new OllamaApi(entity.getBaseUrl());
        return new OllamaEmbeddingModel(ollamaApi, OllamaOptions.builder()
                .model(entity.getModel())
                .build(), ObservationRegistry.NOOP, ModelManagementOptions.defaults());
    }

    /**
     * 获取ZhiPuAiEmbeddingModel
     */
    private ZhiPuAiEmbeddingModel getZhiPuAiEmbeddingModel(ZhiPuAiEmbeddingEntity entity) {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(entity.getApiKey());
        return new ZhiPuAiEmbeddingModel(zhiPuAiApi, MetadataMode.EMBED, ZhiPuAiEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取MistralAiEmbeddingModel
     */
    private MistralAiEmbeddingModel getMistralAiEmbeddingModel(MistralAiEmbeddingEntity entity) {
        MistralAiApi mistralAiApi = new MistralAiApi(entity.getApiKey());
        return new MistralAiEmbeddingModel(mistralAiApi, MetadataMode.EMBED, MistralAiEmbeddingOptions.builder()
                .withEncodingFormat("float")
                .withModel(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取QianFanEmbeddingModel
     */
    private QianFanEmbeddingModel getQianFanEmbeddingModel(QianFanEmbeddingEntity entity) {
        QianFanApi qianFanApi = new QianFanApi(entity.getApiKey(), entity.getSecretKey());
        return new QianFanEmbeddingModel(qianFanApi, MetadataMode.EMBED, QianFanEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取MiniMaxEmbeddingModel
     */
    private MiniMaxEmbeddingModel getMiniMaxEmbeddingModel(MiniMaxEmbeddingEntity entity) {
        MiniMaxApi miniMaxApi = new MiniMaxApi(entity.getApiKey());
        return new MiniMaxEmbeddingModel(miniMaxApi, MetadataMode.EMBED, MiniMaxEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取AzureOpenAiEmbeddingModel
     */
    private AzureOpenAiEmbeddingModel getAzureOpenAiEmbeddingModel(AzureOpenAiEmbeddingEntity entity) {
        OpenAIClientBuilder clientBuilder = new OpenAIClientBuilder().credential(new AzureKeyCredential(entity.getApiKey())).endpoint(entity.getEndpoint()).serviceVersion(entity.getModelVersion());
        return new AzureOpenAiEmbeddingModel(clientBuilder.buildClient(), MetadataMode.EMBED, AzureOpenAiEmbeddingOptions.builder()
                .deploymentName(entity.getModel())
                .build(), ObservationRegistry.NOOP);
    }

}
