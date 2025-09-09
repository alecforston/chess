package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition piecePosition) {
        return switch (type) {
            case KING -> kingMoves(board, piecePosition);
            case QUEEN -> throw new RuntimeException("Piece not implemented");
            case BISHOP -> bishopMoves(board, piecePosition);
            case KNIGHT -> knightMoves(board, piecePosition);
            case ROOK -> rookMoves(board, piecePosition);
            case PAWN -> throw new RuntimeException("Piece not implemented");
            case null -> throw new RuntimeException("Piece not implemented");
        };
    }

    /**
     * King moves one square vertically or horizontally
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    private ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        int[][] spots = {{1,1}, {1,-1}, {-1,1}, {-1,-1}, {1,0}, {-1,0}, {0,1}, {0,-1}};

        for(int[] spot : spots){
            int x = spot[0];
            int y = spot[1];
            ChessPosition newPosition = new ChessPosition(row + x, col + y);

            if(!isValidPosition(newPosition)) { // Skip if a the square is outside the board
                continue;
            }

            // If the position is empty, or contains an enemy piece, add that as a possible king move
            if (isEmptySquare(board, newPosition) || ((!isEmptySquare(board, newPosition) && isDifferentColor(board, startPosition, newPosition)))){
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
        return moves;
    }

    /**
     * Bishop moves two or more squares diagonally
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    public ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        int[][] direction = {{1,1}, {-1,1}, {1,-1}, {-1,-1}};

        for(int[] dir : direction){
            int x = dir[0];
            int y = dir[1];
            ChessPosition newPosition = new ChessPosition(row + x, col + y);

            while(isValidPosition(newPosition) && board.getPiece(newPosition) == null){
                moves.add(new ChessMove(startPosition, newPosition, null));
                x += dir[0];
                y += dir[1];
                newPosition = new ChessPosition(row + x, col + y);
            }

            if (isValidPosition(newPosition) && isDifferentColor(board, startPosition, newPosition)){
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
        return moves;
    }

    /**
     * Knight jumps one square vertically and two squares horizontally
     * or jumps one square horizontally and two squares vertically
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    private ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        int[][] spots = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {-1,2}, {1,-2}, {-1,-2}};

        for(int[] spot : spots){
            int x = spot[0];
            int y = spot[1];
            ChessPosition newPosition = new ChessPosition(row + x, col + y);

            if(!isValidPosition(newPosition)) { // Skip if the square is outside the board
                continue;
            }

            // If the position is empty, or contains an enemy piece, add that as a possible king move
            if (isEmptySquare(board, newPosition) || ((!isEmptySquare(board, newPosition) && isDifferentColor(board, startPosition, newPosition)))){
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
        return moves;
    }

    /**
     * Bishop moves two or more squares diagonally
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    public ArrayList<ChessMove> rookMoves(ChessBoard board, ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        int[][] direction = {{1,0}, {0,1}, {0,-1}, {-1,0}};

        for(int[] dir : direction){
            int x = dir[0];
            int y = dir[1];
            ChessPosition newPosition = new ChessPosition(row + x, col + y);

            while(isValidPosition(newPosition) && board.getPiece(newPosition) == null){
                moves.add(new ChessMove(startPosition, newPosition, null));
                x += dir[0];
                y += dir[1];
                newPosition = new ChessPosition(row + x, col + y);
            }

            if (isValidPosition(newPosition) && isDifferentColor(board, startPosition, newPosition)){
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
        return moves;
    }

    /**
     * @return boolean of the colors of two positions
     * true if they are different colors, false if they are the same color
     */
    private boolean isDifferentColor(ChessBoard board, ChessPosition pos1, ChessPosition pos2) {
        return board.getPiece(pos1).getTeamColor() != board.getPiece(pos2).getTeamColor();
    }

    /**
     * @return boolean of if there is no piece on the selected position
     */
    private boolean isEmptySquare(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) == null;
    }

    /**
     * @return boolean of if the position is on the board
     */
    private boolean isValidPosition(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return(row >= 1 && row <= 8 && col >= 1 && col <= 8);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "Piece{" +
                "Color=" + pieceColor +
                ", t: " + type +
                '}';
    }
}
