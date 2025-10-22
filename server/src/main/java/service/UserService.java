package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        userDAO.createUser(user);

        String authToken = generateToken();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);

        return auth;
    }

    public AuthData login(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: bad request");
        }

        UserData existingUser = userDAO.getUser(user.username());
        if (existingUser == null || !existingUser.password().equals(user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = generateToken();
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

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}