package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.ChatServiceType;
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
                .setN(model.getN())
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
        Model model = new Model();
        if ("chat".equals(modelAddDto.getType())) {
            if (ChatServiceType.DeepSeek.getValue().equals(modelAddDto.getService())) {

            } else if (ChatServiceType.OpenAI.getValue().equals(modelAddDto.getService())) {

            } else if (ChatServiceType.Ollama.getValue().equals(modelAddDto.getService())) {

            } else if (ChatServiceType.ZhiPuAI.getValue().equals(modelAddDto.getService())) {

            } else if (ChatServiceType.MistralAI.getValue().equals(modelAddDto.getService())) {

            } else if (ChatServiceType.MiniMax.getValue().equals(modelAddDto.getService())) {

            } else if (ChatServiceType.AzureOpenAI.getValue().equals(modelAddDto.getService())) {

            } else {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "无此模型服务");
            }
        } else if ("embedding".equals(modelAddDto.getType())) {

        } else if ("image".equals(modelAddDto.getType())) {

        } else if ("transcription".equals(modelAddDto.getType())) {

        } else if ("tts".equals(modelAddDto.getType())) {

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
