package github.microgalaxy.mqtt.broker.server.http.resource;

import github.microgalaxy.mqtt.broker.server.http.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class FileProcessor {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);
    private static Map<String, Byte[]> JAR_FILE_CACHE = new ConcurrentHashMap<>();
    private static boolean JAR_MODE = false;

    public static Byte[] parseJarFile(String classDir, String urlFileName) throws Exception {
        if (StringUtils.isEmpty(urlFileName)) throw new IllegalArgumentException("The file path cannot be empty");
        String dir = HttpUtils.resolverPath(classDir);
        String classFileDir = dir + urlFileName;
        Byte[] bytes = JAR_FILE_CACHE.get(classFileDir);
        if (!ObjectUtils.isEmpty(bytes)) {
            return bytes;
        }
        if(!JAR_MODE){
            ClassPathResource cpr = new ClassPathResource(classFileDir);
            if (cpr.getFile().isDirectory())
                throw new IllegalArgumentException("The file path is not a file: " + urlFileName);
            byte[] fileBytes = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            Byte[] fBytes = new Byte[fileBytes.length];
            IntStream.range(0, fileBytes.length).forEach(i -> fBytes[i] = fileBytes[i]);
            JAR_FILE_CACHE.put(classFileDir, fBytes);
            return JAR_FILE_CACHE.get(classFileDir);
        }

        URL url = new ClassPathResource(HttpUtils.resolverPath(classDir)).getURL();
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory() && jarEntry.getName().contains(dir)) {
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                byte[] fileBytes = FileCopyUtils.copyToByteArray(inputStream);
                Byte[] fBytes = new Byte[fileBytes.length];
                IntStream.range(0, fileBytes.length).forEach(i -> fBytes[i] = fileBytes[i]);
                JAR_FILE_CACHE.put(jarEntry.getName(), fBytes);
            }
        }
        return JAR_FILE_CACHE.get(classFileDir);
    }


    public static List<String> getClassPathFiles(String classPath) throws Exception {
        if (StringUtils.isEmpty(classPath)) throw new IllegalArgumentException("The file path cannot be empty");
        ClassPathResource classPathResource = new ClassPathResource(classPath);
        if (!classPathResource.exists()) {
            return Collections.EMPTY_LIST;
        }
        URL url = classPathResource.getURL();
        if (ResourceUtils.isFileURL(url)) {
            return getUrlFiles(classPathResource.getFile(),classPath);
        }else {
            JAR_MODE = true;
            return getUrlJarFiles(url,classPath);
        }
    }

    private static List<String> getUrlFiles(File rootDir,String classPath) throws Exception {
        List<File> fileList = new ArrayList<>();
        findFile(rootDir,fileList);
        return fileList.stream().filter(Objects::nonNull).map(f -> f.getPath().replace(rootDir.getPath(), ""))
                .map(HttpUtils::pathToWebUrl)
                .collect(Collectors.toList());
    }

    private static List<String> getUrlJarFiles(URL url,String classPath) throws Exception {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> jarFilePaths = new ArrayList<>();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory() && jarEntry.getName().contains(classPath)) {
                jarFilePaths.add(jarEntry.getName().replace(classPath, ""));
            }
        }
        return jarFilePaths;
    }

    private static void findFile(File file, List<File> fileList) {
        if (file.isFile()) {
            fileList.add(file);
            return;
        }
        if (file.listFiles().length <= 0) return;
        Arrays.stream(file.listFiles())
                .forEach(f -> {
                    if (f.isFile()) {
                        fileList.add(f);
                    } else {
                        findFile(f, fileList);
                    }
                });
    }
}
