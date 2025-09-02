package top.sharehome.springbootinittemplate.controller.model;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.service.ModelService;

/**
 * 模型控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/model")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class ModelController {

    @Resource
    private ModelService modelService;

}
