package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Starting test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }

    //positive tests

    @Test
    @DisplayName("Register")
    void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "player1@email.com");
        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    @DisplayName("Login")
    void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");

        var authData = facade.login("player1", "password");
        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    @DisplayName("Logout")
    void logoutPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");

        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    @DisplayName("Create Game")
    void createGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");

        int gameID = facade.createGame("TestGame", authData.authToken());
        assertTrue(gameID > 0);
    }

    @Test
    @DisplayName("List Games")
    void listGamesPositiveEmpty() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");

        Collection<GameData> games = facade.listGames(authData.authToken());
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    @DisplayName("List Games")
    void listGamesPositiveMultiple() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");

        facade.createGame("Game1", authData.authToken());
        facade.createGame("Game2", authData.authToken());
        facade.createGame("Game3", authData.authToken());

        Collection<GameData> games = facade.listGames(authData.authToken());
        assertNotNull(games);
        assertEquals(3, games.size());
    }

    @Test
    @DisplayName("Join Game")
    void joinGamePositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame("TestGame", authData.authToken());

        assertDoesNotThrow(() -> facade.joinGame(ChessGame.TeamColor.WHITE, gameID, authData.authToken()));
    }

    @Test
    @DisplayName("Clear")
    void clearPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("TestGame", authData.authToken());
        assertDoesNotThrow(() -> facade.clear());

        Exception exception = assertThrows(Exception.class, () -> {
            facade.login("player1", "password");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    //Negative tests
    @Test
    @DisplayName("Register a Duplicate Username")
    void registerNegativeDuplicate() throws Exception {
        facade.register("player1", "password", "p1@email.com");

        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("player1", "different", "different@email.com");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("taken") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Register with Missing fields")
    void registerNegativeMissingFields() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("player1", null, "p1@email.com");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("error") ||
                exception.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    @DisplayName("Login with wrong password")
    void loginNegativeWrongPassword() throws Exception {
        facade.register("player1", "password", "p1@email.com");

        Exception exception = assertThrows(Exception.class, () -> {
            facade.login("player1", "notpassword");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Login to Nonexistant Account")
    void loginNegativeUserNotExist() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.login("nonexistent", "password");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Logout With Invalid Auth Token")
    void logoutNegativeInvalidAuth() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.logout("invalid_auth_token");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Create Game With Invalid Auth Token")
    void createGameNegativeInvalidAuth() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.createGame("TestGame", "invalid_auth_token");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Create Game Without Game Name")
    void createGameNegativeNullName() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");

        Exception exception = assertThrows(Exception.class, () -> {
            facade.createGame(null, authData.authToken());
        });
        String message = exception.getMessage();
        assertTrue(message == null ||
                        message.toLowerCase().contains("error") ||
                        message.toLowerCase().contains("bad request"),
                "Expected error but got: " + message);
    }

    @Test
    @DisplayName("List Games With Invalid Auth Token")
    void listGamesNegativeInvalidAuth() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.listGames("invalid_auth_token");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Join Game With Invalid Auth Token")
    void joinGameNegativeInvalidAuth() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame("TestGame", authData.authToken());

        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(ChessGame.TeamColor.WHITE, gameID, "invalid_auth_token");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Join Game With Duplicate Color")
    void joinGameNegativeColorTaken() throws Exception {
        var authData1 = facade.register("player1", "password1", "p1@email.com");
        var authData2 = facade.register("player2", "password2", "p2@email.com");

        int gameID = facade.createGame("TestGame", authData1.authToken());
        facade.joinGame(ChessGame.TeamColor.WHITE, gameID, authData1.authToken());

        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(ChessGame.TeamColor.WHITE, gameID, authData2.authToken());
        });
        assertTrue(exception.getMessage().toLowerCase().contains("taken") ||
                exception.getMessage().toLowerCase().contains("error"));
    }

    @Test
    @DisplayName("Join Game With Invalid Game ID")
    void joinGameNegativeInvalidGameId() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");

        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(ChessGame.TeamColor.WHITE, 99999, authData.authToken());
        });
        assertTrue(exception.getMessage().toLowerCase().contains("error") ||
                exception.getMessage().toLowerCase().contains("bad request"));
    }
}
