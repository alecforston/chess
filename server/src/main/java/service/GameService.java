package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String gameName, String authToken) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }

        ChessGame game = new ChessGame();
        GameData gameData = new GameData(0, null, null, gameName, game);
        return gameDAO.createGame(gameData);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        return gameDAO.listGames();
    }

    public void joinGame(ChessGame.TeamColor playerColor, int gameID, String authToken)
            throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = auth.username();
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (whiteUsername != null && !whiteUsername.equals(username)) {
                throw new DataAccessException("Error: already taken");
            }
            whiteUsername = username;
        } else if (playerColor == ChessGame.TeamColor.BLACK) {
            if (blackUsername != null && !blackUsername.equals(username)) {
                throw new DataAccessException("Error: already taken");
            }
            blackUsername = username;
        }

        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername,
                game.gameName(), game.game());
        gameDAO.updateGame(updatedGame);
    }
}