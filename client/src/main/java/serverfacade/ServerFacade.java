package serverfacade;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    /**
     * Clear database
     */
    public void clear() throws Exception {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws Exception {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }

            if (request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String reqData = gson.toJson(request);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }

            http.connect();
            throwIfNotSuccessful(http);

            if (responseClass != null) {
                try (InputStream respBody = http.getInputStream()) {
                    InputStreamReader reader = new InputStreamReader(respBody);
                    return gson.fromJson(reader, responseClass);
                }
            }
            return null;
        } catch (Exception ex) {
            throw new Exception("Error: " + ex.getMessage());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respBody = http.getErrorStream()) {
                if (respBody != null) {
                    InputStreamReader reader = new InputStreamReader(respBody);
                    var errorResponse = gson.fromJson(reader, Map.class);
                    String message = (String) errorResponse.get("message");
                    throw new Exception(message != null ? message : "Request failed with status: " + status);
                }
            }
            throw new Exception("Request failed with status: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status >= 200 && status < 300;
    }
}