package top.sharehome.springbootinittemplate.controller.file;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.common.base.Constants;

/**
 * 管理员管理文件控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/admin/file")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class AdminFileController {

}
