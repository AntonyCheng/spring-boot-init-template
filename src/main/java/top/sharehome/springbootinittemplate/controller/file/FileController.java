package top.sharehome.springbootinittemplate.controller.file;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.dto.file.FileAddDto;
import top.sharehome.springbootinittemplate.model.dto.file.FilePageDto;
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.vo.file.FileExportVo;
import top.sharehome.springbootinittemplate.model.vo.file.FilePageVo;
import top.sharehome.springbootinittemplate.service.FileService;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;

import java.util.List;

/**
 * 文件控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/file")
@SaCheckLogin
public class FileController {

    /**
     * 文件最大大小
     */
    private static final int FILE_MAX_SIZE = 10 * 1024 * 1024;

    @Resource
    private FileService fileService;

    /**
     * 管理员分页查询文件信息
     *
     * @param filePageDto 文件信息查询条件
     * @param pageModel   分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ControllerLog(description = "管理员查询文件信息", operator = Operator.QUERY)
    @SaCheckRole(value = {Constants.ROLE_ADMIN})
    public R<Page<FilePageVo>> pageFile(FilePageDto filePageDto, PageModel pageModel) {
        // 处理一下文件后缀名查询条件，如果存在则转为小写
        if (StringUtils.isNotBlank(filePageDto.getOssType())) {
            filePageDto.setOssType(filePageDto.getOssType().toLowerCase());
        }
        Page<FilePageVo> file = fileService.pageFile(filePageDto, pageModel);
        return R.ok(file);
    }

    /**
     * 管理员添加文件信息
     *
     * @param fileAddDto 被添加文件信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @ControllerLog(description = "管理员添加文件信息", operator = Operator.INSERT)
    @SaCheckRole(value = {Constants.ROLE_ADMIN})
    public R<String> addFile(@Validated({PostGroup.class}) FileAddDto fileAddDto) {
        MultipartFile file = fileAddDto.getFile();
        if (file.getSize() == 0 || file.getSize() > FILE_MAX_SIZE) {
            throw new CustomizeReturnException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE, "文件不得大于10MB");
        }
        fileService.addFile(file);
        return R.ok("添加成功");
    }

    /**
     * 管理员删除文件信息
     *
     * @param id 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ControllerLog(description = "管理员删除文件信息", operator = Operator.DELETE)
    @SaCheckRole(value = {Constants.ROLE_ADMIN})
    public R<String> deleteFile(@PathVariable("id") Long id) {
        fileService.deleteFile(id);
        return R.ok("删除成功");
    }

    /**
     * 导出文件表格
     *
     * @return 导出表格
     */
    @GetMapping("/export")
    @ControllerLog(description = "管理员导出文件表格", operator = Operator.QUERY)
    @SaCheckRole(value = {Constants.ROLE_ADMIN})
    public R<Void> exportFile(HttpServletResponse response) {
        List<FileExportVo> list = fileService.exportExcelList();
        ExcelUtils.exportHttpServletResponse(list, "文件表", FileExportVo.class, response);
        return R.empty();
    }

}
