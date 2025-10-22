package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import java.util.Collection;

public class GameServiceTest {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;
    private String validAuthToken;

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);

        gameDAO.clear();
        authDAO.clear();

        validAuthToken = "valid-token-123";
        authDAO.createAuth(new AuthData(validAuthToken, "testuser"));
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() throws DataAccessException {
        int gameID = gameService.createGame("Test Game", validAuthToken);

        Assertions.assertTrue(gameID > 0);

        GameData game = gameDAO.getGame(gameID);
        Assertions.assertNotNull(game);
        Assertions.assertEquals("Test Game", game.gameName());
    }

    @Test
    @DisplayName("Create Game Unauthorized")
    public void createGameUnauthorized() {
        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameService.createGame("Test Game", "invalid-token")
        );

        Assertions.assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    @DisplayName("Create Game Null Name")
    public void createGameNullName() {
        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameService.createGame(null, validAuthToken)
        );

        Assertions.assertTrue(exception.getMessage().contains("bad request"));
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() throws DataAccessException {
        gameService.createGame("Game 1", validAuthToken);
        gameService.createGame("Game 2", validAuthToken);
        gameService.createGame("Game 3", validAuthToken);

        Collection<GameData> games = gameService.listGames(validAuthToken);

        Assertions.assertNotNull(games);
        Assertions.assertEquals(3, games.size());
    }

    @Test
    @DisplayName("List Games Empty")
    public void listGamesEmpty() throws DataAccessException {
        Collection<GameData> games = gameService.listGames(validAuthToken);

        Assertions.assertNotNull(games);
        Assertions.assertEquals(0, games.size());
    }

    @Test
    @DisplayName("List Games Unauthorized")
    public void listGamesUnauthorized() {
        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameService.listGames("invalid-token")
        );

        Assertions.assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    @DisplayName("Join Game Success as White")
    public void joinGameSuccessWhite() throws DataAccessException {
        int gameID = gameService.createGame("Test Game", validAuthToken);

        gameService.joinGame(ChessGame.TeamColor.WHITE, gameID, validAuthToken);

        GameData game = gameDAO.getGame(gameID);
        Assertions.assertEquals("testuser", game.whiteUsername());
        Assertions.assertNull(game.blackUsername());
    }

    @Test
    @DisplayName("Join Game Success as Black")
    public void joinGameSuccessBlack() throws DataAccessException {
        int gameID = gameService.createGame("Test Game", validAuthToken);

        gameService.joinGame(ChessGame.TeamColor.BLACK, gameID, validAuthToken);

        GameData game = gameDAO.getGame(gameID);
        Assertions.assertNull(game.whiteUsername());
        Assertions.assertEquals("testuser", game.blackUsername());
    }

    @Test
    @DisplayName("Join Game Spot Taken")
    public void joinGameSpotTaken() throws DataAccessException {
        int gameID = gameService.createGame("Test Game", validAuthToken);

        // First user joins as white
        gameService.joinGame(ChessGame.TeamColor.WHITE, gameID, validAuthToken);

        // Create second user
        String secondAuthToken = "second-token-456";
        authDAO.createAuth(new AuthData(secondAuthToken, "seconduser"));

        // Second user tries to join as white
        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame(ChessGame.TeamColor.WHITE, gameID, secondAuthToken)
        );

        Assertions.assertTrue(exception.getMessage().contains("already taken"));
    }

    @Test
    @DisplayName("Join Game Unauthorized")
    public void joinGameUnauthorized() throws DataAccessException {
        int gameID = gameService.createGame("Test Game", validAuthToken);

        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame(ChessGame.TeamColor.WHITE, gameID, "invalid-token")
        );

        Assertions.assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    @DisplayName("Join Game Invalid Game ID")
    public void joinGameInvalidID() {
        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame(ChessGame.TeamColor.WHITE, 9836, validAuthToken)
        );

        Assertions.assertTrue(exception.getMessage().contains("bad request"));
    }
}