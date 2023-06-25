package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Move;
import XXLChess.Board;

public class Amazon extends Piece{
    public static final Move[] AMAZON_MOVES;

    static{
        Move[][] moves = {KNIGHT_MOVES, BISHOP_MOVES, ROOK_MOVES};
        AMAZON_MOVES = Piece.combineMoves(moves);
    }

    public Amazon(boolean isWhite, Location location, Board board){
        super(PieceIndex.AMAZON, isWhite, location, AMAZON_MOVES, 12, board);
    }
}