package xin.lain;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    public static String configPath = "config.yaml";
    public static String cookies;
    public static Long telegram;
    public static String token;
    public static String username;
    public static String csrf;

    public static void config() {
        Path path = Paths.get(configPath);
        if (!Files.exists(path)) {
            Map<String, Object> defaultConfig = new HashMap<>();

            defaultConfig.put("cookie", "cookies");
            defaultConfig.put("telegram", "telegram");
            defaultConfig.put("token", "token");
            defaultConfig.put("username", "username");

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (Map.Entry<String, Object> entry : defaultConfig.entrySet()) {
                    writer.write(entry.getKey() + ": " + entry.getValue());
                    writer.newLine();
                }
                System.out.println("未找到 " + configPath + " 已为您自动创建");
            } catch (Exception e) {
                System.err.println("读取 " + configPath + " 时出现问题");
                return;
            }
        }
        try (InputStream inputStream = Files.newInputStream(path.toFile().toPath())) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(inputStream);
            System.out.println("成功加载 " + configPath);

            cookies = (String) yamlMap.get("cookies");
            telegram = (Long) yamlMap.get("telegram");
            token = (String) yamlMap.get("token");
            username = (String) yamlMap.get("username");

            csrf = extractValue(cookies, "bili_jct");

        } catch (Exception e) {
            System.err.println("读取 " + configPath + " 时出现问题");
        }
    }

    public static String extractValue(String data, String key) {
        String pattern = key + "=([^;]*);";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(data);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
