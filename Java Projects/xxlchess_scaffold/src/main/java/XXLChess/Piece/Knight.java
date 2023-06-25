package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Board;

public class Knight extends Piece{
    public Knight(boolean isWhite, Location location, Board board){
        super(PieceIndex.KNIGHT, isWhite, location, KNIGHT_MOVES, 2, board);
    }
}