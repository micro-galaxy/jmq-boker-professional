package github.microgalaxy.mqtt.broker.server.http.dispatcher.exception;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String msg) {
        super(msg);
        code = 500;
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
