package github.microgalaxy.mqtt.broker.user;

/**
 * dashboard 用户管理
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public interface IDashboardUser {

    /**
     * dashboard 用户登录认证
     *
     * @param user
     * @param <T>
     * @return
     */
    <T> T userAuth(User user);

    /**
     * 修改用户密码
     *
     * @param user
     * @param <T>
     * @return
     */
    <T> T userRePassword(User user);

    /**
     * 新增用户
     *
     * @param user
     * @param <T>
     * @return
     */
    <T> T addUser(User user);

    /**
     * 删除用户
     *
     * @param user
     * @param <T>
     * @return
     */
    <T> T deleteUser(User user);
}
