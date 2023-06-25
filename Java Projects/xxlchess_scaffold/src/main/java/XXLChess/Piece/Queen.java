package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Move;
import XXLChess.Board;

public class Queen extends Piece{
    public static final Move[] QUEEN_MOVES;

    static{
        Move[][] moves = {ROOK_MOVES, BISHOP_MOVES};
        QUEEN_MOVES = Piece.combineMoves(moves);
    }

    public Queen(boolean isWhite, Location location, Board board){
        super(PieceIndex.QUEEN, isWhite, location, QUEEN_MOVES, 9.5, board);
    }
}