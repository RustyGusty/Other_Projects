package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Board;

public class Bishop extends Piece{

    public Bishop(boolean isWhite, Location location, Board board){
        super(PieceIndex.BISHOP, isWhite, location, BISHOP_MOVES, 3.625, board);
    }
}