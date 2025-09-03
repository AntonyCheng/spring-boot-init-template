package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.dto.model.ModelAddDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelPageDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelUpdateInfoDto;
import top.sharehome.springbootinittemplate.model.dto.model.ModelUpdateStateDto;
import top.sharehome.springbootinittemplate.model.entity.Model;
import top.sharehome.springbootinittemplate.model.vo.model.ModelExportVo;
import top.sharehome.springbootinittemplate.model.vo.model.ModelPageVo;

import java.util.List;

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

    /**
     * 管理员添加模型
     *
     * @param modelAddDto 被添加模型信息
     */
    void addModel(ModelAddDto modelAddDto);

    /**
     * 管理员根据ID删除模型
     *
     * @param id 被删除模型的ID
     */
    void deleteModel(Long id);

    /**
     * 管理员修改模型信息
     *
     * @param modelUpdateInfoDto 被修改后的模型信息
     */
    void updateInfo(ModelUpdateInfoDto modelUpdateInfoDto);

    /**
     * 管理员修改模型状态
     *
     * @param modelUpdateStateDto 被修改模型的ID对象
     */
    void updateState(ModelUpdateStateDto modelUpdateStateDto);

    /**
     * 管理员导出模型表格
     * 此时导出的是逻辑未删除的数据，如果想要逻辑删除的数据：
     * 1、重新创建和Model类mapper，service，controller相似的一个类，比如AModel，AModel的isDeleted字段打上TableLogic(value="false")，然后查询。（非常不推荐）
     * 2、在ModelMapper当中使用@Select注解编写SQL查询。
     *
     * @return 模型列表结果
     */
    List<ModelExportVo> exportExcelList();

}
