package top.sharehome.springbootinittemplate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.UserMapper;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthEmailCodeDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRegisterDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRetrievePasswordDto;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.service.AuthService;
import top.sharehome.springbootinittemplate.utils.email.EmailUtils;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.cache.CacheUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.util.Objects;

/**
 * 鉴权认证服务实现类
 * todo 补充添加人员时邮箱必要项
 *
 * @author AntonyCheng
 */
@Service
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Value(value = "${spring.application.name}")
    private String applicationName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(AuthRegisterDto authRegisterDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, authRegisterDto.getAccount());
        // 判断用户账户是否已经存在
        if (userMapper.exists(userLambdaQueryWrapper)) {
            throw new CustomizeReturnException(ReturnCode.USERNAME_ALREADY_EXISTS);
        }
        // 插入新用户数据
        User user = new User()
                .setAccount(authRegisterDto.getAccount())
                .setPassword(authRegisterDto.getPassword())
                .setEmail(authRegisterDto.getEmail());
        int insertResult = userMapper.insert(user);
        if (insertResult == 0) {
            throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
        }
    }

    @Override
    @Transactional(noRollbackFor = CustomizeReturnException.class, rollbackFor = Exception.class)
    public AuthLoginVo login(AuthLoginDto authLoginDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount, authLoginDto.getAccount());
        User userInDatabase = userMapper.selectOne(userLambdaQueryWrapper);
        // 判断数据库中是否存在该登录用户的数据
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_DOES_NOT_EXIST);
        }
        // 判断该用户是否被禁用
        if (Objects.equals(userInDatabase.getState(), Constants.USER_DISABLE_STATE)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_BANNED);
        }
        // 判断用户密码是否正确，在连续错误输入的情况下自动封禁
        if (!Objects.equals(userInDatabase.getPassword(), authLoginDto.getPassword())) {
            LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            // 连续5次输入错误密码后封禁账号
            Integer maxLoginNum = 5;
            if (userInDatabase.getLoginNum() < maxLoginNum) {
                userLambdaUpdateWrapper
                        .set(User::getLoginNum, userInDatabase.getLoginNum() + 1)
                        .eq(User::getAccount, userInDatabase.getAccount());
                int updateResult = userMapper.update(userLambdaUpdateWrapper);
                if (updateResult == 0) {
                    throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
                }
                if (!Objects.equals(userInDatabase.getLoginNum(), maxLoginNum - 1)) {
                    throw new CustomizeReturnException(ReturnCode.PASSWORD_VERIFICATION_FAILED, "第" + (userInDatabase.getLoginNum() + 1) + "次错误");
                } else {
                    throw new CustomizeReturnException(ReturnCode.PASSWORD_VERIFICATION_FAILED, "请找回密码，否则封禁账号");
                }
            } else {
                userLambdaUpdateWrapper
                        .set(User::getState, Constants.USER_DISABLE_STATE)
                        .set(User::getLoginNum, 0)
                        .eq(User::getAccount, userInDatabase.getAccount());
                int updateResult = userMapper.update(userLambdaUpdateWrapper);
                if (updateResult == 0) {
                    throw new CustomizeReturnException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
                }
                throw new CustomizeReturnException(ReturnCode.PASSWORD_VERIFICATION_FAILED, "多次出错，账号已被封禁");
            }
        }
        AuthLoginVo authLoginVo = new AuthLoginVo();
        BeanUtils.copyProperties(userInDatabase, authLoginVo);
        return authLoginVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkEmailCode(AuthRetrievePasswordDto authRetrievePasswordDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .eq(User::getAccount, authRetrievePasswordDto.getAccount())
                .eq(User::getEmail, authRetrievePasswordDto.getEmail());
        User userInDatabase = userMapper.selectOne(userLambdaQueryWrapper);
        // 判断数据库中是否存在该登录用户的数据
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.ACCOUNT_AND_EMAIL_DO_NOT_MATCH);
        }
        // 判断该用户是否被禁用
        if (Objects.equals(userInDatabase.getState(), Constants.USER_DISABLE_STATE)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_BANNED);
        }
        String emailKey = KeyPrefixConstants.EMAIL_RETRIEVE_PASSWORD_PREFIX + userInDatabase.getId();
        String code = CacheUtils.getString(emailKey);
        // 判断邮箱验证码是否过期
        if (Objects.nonNull(code)) {
            if (Objects.equals(code, authRetrievePasswordDto.getPasswordCode())) {
                LoginUtils.logout(userInDatabase.getId());
                LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                userLambdaUpdateWrapper
                        .set(User::getPassword, authRetrievePasswordDto.getNewPassword())
                        .set(User::getLoginNum, 0)
                        .eq(User::getId, userInDatabase.getId());
                userMapper.update(userLambdaUpdateWrapper);
                CacheUtils.deleteString(emailKey);
            } else {
                throw new CustomizeReturnException(ReturnCode.CAPTCHA_IS_INCORRECT);
            }
        } else {
            throw new CustomizeReturnException(ReturnCode.CAPTCHA_HAS_EXPIRED);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public void getEmailCode(AuthEmailCodeDto authEmailCodeDto) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .eq(User::getAccount, authEmailCodeDto.getAccount())
                .eq(User::getEmail, authEmailCodeDto.getEmail());
        User userInDatabase = userMapper.selectOne(userLambdaQueryWrapper);
        // 判断数据库中是否存在该登录用户的数据
        if (Objects.isNull(userInDatabase)) {
            throw new CustomizeReturnException(ReturnCode.ACCOUNT_AND_EMAIL_DO_NOT_MATCH);
        }
        // 判断该用户是否被禁用
        if (Objects.equals(userInDatabase.getState(), Constants.USER_DISABLE_STATE)) {
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_BANNED);
        }
        String emailKey = KeyPrefixConstants.EMAIL_RETRIEVE_PASSWORD_PREFIX + userInDatabase.getId();
        // 随机六位数字验证码
        String code = RandomStringUtils.randomNumeric(6);
        Long expired = CacheUtils.getStringExpired(emailKey);
        // 防止用户快速多次请求邮箱验证码，只要验证码没有过期，就不能二次请求
        if (Objects.equals(expired, 0L)) {
            String subject = "找回密码";
            String emailContent = "[" + applicationName + "]-找回密码验证码为 <b>" + code + "</b> ,五分钟后失效。";
            EmailUtils.sendWithHtml(userInDatabase.getEmail(), subject, emailContent);
            CacheUtils.putString(emailKey, code, 300);
        } else {
            throw new CustomizeReturnException(ReturnCode.TOO_MANY_REQUESTS, "请在" + expired + "秒后重试");
        }
    }

}