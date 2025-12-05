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

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    /**
     * Register new user
     */
    public AuthData register(String username, String password, String email) throws Exception {
        var path = "/user";
        var body = new UserData(username, password, email);
        return makeRequest("POST", path, body, null, AuthData.class);
    }

    /**
     * Login existing user
     */
    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var body = new UserData(username, password, null);
        return makeRequest("POST", path, body, null, AuthData.class);
    }

    /**
     * Logout current user
     */
    public void logout(String authToken) throws Exception {
        var path = "/session";
        makeRequest("DELETE", path, null, authToken, null);
    }

    /**
     * Create new game
     */
    public int createGame(String gameName, String authToken) throws Exception {
        var path = "/game";
        var body = Map.of("gameName", gameName);
        record CreateGameResponse(int gameID) {}
        var response = makeRequest("POST", path, body, authToken, CreateGameResponse.class);
        return response.gameID();
    }

    /**
     * List all games
     */
    public Collection<GameData> listGames(String authToken) throws Exception {
        var path = "/game";
        record ListGamesResponse(Collection<GameData> games) {}
        var response = makeRequest("GET", path, null, authToken, ListGamesResponse.class);
        return response.games();
    }

    /**
     * Join a game as a player
     */
    public void joinGame(ChessGame.TeamColor playerColor, int gameID, String authToken) throws Exception {
        var path = "/game";
        var body = Map.of("playerColor", playerColor.toString(), "gameID", gameID);
        makeRequest("PUT", path, body, authToken, null);
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
            throw new Exception(ex.getMessage());
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