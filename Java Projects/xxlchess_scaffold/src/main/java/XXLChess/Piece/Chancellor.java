package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Move;
import XXLChess.Board;

public class Chancellor extends Piece{
    public static final Move[] CHANCELLOR_MOVES;

    static{
        Move[][] moves = {KNIGHT_MOVES, ROOK_MOVES};
        CHANCELLOR_MOVES = Piece.combineMoves(moves);
    }

    public Chancellor(boolean isWhite, Location location, Board board){
        super(PieceIndex.CHANCELLOR, isWhite, location, CHANCELLOR_MOVES, 8.5, board);
    }
}