package cn.javabook.chapter02.annotate.base;

import cn.javabook.chapter02.annotate.orm.User;
import javax.annotation.Resource;

/**
 * 用户接口控制器
 *
 */
public class UserController {
    @Resource
    private UserService userService;

    // 用户登录接口
    public void login(User user) {
        // 直接调用UserServiceImpl实现类的各种对象和方法
        // TODO
    }

    // 用户登出接口
    public void logout(String userid) {
        // 直接调用UserServiceImpl实现类的各种对象和方法，而且不用重复实例化
        // TODO
    }
}
