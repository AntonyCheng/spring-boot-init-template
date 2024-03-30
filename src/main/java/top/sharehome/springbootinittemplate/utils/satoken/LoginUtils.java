package top.sharehome.springbootinittemplate.utils.satoken;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.entity.User;
import top.sharehome.springbootinittemplate.model.vo.auth.AuthLoginVo;
import top.sharehome.springbootinittemplate.service.AuthService;

import java.util.Objects;

/**
 * 登录鉴权工具类
 *
 * @author AntonyCheng
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUtils {

    private static final AuthService authService = SpringContextHolder.getBean(AuthService.class);

    /**
     * 用户登录
     *
     * @param loginUser 登录用户信息
     */
    public static void login(AuthLoginVo loginUser) {
        if (!StpUtil.isLogin()) {
            // 存入一份到缓存中
            SaHolder.getStorage()
                    .set(Constants.LOGIN_USER_KEY, loginUser);
            StpUtil.login(loginUser.getId());
            StpUtil.getSession().set(Constants.LOGIN_USER_KEY, loginUser);
        } else {
            // 如果重复登录，就需要验证当前登录账号和将要登录账号是否相同，不相同则挤掉原账号，确保一个浏览器会话只存有一个账户信息
            if (!Objects.equals(Long.parseLong((String) StpUtil.getLoginId()), loginUser.getId())) {
                StpUtil.logout();
                // 存入一份到缓存中
                SaHolder.getStorage()
                        .set(Constants.LOGIN_USER_KEY, loginUser);
                StpUtil.login(loginUser.getId());
                StpUtil.getSession().set(Constants.LOGIN_USER_KEY, loginUser);
            }
        }
    }

    /**
     * 同步缓存中的用户登录信息
     */
    public static void syncLoginUser() {
        Long loginUserId = Long.valueOf((String) StpUtil.getLoginId());
        User userInDatabase = authService.getById(loginUserId);
        if (Objects.isNull(userInDatabase)) {
            StpUtil.logout(loginUserId);
            throw new CustomizeReturnException(ReturnCode.ACCESS_UNAUTHORIZED);
        }
        if (Objects.equals(userInDatabase.getState(),Constants.USER_DISABLE_STATE)){
            throw new CustomizeReturnException(ReturnCode.USER_ACCOUNT_BANNED);
        }
        AuthLoginVo loginUser = new AuthLoginVo();
        BeanUtils.copyProperties(userInDatabase, loginUser);
        SaHolder.getStorage()
                .set(Constants.LOGIN_USER_KEY, loginUser);
        StpUtil.getSession().set(Constants.LOGIN_USER_KEY, loginUser);
    }

    /**
     * 从缓存中获取登录用户信息
     */
    public static AuthLoginVo getLoginUser() {
        AuthLoginVo loginUser = (AuthLoginVo) SaHolder.getStorage().get(Constants.LOGIN_USER_KEY);
        if (Objects.nonNull(loginUser)) {
            return loginUser;
        }
        loginUser = (AuthLoginVo) StpUtil.getSession().get(Constants.LOGIN_USER_KEY);
        SaHolder.getStorage().set(Constants.LOGIN_USER_KEY, loginUser);
        return loginUser;
    }

    /**
     * 从缓存中获取登录用户ID
     */
    public static Long getLoginUserId() {
        return getLoginUser().getId();
    }

    /**
     * 从缓存中获取登录用户账户
     */
    public static String getLoginUserAccount() {
        return getLoginUser().getAccount();
    }

    /**
     * 登录用户账户角色是否为管理员
     */
    public static boolean isAdmin() {
        return Constants.ROLE_ADMIN.equals(getLoginUser().getRole());
    }

    /**
     * 根据用户LoginId指定用户退出
     */
    public static void logout(Object loginId) {
        StpUtil.logout(loginId);
    }

    /**
     * 用户退出
     */
    public static void logout() {
        StpUtil.logout();
    }

}