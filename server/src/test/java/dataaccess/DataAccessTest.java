package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTest {

    private static MySQLUserDAO userDAO;
    private static MySQLAuthDAO authDAO;
    private static MySQLGameDAO gameDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    //User DAO Tests

    @Test
    @Order(1)
    @DisplayName("Create User Success")
    public void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("testuser");
        assertNotNull(retrieved);
        assertEquals("testuser", retrieved.username());
        assertEquals("test@email.com", retrieved.email());
        // Password should be hashed, not the same as original
        assertNotEquals("password123", retrieved.password());
        assertTrue(retrieved.password().startsWith("$2a$")); // BCrypt hash starts with $2a$
    }

    @Test
    @Order(2)
    @DisplayName("Create User Duplicate Failure")
    public void createUserDuplicateFailure() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(user);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Create User Null Username")
    public void createUserNullUsername() {
        UserData user = new UserData(null, "password123", "test@email.com");

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(user);
        });
    }

    @Test
    @Order(4)
    @DisplayName("Get User Not Found")
    public void getUserNotFound() throws DataAccessException {
        UserData retrieved = userDAO.getUser("nonexistent");
        assertNull(retrieved);
    }

    @Test
    @Order(5)
    @DisplayName("Get User Success")
    public void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("testuser");
        assertNotNull(retrieved);
        assertEquals("testuser", retrieved.username());
        assertEquals("test@email.com", retrieved.email());
    }

    @Test
    @Order(6)
    @DisplayName("Verify User Password Success")
    public void verifyUserPasswordSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userDAO.createUser(user);

        boolean verified = userDAO.verifyUser("testuser", "password123");
        assertTrue(verified);
    }

    @Test
    @Order(7)
    @DisplayName("Verify User Password Failure")
    public void verifyUserPasswordFailure() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userDAO.createUser(user);

        boolean verified = userDAO.verifyUser("testuser", "wrongpassword");
        assertFalse(verified);
    }

    @Test
    @Order(8)
    @DisplayName("Verify User Nonexistent")
    public void verifyUserNonexistent() throws DataAccessException {
        boolean verified = userDAO.verifyUser("nonexistent", "password123");
        assertFalse(verified);
    }

    @Test
    @Order(9)
    @DisplayName("Clear Users")
    public void clearUsers() throws DataAccessException {
        UserData user1 = new UserData("testuser1", "password123", "test1@email.com");
        UserData user2 = new UserData("testuser2", "password456", "test2@email.com");
        userDAO.createUser(user1);
        userDAO.createUser(user2);

        userDAO.clear();

        UserData retrieved1 = userDAO.getUser("testuser1");
        UserData retrieved2 = userDAO.getUser("testuser2");
        assertNull(retrieved1);
        assertNull(retrieved2);
    }

    // Auth DAO Tests

    @Test
    @Order(10)
    @DisplayName("Create Auth Success")
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token123");
        assertNotNull(retrieved);
        assertEquals("token123", retrieved.authToken());
        assertEquals("testuser", retrieved.username());
    }

    @Test
    @Order(11)
    @DisplayName("Create Auth Null Token")
    public void createAuthNullToken() {
        AuthData auth = new AuthData(null, "testuser");

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(auth);
        });
    }

    @Test
    @Order(12)
    @DisplayName("Get Auth Not Found")
    public void getAuthNotFound() throws DataAccessException {
        AuthData retrieved = authDAO.getAuth("nonexistent");
        assertNull(retrieved);
    }

    @Test
    @Order(13)
    @DisplayName("Get Auth Success")
    public void getAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token123");
        assertNotNull(retrieved);
        assertEquals("token123", retrieved.authToken());
        assertEquals("testuser", retrieved.username());
    }

    @Test
    @Order(14)
    @DisplayName("Delete Auth Success")
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testuser");
        authDAO.createAuth(auth);

        authDAO.deleteAuth("token123");

        AuthData retrieved = authDAO.getAuth("token123");
        assertNull(retrieved);
    }

    @Test
    @Order(15)
    @DisplayName("Delete Auth Not Found")
    public void deleteAuthNotFound() {
        assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("nonexistent");
        });
    }

    @Test
    @Order(16)
    @DisplayName("Clear Auth")
    public void clearAuth() throws DataAccessException {
        AuthData auth1 = new AuthData("token123", "testuser1");
        AuthData auth2 = new AuthData("token456", "testuser2");
        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);

        authDAO.clear();

        AuthData retrieved1 = authDAO.getAuth("token123");
        AuthData retrieved2 = authDAO.getAuth("token456");
        assertNull(retrieved1);
        assertNull(retrieved2);
    }

    @Test
    @Order(17)
    @DisplayName("Create Multiple Auth Tokens for Same User")
    public void createMultipleAuthTokens() throws DataAccessException {
        AuthData auth1 = new AuthData("token123", "testuser");
        AuthData auth2 = new AuthData("token456", "testuser");
        authDAO.createAuth(auth1);
        authDAO.createAuth(auth2);

        AuthData retrieved1 = authDAO.getAuth("token123");
        AuthData retrieved2 = authDAO.getAuth("token456");

        assertNotNull(retrieved1);
        assertNotNull(retrieved2);
        assertEquals("testuser", retrieved1.username());
        assertEquals("testuser", retrieved2.username());
    }

    // Game DAO Tests

    @Test
    @Order(18)
    @DisplayName("Create Game Success")
    public void createGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, "Test Game", chessGame);

        int gameID = gameDAO.createGame(game);
        assertTrue(gameID > 0);

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals("Test Game", retrieved.gameName());
        assertNotNull(retrieved.game());
        assertNull(retrieved.whiteUsername());
        assertNull(retrieved.blackUsername());
    }

    @Test
    @Order(19)
    @DisplayName("Create Game Null Name")
    public void createGameNullName() {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, null, chessGame);

        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(game);
        });
    }

    @Test
    @Order(20)
    @DisplayName("Create Game with Players")
    public void createGameWithPlayers() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, "whitePlayer", "blackPlayer", "Test Game", chessGame);

        int gameID = gameDAO.createGame(game);

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals("whitePlayer", retrieved.whiteUsername());
        assertEquals("blackPlayer", retrieved.blackUsername());
    }

    @Test
    @Order(21)
    @DisplayName("Get Game Not Found")
    public void getGameNotFound() throws DataAccessException {
        GameData retrieved = gameDAO.getGame(9999);
        assertNull(retrieved);
    }

    @Test
    @Order(22)
    @DisplayName("Get Game Success")
    public void getGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, "Test Game", chessGame);
        int gameID = gameDAO.createGame(game);

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals(gameID, retrieved.gameID());
        assertEquals("Test Game", retrieved.gameName());
    }

    @Test
    @Order(23)
    @DisplayName("Update Game Success")
    public void updateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, "Test Game", chessGame);

        int gameID = gameDAO.createGame(game);

        GameData updatedGame = new GameData(gameID, "white", "black", "Updated Game", chessGame);
        gameDAO.updateGame(updatedGame);

        GameData retrieved = gameDAO.getGame(gameID);
        assertEquals("white", retrieved.whiteUsername());
        assertEquals("black", retrieved.blackUsername());
        assertEquals("Updated Game", retrieved.gameName());
    }

    @Test
    @Order(24)
    @DisplayName("Update Game Not Found")
    public void updateGameNotFound() {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(9999, "white", "black", "Nonexistent Game", chessGame);

        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(game);
        });
    }

    @Test
    @Order(25)
    @DisplayName("List Games Empty")
    public void listGamesEmpty() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    @Order(26)
    @DisplayName("List Games Multiple")
    public void listGamesMultiple() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game1 = new GameData(0, null, null, "Game 1", chessGame);
        GameData game2 = new GameData(0, "white", null, "Game 2", chessGame);
        GameData game3 = new GameData(0, null, "black", "Game 3", chessGame);

        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        gameDAO.createGame(game3);

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(3, games.size());
    }

    @Test
    @Order(27)
    @DisplayName("Clear Games")
    public void clearGames() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game1 = new GameData(0, null, null, "Game 1", chessGame);
        GameData game2 = new GameData(0, null, null, "Game 2", chessGame);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        gameDAO.clear();

        Collection<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    @Order(28)
    @DisplayName("Game Serialization Preserves State")
    public void gameSerializationPreservesState() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        chessGame.setTeamTurn(ChessGame.TeamColor.BLACK);

        GameData game = new GameData(0, "white", "black", "Test Game", chessGame);
        int gameID = gameDAO.createGame(game);

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved.game());
        assertEquals(ChessGame.TeamColor.BLACK, retrieved.game().getTeamTurn());
        assertNotNull(retrieved.game().getBoard());
    }

    @Test
    @Order(29)
    @DisplayName("Game Auto-Increment IDs")
    public void gameAutoIncrementIDs() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game1 = new GameData(0, null, null, "Game 1", chessGame);
        GameData game2 = new GameData(0, null, null, "Game 2", chessGame);
        GameData game3 = new GameData(0, null, null, "Game 3", chessGame);

        int id1 = gameDAO.createGame(game1);
        int id2 = gameDAO.createGame(game2);
        int id3 = gameDAO.createGame(game3);

        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }

    @Test
    @Order(30)
    @DisplayName("Update Game Preserves GameID")
    public void updateGamePreservesGameID() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, "Test Game", chessGame);
        int originalGameID = gameDAO.createGame(game);

        GameData updatedGame = new GameData(originalGameID, "white", "black", "Updated", chessGame);
        gameDAO.updateGame(updatedGame);

        GameData retrieved = gameDAO.getGame(originalGameID);
        assertNotNull(retrieved);
        assertEquals(originalGameID, retrieved.gameID());
    }
}