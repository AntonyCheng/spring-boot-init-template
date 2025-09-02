package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.dto.model.ModelPageDto;
import top.sharehome.springbootinittemplate.model.entity.Model;
import top.sharehome.springbootinittemplate.model.vo.model.ModelPageVo;

/**
 * 模型服务接口
 *
 * @author AntonyCheng
 */
public interface ModelService extends IService<Model> {

    /**
     * 管理员分页查询模型信息
     *
     * @param modelPageDto 模型信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    Page<ModelPageVo> pageModel(ModelPageDto modelPageDto, PageModel pageModel);
}
