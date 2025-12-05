package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    private static final String LIGHT_SQUARE = SET_BG_COLOR_WHITE;
    private static final String DARK_SQUARE = SET_BG_COLOR_DARK_GREEN;
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY;

    public static void drawBoard(ChessGame.TeamColor perspective) {
        ChessGame game = new ChessGame();
        game.getBoard().resetBoard();

        System.out.println();

        if (perspective == ChessGame.TeamColor.WHITE) {
            drawBoardWhitePerspective(game.getBoard());
        } else {
            drawBoardBlackPerspective(game.getBoard());
        }

        System.out.println();
    }

    private static void drawBoardWhitePerspective(ChessBoard board) {
        drawBorder(true);

        for (int row = 8; row >= 1; row--) {
            drawRow(board, row, true);
        }

        drawBorder(true);
    }

    private static void drawBoardBlackPerspective(ChessBoard board) {
        drawBorder(false);

        for (int row = 1; row <= 8; row++) {
            drawRow(board, row, false);
        }

        drawBorder(false);
    }

    private static void drawBorder(boolean whitePerspective) {
        System.out.print(BORDER_COLOR + "   ");

        if (whitePerspective) {
            for (char col = 'a'; col <= 'h'; col++) {
                System.out.print(" " + col + " ");
            }
        } else {
            for (char col = 'h'; col >= 'a'; col--) {
                System.out.print(" " + col + " ");
            }
        }

        System.out.print("   " + RESET_BG_COLOR);
        System.out.println();
    }

    private static void drawRow(ChessBoard board, int row, boolean whitePerspective) {
        System.out.print(BORDER_COLOR + " " + row + " " + RESET_BG_COLOR);

        if (whitePerspective) {
            for (int col = 1; col <= 8; col++) {
                drawSquare(board, row, col);
            }
        } else {
            for (int col = 8; col >= 1; col--) {
                drawSquare(board, row, col);
            }
        }

        System.out.print(BORDER_COLOR + " " + row + " " + RESET_BG_COLOR);
        System.out.println();
    }

    private static void drawSquare(ChessBoard board, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);

        boolean isLightSquare = (row + col) % 2 == 0;
        String bgColor = isLightSquare ?  DARK_SQUARE: LIGHT_SQUARE;

        System.out.print(bgColor);

        if (piece == null) {
            System.out.print(EMPTY);
        } else {
            System.out.print(getPieceString(piece));
        }

        System.out.print(RESET_BG_COLOR);
    }

    private static String getPieceString(ChessPiece piece) {
        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;

        String pieceStr = switch (piece.getPieceType()) {
            case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? WHITE_KING : BLACK_KING;
            case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? WHITE_PAWN : BLACK_PAWN;
        };

        return color + pieceStr + RESET_TEXT_COLOR;
    }
}