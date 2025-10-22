package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import chess.ChessGame;

public class ClearServiceTest {

    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearService clearService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    @DisplayName("Clear All Data")
    public void clearAllData() throws DataAccessException {
        // Add some data
        userDAO.createUser(new UserData("user1", "pass1", "email1@test.com"));
        userDAO.createUser(new UserData("user2", "pass2", "email2@test.com"));

        ChessGame game = new ChessGame();
        gameDAO.createGame(new GameData(0, null, null, "Game 1", game));
        gameDAO.createGame(new GameData(0, null, null, "Game 2", game));

        authDAO.createAuth(new AuthData("token1", "user1"));
        authDAO.createAuth(new AuthData("token2", "user2"));

        Assertions.assertNotNull(userDAO.getUser("user1"));
        Assertions.assertNotNull(authDAO.getAuth("token1"));
        Assertions.assertEquals(2, gameDAO.listGames().size());

        clearService.clear();

        Assertions.assertNull(userDAO.getUser("user1"));
        Assertions.assertNull(userDAO.getUser("user2"));
        Assertions.assertNull(authDAO.getAuth("token1"));
        Assertions.assertNull(authDAO.getAuth("token2"));
        Assertions.assertEquals(0, gameDAO.listGames().size());
    }
}