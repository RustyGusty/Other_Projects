package XXLChess.Piece;
import XXLChess.Board;
import XXLChess.Location;
import XXLChess.Move;

public class King extends Piece{

    public King(boolean isWhite, Location location, Board board){
        super(PieceIndex.KING, isWhite, location, KING_MOVES, 9999999, board);
    }

    //Same but check for castling rights
    @Override
    public boolean isNewValidMove(Location newLocation){
        if(super.isNewValidMove(newLocation))
            return true;
        else if(newLocation.equals(new Location(location.x + 2, location.y)) && canCastle(1))
            return true;
        else if(newLocation.equals(new Location(location.x - 2, location.y)) && canCastle(-1))
            return true;
        return false;
    }

    /**
     * Castling rules:
     * King and Rook cannot have moved before,
     * King cannot castle out of, through, or into check,
     * No pieces obstructing movement of king and rook,
     * Castling short moves king 2 squares right and rook to the left side
     * 
     * @param dir - 1 for short castling (right), -1 for long castling
     * @return true iff can castle in the given direction
     */
    public boolean canCastle(int dir){
        if(hasMoved || board.isInCheck(isWhite)) return false;
        Location curLoc = new Location(location.x, location.y);
        Move castleMove = new Move(dir, 0, true);
        Piece curPiece = null;
        // Only check for attacked squares twice
        int i = 0;
        // Shift over by dir until I hit a piece or the edge of the board
        while(curLoc.stepForward(castleMove)){
            if(i < 2 && board.isAttackedBy(!isWhite, curLoc)) return false;
            i++;
            curPiece = board.getPiece(curLoc);
            if(curPiece != null) {
                // If it's a non-rook piece, then castling not allowed
                if(!(curPiece instanceof Rook)){
                    return false;
                } else {
                    // If the rook has already moved, then return false
                    if(curPiece.hasMoved) return false;
                    break;
                }
            }
        }
        // If no rook was found, then return false
        if(!(curPiece instanceof Rook)) return false;
        // If the rook was right next to the king, then check the next space over also
        if((dir == 1 && curLoc.x <= location.x + 1) || (dir == -1 && curLoc.x >= location.x - 1)){
            curLoc.stepForward(castleMove);
            if(board.isAttackedBy(!isWhite, curLoc) || board.getPiece(curLoc) != null) return false;
        }
        return true;
    }
}
