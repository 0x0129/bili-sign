package xin.lain;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetResponse {
    public static String post(String urlString, String jsonPath, String contentType, String parameters, String cookie) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Cookie", cookie);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(parameters.getBytes(StandardCharsets.UTF_8));
            }

            return string(jsonPath, connection);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static String get(String urlString, String jsonPath, String cookie) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", cookie);
            connection.setDoOutput(true);

            return string(jsonPath, connection);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private static String string(String jsonPath, HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return parseJson(response.toString(), jsonPath);
    }

    public static String parseJson(String json, String jsonPath) {
        JSONObject jsonObject = JSON.parseObject(json);
        String[] paths = jsonPath.split("/");
        Object current = jsonObject;
        for (String path : paths) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(path);
            } else if (current instanceof JSONArray) {
                current = ((JSONArray) current).get(Integer.parseInt(path));
            } else {
                return null;
            }
        }
        return current == null ? null : current.toString();
    }

}
