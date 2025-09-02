package top.sharehome.springbootinittemplate.controller.model;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.dto.model.ModelPageDto;
import top.sharehome.springbootinittemplate.model.vo.model.ModelPageVo;
import top.sharehome.springbootinittemplate.service.ModelService;

/**
 * 模型控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/model")
@SaCheckLogin
public class ModelController {

    @Resource
    private ModelService modelService;

    /**
     * 管理员分页查询模型信息
     */
    @GetMapping("/page")
    @ControllerLog(description = "管理员查询模型信息", operator = Operator.QUERY)
    @SaCheckRole(value = {Constants.ROLE_ADMIN})
    public R<Page<ModelPageVo>> pageModel(ModelPageDto modelPageDto, PageModel pageModel) {
        Page<ModelPageVo> page = modelService.pageModel(modelPageDto, pageModel);
        return R.ok(page);
    }

}
