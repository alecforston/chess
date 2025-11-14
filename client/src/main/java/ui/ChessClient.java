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

        String[] tokens = input.toLowerCase().split("\\s+");
        String cmd = tokens[0];

        if (state == State.LOGGED_OUT) {
            return switch (cmd) {
                case "help" -> helpPrelogin();
                case "quit" -> "quit";
                case "login" -> login();
                case "register" -> register();
                default -> "Unknown command. Type 'help' for available commands.";
            };
        } else {
            return switch (cmd) {
                case "help" -> helpPostlogin();
                case "logout" -> logout();
                case "create" -> createGame();
                case "list" -> listGames();
                case "play" -> playGame();
                case "observe" -> observeGame();
                default -> "Unknown command. Type 'help' for available commands.";
            };
        }
    }

    private String helpPrelogin() {
        return SET_TEXT_COLOR_BLUE +
                "Available commands:\n" +
                "  register - Create a new account\n" +
                "  login - Login to your account\n" +
                "  quit - Exit the program\n" +
                "  help - Display this help message" +
                RESET_TEXT_COLOR;
    }

    private String helpPostlogin() {
        return SET_TEXT_COLOR_BLUE +
                "Available commands:\n" +
                "  create - Create a new game\n" +
                "  list - List all games\n" +
                "  play <game number> <WHITE|BLACK> - Join a game\n" +
                "  observe <game number> - Watch a game\n" +
                "  logout - Logout from your account\n" +
                "  help - Display this help message" +
                RESET_TEXT_COLOR;
    }

    private String register() {
        try {
            System.out.print("Username: ");
            String user = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pass = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                return "Error: All fields are required.";
            }

            AuthData authData = server.register(user, pass, email);
            authToken = authData.authToken();
            username = authData.username();
            state = State.LOGGED_IN;

            return SET_TEXT_COLOR_GREEN + "Successfully registered and logged in as " + username + "!" + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }

    private String login() {
        try {
            System.out.print("Username: ");
            String user = scanner.nextLine().trim();
            System.out.print("Password: ");
            String pass = scanner.nextLine().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                return "Error: Username and password are required.";
            }

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

    private String createGame() {
        try {
            System.out.print("Game name: ");
            String gameName = scanner.nextLine().trim();

            if (gameName.isEmpty()) {
                return "Error: Game name is required.";
            }

            int gameID = server.createGame(gameName, authToken);
            return SET_TEXT_COLOR_GREEN + "Game created successfully! (ID: " + gameID + ")" + RESET_TEXT_COLOR;
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

    private String playGame() {
        try {
            System.out.print("Game number: ");
            String numStr = scanner.nextLine().trim();
            System.out.print("Color (WHITE/BLACK): ");
            String colorStr = scanner.nextLine().trim().toUpperCase();

            if (numStr.isEmpty() || colorStr.isEmpty()) {
                return "Error: Game number and color are required.";
            }

            int gameNum;
            try {
                gameNum = Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                return "Error: Invalid game number.";
            }

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

    private String observeGame() {
        try {
            System.out.print("Game number: ");
            String numStr = scanner.nextLine().trim();

            if (numStr.isEmpty()) {
                return "Error: Game number is required.";
            }

            int gameNum;
            try {
                gameNum = Integer.parseInt(numStr);
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