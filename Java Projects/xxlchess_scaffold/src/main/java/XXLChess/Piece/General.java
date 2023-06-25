package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Move;
import XXLChess.Board;

public class General extends Piece{
    public static final Move[] GENERAL_MOVES;

    static{
        Move[][] moves = {KNIGHT_MOVES, KING_MOVES};
        GENERAL_MOVES = Piece.combineMoves(moves);
    }

    public General(boolean isWhite, Location location, Board board){
        super(PieceIndex.GENERAL, isWhite, location, GENERAL_MOVES, 5, board);
    }
}
