package XXLChess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import XXLChess.Piece.*;

/**
 * Deals with anything relating to the board (initializing the board, 
 * getting pieces from the board, moving pieces on the board, checking for checks, etc.)
 */
public class Board {


    public final Piece[][] board; // a 2D array of pieces. Empty spaces are null 
    public final Set<Location> whiteAttackedSet; // A Set of all squares attacked by white
    public final Set<Location> blackAttackedSet; // A Set of all squares attacked by black
    public final Set<Piece> pieceList; // A Set of all pieces in play

    private King whiteKing; // The white king, initialized in initializeBoard 
    private King blackKing; // The black king 
    
    /**
     * Constructor for building a board based on the layout found in levelFilename
     * @param width size of the square board (squares only)
     * @param levelFilename String with the filename of the properly formatted layout (see initializeBoard)
     * @param whiteMovesFirst true if white moves first (like normal), false otherwise
     */
    public Board(int width, String levelFilename, boolean whiteMovesFirst){
        board = new Piece[width][width];
        whiteAttackedSet = new HashSet<Location>();
        blackAttackedSet = new HashSet<Location>();
        pieceList = new HashSet<Piece>();
        initializeBoard(levelFilename);
        initializeMoves(whiteMovesFirst);
    }
    
    /**
     * Constructor for building an empty board
     * @param width size of the square board (squares only)
     */
    public Board(int width) {
        board = new Piece[width][width];
        whiteAttackedSet = new HashSet<Location>();
        blackAttackedSet = new HashSet<Location>();
        pieceList = new HashSet<Piece>();
    }

    /**
     * Given a Location, returns the piece on the board associated with that Location (null if empty)
     * @param loc Location of the piece to be returned
     * @return the Piece on that Location on the board, null if that Location is empty
     */
    public Piece getPiece(Location loc) {
        return board[loc.y][loc.x];
    }
    
    /**
     * Given a row and col, returns the piece on the board associated with that row and col (null if empty)
     * Note that this is identical to getPieceFromLocation but with indicies instead
     * @param row int representing the row number
     * @param col int representing the column number
     * @return the Piece on that row and col combo on the board, null if that Location is empty
     */
    public Piece getPiece(int row, int col){
        return board[row][col];
    }

    /**
     * Places newPiece onto the Location specified by targetLocation, returning the overwritten Piece
     * (null if no Piece overwritten)
     * @param newPiece Piece to be placed on the board
     * @param targetLocation Location for the Piece to be placed in
     * @return The Piece that was in targetLocation but was replaced by setPiece
     */
    public Piece setPiece(Piece newPiece, Location targetLocation){
        Piece oldPiece = getPiece(targetLocation);
        board[targetLocation.y][targetLocation.x] = newPiece;
        return oldPiece;
    }

    /**
     * If removedPiece is non-null, removes that piece from the game and returns
     * whether or not the removal was a success
     * @param removedPiece Piece to be removed from the game
     * @return {@code removedPiece != null}, or whether a piece was actually removed
     */
    public boolean removePiece(Piece removedPiece) {
        if(removedPiece == null)
            return false;
        pieceList.remove(removedPiece);
        setPiece(null, removedPiece.location);
        return true;
    }

