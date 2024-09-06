package top.sharehome.springbootinittemplate.controller.file;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.standard.expression.EachUtils;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.model.dto.file.AdminFilePageDto;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.file.AdminFileExportVo;
import top.sharehome.springbootinittemplate.model.vo.file.AdminFilePageVo;
import top.sharehome.springbootinittemplate.service.FileService;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;

import java.util.List;

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

    @Resource
    private FileService fileService;

    /**
     * 管理员分页查询文件信息
     *
     * @param adminFilePageDto 文件信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ControllerLog(description = "管理员查询文件信息", operator = Operator.QUERY)
    public R<Page<AdminFilePageVo>> pageFile(AdminFilePageDto adminFilePageDto, PageModel pageModel) {
        Page<AdminFilePageVo> file = fileService.adminPageFile(adminFilePageDto, pageModel);
        return R.ok(file);
    }

    /**
     * 管理员删除文件信息
     *
     * @param id 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ControllerLog(description = "管理员删除文件信息", operator = Operator.DELETE)
    public R<String> deleteFile(@PathVariable("id") Long id) {
        fileService.adminDeleteFile(id);
        return R.ok("删除成功");
    }

    /**
     * 导出文件表格
     *
     * @return 导出表格
     */
    @GetMapping("/export")
    @ControllerLog(description = "管理员导出文件表格", operator = Operator.QUERY)
    public R<Void> exportFile(HttpServletResponse response) {
        List<AdminFileExportVo> list = fileService.adminExportExcelList();
        ExcelUtils.exportHttpServletResponse(list,"文件表",AdminFileExportVo.class,response);
        return R.empty();
    }

}
