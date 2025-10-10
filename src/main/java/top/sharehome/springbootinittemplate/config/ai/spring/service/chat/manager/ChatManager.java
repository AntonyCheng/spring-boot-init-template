package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.manager;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.util.HttpClientOptions;
import com.knuddels.jtokkit.api.EncodingType;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.*;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * AI Chat管理器
 *
 * @author AntonyCheng
 */
public class ChatManager {

    /**
     * 根据Model类型获取ChatClient
     */
    public static ChatClient getChatClient(ChatModelBase model) {
        if (model instanceof DeepSeekChatEntity entity) {
            return getDeepSeekClient(entity);
        } else if (model instanceof OpenAiChatEntity entity) {
            return getOpenAiClient(entity);
        } else if (model instanceof OllamaChatEntity entity) {
            return getOllamaClient(entity);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return getZhiPuAiClient(entity);
        } else if (model instanceof MistralAiChatEntity entity) {
            return getMistralAiClient(entity);
        } else if (model instanceof MiniMaxChatEntity entity) {
            return getMiniMaxClient(entity);
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return getAzureOpenAiClient(entity);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 根据Model类型获取ChatModel
     */
    public static ChatModel getChatModel(ChatModelBase model) {
        if (model instanceof DeepSeekChatEntity entity) {
            return getDeepSeekModel(entity);
        } else if (model instanceof OpenAiChatEntity entity) {
            return getOpenAiModel(entity);
        } else if (model instanceof OllamaChatEntity entity) {
            return getOllamaModel(entity);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return getZhiPuAiModel(entity);
        } else if (model instanceof MistralAiChatEntity entity) {
            return getMistralAiModel(entity);
        } else if (model instanceof MiniMaxChatEntity entity) {
            return getMiniMaxModel(entity);
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return getAzureOpenAiModel(entity);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 根据Model类型获取Token编码类型
     * DeepSeek ==> O200K_BASE
     * OpenAi/AzureOpenAi ==> CL100K_BASE
     * 其他 ==> P50K_BASE
     */
    public static EncodingType getEncodingType(ChatModelBase model) {
        return model instanceof DeepSeekChatEntity ? EncodingType.O200K_BASE :
                model instanceof OpenAiChatEntity || model instanceof AzureOpenAiChatEntity ? EncodingType.CL100K_BASE : EncodingType.P50K_BASE;
    }

    /**
     * 获取DeepSeek ChatClient
     */
    private static ChatClient getDeepSeekClient(DeepSeekChatEntity entity) {
        return ChatClient.builder(getDeepSeekModel(entity)).build();
    }

    /**
     * 获取DeepSeek ChatModel
     */
    private static ChatModel getDeepSeekModel(DeepSeekChatEntity entity) {
        return DeepSeekChatModel.builder()
                .deepSeekApi(
                        DeepSeekApi.builder()
                                .restClientBuilder(getRestClient(entity))
                                .baseUrl(entity.getBaseUrl())
                                .apiKey(new SimpleApiKey(entity.getApiKey()))
                                .build()
                )
                .defaultOptions(
                        DeepSeekChatOptions.builder()
                                .model(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTopP())
                                .maxTokens(entity.getMaxTokens())
                                .build()
                ).build();
    }

    /**
     * 获取OpenAi ChatClient
     */
    private static ChatClient getOpenAiClient(OpenAiChatEntity entity) {
        return ChatClient.builder(getOpenAiModel(entity)).build();
    }

    /**
     * 获取OpenAi ChatClient
     */
    private static ChatModel getOpenAiModel(OpenAiChatEntity entity) {
        return OpenAiChatModel.builder()
                .openAiApi(
                        OpenAiApi.builder()
                                .restClientBuilder(getRestClient(entity))
                                .baseUrl(entity.getBaseUrl())
                                .apiKey(new SimpleApiKey(entity.getApiKey()))
                                .build())
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTopP())
                                .maxTokens(entity.getMaxTokens())
                                .build()
                ).build();
    }

    /**
     * 获取Ollama ChatClient
     */
    private static ChatClient getOllamaClient(OllamaChatEntity entity) {
        return ChatClient.builder(getOllamaModel(entity)).build();
    }

    /**
     * 获取Ollama ChatClient
     */
    private static ChatModel getOllamaModel(OllamaChatEntity entity) {
        return OllamaChatModel.builder()
                .ollamaApi(
                        OllamaApi.builder()
                                .restClientBuilder(getRestClient(entity))
                                .baseUrl(entity.getBaseUrl())
                                .build()
                )
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTopP())
                                .build()
                )
                .toolCallingManager(
                        DefaultToolCallingManager.builder()
                                .observationRegistry(ObservationRegistry.NOOP)
                                .toolCallbackResolver(new DelegatingToolCallbackResolver(List.of()))
                                .toolExecutionExceptionProcessor(
                                        DefaultToolExecutionExceptionProcessor.builder()
                                                .alwaysThrow(true)
                                                .build()
                                ).build()
                )
                .observationRegistry(
                        ObservationRegistry.NOOP
                )
                .build();
    }

