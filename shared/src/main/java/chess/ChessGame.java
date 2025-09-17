package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        //If there isn't a piece, then return null
        if (piece == null) {
            return null;
        }

        ArrayList<ChessMove> validMovesList = new ArrayList<>();
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);

        // Look at each potential move. If it doesn't put the king in check, then add to the list
        for (ChessMove move : potentialMoves) {
            if (!putsInCheck(piece.getTeamColor(), move)) {
                validMovesList.add(move);
            }
        }
        Set<ChessMove> validMovesSet = new HashSet<>(validMovesList);
        return validMovesSet;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece selectedPiece = board.getPiece(move.getStartPosition());
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        // Execute the move
        ChessPiece capturedPiece = board.getPiece(endPosition);
        board.removePiece(startPosition);
        if (move.getPromotionPiece() == null) {
            board.addPiece(endPosition, selectedPiece);
        } else {
            ChessPiece promotedPiece = new ChessPiece(currentTeam, move.getPromotionPiece());
            board.addPiece(endPosition, promotedPiece);
        }

        // If the move puts the current team in check, undo the move
        if (isInCheck(selectedPiece.getTeamColor())) {
            board.addPiece(startPosition, selectedPiece);
            board.removePiece(endPosition);
            if (capturedPiece != null) {
                board.addPiece(endPosition, capturedPiece);
            }
            throw new InvalidMoveException("Move puts the king in check. Invalid move.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        //Iterate through the whole board
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                //Look at the piece at the position
                ChessPosition piecePosition = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(piecePosition);
                //If isn't null and not the team color, then check if it can attack the king
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, piecePosition);
                    for (ChessMove move : possibleMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if a move puts the team's own king in check
     *
     * @param color the team color to check
     * @param move  the move to test
     * @return true if the move puts the team in check, otherwise false
     */
    private boolean putsInCheck(TeamColor color, ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece selectedPiece = board.getPiece(move.getStartPosition());
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece capturedPiece = board.getPiece(endPosition);

        //Remove the starting piece
        board.removePiece(startPosition);
        //Add the promoted piece or regular piece
        if (move.getPromotionPiece() == null) {
            board.addPiece(endPosition, selectedPiece);
        } else {
            ChessPiece promotedPiece = new ChessPiece(currentTeam, move.getPromotionPiece());
            board.addPiece(endPosition, promotedPiece);
        }

        boolean inCheck = isInCheck(color);

        // Undo the move
        board.removePiece(endPosition);
        board.addPiece(startPosition, selectedPiece);
        if (capturedPiece != null) {
            board.addPiece(endPosition, capturedPiece);
        }

        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition piecePosition = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(piecePosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(piecePosition);
                    if (!possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition piecePosition = new ChessPosition(x, y);
                ChessPiece piece = board.getPiece(piecePosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(piecePosition);
                    if (!possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines the position of the king of the team
     *
     * @param teamColor
     * @return Position of the king of the teamColor
     */
    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPiece potentialKing = board.getPiece(new ChessPosition(x, y));
                if (potentialKing != null && potentialKing.getPieceType() == ChessPiece.PieceType.KING && potentialKing.getTeamColor() == teamColor) {
                    return new ChessPosition(x, y);
                }
            }
        }
        return null; // Should never happen if the board is correctly set up
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, board);
    }
}
