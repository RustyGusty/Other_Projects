package XXLChess.Piece;
import XXLChess.Location;
import XXLChess.Board;

public class Camel extends Piece{
    public Camel(boolean isWhite, Location location, Board board){
        super(PieceIndex.CAMEL, isWhite, location, CAMEL_MOVES, 2, board);
    }
}
