package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "testexample@email.com");
        AuthData result = userService.register(user);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testuser", result.username());
        Assertions.assertNotNull(result.authToken());

        UserData storedUser = userDAO.getUser("testuser");
        Assertions.assertNotNull(storedUser);
        Assertions.assertEquals("testuser", storedUser.username());
    }

    @Test
    @DisplayName("Register Duplicate Username")
    public void registerDuplicate() throws DataAccessException {
        UserData user1 = new UserData("testuser", "password123", "test1@email.com");
        userService.register(user1);

        UserData user2 = new UserData("testuser", "password456", "test2@email.com");

        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> userService.register(user2)
        );

        Assertions.assertTrue(exception.getMessage().contains("already taken"));
    }

    @Test
    @DisplayName("Register Null Username")
    public void registerNullUsername() {
        UserData user = new UserData(null, "password123", "test@email.com");

        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> userService.register(user)
        );

        Assertions.assertTrue(exception.getMessage().contains("bad request"));
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userService.register(user);

        UserData loginUser = new UserData("testuser", "password123", null);
        AuthData result = userService.login(loginUser);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testuser", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Login Wrong Password")
    public void loginWrongPassword() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        userService.register(user);

        UserData loginUser = new UserData("testuser", "wrongpassword", null);

        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> userService.login(loginUser)
        );

        Assertions.assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    @DisplayName("Login Nonexistent User")
    public void loginNonexistentUser() {
        UserData loginUser = new UserData("nonexistent", "password123", null);

        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> userService.login(loginUser)
        );

        Assertions.assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() throws DataAccessException {
        UserData user = new UserData("testuser", "password123", "test@email.com");
        AuthData auth = userService.register(user);

        Assertions.assertDoesNotThrow(() -> userService.logout(auth.authToken()));

        Assertions.assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    @DisplayName("Logout Invalid Token")
    public void logoutInvalidToken() {
        DataAccessException exception = Assertions.assertThrows(
                DataAccessException.class,
                () -> userService.logout("invalid-token")
        );

        Assertions.assertTrue(exception.getMessage().contains("unauthorized"));
    }
}