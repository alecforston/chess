package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JsonMapper;
import model.*;
import service.*;
import java.lang.reflect.Type;
import java.util.*;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JsonMapper() {
                @Override
                public String toJsonString(Object obj, Type type) {
                    return gson.toJson(obj, type);
                }

                @Override
                public <T> T fromJsonString(String json, Type targetType) {
                    return gson.fromJson(json, targetType);
                }
            });
        });

        // Delete the endpoint
        javalin.delete("/db", this::handleClear);

        // The 3 user endpoints
        javalin.post("/user", this::handleRegister);
        javalin.post("/session", this::handleLogin);
        javalin.delete("/session", this::handleLogout);

        // 3 game endpoints
        javalin.get("/game", this::handleListGames);
        javalin.post("/game", this::handleCreateGame);
        javalin.put("/game", this::handleJoinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void handleClear(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private void handleRegister(Context ctx) {
        try {
            UserData user = gson.fromJson(ctx.body(), UserData.class);
            AuthData auth = userService.register(user);
            ctx.status(200);
            ctx.json(auth);
        } catch (DataAccessException e) {
            handleDataAccessException(e, ctx);
        }
    }

    private void handleLogin(Context ctx) {
        try {
            UserData user = gson.fromJson(ctx.body(), UserData.class);
            AuthData auth = userService.login(user);
            ctx.status(200);
            ctx.json(auth);
        } catch (DataAccessException e) {
            handleDataAccessException(e, ctx);
        }
    }

    private void handleLogout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                throw new DataAccessException("Error: unauthorized");
            }
            userService.logout(authToken);
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e) {
            handleDataAccessException(e, ctx);
        }
    }

    private void handleListGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                throw new DataAccessException("Error: unauthorized");
            }
            Collection<GameData> games = gameService.listGames(authToken);
            ctx.status(200);
            ctx.json(Map.of("games", games));
        } catch (DataAccessException e) {
            handleDataAccessException(e, ctx);
        }
    }

    private void handleCreateGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                throw new DataAccessException("Error: unauthorized");
            }

            Map<String, String> requestBody = gson.fromJson(ctx.body(), Map.class);
            String gameName = requestBody.get("gameName");

            int gameID = gameService.createGame(gameName, authToken);
            ctx.status(200);
            ctx.json(Map.of("gameID", gameID));
        } catch (DataAccessException e) {
            handleDataAccessException(e, ctx);
        }
    }

    private void handleJoinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                throw new DataAccessException("Error: unauthorized");
            }

            Map<String, Object> requestBody = gson.fromJson(ctx.body(), Map.class);

            if (!requestBody.containsKey("playerColor")) {
                throw new DataAccessException("Error: bad request");
            }

            String playerColorStr = (String) requestBody.get("playerColor");
            if (playerColorStr == null || playerColorStr.isEmpty()) {
                throw new DataAccessException("Error: bad request");
            }
            if (!playerColorStr.equals("WHITE") && !playerColorStr.equals("BLACK")) {
                throw new DataAccessException("Error: bad request");
            }

            ChessGame.TeamColor playerColor = ChessGame.TeamColor.valueOf(playerColorStr);

            Object bodyRequest = requestBody.get("gameID");
            if (bodyRequest == null) {
                throw new DataAccessException("Error: bad request");
            }

            int gameID;
            if (bodyRequest instanceof Double) {
                gameID = ((Double) bodyRequest).intValue();
            } else if (bodyRequest instanceof Integer) {
                gameID = (Integer) bodyRequest;
            } else {
                throw new DataAccessException("Error: bad request");
            }

            gameService.joinGame(playerColor, gameID, authToken);
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e) {
            handleDataAccessException(e, ctx);
        }
    }

    private void handleDataAccessException(DataAccessException e, Context ctx) {
        String message = e.getMessage();

        if (message.contains("unauthorized")) {
            ctx.status(401);
        } else if (message.contains("already taken")) {
            ctx.status(403);
        } else if (message.contains("bad request")) {
            ctx.status(400);
        } else {
            ctx.status(500);
        }

        ctx.json(Map.of("message", message));
    }
}