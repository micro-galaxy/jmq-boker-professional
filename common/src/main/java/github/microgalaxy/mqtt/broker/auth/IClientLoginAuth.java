package github.microgalaxy.mqtt.broker.auth;

/**
 * mqtt客户端登录认证服务接口
 *
 * @author Microgalaxy （https://github.com/micro-galaxy）
 */
@FunctionalInterface
public interface IClientLoginAuth {

    /**
     * 登录认证
     *
     * @param clientLoginAuth
     * @return
     */
    boolean loginAuth(ClientLoginAuth clientLoginAuth);
}
