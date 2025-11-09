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
import top.sharehome.springbootinittemplate.model.common.PageModel;
import top.sharehome.springbootinittemplate.model.dto.user.*;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.model.vo.user.UserExportVo;
import top.sharehome.springbootinittemplate.model.vo.user.UserPageVo;
import top.sharehome.springbootinittemplate.service.UserService;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;
import top.sharehome.springbootinittemplate.utils.oss.local.OssLocalUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.io.IOException;
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
    public Page<UserPageVo> pageUser(UserPageDto userPageDto, PageModel pageModel) {
        Page<User> page = pageModel.build();
        Page<UserPageVo> res = pageModel.build();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 构造查询条件
        userLambdaQueryWrapper
                .eq(StringUtils.isNotBlank(userPageDto.getRole()), User::getRole, userPageDto.getRole())
                .eq(Objects.nonNull(userPageDto.getState()), User::getState, userPageDto.getState())
                .like(StringUtils.isNotBlank(userPageDto.getAccount()), User::getAccount, userPageDto.getAccount())
                .like(StringUtils.isNotBlank(userPageDto.getName()), User::getName, userPageDto.getName())
                .like(StringUtils.isNotBlank(userPageDto.getEmail()), User::getEmail, userPageDto.getEmail());
        // 构造查询排序（默认按照创建时间升序排序）
        userLambdaQueryWrapper.orderByAsc(User::getCreateTime);

        // 分页查询
        userMapper.selectPage(page, userLambdaQueryWrapper);

        // 返回值处理（Entity ==> Vo）
        List<UserPageVo> newRecords = page.getRecords().stream().map(user -> {
            File avatarFile = Objects.equals(user.getAvatarId(), Constants.NULL_ID) ? null : fileMapper.selectById(user.getAvatarId());
            return new UserPageVo()
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
    public void addUser(UserAddDto userAddDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, userAddDto.getAccount());
        // 禁止添加已存在同名账号
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 插入数据库
        User user = new User()
                .setAvatarId(Constants.NULL_ID)
                .setAccount(userAddDto.getAccount())
                .setPassword(userAddDto.getPassword())
                .setEmail(userAddDto.getEmail())
                .setName(userAddDto.getName());
        int insertResult = userMapper.insert(user);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
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
        //if (!Objects.equals(userInDatabase.getAvatarId(), Constants.NULL_ID)) {
        //    MinioUtils.delete(userInDatabase.getAvatarId());
        //}
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInfo(UserUpdateInfoDto userUpdateInfoDto) {
        User userInDatabase = userMapper.selectById(userUpdateInfoDto.getId());
        // 无法对非存在或管理员账号进行操作
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        if (StringUtils.equals(userInDatabase.getRole(), Constants.ROLE_ADMIN)) {
            throw new CustomizeReturnException(ReturnCode.ABNORMAL_USER_OPERATION, "无法对管理员进行操作");
        }
        // 禁止添加非自身的已存在同名账号
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, userUpdateInfoDto.getAccount());
        userLambdaQueryWrapper.ne(User::getId, userUpdateInfoDto.getId());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 数据只有发生更新之后才可以进行数据库操作
        if (Objects.equals(userUpdateInfoDto.getAccount(), userInDatabase.getAccount())
                && Objects.equals(userUpdateInfoDto.getName(), userInDatabase.getName())
                && Objects.equals(userUpdateInfoDto.getEmail(), userInDatabase.getEmail())) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS, "信息未发生更改");
        }
        User user = new User()
                .setId(userUpdateInfoDto.getId())
                .setAccount(userUpdateInfoDto.getAccount())
                .setEmail(userUpdateInfoDto.getEmail())
                .setName(userUpdateInfoDto.getName());
        int updateResult = userMapper.updateById(user);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(userUpdateInfoDto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateState(UserUpdateStateDto userUpdateStateDto) {
        Long id = userUpdateStateDto.getId();
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
    public void resetPassword(UserResetPasswordDto userResetPasswordDto) {
        User userInDatabase = userMapper.selectById(userResetPasswordDto.getId());
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
                .set(User::getPassword, userResetPasswordDto.getNewPassword())
                .eq(User::getId, userResetPasswordDto.getId());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        // 用户信息发生修改之后需要重新登陆
        LoginUtils.logout(userInDatabase.getId());
    }

    @Override
    public List<UserExportVo> exportExcelList() {
        List<User> usersInDatabase = userMapper.selectList(null);
        return usersInDatabase.stream().map(user -> {
            UserExportVo userExportVo = new UserExportVo();
            userExportVo.setId(user.getId());
            userExportVo.setAccount(user.getAccount());
            userExportVo.setEmail(user.getEmail());
            userExportVo.setName(user.getName());
            File avatarFile = Objects.equals(user.getAvatarId(), Constants.NULL_ID) ? null : fileMapper.selectById(user.getAvatarId());
            userExportVo.setAvatar(Objects.isNull(avatarFile) ? null : avatarFile.getUrl());
            userExportVo.setRole(StringUtils.equals(user.getRole(), Constants.ROLE_ADMIN) ? "管理员" : "用户");
            userExportVo.setState(!Objects.equals(user.getState(), Constants.USER_DISABLE_STATE) ? "启用" : "禁用");
            userExportVo.setCreateTime(user.getCreateTime());
            userExportVo.setUpdateTime(user.getUpdateTime());
            return userExportVo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccount(String newAccount) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, newAccount);
        userLambdaQueryWrapper.ne(User::getId, LoginUtils.getLoginUserIdOrThrow());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getAccount, newAccount);
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserIdOrThrow());
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
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserIdOrThrow());
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
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserIdOrThrow());
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
        Long userId = LoginUtils.getLoginUserIdOrThrow();
        userLambdaQueryWrapper
                .eq(User::getId, userId)
                .eq(User::getPassword, oldPassword)
                .last("limit 1");
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
        File avatarFile = OssLocalUtils.upload(file, avatarPath);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.set(User::getAvatarId, avatarFile.getId());
        userLambdaUpdateWrapper.eq(User::getId, LoginUtils.getLoginUserIdOrThrow());
        int updateResult = userMapper.update(userLambdaUpdateWrapper);
        if (updateResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
        AuthLoginVo loginUser = (AuthLoginVo) SaHolder.getStorage().get(Constants.LOGIN_USER_KEY);
        if (fileMapper.exists(new LambdaQueryWrapper<File>().eq(File::getId, loginUser.getAvatarId()))) {
            OssLocalUtils.delete(loginUser.getAvatarId());
        }
        LoginUtils.syncLoginUser();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importUser(MultipartFile file) {
        try {
            List<UserImportDto> userImportDtoList = ExcelUtils.importStreamSyncAndClose(file.getInputStream(), UserImportDto.class);
            List<User> list = userImportDtoList.stream().map(user -> {
                if (StringUtils.isBlank(user.getAccount())) {
                    throw new CustomizeReturnException(ReturnCode.EXCEL_FILE_ERROR, "存在账号[账号：" + user.getAccount() + " 邮箱：" + user.getEmail() + " 名称：" + user.getName() + "]不完整");
                }
                if (!user.getAccount().matches("^[A-Za-z0-9]{2,16}$")) {
                    throw new CustomizeReturnException(ReturnCode.EXCEL_FILE_ERROR, "存在账号[" + user.getAccount() + "]格式错误，要求介于2-16位且不包含特殊字符");
                }
                if (StringUtils.isBlank(user.getEmail())) {
                    throw new CustomizeReturnException(ReturnCode.EXCEL_FILE_ERROR, "存在邮箱[账号：" + user.getAccount() + " 邮箱：" + user.getEmail() + " 名称：" + user.getName() + "]不完整");
                }
                if (!Constants.REGEX_EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                    throw new CustomizeReturnException(ReturnCode.EXCEL_FILE_ERROR, "存在邮箱[" + user.getEmail() + "]格式错误");
                }
                if (StringUtils.isBlank(user.getName())) {
                    throw new CustomizeReturnException(ReturnCode.EXCEL_FILE_ERROR, "存在名称[账号：" + user.getAccount() + " 邮箱：" + user.getEmail() + " 名称：" + user.getName() + "]不完整");
                }
                if (!user.getName().matches("^.{1,16}$")) {
                    throw new CustomizeReturnException(ReturnCode.EXCEL_FILE_ERROR, "存在名称[" + user.getName() + "]格式错误，要求介于1-16位");
                }
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper
                        .eq(User::getAccount, user.getAccount());
                if (userMapper.exists(userLambdaQueryWrapper)) {
                    throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS, "账号或工号已经存在[账号：" + user.getAccount() + " 名称：" + user.getName() + "]");
                }
                return new User()
                        .setAvatarId(Constants.NULL_ID)
                        .setAccount(user.getAccount())
                        .setPassword("123456")
                        .setName(user.getName())
                        .setEmail(user.getEmail());
            }).distinct().toList();
            if (Objects.equals(list.size(), 0)) {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "用户信息表数据为空");
            }
            if (!Objects.equals(userImportDtoList.size(), list.size())) {
                throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS, "存在" + (userImportDtoList.size() - list.size()) + "条重复数据");
            }
            userMapper.insert(list, 2000);
        } catch (IOException e) {
            throw new CustomizeReturnException(ReturnCode.EXCEL_FILE_ERROR, "读取用户信息文件出错");
        }
    }
}
