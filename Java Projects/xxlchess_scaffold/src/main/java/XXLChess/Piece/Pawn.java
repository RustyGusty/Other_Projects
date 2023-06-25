package XXLChess.Piece;
import XXLChess.Board;
import XXLChess.Location;
import XXLChess.Move;
import XXLChess.App;

public class Pawn extends Piece{
    private final Move[] pawnCaptures; 
    private final int promotionRow;

    public Pawn(boolean isWhite, Location location, Board board){
        super(PieceIndex.PAWN, isWhite, location, new Move[1], 1, board);
        pawnCaptures = new Move[2];
        int y;
        if(isWhite){
            y = -1;
            promotionRow = App.BOARD_WIDTH - 8;
        }
        else {
            y = 1;
            promotionRow = 7;   
        }

        possibleMoves[0] = new Move(0, y, false);
        pawnCaptures[0] = new Move(-1, y, false);
        pawnCaptures[1] = new Move(1, y, false);
    }

    // Same save for funky pawn
    @Override
    public boolean isNewValidMove(Location newLocation) {
        Piece targetPiece = board.getPiece(newLocation);
        if(targetPiece == null) {
            Location oneStep = this.location.getLocationFromMove(possibleMoves[0], 1);
            if(board.getPiece(oneStep) == null){
                if(newLocation.equals(oneStep))
                    return true;
                    else if (!hasMoved && (location.y == 1 || location.y == App.BOARD_WIDTH - 2)) {
                    Location twoStep = oneStep.getLocationFromMove(possibleMoves[0], 1);
                    if(board.getPiece(twoStep) == null)
                        if(newLocation.equals(twoStep))
                            return true;
                }
            }
        }
        else if(targetPiece.isWhite != this.isWhite)
            for(int i = 0 ; i < 2; i++) 
                if(newLocation.equals(this.location.getLocationFromMove(pawnCaptures[i], 1)))
                    return true;
        return false;
    }
    
    /**
     * Checks for promotion. If promotion happens, make it happen by placing a queen instead of this guy
     */
    private void promotion(){
        if(location.y == promotionRow){
            new Queen(isWhite, location, board).addPiece();
            board.pieceList.remove(this);
        }
    }

    // Same but check promotion
    @Override
    public double makeMove(Location targetLocation){
        double pieceValue = super.makeMove(targetLocation);
        promotion();
        return pieceValue;
    }
}
