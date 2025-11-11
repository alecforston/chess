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

}
