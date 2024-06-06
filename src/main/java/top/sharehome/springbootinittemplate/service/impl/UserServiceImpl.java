package top.sharehome.springbootinittemplate.service.impl;

import cn.dev33.satoken.context.SaHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeTransactionException;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserAddDto;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserPageDto;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserResetPasswordDto;
import top.sharehome.springbootinittemplate.model.dto.user.AdminUserUpdateInfoDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.page.PageModel;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserExportVo;
import top.sharehome.springbootinittemplate.model.vo.user.AdminUserPageVo;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.oss.minio.MinioUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true, rollbackFor = CustomizeTransactionException.class)
    public Page<AdminUserPageVo> adminPageUser(AdminUserPageDto adminUserPageDto, PageModel pageModel) {
        Page<User> page = pageModel.build();
        Page<AdminUserPageVo> res = pageModel.build();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构造查询条件
        userLambdaQueryWrapper
                .eq(StringUtils.isNotBlank(adminUserPageDto.getRole()), User::getRole, adminUserPageDto.getRole())
                .eq(Objects.nonNull(adminUserPageDto.getState()), User::getState, adminUserPageDto.getState())
                .like(StringUtils.isNotBlank(adminUserPageDto.getAccount()), User::getAccount, adminUserPageDto.getAccount())
                .like(StringUtils.isNotBlank(adminUserPageDto.getName()), User::getName, adminUserPageDto.getName());
        // 构造查询排序（默认按照创建时间升序排序）
        userLambdaQueryWrapper.orderByAsc(User::getCreateTime);

        // 分页查询
        userMapper.selectPage(page, userLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<AdminUserPageVo> newRecords = page.getRecords().stream().map(user -> {
            AdminUserPageVo adminUserPageVo = new AdminUserPageVo();
            adminUserPageVo.setId(user.getId());
            adminUserPageVo.setAccount(user.getAccount());
            adminUserPageVo.setName(user.getName());
            adminUserPageVo.setAvatar(user.getAvatar());
            adminUserPageVo.setRole(user.getRole());
            adminUserPageVo.setState(user.getState());
            adminUserPageVo.setCreateTime(user.getCreateTime());
            return adminUserPageVo;
        }).collect(Collectors.toList());
        BeanUtils.copyProperties(page, res, "records");
        res.setRecords(newRecords);

        return res;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void adminAddUser(AdminUserAddDto adminUserAddDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, adminUserAddDto.getAccount());
        // 禁止添加已存在同名账号
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 插入数据库
        User user = new User();
        user.setAccount(adminUserAddDto.getAccount());
        user.setPassword(adminUserAddDto.getPassword());
        user.setName(adminUserAddDto.getName());
        int insertResult = userMapper.insert(user);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
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
        int deleteResult = userMapper.deleteById(id);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.logout(id);
        // 如果业务上有需求在删除用户之后删除用户头像...
        //MinioUtils.delete(userInDatabase.getAvatar());
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
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
        if (Objects.equals(adminUserUpdateInfoDto.getAccount(), userInDatabase.getAccount()) && Objects.equals(adminUserUpdateInfoDto.getName(), userInDatabase.getName())) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS, "信息未发生更改");
        }
        User user = new User()
                .setId(adminUserUpdateInfoDto.getId())
                .setAccount(adminUserUpdateInfoDto.getAccount())
                .setName(adminUserUpdateInfoDto.getName());
        int updateResult = userMapper.updateById(user);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(adminUserUpdateInfoDto.getId());
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void adminUpdateState(Long id) {
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
            userLambdaUpdateWrapper.set(User::getState, Constants.USER_ENABLE_STATE);
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
    @Transactional(rollbackFor = CustomizeTransactionException.class)
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
            adminUserExportVo.setName(user.getName());
            adminUserExportVo.setAvatar(user.getAvatar());
            adminUserExportVo.setRole(StringUtils.equals(user.getRole(), Constants.ROLE_ADMIN) ? "管理员" : "用户");
            adminUserExportVo.setState(!Objects.equals(user.getState(), Constants.USER_DISABLE_STATE) ? "启用" : "禁用");
            adminUserExportVo.setCreateTime(user.getCreateTime());
            adminUserExportVo.setUpdateTime(user.getUpdateTime());
            return adminUserExportVo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = CannotCreateTransactionException.class)
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
    @Transactional(rollbackFor = CannotCreateTransactionException.class)
    public void updateName(String newName) {
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getName, newName);
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        LoginUtils.syncLoginUser();
        System.out.println(LoginUtils.getLoginUser());
    }

    @Override
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
    @Transactional(rollbackFor = CannotCreateTransactionException.class)
    public void updateAvatar(MultipartFile file) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String avatarPath = "avatar/" + date;
        String url = MinioUtils.upload(file, avatarPath);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(StringUtils.isNotEmpty(url), User::getAvatar, url);
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        AuthLoginVo loginUser = (AuthLoginVo) SaHolder.getStorage().get(Constants.LOGIN_USER_KEY);
        if (StringUtils.isNotEmpty(loginUser.getAvatar())) {
            MinioUtils.delete(loginUser.getAvatar());
        }
        LoginUtils.syncLoginUser();
    }
}
