package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import serverfacade.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private final Scanner scanner;
    private String authToken = null;
    private String username = null;
    private State state = State.LOGGED_OUT;
    private Map<Integer, GameData> gameMap = new HashMap<>();

    private enum State {
        LOGGED_OUT,
        LOGGED_IN
    }

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Welcome to 240 Chess Client!");
        System.out.println("Type 'help' to get started.");
        System.out.println();

        while (true) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                String result = processCommand(line.trim());
                if (result.equals("quit")) {
                    break;
                }
                if (!result.isEmpty()) {
                    System.out.println(result);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printPrompt() {
        if (state == State.LOGGED_OUT) {
            System.out.print(RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print(RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }

    private String processCommand(String input) throws Exception {
        if (input.isEmpty()) {
            return "";
        }

        String[] tokens = input.split("\\s+");
        String cmd = tokens[0].toLowerCase();

        if (state == State.LOGGED_OUT) {
            return switch (cmd) {
                case "help" -> helpPrelogin();
                case "quit" -> "quit";
                case "login" -> login(tokens);
                case "register" -> register(tokens);
                default -> "Unknown command. Type 'help' for available commands.";
            };
        } else {
            return switch (cmd) {
                case "help" -> helpPostlogin();
                case "logout" -> logout();
                case "create" -> createGame(tokens);
                case "list" -> listGames();
                case "play" -> playGame(tokens);
                case "observe" -> observeGame(tokens);
                default -> "Unknown command. Type 'help' for available commands.";
            };
        }
    }

    private String helpPrelogin() {
        return SET_TEXT_COLOR_BLUE +
                "Available commands:\n" +
                "  register <username> <password> <email> - Create a new account\n" +
                "  login <username> <password> - Login to your account\n" +
                "  quit - Exit the program\n" +
                "  help - Display this help message" +
                RESET_TEXT_COLOR;
    }

    private String helpPostlogin() {
        return SET_TEXT_COLOR_BLUE +
                "Available commands:\n" +
                "  create <game name> - Create a new game\n" +
                "  list - List all games\n" +
                "  play <game number> <WHITE|BLACK> - Join a game\n" +
                "  observe <game number> - Watch a game\n" +
                "  logout - Logout from your account\n" +
                "  help - Display this help message" +
                RESET_TEXT_COLOR;
    }

    private String register(String[] tokens) {
        try {
            if (tokens.length != 4) {
                return "Usage: register <username> <password> <email>";
            }

            String user = tokens[1];
            String pass = tokens[2];
            String email = tokens[3];

            AuthData authData = server.register(user, pass, email);
            authToken = authData.authToken();
            username = authData.username();
            state = State.LOGGED_IN;

            return SET_TEXT_COLOR_GREEN + "Successfully registered and logged in as " + username + "!" + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }

    private String login(String[] tokens) {
        try {
            if (tokens.length != 3) {
                return "Usage: login <username> <password>";
            }

            String user = tokens[1];
            String pass = tokens[2];

            AuthData authData = server.login(user, pass);
            authToken = authData.authToken();
            username = authData.username();
            state = State.LOGGED_IN;

            return SET_TEXT_COLOR_GREEN + "Successfully logged in as " + username + "!" + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return "Login failed: " + e.getMessage();
        }
    }

    private String logout() {
        try {
            server.logout(authToken);
            authToken = null;
            username = null;
            state = State.LOGGED_OUT;
            gameMap.clear();

            return SET_TEXT_COLOR_GREEN + "Successfully logged out!" + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return "Logout failed: " + e.getMessage();
        }
    }

    private String createGame(String[] tokens) {
        try {
            if (tokens.length < 2) {
                return "Usage: create <game name>";
            }

            StringBuilder gameNameBuilder = new StringBuilder();
            for (int i = 1; i < tokens.length; i++) {
                if (i > 1) {
                    gameNameBuilder.append(" ");
                }
                gameNameBuilder.append(tokens[i]);
            }
            String gameName = gameNameBuilder.toString();

            int gameID = server.createGame(gameName, authToken);
            return SET_TEXT_COLOR_GREEN + "Game created successfully!" + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return "Failed to create game: " + e.getMessage();
        }
    }

    private String listGames() {
        try {
            Collection<GameData> games = server.listGames(authToken);

            if (games.isEmpty()) {
                return "No games available.";
            }

            gameMap.clear();
            int index = 1;
            StringBuilder sb = new StringBuilder();
            sb.append(SET_TEXT_COLOR_BLUE).append("Games:\n");

            for (GameData game : games) {
                gameMap.put(index, game);
                sb.append(String.format("  %d. %s\n", index, game.gameName()));
                sb.append(String.format("     White: %s\n",
                        game.whiteUsername() == null ? "(empty)" : game.whiteUsername()));
                sb.append(String.format("     Black: %s\n",
                        game.blackUsername() == null ? "(empty)" : game.blackUsername()));
                index++;
            }
            sb.append(RESET_TEXT_COLOR);

            return sb.toString();
        } catch (Exception e) {
            return "Failed to list games: " + e.getMessage();
        }
    }

    private String playGame(String[] tokens) {
        try {
            if (tokens.length != 3) {
                return "Usage: play <game number> <WHITE|BLACK>";
            }

            int gameNum;
            try {
                gameNum = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                return "Error: Invalid game number.";
            }

            String colorStr = tokens[2].toUpperCase();

            if (!gameMap.containsKey(gameNum)) {
                return "Error: Invalid game number. Use 'list' to see available games.";
            }

            if (!colorStr.equals("WHITE") && !colorStr.equals("BLACK")) {
                return "Error: Color must be WHITE or BLACK.";
            }

            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(colorStr);
            GameData game = gameMap.get(gameNum);

            server.joinGame(color, game.gameID(), authToken);

            BoardDrawer.drawBoard(color);

            return SET_TEXT_COLOR_GREEN + "Successfully joined game as " + colorStr + "!" + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return "Failed to join game: " + e.getMessage();
        }
    }

    private String observeGame(String[] tokens) {
        try {
            if (tokens.length != 2) {
                return "Usage: observe <game number>";
            }

            int gameNum;
            try {
                gameNum = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                return "Error: Invalid game number.";
            }

            if (!gameMap.containsKey(gameNum)) {
                return "Error: Invalid game number. Use 'list' to see available games.";
            }

            BoardDrawer.drawBoard(ChessGame.TeamColor.WHITE);

            return SET_TEXT_COLOR_GREEN + "Observing game..." + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return "Failed to observe game: " + e.getMessage();
        }
    }
}