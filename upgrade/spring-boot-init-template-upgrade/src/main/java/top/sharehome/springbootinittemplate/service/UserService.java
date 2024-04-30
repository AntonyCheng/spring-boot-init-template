package top.sharehome.springbootinittemplate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.model.entity.User;

/**
 * 用户服务接口
 *
 * @author AntonyCheng
 */
public interface UserService extends IService<User> {

    /**
     * 更新账号
     *
     * @param newAccount 新账号
     */
    void updateAccount(String newAccount);

    /**
     * 更新名称
     *
     * @param newName 新名称
     */
    void updateName(String newName);

    /**
     * 更新密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(String oldPassword, String newPassword);

    /**
     * 更新头像
     *
     * @param file 新头像文件
     */
    void updateAvatar(MultipartFile file);
}
