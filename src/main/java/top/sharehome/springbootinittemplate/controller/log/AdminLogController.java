package top.sharehome.springbootinittemplate.controller.log;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.model.dto.log.AdminLogPageDto;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.log.AdminLogPageVo;
import top.sharehome.springbootinittemplate.service.LogService;

/**
 * 管理员管理日志控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/admin/log")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class AdminLogController {

    @Resource
    private LogService logService;

    /**
     * 管理员分页查询日志信息
     *
     * @param adminLogPageDto 日志信息查询条件
     * @param pageModel       分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public R<Page<AdminLogPageVo>> pageLog(AdminLogPageDto adminLogPageDto, PageModel pageModel) {
        Page<AdminLogPageVo> page = logService.adminPageLog(adminLogPageDto, pageModel);
        return R.ok(page);
    }

    /**
     * 管理员清空日志信息
     *
     * @return 清空结果
     */
    @DeleteMapping("/clear")
    public R<String> clearLog() {
        logService.adminClearLog();
        return R.ok("清空日志成功");
    }

}
