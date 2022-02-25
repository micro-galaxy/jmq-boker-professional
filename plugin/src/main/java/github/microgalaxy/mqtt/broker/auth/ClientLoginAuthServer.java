package github.microgalaxy.mqtt.broker.auth;


import org.springframework.stereotype.Service;

/**
 * @author Microgalaxy
 */
@Service
public class ClientLoginAuthServer implements IClientLoginAuth {
    @Override
    public boolean loginAuth(ClientLoginAuth clientLoginAuth) {
//        throw new RuntimeException("账号密码错误");
        return true;
    }
}