    /**
     * Given a filename level, preps the board accordingly, initializing all pieces on it.
     * File must be formatted with up to BOARD_WIDTH characters before a \n or \r character
     * is found, and must also only contain valid chess characters pbnckaeqg and uppercase
     * variants, as well as ' ' for empty locations
     * @param level the filename for a text file formatted properly
     */
    public void initializeBoard(String level) {
        File file = new File(level);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            int ch; PieceIndex p; boolean isWhite; Location newLoc;
            for(int i = 0; i < App.BOARD_WIDTH; i++){ 
                ch = br.read();
                for(int j = 0; j < App.BOARD_WIDTH; j++) {
                    if(ch == '\n' || ch == -1) break;
                    if(ch == ' ' || ch == '\r') {
                        ch = br.read();
                        continue;
                    }
                    p = PieceIndex.letterToIndex((char) ch);
                    if(p == null) {
                        br.close();
                        throw new IOException();
                    }
                    isWhite = Character.isLowerCase(ch);
                    newLoc = new Location(j, i);
                    addPieceFromPieceIndex(p, isWhite, newLoc);
                    ch = br.read();
                }
                if(ch == '\r') {
                    br.read();
                }
                else if(ch == -1){
                    br.close();
                    break;
                }
                else if(ch != '\n'){
                    br.close();
                    throw new IOException();
                }
            }
            br.close();
        }
        catch(FileNotFoundException e){ 
            System.out.println(level + " not found.");
            System.exit(1);
        }
        catch(IOException e) {
            System.out.println("The text file was not properly formatted");
            System.exit(1);
        }
    }

    /**
     * Given a PieceIndex, adds the corresponding piece to the board, updating the
     * whiteKing and blackKing fields accordingly, returning the newly added piece
     * @param p PieceIndex of the piece to be added
     * @param isWhite {@code true} if the piece is to be white's, {@code false} if the piece is black's
     * @param newLoc the position of the new piece on the board
     * @return the newly added piece
     */
    public Piece addPieceFromPieceIndex(PieceIndex p, boolean isWhite, Location newLoc) {

        switch(p){
            case PAWN:
                return new Pawn(isWhite, newLoc, this).addPiece();
            case BISHOP:
                return new Bishop(isWhite, newLoc, this).addPiece();
            case KNIGHT:
                return new Knight(isWhite, newLoc, this).addPiece();
            case CAMEL:
                return new Camel(isWhite, newLoc, this).addPiece();
            case ROOK:
                return new Rook(isWhite, newLoc, this).addPiece();
            case GENERAL:
                return new General(isWhite, newLoc, this).addPiece();
            case CHANCELLOR:
                return new Chancellor(isWhite, newLoc, this).addPiece();
            case ARCHBISHOP:
                return new Archbishop(isWhite, newLoc, this).addPiece();
            case AMAZON:
                return new Amazon(isWhite, newLoc, this).addPiece();
            case KING:
                King newKing = new King(isWhite, newLoc, this);
                newKing.addPiece();
                if(isWhite)
                    whiteKing = newKing;
                else 
                    blackKing = newKing;
                return newKing;
            case QUEEN:
                return new Queen(isWhite, newLoc, this).addPiece();
            default:
                return null;
        }
    }

    /**
     * Updates attackedSpaces set for the owner of the attacking piece, by adding
     * all valid moves of the attackingPiece to it
     * @param attackingPiece Piece that is threatening to move
     */
    public void addAttackedSpace(Piece attackingPiece){
        Set<Location> attackedSet = attackingPiece.isWhite ? whiteAttackedSet : blackAttackedSet;
        for(Location loc : attackingPiece.validMoves){
            attackedSet.add(loc);
        }
    }

    /**
     * Returns true if the attacking player specified by whiteAttacking has targetLocation as
     * a valid move towards it
     * @param whiteAttacking {@code true} if white is moving, {@code false} if black is moving
     * @param targetLocation Location which is being checked
     * @return {@code true} if targetLocation is a valid attack by the player, {@code false} otherwise
     */
    public boolean isAttackedBy(boolean whiteAttacking, Location targetLocation) {
        if(whiteAttacking) {
            return whiteAttackedSet.contains(targetLocation);
        } else {
            return blackAttackedSet.contains(targetLocation);
        }
    }

    /**
     * Used for temporary moves so that the attackedSpaces set is preserved but allowing checking newly-moved pieces
     * Returns true if the player specified by isWhite is in check, false otherwise
     * @param isWhite {@code true} if white is being checked, {@code false} if black is being checked
     * @return {@code true} if isWhite is in check, {@code} false if isWhite is not in check
     */
    private boolean calculateCheck(boolean isWhite) {
        King targetKing = blackKing;
        if(isWhite)
            targetKing = whiteKing;
        for(Piece piece : pieceList){
            Set<Location> attackedSpaces = piece.initializePieceMoves();
            if(attackedSpaces.contains(targetKing.location))
                return true;
        }
        return false;
    }

    /**
     * Returns true if the player specified by isWhite is in check, false otherwise
     * @param isWhite {@code true} if white is being checked, {@code false} if black is being checked
     * @return {@code true} if isWhite is in check, {@code false} if isWhite is not in check
     */
    public boolean isInCheck(boolean isWhite){
        if(isWhite){
            return isAttackedBy(false, whiteKing.location);
        } else {
            return isAttackedBy(true, blackKing.location);
        }
    }

    /**
     * Simulates a move of movedPiece to targetLocation and checks if that would result in check.
     * Resets the board to how it was originally and returns true if the king was in check, false otherwise
     * @param movedPiece Piece that is being tested for movement
     * @param targetLocation Location of the target destination of the move 
     * @return {@code true} if the owner of movedPiece is in check after the move, {@code false} otherwise
     */
    public boolean moveAndInCheck(Piece movedPiece, Location targetLocation){
        Location originalLocation = movedPiece.location;
        Piece capturedPiece = getPiece(targetLocation);
        movedPiece.makeMove(targetLocation);
        boolean res = calculateCheck(movedPiece.isWhite);
        if((movedPiece instanceof Pawn)){
            Piece resPiece = getPiece(targetLocation);
            if(!(resPiece instanceof Pawn)) {
                pieceList.remove(resPiece);
                movedPiece.addPiece();
            } 
            movedPiece.location = originalLocation;
            setPiece(movedPiece, originalLocation);
            setPiece(null, targetLocation);

        } else {
            movedPiece.makeMove(originalLocation);
        }
        if(capturedPiece != null){
            capturedPiece.addPiece();
        }
        
        return res;
    }

    /**
     * Initializes all possible moves of all the pieces given in pieceList, as well as the 
     * attackedSpaces sets, returning true if this is checkmate
     * @param isWhiteTurn {@code true} if white is about to move, {@code false} if black is about to move
     * @return {@code true} if the mover has a valid move, {@code false} if the owner has no valid moves and is in checkmate (or stalemate)
     */
    /**
     * Initializes all possible moves of all the pieces given in pieceList, as well as the 
     * attackedSpaces sets, returning true if the moving player has no valid moves
     * @param isWhiteTurn {@code true} if white is about to move, {@code false} if black is about to move
     * @return {@code true} if the mover has a valid move, {@code false} if the owner has no valid moves and is in checkmate (or stalemate)
     */
    public boolean initializeMoves(boolean isWhiteTurn){
        whiteAttackedSet.clear(); // Clear the whiteAttackedSet
        blackAttackedSet.clear(); // Clear the blackAttackedSet

        boolean moveAvailable = false; // Default state is that no move is available
        
        King targetKing = getKing(isWhiteTurn); // Get the correct king, used for checking things later
        
        for(Piece piece : pieceList) {
            if(!(piece instanceof King)) {
                piece.validMoves = piece.initializePieceMoves();
                addAttackedSpace(piece);
            }
        }
        // Castling depends on the attacked spaces in a special way, so handle those last
        whiteKing.validMoves = whiteKing.initializePieceMoves();
        addAttackedSpace(whiteKing);
        blackKing.validMoves = blackKing.initializePieceMoves();
        addAttackedSpace(blackKing);
        // Save this variable so we don't recall the function each time
        boolean inCheck = isInCheck(isWhiteTurn);
        // Can't iterate over a changing set, so have to make temporary shallow copies
        Set<Piece> tempList = new HashSet<Piece>(pieceList);

        // Iterate over every single possible move of the moving player
        // To see if we need to make any corrections from our naive movelist
        for(Piece piece : tempList) {
            // We don't need to worry about the opponent player since they're not moving
            if(piece.isWhite == isWhiteTurn) {
                // If the king is already in check, or if that piece is attacked, or the piece is the king
                // then we physically test all of those moves to see if one of them is actually invalid
                if (inCheck || piece == targetKing || isAttackedBy(!isWhiteTurn, piece.location)){
                    // Temporary set for iterating
                    Set<Location> tempSet = new HashSet<Location>(piece.validMoves);
                    for(Location targetLocation : tempSet) {
                        // If after the move, the moving player is in check, then disqualify that move
                        if(moveAndInCheck(piece, targetLocation))
                            piece.validMoves.remove(targetLocation);
                        
                    }
                }
                // If we were able to find available, then set moveAvailable to be true
                if(!moveAvailable && !piece.validMoves.isEmpty())
                    moveAvailable = true;
            }
        }
        return moveAvailable;
    }


    /**
     * @param whiteTurn
     * @return
     */
    public King getKing(boolean whiteTurn){
        return whiteTurn ? whiteKing : blackKing;
    }


}
