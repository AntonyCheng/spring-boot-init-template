package top.sharehome.springbootinittemplate.controller.user;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import jakarta.annotation.Resource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.common.validate.PutGroup;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.dto.user.*;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/user")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN, Constants.ROLE_USER}, mode = SaMode.OR)
public class UserController {

    /**
     * 头像最大大小
     */
    private static final int AVATAR_MAX_SIZE = 1024 * 1024;

    /**
     * 头像文件后缀集合
     */
    private static final List<String> AVATAR_SUFFIX_LIST = new ArrayList<>() {
        {
            add("png");
            add("jpg");
            add("jpeg");
        }
    };

    @Resource
    private UserService userService;

    /**
     * 用户更新账号
     *
     * @param userUpdateAccountDto 用户更新账号Dto类
     * @return 返回更新结果
     */
    @PutMapping("/update/account")
    @ControllerLog(description = "用户更新自身账号", operator = Operator.UPDATE)
    public R<String> updateAccount(@RequestBody @Validated(PutGroup.class) UserUpdateAccountDto userUpdateAccountDto) {
        if (StringUtils.equals(LoginUtils.getLoginUserAccount(), userUpdateAccountDto.getNewAccount())) {
            throw new CustomizeReturnException(ReturnCode.NEW_ACCOUNT_AND_OLD_ACCOUNT_ARE_SAME);
        }
        userService.updateAccount(userUpdateAccountDto.getNewAccount());
        return R.ok("更新账号成功");
    }

    /**
     * 用户更新名称
     *
     * @param userUpdateNameDto 用户更新名称Dto类
     * @return 返回更新结果
     */
    @PutMapping("/update/name")
    @ControllerLog(description = "用户更新自身名称", operator = Operator.UPDATE)
    public R<String> updateName(@RequestBody @Validated(PutGroup.class) UserUpdateNameDto userUpdateNameDto) {
        if (StringUtils.equals(LoginUtils.getLoginUserOrThrow().getName(), userUpdateNameDto.getNewName())) {
            throw new CustomizeReturnException(ReturnCode.NEW_USERNAME_AND_OLD_USERNAME_ARE_SAME);
        }
        userService.updateName(userUpdateNameDto.getNewName());
        return R.ok("更新账号成功");
    }

    /**
     * 用户更新邮箱
     *
     * @param userUpdateEmailDto 用户更新邮箱Dto类
     * @return 返回更新结果
     */
    @PutMapping("/update/email")
    @ControllerLog(description = "用户更新自身邮箱", operator = Operator.UPDATE)
    @SaIgnore
    public R<String> updateEmail(@RequestBody @Validated(PutGroup.class) UserUpdateEmailDto userUpdateEmailDto) {
        if (StringUtils.equals(LoginUtils.getLoginUserOrThrow().getEmail(), userUpdateEmailDto.getNewEmail())) {
            throw new CustomizeReturnException(ReturnCode.NEW_EMAIL_AND_OLD_EMAIL_ARE_SAME);
        }
        userService.updateEmail(userUpdateEmailDto.getNewEmail());
        return R.ok("更新邮箱成功");
    }

    /**
     * 用户更新密码
     *
     * @param userUpdatePasswordDto 用户更新密码Dto类
     * @return 返回更新结果
     */
    @PutMapping("/update/password")
    @ControllerLog(description = "用户更新自身密码", operator = Operator.UPDATE)
    public R<String> updatePassword(@RequestBody @Validated({PutGroup.class}) UserUpdatePasswordDto userUpdatePasswordDto) {
        if (!StringUtils.equals(userUpdatePasswordDto.getNewPassword(), userUpdatePasswordDto.getCheckNewPassword())) {
            throw new CustomizeReturnException(ReturnCode.PASSWORD_AND_SECONDARY_PASSWORD_NOT_SAME);
        }
        if (StringUtils.equals(userUpdatePasswordDto.getNewPassword(), userUpdatePasswordDto.getOldPassword())) {
            throw new CustomizeReturnException(ReturnCode.NEW_PASSWORD_AND_OLD_PASSWORD_ARE_SAME);
        }
        userService.updatePassword(userUpdatePasswordDto.getOldPassword(), userUpdatePasswordDto.getNewPassword());
        return R.ok("更新密码成功，请重新登录");
    }

    /**
     * 用户更新头像（前提：需要配置对象存储，该接口默认使用MinIO）
     *
     * @param userUpdateAvatarDto 用户更新头像Dto类
     * @return 返回更新结果
     */
    @PutMapping("/update/avatar")
    @ControllerLog(description = "用户更新自身头像", operator = Operator.UPDATE)
    public R<String> updateAvatar(@Validated({PutGroup.class}) UserUpdateAvatarDto userUpdateAvatarDto) {
        MultipartFile file = userUpdateAvatarDto.getFile();
        if (file.getSize() == 0 || file.getSize() > AVATAR_MAX_SIZE) {
            throw new CustomizeReturnException(ReturnCode.USER_UPLOADED_FILE_IS_TOO_LARGE, "头像不得大于1MB");
        }
        String originalName = StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName();
        String suffix = FilenameUtils.getExtension(originalName).toLowerCase();
        if (!AVATAR_SUFFIX_LIST.contains(suffix)) {
            throw new CustomizeReturnException(ReturnCode.USER_UPLOADED_FILE_TYPE_MISMATCH, "头像仅支持png和jpg格式");
        }
        userService.updateAvatar(file);
        return R.ok("更新头像成功");
    }
}
