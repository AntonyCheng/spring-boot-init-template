package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.*;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.AzureOpenAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.OpenAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.StabilityAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.ZhiPuAiImageType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.AzureOpenAiTranscriptionType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.OpenAiTranscriptionType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.enums.OpenAiTtsType;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.ModelMapper;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.dto.model.ModelAddDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelPageDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelUpdateInfoDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelUpdateStateDto;
import top.sharehome.springbootinittemplate.model.entity.Model;
import top.sharehome.springbootinittemplate.model.vo.model.ModelExportVo;
import top.sharehome.springbootinittemplate.model.vo.model.ModelPageVo;
import top.sharehome.springbootinittemplate.service.ModelService;

import java.util.List;
import java.util.Objects;

/**
 * 模型服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

    @Resource
    private ModelMapper modelMapper;

    @Override
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
                .setInfoName(model.getInfoName())
                .setVersion(model.getVersion())
                .setState(model.getState())
                .setCreateTime(model.getCreateTime())).toList();
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    public void addModel(ModelAddDto modelAddDto) {
        // 获取参数
        String type = modelAddDto.getType();
        String service = modelAddDto.getService();
        String name = modelAddDto.getName();
        String baseUrl = modelAddDto.getBaseUrl();
        String apiKey = modelAddDto.getApiKey();
        Long readTimeout = modelAddDto.getReadTimeout();
        Double temperature = modelAddDto.getTemperature();
        Double topP = modelAddDto.getTopP();
        String infoName = modelAddDto.getInfoName();
        String version = modelAddDto.getVersion();
        // 构建实体类
        Model model = new Model();
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
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(OpenAiImageType.getTypeByName(infoName))) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfoName(infoName)
                        .setReadTimeout(realReadTimeout);
            } else if (ImageServiceType.Stability.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(StabilityAiImageType.getTypeByName(infoName))) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfoName(infoName)
                        .setReadTimeout(realReadTimeout);
            } else if (ImageServiceType.ZhiPuAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(ZhiPuAiImageType.getTypeByName(infoName))) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfoName(infoName)
                        .setReadTimeout(realReadTimeout);
            } else if (ImageServiceType.AzureOpenAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(AzureOpenAiImageType.getTypeByName(infoName))) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfoName(infoName)
                        .setReadTimeout(realReadTimeout);
            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else if ("transcription".equals(type)) {
            Double realTemperature = Objects.isNull(temperature) ? TranscriptionModelBase.DEFAULT_TEMPERATURE : temperature;
            if (TranscriptionServiceType.OpenAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(OpenAiTranscriptionType.getTypeByName(infoName))) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setTemperature(realTemperature)
                        .setInfoName(infoName)
                        .setReadTimeout(realReadTimeout);
            } else if (TranscriptionServiceType.AzureOpenAI.getValue().equals(service)){
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(AzureOpenAiTranscriptionType.getTypeByName(infoName))) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setTemperature(realTemperature)
                        .setInfoName(infoName)
                        .setReadTimeout(realReadTimeout);
            }else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else if ("tts".equals(type)) {
            if (TtsServiceType.OpenAI.getValue().equals(service)) {
                if (StringUtils.isAnyBlank(baseUrl, apiKey, infoName) || Objects.isNull(OpenAiTtsType.getTypeByName(infoName))) {
                    throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, service + "必要参数缺失/错误");
                }
                model.setType(type).setService(service).setName(name)
                        .setBaseUrl(baseUrl)
                        .setApiKey(apiKey)
                        .setInfoName(infoName)
                        .setReadTimeout(realReadTimeout);
            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型类型");
        }
    }

    @Override
    public void deleteModel(Long id) {

    }

    @Override
    public void updateInfo(ModelUpdateInfoDto modelUpdateInfoDto) {

    }

    @Override
    public void updateState(ModelUpdateStateDto modelUpdateStateDto) {

    }

    @Override
    public List<ModelExportVo> exportExcelList() {
        return List.of();
    }


}
