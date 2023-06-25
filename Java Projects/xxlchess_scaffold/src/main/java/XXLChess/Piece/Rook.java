package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Board;

public class Rook extends Piece{
    public Rook(boolean isWhite, Location location, Board board){
        super(PieceIndex.ROOK, isWhite, location, ROOK_MOVES, 5.25, board);
    }
}