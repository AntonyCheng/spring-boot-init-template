package top.sharehome.springbootinittemplate.controller.user;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.dto.user.*;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserExportVo;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserPageVo;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员管理用户控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/admin/user")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class AdminUserController {

    /**
     * 用户信息表体积最大值
     */
    private static final int IMPORT_MAX_SIZE = 200 * 1024;

    /**
     * 用户信息表扩展名集合
     */
    private static final List<String> IMPORT_SUFFIX_LIST = new ArrayList<>() {
        {
            add("xls");
            add("xlsx");
        }
    };

    @Resource
    private UserService userService;

    /**
     * 管理员分页查询用户信息
     *
     * @param adminUserPageDto 用户信息查询条件
     * @param pageModel        分页模型
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ControllerLog(description = "管理员查询用户信息", operator = Operator.QUERY)
    public R<Page<AdminUserPageVo>> pageUser(AdminUserPageDto adminUserPageDto, PageModel pageModel) {
        Page<AdminUserPageVo> page = userService.adminPageUser(adminUserPageDto, pageModel);
        return R.ok(page);
    }

    /**
     * 管理员添加用户
     *
     * @param adminUserAddDto 被添加用户信息
     * @return 添加结果
     */
    @PostMapping("/add")
    @ControllerLog(description = "管理员添加用户信息", operator = Operator.INSERT)
    public R<String> addUser(@RequestBody @Validated({PostGroup.class}) AdminUserAddDto adminUserAddDto) {
        userService.adminAddUser(adminUserAddDto);
        return R.ok("添加成功");
    }

    /**
     * 管理员根据ID删除用户
     *
     * @param id 被删除用户的ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ControllerLog(description = "管理员删除用户信息", operator = Operator.DELETE)
    public R<String> deleteUser(@PathVariable("id") Long id) {
        userService.adminDeleteUser(id);
        return R.ok("删除成功");
    }

    /**
     * 管理员修改用户信息
     *
     * @param adminUserUpdateInfoDto 被修改后的用户信息
     * @return 修改结果
     */
    @PutMapping("/update/info")
    @ControllerLog(description = "管理员修改用户信息", operator = Operator.UPDATE)
    public R<String> updateInfo(@RequestBody @Validated({PutGroup.class}) AdminUserUpdateInfoDto adminUserUpdateInfoDto) {
        userService.adminUpdateInfo(adminUserUpdateInfoDto);
        return R.ok("修改信息成功");
    }

    /**
     * 管理员修改用户状态
     *
     * @param adminUserUpdateStateDto 被修改用户的ID对象
     * @return 修改结果
     */
    @PutMapping("/update/state")
    @ControllerLog(description = "管理员修改用户状态", operator = Operator.UPDATE)
    public R<String> updateState(@RequestBody @Validated({PutGroup.class}) AdminUserUpdateStateDto adminUserUpdateStateDto) {
        userService.adminUpdateState(adminUserUpdateStateDto);
        return R.ok("修改状态成功");
    }

    /**
     * 管理员重置用户密码
     *
     * @param adminUserResetPasswordDto 被重置密码信息
     * @return 重置结果
     */
    @PutMapping("/reset/password")
    @ControllerLog(description = "管理员重置用户密码", operator = Operator.UPDATE)
    public R<String> resetPassword(@RequestBody @Validated({PutGroup.class}) AdminUserResetPasswordDto adminUserResetPasswordDto) {
        userService.adminResetPassword(adminUserResetPasswordDto);
        return R.ok("重置密码成功");
    }

    /**
     * 导出用户表格
     *
     * @return 导出表格
     */
    @GetMapping("/export")
    @ControllerLog(description = "管理员导出用户表格", operator = Operator.EXPORT)
    public R<Void> exportUser(HttpServletResponse response) {
        List<AdminUserExportVo> list = userService.adminExportExcelList();
        ExcelUtils.exportHttpServletResponse(list, "用户表", AdminUserExportVo.class, response);
        return R.empty();
    }

    /**
     * 导出用户信息表模板
     *
     * @param response 响应
     */
    @GetMapping("/template")
    @ControllerLog(description = "管理员导出用户表格模板", operator = Operator.EXPORT)
    public R<Void> exportUserTemplate(HttpServletResponse response) {
        ExcelUtils.exportTemplateHttpServletResponse("用户信息表", AdminUserTemplateDto.class, response);
        return R.empty();
    }

    /**
     * 导入用户信息表
     *
     * @param adminUserExcelDto 导入用户信息表Dto类
     * @return 导入结果
     */
    @PostMapping("/import")
    @ControllerLog(description = "管理员导入用户信息表", operator = Operator.INSERT)
    public R<String> importUser(@Validated({PostGroup.class}) AdminUserExcelDto adminUserExcelDto){
        MultipartFile file = adminUserExcelDto.getFile();
        if (file.getSize() == 0 || file.getSize() > IMPORT_MAX_SIZE) {
            throw new CustomizeReturnException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE, "用户信息表不得大于200KB");
        }
        String originalName = StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName();
        String suffix = FilenameUtils.getExtension(originalName).toLowerCase();
        if (!IMPORT_SUFFIX_LIST.contains(suffix)) {
            throw new CustomizeReturnException(ReturnCode.USER_UPLOADED_FILE_TYPE_MISMATCH, "用户信息表仅支持xls和xlsx格式");
        }
        userService.adminImportUser(file);
        return R.ok("导入成功，默认密码为123456");
    }

}
