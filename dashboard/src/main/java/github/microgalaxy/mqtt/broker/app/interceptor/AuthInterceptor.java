package github.microgalaxy.mqtt.broker.app.interceptor;

import github.microgalaxy.mqtt.broker.server.http.dispatcher.HandlerInvoke;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.interceptor.IHandlerInterceptor;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import github.microgalaxy.mqtt.broker.user.User;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class AuthInterceptor implements IHandlerInterceptor {
    @Resource
    private IgniteCache<String, User> dashboardUser;

    @Override
    public boolean preHandle(HttpRequest httpRequest, HttpResponse httpResponse, HandlerInvoke handler) {
        NeedAuth needAuth = handler.getMethod().getAnnotation(NeedAuth.class);
        if (ObjectUtils.isEmpty(needAuth)) {
            return true;
        }
        String authorization = httpRequest.getHeader().get("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            httpResponse.writeJson(ResponseWrapper.error(HttpResponseStatus.UNAUTHORIZED.code(), HttpResponseStatus.UNAUTHORIZED.reasonPhrase()));
            return false;
        }
        List<String> split = Arrays.asList(authorization.split(" "));
        User user = dashboardUser.get(split.get(0));
        if (ObjectUtils.isEmpty(user)) {
            httpResponse.writeJson(ResponseWrapper.error(HttpResponseStatus.UNAUTHORIZED.code(), HttpResponseStatus.UNAUTHORIZED.reasonPhrase()));
            return false;
        }
        String passMd5 = DigestUtils.md5DigestAsHex((user.getUsername() + user.getPassword()).getBytes(StandardCharsets.UTF_8));
        boolean match = Objects.equals(passMd5, split.get(1));
        if (!match) httpResponse.writeJson(ResponseWrapper.error(HttpResponseStatus.UNAUTHORIZED.code(), HttpResponseStatus.UNAUTHORIZED.reasonPhrase()));
        return match;
    }
}
