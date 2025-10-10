package top.sharehome.springbootinittemplate.service.impl;

import com.azure.ai.openai.OpenAIServiceVersion;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.*;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl.AiChatServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.*;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.impl.AiEmbeddingServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.entity.*;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.impl.AiImageServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.AzureOpenAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.OpenAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.StabilityAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.ZhiPuAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.AzureOpenAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.OpenAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.StabilityAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.ZhiPuAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.impl.AiTranscriptionServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.AzureOpenAiTranscriptionType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.OpenAiTranscriptionType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.impl.AiTtsServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.enums.OpenAiTtsType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.ModelMapper;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.dto.model.ModelAddOrUpdateDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelPageDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelUpdateStateDto;
import top.sharehome.springbootinittemplate.model.entity.Model;
import top.sharehome.springbootinittemplate.model.vo.model.ModelExportVo;
import top.sharehome.springbootinittemplate.model.vo.model.ModelPageVo;
import top.sharehome.springbootinittemplate.service.ModelService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 模型服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private AiChatServiceImpl chatService;

    @Resource
    private AiEmbeddingServiceImpl embeddingService;

    @Resource
    private AiImageServiceImpl imageService;

    @Resource
    private AiTranscriptionServiceImpl transcriptionService;

    @Resource
    private AiTtsServiceImpl ttsService;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<ModelPageVo> pageModel(ModelPageDto modelPageDto, PageModel pageModel) {
        Page<Model> page = pageModel.build();
        Page<ModelPageVo> res = pageModel.build();

        LambdaQueryWrapper<Model> modelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构造查询条件
        modelLambdaQueryWrapper
                .eq(StringUtils.isNotBlank(modelPageDto.getType()), Model::getType, modelPageDto.getType())
                .eq(StringUtils.isNotBlank(modelPageDto.getService()), Model::getService, modelPageDto.getService())
                .eq(Objects.nonNull(modelPageDto.getState()), Model::getState, modelPageDto.getState())
                .like(StringUtils.isNotBlank(modelPageDto.getName()), Model::getName, modelPageDto.getName())
                .like(StringUtils.isNotBlank(modelPageDto.getBaseUrl()), Model::getBaseUrl, modelPageDto.getBaseUrl());
        // 构造查询排序（默认按照创建时间升序排序）
        modelLambdaQueryWrapper.orderByAsc(Model::getCreateTime);

        // 分页查询
        modelMapper.selectPage(page, modelLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<ModelPageVo> newRecords = page.getRecords().stream().map(model -> new ModelPageVo()
                .setId(model.getId())
                .setType(model.getType())
                .setService(model.getService())
                .setName(model.getName())
                .setBaseUrl(model.getBaseUrl())
                .setApiKey(model.getApiKey())
                .setReadTimeout(model.getReadTimeout())
                .setTemperature(model.getTemperature())
                .setTopP(model.getTopP())
                .setInfo(model.getInfo())
                .setVersion(model.getVersion())
                .setState(model.getState())
                .setCreateTime(model.getCreateTime())).toList();
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addModel(ModelAddOrUpdateDto modelAddOrUpdateDto) {
        Model model = this.getModelByDto(modelAddOrUpdateDto);
        int insertResult = modelMapper.insert(model);
        CompletableFuture.runAsync(() -> {

        });
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    public void deleteModel(Long id) {
        Model modelInDatabase = modelMapper.selectById(id);
        if (Objects.isNull(modelInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.DATA_DOES_NOT_EXIST);
        }
        int modelDeleteResult = modelMapper.deleteById(id);
        if (modelDeleteResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 删除其他和模型关联的数据
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInfo(ModelAddOrUpdateDto modelAddOrUpdateDto) {
        Model modelInDatabase = modelMapper.selectById(modelAddOrUpdateDto.getId());
        if (Objects.isNull(modelInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.DATA_DOES_NOT_EXIST);
        }
        Model model = this.getModelByDto(modelAddOrUpdateDto);
        int updateResult = modelMapper.updateById(model);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    public void updateState(ModelUpdateStateDto modelUpdateStateDto) {
        Long id = modelUpdateStateDto.getId();
        LambdaUpdateWrapper<Model> modelLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

    }

    @Override
    public List<ModelExportVo> exportExcelList() {
        return List.of();
    }

    private Model getModelByDto(ModelAddOrUpdateDto modelAddOrUpdateDto) {
        // 获取参数
        Long id = modelAddOrUpdateDto.getId();
        String type = modelAddOrUpdateDto.getType();
        String service = modelAddOrUpdateDto.getService();
        String name = modelAddOrUpdateDto.getName();
        String baseUrl = modelAddOrUpdateDto.getBaseUrl();
        String apiKey = modelAddOrUpdateDto.getApiKey();
        Long readTimeout = modelAddOrUpdateDto.getReadTimeout();
        Double temperature = modelAddOrUpdateDto.getTemperature();
        Double topP = modelAddOrUpdateDto.getTopP();
        String infoName = modelAddOrUpdateDto.getInfoName();
        String version = modelAddOrUpdateDto.getVersion();
        // 构建实体类
        Model model = new Model();
        model.setId(id);
        Long realReadTimeout = Objects.isNull(readTimeout) ? ChatModelBase.DEFAULT_READ_TIMEOUT : readTimeout;
        if ("chat".equals(type)) {
            Double realTemperature = Objects.isNull(temperature) ? ChatModelBase.DEFAULT_TEMPERATURE : temperature;
            Double realTopP = Objects.isNull(topP) ? ChatModelBase.DEFAULT_TEMPERATURE : topP;
            if (ChatServiceType.DeepSeek.getValue().equals(service)
                || ChatServiceType.OpenAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setTemperature(realTemperature)
                        .setTopP(realTopP)
                        .setReadTimeout(realReadTimeout);
            } else if (ChatServiceType.Ollama.getValue().equals(service)) {
                if (StringUtils.isBlank(baseUrl)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setTemperature(realTemperature)
                        .setTopP(realTopP)
                        .setReadTimeout(realReadTimeout);
            } else if (ChatServiceType.ZhiPuAI.getValue().equals(service)
                       || ChatServiceType.MistralAI.getValue().equals(service)
                       || ChatServiceType.MiniMax.getValue().equals(service)) {
                if (StringUtils.isBlank(apiKey)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setApiKey(apiKey)
                        .setTemperature(realTemperature)
                        .setTopP(realTopP)
                        .setReadTimeout(realReadTimeout);
            } else if (ChatServiceType.AzureOpenAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey, version)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setVersion(version)
                        .setTemperature(realTemperature)
                        .setTopP(realTopP)
                        .setReadTimeout(realReadTimeout);
            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else if ("embedding".equals(type)) {
            if (ChatServiceType.OpenAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setReadTimeout(realReadTimeout);
            } else if (EmbeddingServiceType.Ollama.getValue().equals(service)) {
                if (StringUtils.isBlank(baseUrl)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setReadTimeout(realReadTimeout);
            } else if (EmbeddingServiceType.ZhiPuAI.getValue().equals(service)
                       || EmbeddingServiceType.MistralAI.getValue().equals(service)
                       || EmbeddingServiceType.MiniMax.getValue().equals(service)) {
                if (StringUtils.isBlank(apiKey)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setApiKey(apiKey)
                        .setReadTimeout(realReadTimeout);
            } else if (EmbeddingServiceType.AzureOpenAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey, version)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setVersion(version)
                        .setReadTimeout(realReadTimeout);
            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else if ("image".equals(type)) {
            if (ImageServiceType.OpenAI.getValue().equals(service)) {
                OpenAiImageType imageType = OpenAiImageType.getTypeByName(infoName);
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(imageType)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(imageType.getModel())
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfo(imageType.toJsonStr())
                        .setReadTimeout(realReadTimeout);
            } else if (ImageServiceType.Stability.getValue().equals(service)) {
                StabilityAiImageType imageType = StabilityAiImageType.getTypeByName(infoName);
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(imageType)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(imageType.getModel())
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfo(imageType.toJsonStr())
                        .setReadTimeout(realReadTimeout);
            } else if (ImageServiceType.ZhiPuAI.getValue().equals(service)) {
                ZhiPuAiImageType imageType = ZhiPuAiImageType.getTypeByName(infoName);
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(imageType)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(imageType.getModel())
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfo(imageType.toJsonStr())
                        .setReadTimeout(realReadTimeout);
            } else if (ImageServiceType.AzureOpenAI.getValue().equals(service)) {
                AzureOpenAiImageType imageType = AzureOpenAiImageType.getTypeByName(infoName);
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(imageType)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(imageType.getModel())
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfo(imageType.toJsonStr())
                        .setReadTimeout(realReadTimeout);
            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else if ("transcription".equals(type)) {
            Double realTemperature = Objects.isNull(temperature) ? TranscriptionModelBase.DEFAULT_TEMPERATURE : temperature;
            if (TranscriptionServiceType.OpenAI.getValue().equals(service)) {
                OpenAiTranscriptionType transcriptionType = OpenAiTranscriptionType.getTypeByName(infoName);
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(transcriptionType)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(transcriptionType.getModel())
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setTemperature(realTemperature)
                        .setInfo(transcriptionType.toJsonStr())
                        .setReadTimeout(realReadTimeout);
            } else if (TranscriptionServiceType.AzureOpenAI.getValue().equals(service)) {
                AzureOpenAiTranscriptionType transcriptionType = AzureOpenAiTranscriptionType.getTypeByName(infoName);
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(transcriptionType)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(transcriptionType.getModel())
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setTemperature(realTemperature)
                        .setInfo(transcriptionType.toJsonStr())
                        .setReadTimeout(realReadTimeout);
            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else if ("tts".equals(type)) {
            if (TtsServiceType.OpenAI.getValue().equals(service)) {
                OpenAiTtsType ttsType = OpenAiTtsType.getTypeByName(infoName);
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(ttsType)) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(ttsType.getModel())
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfo(ttsType.getModel())
                        .setReadTimeout(realReadTimeout);
            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型类型");
        }
        return model;
    }

    private void validateModel(Model model) {
        String type = model.getType();
        String service = model.getService();
        String prompt = "Only say 'hi'";
        if ("chat".equals(type)) {
            ChatModelBase chatModel;
            if (ChatServiceType.DeepSeek.getValue().equals(service)) {
                chatModel = new DeepSeekChatEntity(model.getName(), model.getApiKey(), model.getBaseUrl(), 10);
            } else if (ChatServiceType.OpenAI.getValue().equals(service)) {
                chatModel = new OpenAiChatEntity(model.getName(), model.getApiKey(), model.getBaseUrl(), 10);
            } else if (ChatServiceType.Ollama.getValue().equals(service)) {
                chatModel = new OllamaChatEntity(model.getName(), model.getBaseUrl());
            } else if (ChatServiceType.ZhiPuAI.getValue().equals(service)) {
                chatModel = new ZhiPuAiChatEntity(model.getName(), model.getApiKey(), 10);
            } else if (ChatServiceType.MistralAI.getValue().equals(service)) {
                chatModel = new MistralAiChatEntity(model.getName(), model.getApiKey(), 10);
            } else if (ChatServiceType.MiniMax.getValue().equals(service)) {
                chatModel = new MiniMaxChatEntity(model.getName(), model.getApiKey(), 10);
            } else if (ChatServiceType.AzureOpenAI.getValue().equals(service)) {
                OpenAIServiceVersion res = OpenAIServiceVersion.valueOf(Arrays
                        .stream(OpenAIServiceVersion.values())
                        .filter((x) -> model.getVersion().equals(x.getVersion()))
                        .findFirst()
                        .orElseThrow(() -> new CustomizeReturnException(ReturnCode.FAIL, "Azure OpenAI模型版本无效"))
                        .name());
                chatModel = new AzureOpenAiChatEntity(model.getName(), res, model.getApiKey(), model.getBaseUrl(), 10);
            } else {
                throw new CustomizeReturnException(ReturnCode.FAIL, "模型类型和服务无法匹配");
            }
            ChatResult result = chatService.chatString(chatModel, prompt);
            if (StringUtils.isAllBlank(result.getContent(), result.getReasoningContent())) {
                throw new CustomizeReturnException(ReturnCode.FAIL, "模型无法输出内容");
            }
        } else if ("embedding".equals(type)) {
            EmbeddingModelBase embeddingModel;
            if (EmbeddingServiceType.OpenAI.getValue().equals(service)) {
                embeddingModel = new OpenAiEmbeddingEntity(model.getName(), model.getApiKey(), model.getBaseUrl());
            } else if (EmbeddingServiceType.Ollama.getValue().equals(service)) {
                embeddingModel = new OllamaEmbeddingEntity(model.getName(), model.getBaseUrl());
            } else if (EmbeddingServiceType.ZhiPuAI.getValue().equals(service)) {
                embeddingModel = new ZhiPuAiEmbeddingEntity(model.getName(), model.getApiKey());
            } else if (EmbeddingServiceType.MistralAI.getValue().equals(service)) {
                embeddingModel = new MistralAiEmbeddingEntity(model.getName(), model.getApiKey());
            } else if (EmbeddingServiceType.MiniMax.getValue().equals(service)) {
                embeddingModel = new MiniMaxEmbeddingEntity(model.getName(), model.getApiKey());
            } else if (EmbeddingServiceType.AzureOpenAI.getValue().equals(service)) {
                OpenAIServiceVersion res = OpenAIServiceVersion.valueOf(Arrays
                        .stream(OpenAIServiceVersion.values())
                        .filter((x) -> model.getVersion().equals(x.getVersion()))
                        .findFirst()
                        .orElseThrow(() -> new CustomizeReturnException(ReturnCode.FAIL, "Azure OpenAI模型版本无效"))
                        .name());
                embeddingModel = new AzureOpenAiEmbeddingEntity(model.getName(), res, model.getApiKey(), model.getBaseUrl());
            } else {
                throw new CustomizeReturnException(ReturnCode.FAIL, "模型类型和服务无法匹配");
            }
            float[] result = embeddingService.embedToArray(embeddingModel, prompt);
            if (ArrayUtils.isEmpty(result)) {
                throw new CustomizeReturnException(ReturnCode.FAIL, "模型无法输出内容");
            }
        } else if ("image".equals(type)) {
            ImageModelBase imageModel;
            if (ImageServiceType.OpenAI.getValue().equals(service)) {
                imageModel = new OpenAiImageEntity(OpenAiImageType.getTypeByName(model.getName()), model.getApiKey(), model.getBaseUrl());
            } else if (ImageServiceType.Stability.getValue().equals(service)) {
                imageModel = new StabilityAiImageEntity(StabilityAiImageType.getTypeByName(model.getName()), model.getApiKey(), model.getBaseUrl());
            } else if (ImageServiceType.ZhiPuAI.getValue().equals(service)) {
                imageModel = new ZhiPuAiImageEntity(ZhiPuAiImageType.getTypeByName(model.getName()), model.getApiKey());
            } else if (ImageServiceType.AzureOpenAI.getValue().equals(service)) {
                imageModel = new AzureOpenAiImageEntity(AzureOpenAiImageType.getTypeByName(model.getName()),model.getApiKey(),model.getBaseUrl());
            } else {
                throw new CustomizeReturnException(ReturnCode.FAIL, "模型类型和服务无法匹配");
            }
            List<byte[]> result = imageService.imageToByteArray(imageModel, prompt);
            if (CollectionUtils.isEmpty(result)){
                throw new CustomizeReturnException(ReturnCode.FAIL, "模型无法输出内容");
            }
        }
    }

    public static void main(String[] args) {

        System.out.println();
    }

}
