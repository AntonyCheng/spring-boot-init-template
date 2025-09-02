package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.mapper.ModelMapper;
import top.sharehome.springbootinittemplate.model.entity.Model;
import top.sharehome.springbootinittemplate.service.ModelService;

/**
 * 模型服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

    @Resource
    private ModelMapper modelMapper;

}