    /**
     * 获取ZhiPuAi ChatClient
     */
    private static ChatClient getZhiPuAiClient(ZhiPuAiChatEntity entity) {
        return ChatClient.builder(getZhiPuAiModel(entity)).build();
    }

    /**
     * 获取ZhiPuAi ChatModel
     */
    private static ChatModel getZhiPuAiModel(ZhiPuAiChatEntity entity) {
        return new ZhiPuAiChatModel(
                new ZhiPuAiApi(entity.getApiKey()),
                ZhiPuAiChatOptions.builder()
                        .model(entity.getModel())
                        .temperature(entity.getTemperature())
                        .topP(entity.getTopP())
                        .maxTokens(entity.getMaxTokens())
                        .build()
        );
    }

    /**
     * 获取MistralAi ChatClient
     */
    private static ChatClient getMistralAiClient(MistralAiChatEntity entity) {
        return ChatClient.builder(getMistralAiModel(entity)).build();
    }

    /**
     * 获取MistralAi ChatModel
     */
    private static ChatModel getMistralAiModel(MistralAiChatEntity entity) {
        return MistralAiChatModel.builder()
                .mistralAiApi(new MistralAiApi(entity.getApiKey()))
                .defaultOptions(
                        MistralAiChatOptions.builder()
                                .model(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTopP())
                                .maxTokens(entity.getMaxTokens())
                                .build()
                )
                .build();
    }

    /**
     * 获取MiniMax ChatClient
     */
    private static ChatClient getMiniMaxClient(MiniMaxChatEntity entity) {
        return ChatClient.builder(getMiniMaxModel(entity)).build();
    }

    /**
     * 获取MiniMax ChatModel
     */
    private static ChatModel getMiniMaxModel(MiniMaxChatEntity entity) {
        return new MiniMaxChatModel(
                new MiniMaxApi(entity.getApiKey()),
                MiniMaxChatOptions.builder()
                        .model(entity.getModel())
                        .temperature(entity.getTemperature())
                        .topP(entity.getTopP())
                        .maxTokens(entity.getMaxTokens())
                        .build()
        );
    }

    /**
     * 获取AzureOpenAi ChatClient
     */
    private static ChatClient getAzureOpenAiClient(AzureOpenAiChatEntity entity) {
        return ChatClient.builder(getAzureOpenAiModel(entity)).build();
    }

    /**
     * 获取AzureOpenAi ChatModel
     */
    private static ChatModel getAzureOpenAiModel(AzureOpenAiChatEntity entity) {
        return AzureOpenAiChatModel.builder()
                .openAIClientBuilder(
                        new OpenAIClientBuilder()
                                .httpClient(HttpClient.createDefault(
                                        new HttpClientOptions().setReadTimeout(Duration.ofMillis(entity.getReadTimeout()))
                                ))
                                .credential(new AzureKeyCredential(entity.getApiKey()))
                                .endpoint(entity.getEndpoint())
                                .serviceVersion(entity.getModelVersion())
                )
                .defaultOptions(
                        AzureOpenAiChatOptions.builder()
                                .deploymentName(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTopP())
                                .maxTokens(entity.getMaxTokens())
                                .build()
                )
                .toolCallingManager(DefaultToolCallingManager.builder().build())
                .observationRegistry(ObservationRegistry.NOOP)
                .build();
    }

    /**
     * 获取请求构造类
     */
    private static RestClient.Builder getRestClient(ChatModelBase model) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (Objects.nonNull(model.getReadTimeout())) {
            requestFactory.setReadTimeout(Duration.ofMillis(model.getReadTimeout()));
        }
        return RestClient.builder().requestFactory(requestFactory);
    }

}
