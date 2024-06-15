package top.sharehome.springbootinittemplate.controller.log;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.model.dto.log.AdminLogPageDto;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.log.AdminLogExportVo;
import top.sharehome.springbootinittemplate.model.vo.log.AdminLogPageVo;
import top.sharehome.springbootinittemplate.service.LogService;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;

import java.util.List;

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
     * 管理员删除日志信息
     *
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public R<String> deleteLog(@PathVariable("id") Long id) {
        logService.adminDeleteLog(id);
        return R.ok("删除成功");
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

    /**
     * 导出日志表格
     *
     * @return 导出表格
     */
    @GetMapping("/export")
    public R<Void> exportExcel(HttpServletResponse response) {
        List<AdminLogExportVo> list = logService.adminExportExcelList();
        ExcelUtils.exportHttpServletResponse(list, "日志表", AdminLogExportVo.class, response);
        return R.empty();
    }

}
