package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl;

import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.ai.moonshot.MoonshotChatOptions;
import org.springframework.ai.moonshot.api.MoonshotApi;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.qianfan.QianFanChatOptions;
import org.springframework.ai.qianfan.api.QianFanApi;
import org.springframework.ai.tool.execution.DefaultToolCallExceptionConverter;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.AiChatService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.*;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * AI Chat服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    @Override
    public String chatString(ChatModelBase model, String prompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).call(prompt);
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).call(prompt);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).call(prompt);
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).call(prompt);
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).call(prompt);
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).call(prompt);
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).call(prompt);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, String prompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt).toStream();
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public String chatString(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).call(prompt);
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).call(prompt);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).call(prompt);
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).call(prompt);
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).call(prompt);
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).call(prompt);
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).call(prompt);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt).toStream();
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public String chatString(ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof OpenAiChatEntity entity) {
            Generation generation = this.getOpenAiChatModel(entity).call(prompt).getResult();
            return Objects.nonNull(generation) ? generation.getOutput().getText() : "";
        } else if (model instanceof OllamaChatEntity entity) {
            Generation generation = this.getOllamaChatModel(entity).call(prompt).getResult();
            return Objects.nonNull(generation) ? generation.getOutput().getText() : "";
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            Generation generation = this.getZhiPuAiChatModel(entity).call(prompt).getResult();
            return Objects.nonNull(generation) ? generation.getOutput().getText() : "";
        } else if (model instanceof MoonshotChatEntity entity) {
            Generation generation = this.getMoonshotChatModel(entity).call(prompt).getResult();
            return Objects.nonNull(generation) ? generation.getOutput().getText() : "";
        } else if (model instanceof MistralAiChatEntity entity) {
            Generation generation = this.getMistralAiChatModel(entity).call(prompt).getResult();
            return Objects.nonNull(generation) ? generation.getOutput().getText() : "";
        } else if (model instanceof QianFanChatEntity entity) {
            Generation generation = this.getQianFanChatModel(entity).call(prompt).getResult();
            return Objects.nonNull(generation) ? generation.getOutput().getText() : "";
        } else if (model instanceof MiniMaxChatEntity entity) {
            Generation generation = this.getMiniMaxChatModel(entity).call(prompt).getResult();
            return Objects.nonNull(generation) ? generation.getOutput().getText() : "";
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiChatModel
     */
    private OpenAiChatModel getOpenAiChatModel(OpenAiChatEntity entity) {
        OpenAiApi openAiApi = new OpenAiApi(entity.getBaseUrl(), entity.getApiKey());
        return new OpenAiChatModel(openAiApi, OpenAiChatOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTemperature())
                .build());
    }

    /**
     * 获取OllamaChatModel
     */
    private OllamaChatModel getOllamaChatModel(OllamaChatEntity entity) {
        OllamaApi ollamaApi = new OllamaApi(entity.getBaseUrl());
        return new OllamaChatModel(ollamaApi, OllamaOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build()
                , new DefaultToolCallingManager(ObservationRegistry.NOOP,
                new DelegatingToolCallbackResolver(List.of()),
                new DefaultToolCallExceptionConverter(true)), ObservationRegistry.NOOP, ModelManagementOptions.builder().build());
    }

    /**
     * 获取ZhiPuAiChatModel
     */
    private ZhiPuAiChatModel getZhiPuAiChatModel(ZhiPuAiChatEntity entity) {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(entity.getApiKey());
        return new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取MoonshotChatModel
     */
    private MoonshotChatModel getMoonshotChatModel(MoonshotChatEntity entity) {
        MoonshotApi moonshotApi = new MoonshotApi(entity.getApiKey());
        return new MoonshotChatModel(moonshotApi, MoonshotChatOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取MistralAiChatModel
     */
    private MistralAiChatModel getMistralAiChatModel(MistralAiChatEntity entity) {
        MistralAiApi mistralAiApi = new MistralAiApi(entity.getApiKey());
        return new MistralAiChatModel(mistralAiApi, MistralAiChatOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取QianFanChatModel
     */
    private QianFanChatModel getQianFanChatModel(QianFanChatEntity entity) {
        QianFanApi qianFanApi = new QianFanApi(entity.getApiKey(), entity.getSecretKey());
        return new QianFanChatModel(qianFanApi, QianFanChatOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取MiniMaxChatModel
     */
    private MiniMaxChatModel getMiniMaxChatModel(MiniMaxChatEntity entity) {
        MiniMaxApi miniMaxApi = new MiniMaxApi(entity.getApiKey());
        return new MiniMaxChatModel(miniMaxApi, MiniMaxChatOptions
                .builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

}
