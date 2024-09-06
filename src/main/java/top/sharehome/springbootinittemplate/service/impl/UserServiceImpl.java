package top.sharehome.springbootinittemplate.service.impl;

import cn.dev33.satoken.context.SaHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.FileMapper;
import top.sharehome.springbootinittemplate.mapper.LogMapper;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.user.*;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserExportVo;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserPageVo;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.oss.minio.MinioUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private LogMapper logMapper;

    @Resource
    private FileMapper fileMapper;

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<AdminUserPageVo> adminPageUser(AdminUserPageDto adminUserPageDto, PageModel pageModel) {
        Page<User> page = pageModel.build();
        Page<AdminUserPageVo> res = pageModel.build();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构造查询条件
        userLambdaQueryWrapper
                .eq(StringUtils.isNotBlank(adminUserPageDto.getRole()), User::getRole, adminUserPageDto.getRole())
                .eq(Objects.nonNull(adminUserPageDto.getState()), User::getState, adminUserPageDto.getState())
                .like(StringUtils.isNotBlank(adminUserPageDto.getAccount()), User::getAccount, adminUserPageDto.getAccount())
                .like(StringUtils.isNotBlank(adminUserPageDto.getName()), User::getName, adminUserPageDto.getName())
                .like(StringUtils.isNotBlank(adminUserPageDto.getEmail()), User::getEmail, adminUserPageDto.getEmail());
        // 构造查询排序（默认按照创建时间升序排序）
        userLambdaQueryWrapper.orderByAsc(User::getCreateTime);

        // 分页查询
        userMapper.selectPage(page, userLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<AdminUserPageVo> newRecords = page.getRecords().stream().map(user -> {
            File avatarFile = fileMapper.selectById(user.getAvatarId());
            return new AdminUserPageVo()
                    .setId(user.getId())
                    .setAccount(user.getAccount())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setAvatarId(user.getAvatarId())
                    .setAvatar(Objects.isNull(avatarFile) ? null : avatarFile.getUrl())
                    .setRole(user.getRole())
                    .setState(user.getState())
                    .setCreateTime(user.getCreateTime());
        }).toList();
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminAddUser(AdminUserAddDto adminUserAddDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, adminUserAddDto.getAccount());
        // 禁止添加已存在同名账号
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 插入数据库
        User user = new User()
                .setAccount(adminUserAddDto.getAccount())
                .setPassword(adminUserAddDto.getPassword())
                .setEmail(adminUserAddDto.getEmail())
                .setName(adminUserAddDto.getName());
        int insertResult = userMapper.insert(user);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDeleteUser(Long id) {
        User userInDatabase = userMapper.selectById(id);
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 删除用户信息
        int userDeleteResult = userMapper.deleteById(id);
        if (userDeleteResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 删除该用户在平台中的操作日志记录
        LambdaQueryWrapper<Log> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.eq(Log::getUserId, id);
        logMapper.delete(logLambdaQueryWrapper);
        LoginUtils.logout(id);
        // 如果业务上有需求在删除用户之后删除用户头像...
        //MinioUtils.delete(userInDatabase.getAvatar());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdateInfo(AdminUserUpdateInfoDto adminUserUpdateInfoDto) {
        User userInDatabase = userMapper.selectById(adminUserUpdateInfoDto.getId());
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 禁止添加非自身的已存在同名账号
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, adminUserUpdateInfoDto.getAccount());
        userLambdaQueryWrapper.ne(User::getId, adminUserUpdateInfoDto.getId());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 数据只有发生更新之后才可以进行数据库操作
        if (Objects.equals(adminUserUpdateInfoDto.getAccount(), userInDatabase.getAccount())
                && Objects.equals(adminUserUpdateInfoDto.getName(), userInDatabase.getName())
                && Objects.equals(adminUserUpdateInfoDto.getEmail(), userInDatabase.getEmail())) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS, "信息未发生更改");
        }
        User user = new User()
                .setId(adminUserUpdateInfoDto.getId())
                .setAccount(adminUserUpdateInfoDto.getAccount())
                .setEmail(adminUserUpdateInfoDto.getEmail())
                .setName(adminUserUpdateInfoDto.getName());
        int updateResult = userMapper.updateById(user);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(adminUserUpdateInfoDto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdateState(AdminUserUpdateStateDto adminUserUpdateStateDto) {
        Long id = adminUserUpdateStateDto.getId();
        User userInDatabase = userMapper.selectById(id);
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 根据数据库中用户状态做出更新
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (!Objects.equals(userInDatabase.getState(), Constants.USER_DISABLE_STATE)) {
            userLambdaUpdateWrapper.set(User::getState, Constants.USER_DISABLE_STATE);
        } else {
            userLambdaUpdateWrapper
                    .set(User::getState, Constants.USER_ENABLE_STATE)
                    .set(User::getLoginNum, 0);
        }
        userLambdaUpdateWrapper.eq(User::getId, id);
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminResetPassword(AdminUserResetPasswordDto adminUserResetPasswordDto) {
        User userInDatabase = userMapper.selectById(adminUserResetPasswordDto.getId());
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 重置用户密码
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper
                .set(User::getPassword, adminUserResetPasswordDto.getNewPassword())
                .eq(User::getId, adminUserResetPasswordDto.getId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(userInDatabase.getId());
    }

    @Override
    public List<AdminUserExportVo> adminExportExcelList() {
        List<User> usersInDatabase = userMapper.selectList(null);
        return usersInDatabase.stream().map(user -> {
            AdminUserExportVo adminUserExportVo = new AdminUserExportVo();
            adminUserExportVo.setId(user.getId());
            adminUserExportVo.setAccount(user.getAccount());
            adminUserExportVo.setEmail(user.getEmail());
            adminUserExportVo.setName(user.getName());
            File avatarFile = fileMapper.selectById(user.getAvatarId());
            adminUserExportVo.setAvatar(Objects.isNull(avatarFile) ? null : avatarFile.getUrl());
            adminUserExportVo.setRole(StringUtils.equals(user.getRole(), Constants.ROLE_ADMIN) ? "管理员" : "用户");
            adminUserExportVo.setState(!Objects.equals(user.getState(), Constants.USER_DISABLE_STATE) ? "启用" : "禁用");
            adminUserExportVo.setCreateTime(user.getCreateTime());
            adminUserExportVo.setUpdateTime(user.getUpdateTime());
            return adminUserExportVo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccount(String newAccount) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, newAccount);
        userLambdaQueryWrapper.ne(User::getId, LoginUtils.getLoginUserId());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getAccount, newAccount);
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.syncLoginUser();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateName(String newName) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getName, newName);
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.syncLoginUser();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String newEmail) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getEmail, newEmail);
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.syncLoginUser();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String oldPassword, String newPassword) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long userId = LoginUtils.getLoginUserId();
        userLambdaQueryWrapper.eq(User::getId, userId);
        userLambdaQueryWrapper.eq(User::getPassword, oldPassword);
        User userInResult = userMapper.selectOne(userLambdaQueryWrapper);
        if (Objects.isNull(userInResult)) {
            throw new CustomizeReturnException(ReturnCode.PASSWORD_VERIFICATION_FAILED);
        }
        userInResult.setPassword(newPassword);
        int updateResult = userMapper.updateById(userInResult);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.logout();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(MultipartFile file) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String avatarPath = "avatar/" + date;
        File avatarFile = MinioUtils.upload(file, avatarPath);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getAvatarId, avatarFile.getId());
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        AuthLoginVo loginUser = (AuthLoginVo) SaHolder.getStorage().get(Constants.LOGIN_USER_KEY);
        if (fileMapper.exists(new LambdaQueryWrapper<File>().eq(File::getId, loginUser.getAvatarId()))) {
            MinioUtils.delete(loginUser.getAvatarId());
        }
        LoginUtils.syncLoginUser();
    }
}
