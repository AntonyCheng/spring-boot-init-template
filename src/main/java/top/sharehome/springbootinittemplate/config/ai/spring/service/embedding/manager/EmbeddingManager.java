package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.manager;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.util.HttpClientOptions;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
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
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity.*;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * AI Embedding管理器
 *
 * @author AntonyCheng
 */
public class EmbeddingManager {

    /**
     * 获得向量响应对象
     */
    public static EmbeddingResponse getEmbeddingResponse(EmbeddingModelBase model, List<String> text) {
        return getEmbeddingModel(model).embedForResponse(text);
    }

    /**
     * 获得向量模型对象
     */
    public static EmbeddingModel getEmbeddingModel(EmbeddingModelBase model) {
        if (model instanceof OpenAiEmbeddingEntity entity) {
            return getOpenAiEmbeddingModel(entity);
        } else if (model instanceof OllamaEmbeddingEntity entity) {
            return getOllamaEmbeddingModel(entity);
        } else if (model instanceof ZhiPuAiEmbeddingEntity entity) {
            return getZhiPuAiEmbeddingModel(entity);
        } else if (model instanceof MistralAiEmbeddingEntity entity) {
            return getMistralAiEmbeddingModel(entity);
        } else if (model instanceof MiniMaxEmbeddingEntity entity) {
            return getMiniMaxEmbeddingModel(entity);
        } else if (model instanceof AzureOpenAiEmbeddingEntity entity) {
            return getAzureOpenAiEmbeddingModel(entity);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiEmbeddingModel
     */
    private static OpenAiEmbeddingModel getOpenAiEmbeddingModel(OpenAiEmbeddingEntity entity) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .restClientBuilder(getRestClient(entity))
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
    private static OllamaEmbeddingModel getOllamaEmbeddingModel(OllamaEmbeddingEntity entity) {
        OllamaApi ollamaApi = OllamaApi.builder()
                .restClientBuilder(getRestClient(entity))
                .baseUrl(entity.getBaseUrl())
                .build();
        return new OllamaEmbeddingModel(ollamaApi, OllamaOptions.builder()
                .model(entity.getModel())
                .build(), ObservationRegistry.NOOP, ModelManagementOptions.defaults());
    }

    /**
     * 获取ZhiPuAiEmbeddingModel
     */
    private static ZhiPuAiEmbeddingModel getZhiPuAiEmbeddingModel(ZhiPuAiEmbeddingEntity entity) {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(entity.getApiKey());
        return new ZhiPuAiEmbeddingModel(zhiPuAiApi, MetadataMode.EMBED, ZhiPuAiEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取MistralAiEmbeddingModel
     */
    private static MistralAiEmbeddingModel getMistralAiEmbeddingModel(MistralAiEmbeddingEntity entity) {
        MistralAiApi mistralAiApi = new MistralAiApi(entity.getApiKey());
        return new MistralAiEmbeddingModel(mistralAiApi, MetadataMode.EMBED, MistralAiEmbeddingOptions.builder()
                .withEncodingFormat("float")
                .withModel(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取MiniMaxEmbeddingModel
     */
    private static MiniMaxEmbeddingModel getMiniMaxEmbeddingModel(MiniMaxEmbeddingEntity entity) {
        MiniMaxApi miniMaxApi = new MiniMaxApi(entity.getApiKey());
        return new MiniMaxEmbeddingModel(miniMaxApi, MetadataMode.EMBED, MiniMaxEmbeddingOptions.builder()
                .model(entity.getModel())
                .build(), RetryTemplate.defaultInstance(), ObservationRegistry.NOOP);
    }

    /**
     * 获取AzureOpenAiEmbeddingModel
     */
    private static AzureOpenAiEmbeddingModel getAzureOpenAiEmbeddingModel(AzureOpenAiEmbeddingEntity entity) {
        OpenAIClientBuilder clientBuilder = new OpenAIClientBuilder()
                .httpClient(HttpClient.createDefault(
                        new HttpClientOptions().setReadTimeout(Duration.ofMillis(entity.getReadTimeout()))
                ))
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .serviceVersion(entity.getModelVersion());
        return new AzureOpenAiEmbeddingModel(clientBuilder.buildClient(), MetadataMode.EMBED, AzureOpenAiEmbeddingOptions.builder()
                .deploymentName(entity.getModel())
                .build(), ObservationRegistry.NOOP);
    }

    /**
     * 获取请求构造类
     */
    private static RestClient.Builder getRestClient(EmbeddingModelBase model) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (Objects.nonNull(model.getReadTimeout())) {
            requestFactory.setReadTimeout(Duration.ofMillis(model.getReadTimeout()));
        }
        return RestClient.builder().requestFactory(requestFactory);
    }

}
