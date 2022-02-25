package github.microgalaxy.mqtt.broker.user;

import github.microgalaxy.mqtt.broker.server.http.dispatcher.exception.BusinessException;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import github.microgalaxy.mqtt.broker.server.http.util.EncryptUtil;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Service
public class DashboardUserImpl implements IDashboardUser {
    private final String rsaPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC5fAFdurMMKtqL\n" +
            "MDZJR9EH/WPj3cX+2NCcabu+Qq3wIzgkB1HHR2KnxmyeHcBpAvV14BAITWBLFiFP\n" +
            "lJ1wFsSeUrv4/rqt5GQ+a7jw/E7GboaUVqkGJHDg0vUDMZgBP21MHXLbFG9yAM64\n" +
            "sxMKlSyF74qtRixMNe6DTZaRBm/+sLD+nkHNQLrqe65g94ztlG7pU1p6SVzfcShR\n" +
            "z4+hL5mGq7qPThgei9fxV9EKngJQbj/7YOd4yn+RAuMHP5gnaYE58qvtuQrZQuRF\n" +
            "5UTvuf5byZbZalHtFM4zU/9IucHgGRRRNgaQSUaJ/ed1I0U5vOEHmB83KLawllrN\n" +
            "vk2ZVRrfAgMBAAECggEBALdHlzn8n6toNObQTvP+KMiSUz/4BgsaRpgnykHoNWNS\n" +
            "CclgCii1mwBZiz55+4RITi+ZpX3upzXKukOegCsf9EzVR7R0UBY/1eXwH6DQTy64\n" +
            "S/cL4pyVKYyeJ0humtqwx9ksSC++c5jcv2e2Wxxb5tC5r/gjnOX5bKMF6b+H8uZg\n" +
            "FAe6UNxluUny/kU9mtMiMHAUSW9eFnoLyLHCt63rzBXYt5ZVtggO4Vh7gz97GNJZ\n" +
            "mmZDU+PulK0HeU4cko50MqAlbQjPfoYrtGidde8Z0CJ5cEhzMN4xnfin4ZPjuuJq\n" +
            "X01+LKHZmA04JsWY+T6xvhd6zHUtPkBJauOxOkldZ8ECgYEA9QxaUTO8mSWrfppm\n" +
            "VyiLXrXPp6GZqAKUqwJ5OX8rGO5VqCe9aeCqDgObphdxS74NgyR0R0AitQYg+kcP\n" +
            "LEcaWSYNxvrVCyD16ucMJ4h46tmemsroTQLUIN1eWEF6m1A5bEt3B7Ey7YS8nmXg\n" +
            "Lq26q1Woc8TRXGveMvf+V/qsjrECgYEAwcYrlf91Wy2cZCV90MNU3wPD3FMWKmXs\n" +
            "xg7hCDKHM8tKHqW4RjPuo5PSDbti0rZz8ngKivzdnYJqBhO6bJuBTWT1M1LXERVn\n" +
            "sf+f2tZlF7q1PXdnVHyxOzrpmixoyE9KH94ZBlnLCDrqKEfldSfc33CUJ2MHHPz0\n" +
            "Y7adOA/9Ro8CgYAKcIRIgVO/koND2YASpn+JgWVfVtsc9T8wKeLQOEK1xTGRAGBp\n" +
            "alDUWAMBiG4jM+2PcLYjcvAHlkNBZlWXJVBSC/T6TSVWXAkeAMnbxUyhdLP1FD/h\n" +
            "SAWRgNzMFb4Lb9eCbZScl0f8JnOCrARo+K3DLtsGU5Q3w8CrmzUSunMvgQKBgAID\n" +
            "diZ73hNReQONZC5MKVCb8GgUsPFF190DAJGePMtr/FwvKqgIGB25BoDABrnbzX6i\n" +
            "grGJCSB+320qvVAtmX1dZDLhVHzH9SjjWBzlB23kscduuzMkjI7qAM1qUrhqIoIM\n" +
            "Q8ROII74VcCPZrH99QyZVeDG5acGdd+nYSDZAa+/AoGAJSWNBMTi8TNQInvyK8cu\n" +
            "SalD9fSq1YhecdHkCkcWqm7rISj0Xy6iWusalm/PLu+O5Ml6cpGI+CTVathWLngh\n" +
            "ztrbPkpMa6fNitR9a4mKfWnv26IBxlP+NYNX5LxbhJGCT3GrYqHbjfNq0a7E+CEh\n" +
            "Vi0VE4kECvjX33UXib4y9zI=\n";

    @Resource
    private IgniteCache<String, User> dashboardUser;

    @Override
    public <T> T userAuth(User user) {
        User userInfo = dashboardUser.get(user.getUsername());
        if (ObjectUtils.isEmpty(userInfo))
            throw new BusinessException(HttpURLConnection.HTTP_UNAUTHORIZED, "Username Not Found");
        String passMd5 = DigestUtils.md5DigestAsHex((userInfo.getUsername() + userInfo.getPassword()).getBytes(StandardCharsets.UTF_8));
        if (!Objects.equals(passMd5, user.getPassword()))
            throw new BusinessException(HttpURLConnection.HTTP_UNAUTHORIZED, "Password Error");
        return (T) ResponseWrapper.ok();
    }

    @Override
    public <T> T userRePassword(User user) {
        User userInfo = dashboardUser.get(user.getUsername());
        if (ObjectUtils.isEmpty(userInfo))
            throw new BusinessException("Username Not Found");
        String passMd5 = DigestUtils.md5DigestAsHex((userInfo.getUsername() + userInfo.getPassword()).getBytes(StandardCharsets.UTF_8));
        if (!Objects.equals(passMd5, user.getPassword()))
            throw new BusinessException("Password Error");
        String password = EncryptUtil.decrypt(user.getNewPassword(), rsaPrivateKey);
        userInfo.setPassword(password);
        dashboardUser.put(userInfo.getUsername(), userInfo);
        return (T) ResponseWrapper.ok();
    }

    @Override
    public <T> T addUser(User user) {
        return null;
    }

    @Override
    public <T> T deleteUser(User user) {
        return null;
    }
}
