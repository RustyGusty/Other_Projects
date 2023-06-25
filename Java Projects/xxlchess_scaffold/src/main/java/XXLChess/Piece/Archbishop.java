package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Move;
import XXLChess.Board;

public class Archbishop extends Piece{
    public static final Move[] ARCHBISHOP_MOVES;

    static{
        Move[][] moves = {KNIGHT_MOVES, BISHOP_MOVES};
        ARCHBISHOP_MOVES = Piece.combineMoves(moves);
    }

    public Archbishop(boolean isWhite, Location location, Board board){
        super(PieceIndex.ARCHBISHOP, isWhite, location, ARCHBISHOP_MOVES, 7.5, board);
    }
}