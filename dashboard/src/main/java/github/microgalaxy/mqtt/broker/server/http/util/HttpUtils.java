package github.microgalaxy.mqtt.broker.server.http.util;

import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public abstract class HttpUtils {
    private static final String CLASS_PATH = "classpath:";
    private static final String URI_PARAMS_SPLIT = "?";
    private static final String PARAMS_SPLIT = "&";
    private static final String PARAMS_KEY_SPLIT = "=";
    private static final String WEB_SEPARATOR = "/";
    private static final String WEB_JS = ".js";
    private static final String WEB_CSS = ".css";
    private static final String WEB_IMAGE = ".png,.jpeg";
    private static final String WEB_FONT = "ttf,woff";


    public static Map<String, Object> getRequestUriParams(String requestUri) {
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isEmpty(requestUri) || !requestUri.contains(URI_PARAMS_SPLIT)) return params;
        String paramsUri = requestUri.substring(requestUri.indexOf(URI_PARAMS_SPLIT) + URI_PARAMS_SPLIT.length());
        String[] paramSplit = paramsUri.split(PARAMS_SPLIT);
        IntStream.range(0, paramSplit.length).forEach(i -> {
            String ps = paramSplit[i];
            if (StringUtils.isEmpty(ps)) return;
            if (!ps.contains(PARAMS_KEY_SPLIT)) {
                params.put(ps, null);
                return;
            }
            params.put(ps.substring(0, ps.indexOf(PARAMS_KEY_SPLIT)),
                    ps.substring(ps.indexOf(PARAMS_KEY_SPLIT) + PARAMS_KEY_SPLIT.length()));
        });
        return params;
    }

    public static String getRequestBaseUri(String requestUri) {
        if (StringUtils.isEmpty(requestUri) || !requestUri.contains(URI_PARAMS_SPLIT)) return requestUri;
        return requestUri.substring(0, requestUri.indexOf(URI_PARAMS_SPLIT));
    }

    public static void wireResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        DefaultFullHttpResponse rep = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponse.getStatus(),
                !ObjectUtils.isEmpty(httpResponse.getEx()) || ObjectUtils.isEmpty(httpResponse.getBody()) ? Unpooled.buffer() :
                        Unpooled.wrappedBuffer(httpResponse.getBody()), true);
        HttpHeaders headers = httpResponse.getHeader();
        headers.add(HttpHeaderNames.CONTENT_LENGTH, !ObjectUtils.isEmpty(httpResponse.getEx()) || ObjectUtils.isEmpty(httpResponse.getBody()) ?
                0 : httpResponse.getBody().length);
        if (httpRequest.getHeader().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true)
                && ObjectUtils.isEmpty(httpResponse.getEx()))
            headers.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        if (!ObjectUtils.isEmpty(httpResponse.getEx()))
            headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML);
        rep.headers().setAll(headers);
        httpResponse.getChannel().writeAndFlush(rep);
        if (!ObjectUtils.isEmpty(httpResponse.getEx())) httpResponse.getChannel().close();
    }

    public static String resolverPath(String multipleTypePath) {
        String resourceDir = HttpUtils.getClassPathUrl(multipleTypePath);
        return !StringUtils.isEmpty(resourceDir) ? resourceDir : HttpUtils.getAbsoluteUrl(multipleTypePath);
    }

    public static String getClassPathUrl(String classpath) {
        if (StringUtils.isEmpty(classpath) || !classpath.contains(CLASS_PATH)) return Strings.EMPTY;
        return classpath.replace(CLASS_PATH, "").trim();
    }

    public static String getAbsoluteUrl(String path) {
        if (StringUtils.isEmpty(path)) return Strings.EMPTY;
        path = path.trim();
        if (path.startsWith(File.pathSeparator)) return path;
        if (path.contains(CLASS_PATH)) throw new IllegalArgumentException("Path cannot be resolved: " + path);
        return new File("").getAbsolutePath() + File.separator + path;
    }

    public static String pathToWebUrl(String filepath) {
        if (StringUtils.isEmpty(filepath)) return filepath;
        return filepath.replace(File.separator, WEB_SEPARATOR);
    }

    public static String webUrlToPath(String webPath) {
        if (StringUtils.isEmpty(webPath)) return webPath;
        return webPath.replace(WEB_SEPARATOR, File.separator);
    }

    public static AsciiString fileContentType(String fileName) {
        if (fileName.endsWith(WEB_JS)) {
            return AsciiString.of("application/javascript");
        }
        if (fileName.endsWith(WEB_CSS)) {
            return AsciiString.of("text/css");
        }
        if (Arrays.stream(WEB_IMAGE.split(","))
                .anyMatch(fileName::endsWith)) {
            return AsciiString.of("image/*");
        }
        Optional<String> fontStr = Arrays.stream(WEB_FONT.split(","))
                .filter(fileName::endsWith).findFirst();
        return fontStr.map(s -> AsciiString.of("font/" + s))
                .orElseGet(() -> AsciiString.of("text/html;charset=utf-8"));
    }

}
