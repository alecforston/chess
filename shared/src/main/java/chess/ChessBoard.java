package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //Remove all
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                this.squares[x][y] = null;
            }
        }

        ChessPiece.PieceType[] backRowPieces = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};

        int row = 1, col = 1;
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;

        for (ChessPiece.PieceType type : backRowPieces) {
            addPiece(new ChessPosition(row, col), color, type);
            col++;
        }

        row = 2; col = 1;
        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(row, col + i), color, ChessPiece.PieceType.PAWN);
        }

        row = 8; col = 1;
        color = ChessGame.TeamColor.BLACK;

        for (ChessPiece.PieceType type : backRowPieces) {
            addPiece(new ChessPosition(row, col), color, type);
            col++;
        }

        row = 7; col = 1;
        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(row, col + i), color, ChessPiece.PieceType.PAWN);
        }

    }

    private void addPiece(ChessPosition chessPosition, ChessGame.TeamColor color, ChessPiece.PieceType type) {
        squares[chessPosition.getRow() - 1][chessPosition.getColumn() - 1] = new ChessPiece(color, type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
