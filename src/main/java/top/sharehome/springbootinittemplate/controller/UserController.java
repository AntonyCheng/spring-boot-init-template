package top.sharehome.springbootinittemplate.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.model.dto.user.UserLoginDto;
import top.sharehome.springbootinittemplate.model.dto.user.UserRegisterDto;
import top.sharehome.springbootinittemplate.model.vo.user.UserLoginVo;
import top.sharehome.springbootinittemplate.service.UserService;

import javax.annotation.Resource;

import static top.sharehome.springbootinittemplate.common.base.Constants.USER_ROLE_KEY;

/**
 * 用户控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/user")
@SaCheckLogin
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @SaIgnore
    public R<String> register(@RequestBody @Validated(PostGroup.class) UserRegisterDto userRegisterDto) {
        if (!StringUtils.equals(userRegisterDto.getPassword(), userRegisterDto.getCheckPassword())) {
            throw new CustomizeReturnException(ReturnCode.PASSWORD_AND_SECONDARY_PASSWORD_NOT_SAME);
        }
        userService.register(userRegisterDto);
        return R.ok("注册成功");
    }

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    @SaIgnore
    public R<UserLoginVo> login(@RequestBody @Validated(PostGroup.class) UserLoginDto userLoginDto) {
        UserLoginVo loginUser = userService.login(userLoginDto);
        if (!StpUtil.isLogin()) {
            StpUtil.login(loginUser.getId());
            StpUtil.getSession().set(USER_ROLE_KEY, loginUser.getRole());
        } else {
            // 如果重复登陆，就需要验证当前登陆账号和将要登陆账号是否相同，不相同则挤掉原账号
            if (ObjectUtils.notEqual(StpUtil.getLoginId(), loginUser.getId())) {
                StpUtil.logout();
                StpUtil.login(loginUser.getId());
                StpUtil.getSession().set(USER_ROLE_KEY, loginUser.getRole());
            }
        }
        return R.ok(loginUser);
    }

    /**
     * 用户退出
     */
    @GetMapping("/logout")
    public R<String> logout() {
        StpUtil.logout();
        return R.ok("logout");
    }
}
