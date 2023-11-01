package controller.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class WebClient {
    private final String contextPath;
    private final String requestUrl;
    private final Optional<String> requestMethod;
    private Optional<String> content;
    private String response;
    private int responseCode;

    private WebClient(Builder builder) {
        this.contextPath = builder.contextPath;
        this.requestUrl = builder.requestUrl;
        this.requestMethod = Optional.ofNullable(builder.requestMethod);
        this.content = Optional.ofNullable(builder.content);
    }

    public void sendRequest() {
        try {
            HttpURLConnection connection;
            connection = (HttpURLConnection) new URL(String.format("%s%s", contextPath, requestUrl))
                    .openConnection();
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestMethod(requestMethod.get());
            if (content.isPresent()) {
                connection.setDoOutput(true);
                writeContent(connection, content.get());
            }

            responseCode = connection.getResponseCode();

            if (responseCode < 299) {
                response = readResponse(connection.getInputStream());
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    private String readResponse(InputStream inputStream) throws IOException {
        StringBuilder concat = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                concat.append(data);
            }
        }
        return concat.toString();
    }

    private void writeContent(HttpURLConnection httpsURLConnection, String content) throws IOException {
        try (OutputStream outputStream = httpsURLConnection.getOutputStream()) {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        }
    }

    public static class Builder {
        private String contextPath;
        private String requestUrl;
        private String requestMethod;
        private String content;

        public Builder contextPath(String url) {
            this.contextPath = url;
            return this;
        }

        public Builder requestUrl(String getRequestUrl) {
            this.requestUrl = getRequestUrl;
            return this;
        }

        public Builder requestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public WebClient build() {
            return new WebClient(this);
        }
    }
}
