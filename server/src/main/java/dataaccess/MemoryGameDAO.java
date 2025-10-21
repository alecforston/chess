package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public void clear() {
        games.clear();
        nextGameID = 1;
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        if (game.gameName() == null) {
            throw new DataAccessException("Game name cannot be null");
        }
        int gameID = nextGameID++;
        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(),
                game.gameName(), game.game());
        games.put(gameID, newGame);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game does not exist");
        }
        games.put(game.gameID(), game);
    }
}