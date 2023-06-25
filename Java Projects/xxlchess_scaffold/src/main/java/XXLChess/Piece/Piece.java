package XXLChess.Piece;

import java.util.HashSet;
import java.util.Set;
import XXLChess.App;
import XXLChess.Board;
import XXLChess.Location;
import XXLChess.Move;

/**
 * Contains all methods relating to individual pieces on the board, mainly concerning
 * piece movement
 */
public abstract class Piece {
    
    // The 5 base movement cases that (could) be used by other pieces
    public static final Move[] BISHOP_MOVES;
    public static final Move[] KING_MOVES;
    public static final Move[] KNIGHT_MOVES;
    public static final Move[] CAMEL_MOVES;
    public static final Move[] ROOK_MOVES;
    
    static {
        int[][] b_moves = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        int[][] k_moves = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        int[][] n_moves = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
        int[][] c_moves = {{3, 1}, {1, 3}, {-1, 3}, {-3, 1}, {-3, -1}, {-1, -3}, {1, -3}, {3, -1}};
        int[][] r_moves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

        BISHOP_MOVES = Move.intArrToMoveArr(b_moves, true);
        KING_MOVES = Move.intArrToMoveArr(k_moves, false);
        KNIGHT_MOVES = Move.intArrToMoveArr(n_moves, false);
        CAMEL_MOVES = Move.intArrToMoveArr(c_moves, false);
        ROOK_MOVES = Move.intArrToMoveArr(r_moves, true);
    }

    /**
     * Aids in combining two movesets for pieces like the queen (rook + bishop)
     * @param moves a 2D Move[][] array, where move[i] is the moves of one piece
     * @return a Move[] array with all the moves in one array
     */
    public static Move[] combineMoves(Move[][] moves){
        int length = 0;
        for(Move[] moveset : moves) {
            length += moveset.length;
        }
        Move[] res = new Move[length];
        int ind = 0;
        for(Move[] moveset : moves) {
            System.arraycopy(moveset, 0, res, ind, moveset.length);
            ind += moveset.length;
        } 
        return res;
    }
    

    public final PieceIndex pieceIndex; // Indexed the same as the photos are indexed (ignoring color)
    public final boolean isWhite; // True for a white piece, false for a black piece
    public Location location; // x and y position
    public final Move[] possibleMoves; // an array of moves, with an x-dir, y-dir, and continue flag
                                       // for if the piece can move continuously in that direction
    public Set<Location> validMoves; // A Set that is constantly updated to hold all
                                      // valid end locations at the given board state
    private final double pieceValue; // The value assigned, to be used for a display or AI
    public boolean hasMoved; // true if the piece has moved before, false otherwise
    public Board board; // Reference to the currently-used board


    /**
     * The super constructor to initialize all pieces accordingly
     * @param pieceIndex a PieceIndex enumerator corresponding to the piece type
     * @param isWhite {@code true} if white is the owner, {@code false} if black is the owner
     * @param location Location of the Piece's Location on the board
     * @param possibleMoves The possible moves of this piece as a Move[] array 
     * @param pieceValue double to represent the piece value
     */
    public Piece(PieceIndex pieceIndex, boolean isWhite, Location location, Move[] possibleMoves, double pieceValue, Board board) {
        this.pieceIndex = pieceIndex;
        this.location = location;
        this.isWhite = isWhite;
        this.possibleMoves = possibleMoves;
        this.validMoves = new HashSet<Location>();
        this.pieceValue = pieceValue;
        this.hasMoved = false;
        this.board = board;
    }

    /**
     * Adds this piece to both the board and the pieceList of Board and return it
     * @return the added piece {@code this}
     */
    public Piece addPiece() {
        board.setPiece(this, this.location);
        board.pieceList.add(this);
        return this;
    }

    /**
     * Returns true if targetLocation is a valid move after having initialzed validMoves, false otherwise
     * @param targetLocation Location representing the target being checked against
     * @return {@code true} if this can move there legally, {@code false} otherwise
     */
    public boolean isValidMove(Location targetLocation) {
        return validMoves.contains(targetLocation);
    }

    /**
     * Determine whether a move to this square is a valid move by checking all possible move candidates
     * to see if a move qualifies. This assumes all continuous movement can be blocked.
     * This ignores all other special cases, which should be overwritten in their respective classes 
     * 
     * @param newLocation - The target final location to be checked against
     * @return - true iff the move is a valid move
     */
    public boolean isNewValidMove(Location newLocation){
        // If newLocation out of bounds, return false
        if(!newLocation.isValid()) return false;
        // If newLocation has same color piece as own, return false immediately
        if(board.getPiece(newLocation) != null && board.getPiece(newLocation).isWhite == isWhite) return false;
        for(Move candidateMove : possibleMoves) {
            if(candidateMove.continueFlag) {
                Location curLoc = new Location(this.location.x, this.location.y);
                while(curLoc.stepForward(candidateMove)){
                    if(curLoc.equals(newLocation)) return true;
                    if(board.getPiece(curLoc) != null) break;
                } // Breaks once curLoc is now invalid
            } else {
                Location candidateFinal = this.location.getLocationFromMove(candidateMove, 1);
                if(newLocation.equals(candidateFinal)) return true;
            }
            
        }
        
        return false;
    }

    /**
     * Returns a Set of Locations with all valid target locations
     * @return a Set of all valid target Locations
     */
    public Set<Location> initializePieceMoves() {
        // Start by clearing
        Set<Location> res = new HashSet<Location>();
        Location potentialLocation;
        // and check every possible move if it's valid
        for(int i = 0; i < App.BOARD_WIDTH; i++) {
            for(int j = 0; j < App.BOARD_WIDTH; j++) {
                potentialLocation = new Location(j, i);
                if(this.isNewValidMove(potentialLocation)) {
                    res.add(potentialLocation);
                }
            }
        }
        return res;
    }

    /**
     * Makes the move of this to targetLocation, removing any captured pieces from the game
     * and returning the value of the captured piece (0 if nothing captured)
     * @param targetLocation Location of the square-to-be-moved-to (should be valid)
     * @return a double of the captured piece's value (0 if nothing captured)
     */
    public double makeMove(Location targetLocation) {
        Piece capturedPiece = board.setPiece(this, targetLocation);
        board.setPiece(null, this.location);
        double returnVal = 0;
        if(capturedPiece != null) {
            board.pieceList.remove(capturedPiece);
            returnVal = capturedPiece.pieceValue;
        }
        this.location = targetLocation;
        return returnVal;
    }
}