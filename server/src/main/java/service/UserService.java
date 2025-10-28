package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    /**
     * Register a new user
     */
    public AuthData register(UserData user) throws DataAccessException {
        // Validate input
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }

        // Check if user already exists
        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        // Create the user (password will be hashed in the DAO)
        userDAO.createUser(user);

        // Create auth token
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);

        return auth;
    }

    public AuthData login(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: bad request");
        }

        UserData existingUser = userDAO.getUser(user.username());
        if (existingUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        boolean passwordValid = false;
        if (userDAO instanceof MySQLUserDAO) {
            passwordValid = ((MySQLUserDAO) userDAO).verifyUser(user.username(), user.password());
        } else if (userDAO instanceof MemoryUserDAO) {
            passwordValid = existingUser.password().equals(user.password());
        }

        if (!passwordValid) {
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);

        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}