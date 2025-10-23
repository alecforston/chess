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
            case QUEEN -> queenMoves(board, piecePosition);
            case BISHOP -> bishopMoves(board, piecePosition);
            case KNIGHT -> knightMoves(board, piecePosition);
            case ROOK -> rookMoves(board, piecePosition);
            case PAWN -> pawnMoves(board, piecePosition);
            case null -> throw new RuntimeException("Piece not implemented");
        };
    }

    /**
     * King moves one square vertically or horizontally
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    private ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition startPosition) {
        int[][] spots = {{1,1}, {1,-1}, {-1,1}, {-1,-1}, {1,0}, {-1,0}, {0,1}, {0,-1}};
        return getMovesForOffsets(board, startPosition, spots);
    }

    /**
     * Queen moves one or more squares diagonally or orthogonally
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    private ArrayList<ChessMove> queenMoves(ChessBoard board, ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(bishopMoves(board, startPosition));
        moves.addAll(rookMoves(board, startPosition));
        return moves;
    }

    /**
     * Bishop moves one or more squares diagonally
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    public ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition startPosition) {
        int[][] directions = {{1,1}, {-1,1}, {1,-1}, {-1,-1}};
        return getSlidingMoves(board, startPosition, directions);
    }

    /**
     * Knight jumps one square vertically and two squares horizontally
     * or jumps one square horizontally and two squares vertically
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    private ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition startPosition) {
        int[][] spots = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {-1,2}, {1,-2}, {-1,-2}};
        return getMovesForOffsets(board, startPosition, spots);
    }

    /**
     * Rook moves one or more squares orthogonally
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    public ArrayList<ChessMove> rookMoves(ChessBoard board, ChessPosition startPosition) {
        int[][] directions = {{1,0}, {0,1}, {0,-1}, {-1,0}};
        return getSlidingMoves(board, startPosition, directions);
    }

    /**
     * Generic method to get moves for pieces that move one square in specified offsets
     * (King and Knight)
     */
    private ArrayList<ChessMove> getMovesForOffsets(ChessBoard board, ChessPosition startPosition, int[][] offsets) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        for (int[] offset : offsets) {
            int x = offset[0];
            int y = offset[1];
            ChessPosition newPosition = new ChessPosition(row + x, col + y);

            if (!isValidPosition(newPosition)) {
                continue;
            }

            if (isEmptySquare(board, newPosition) || isDifferentColor(board, startPosition, newPosition)) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
        return moves;
    }

    /**
     * Generic method to get sliding moves for pieces that can move multiple squares
     * in specified directions (Bishop, Rook, Queen)
     */
    private ArrayList<ChessMove> getSlidingMoves(ChessBoard board, ChessPosition startPosition, int[][] directions) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        for (int[] dir : directions) {
            int x = dir[0];
            int y = dir[1];
            ChessPosition newPosition = new ChessPosition(row + x, col + y);

            while (isValidPosition(newPosition) && board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(startPosition, newPosition, null));
                x += dir[0];
                y += dir[1];
                newPosition = new ChessPosition(row + x, col + y);
            }

            if (isValidPosition(newPosition) && isDifferentColor(board, startPosition, newPosition)) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
        return moves;
    }

    /**
     * Pawn moves one or two squares forward on initial move
     * Can only capture diagonally one square
     *
     * @return ArrayList of all positions this chess piece can move to
     */
    public ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        int[][] directions;
        int penultimateRow;
        int startingRow;

        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            directions = new int[][]{{1, 1}, {1, -1}};
            penultimateRow = 7;
            startingRow = 2;
        } else {
            directions = new int[][]{{-1, 1}, {-1, -1}};
            penultimateRow = 2;
            startingRow = 7;
        }

        ChessPosition forwardPosition = new ChessPosition(row + directions[0][0], col);
        ChessPosition doubleForwardPosition = new ChessPosition(row + directions[0][0] * 2, col);

        // Check single move
        if (isEmptySquare(board, forwardPosition)) {
            addPawnMove(moves, startPosition, forwardPosition, row, penultimateRow);
        }

        // Check double move
        if (row == startingRow && isEmptySquare(board, forwardPosition)
                && isEmptySquare(board, doubleForwardPosition)) {
            moves.add(new ChessMove(startPosition, doubleForwardPosition, null));
        }

        // Check diagonal attacks
        for (int[] diag : directions) {
            int x = diag[0];
            int y = diag[1];
            ChessPosition newPosition = new ChessPosition(row + x, col + y);

            if (!isValidPosition(newPosition)) {
                continue;
            }

            if (!isEmptySquare(board, newPosition) && isDifferentColor(board, startPosition, newPosition)) {
                addPawnMove(moves, startPosition, newPosition, row, penultimateRow);
            }
        }
        return moves;
    }

    /**
     * Adds a pawn move with promotion options if on penultimate row
     */
    private void addPawnMove(ArrayList<ChessMove> moves, ChessPosition start,
                             ChessPosition end, int currentRow, int penultimateRow) {
        if (currentRow == penultimateRow) {
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
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
        return (row >= 1 && row <= 8 && col >= 1 && col <= 8);
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