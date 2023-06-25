package XXLChess;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import XXLChess.Piece.PieceIndex;

/**
 * Assumes conditions given by {@code createSinglePieceBlockedBoard} in ChessTest.
 * InitHelper holds all Sets of valid moves for all the non-pawn piece types.
 * This is all done by hand to check as much of the validation code as possible
 */
public class InitHelper {
    /**
     * The main field initialized in InitHelper, initializing all moves with
     * PieceIndex
     * ordinal placements except for the Pawn
     */
    public static final List<Set<Location>> moveList;

    public static final Set<Location> VALID_KNIGHT_MOVES;
    public static final Set<Location> VALID_BISHOP_MOVES;
    public static final Set<Location> VALID_ROOK_MOVES;
    public static final Set<Location> VALID_KING_MOVES;
    public static final Set<Location> VALID_CAMEL_MOVES;
    public static final Set<Location> VALID_ARCHBISHOP_MOVES;
    public static final Set<Location> VALID_CHANCELLOR_MOVES;
    public static final Set<Location> VALID_AMAZON_MOVES;
    public static final Set<Location> VALID_QUEEN_MOVES;
    public static final Set<Location> VALID_GENERAL_MOVES;

    static {
        VALID_KNIGHT_MOVES = initKnight();
        VALID_BISHOP_MOVES = initBishop();
        VALID_ROOK_MOVES = initRook();
        VALID_KING_MOVES = initKing();
        VALID_CAMEL_MOVES = initCamel();
        VALID_ARCHBISHOP_MOVES = combineSets(VALID_BISHOP_MOVES, VALID_KNIGHT_MOVES);
        VALID_CHANCELLOR_MOVES = combineSets(VALID_KNIGHT_MOVES, VALID_ROOK_MOVES);
        VALID_QUEEN_MOVES = combineSets(VALID_BISHOP_MOVES, VALID_ROOK_MOVES);
        VALID_AMAZON_MOVES = combineSets(VALID_KNIGHT_MOVES, VALID_QUEEN_MOVES);
        VALID_GENERAL_MOVES = combineSets(VALID_KING_MOVES, VALID_KNIGHT_MOVES);
        moveList = new ArrayList<Set<Location>>();
        for (int i = 0; i < PieceIndex.size; i++) {
            moveList.add(null);
        }
        moveList.set(PieceIndex.KNIGHT.ordinal(), VALID_KNIGHT_MOVES);
        moveList.set(PieceIndex.BISHOP.ordinal(), VALID_BISHOP_MOVES);
        moveList.set(PieceIndex.ROOK.ordinal(), VALID_ROOK_MOVES);
        moveList.set(PieceIndex.KING.ordinal(), VALID_KING_MOVES);
        moveList.set(PieceIndex.CAMEL.ordinal(), VALID_CAMEL_MOVES);
        moveList.set(PieceIndex.ARCHBISHOP.ordinal(), VALID_ARCHBISHOP_MOVES);
        moveList.set(PieceIndex.CHANCELLOR.ordinal(), VALID_CHANCELLOR_MOVES);
        moveList.set(PieceIndex.GENERAL.ordinal(), VALID_GENERAL_MOVES);
        moveList.set(PieceIndex.QUEEN.ordinal(), VALID_QUEEN_MOVES);
        moveList.set(PieceIndex.AMAZON.ordinal(), VALID_AMAZON_MOVES);

    }

    /**
     * Converts an nx2 array of integers into an array of n locations
     * 
     * @param locsAsArray the nx2 array of ints
     * @return an array of n locations
     */
    private static Set<Location> intArrtoLocArr(int[][] locsAsArray) {
        Set<Location> res = new HashSet<Location>(locsAsArray.length);

        for (int i = 0; i < locsAsArray.length; i++) {
            res.add(new Location(locsAsArray[i]));
        }

        return res;
    }

    /**
     * Returns a set of all possible moves of a knight on an empty board starting at
     * (6, 6)
     * 
     * @return A HashSet of Locations of all possible moves from (6, 6)
     */
    private static Set<Location> initKnight() {
        int[][] locsAsArray = { { 8, 7 },
                { 5, 8 },
                { 4, 7 },
                { 4, 5 },
                { 5, 4 },
                { 7, 4 },
                { 8, 5 } };
        return intArrtoLocArr(locsAsArray);
    }

    /**
     * Returns a set of all possible moves of a bishop on an empty board starting at
     * (6, 6)
     * 
     * @return A HashSet of Locations of all possible moves from (6, 6)
     */
    private static Set<Location> initBishop() {
        int[][] locsAsArray = { { 5, 5 },
                { 7, 5 },
                { 8, 4 },
                { 9, 3 },
                { 10, 2 },
                { 11, 1 },
                { 12, 0 },
                { 5, 7 },
                { 4, 8 },
                { 3, 9 },
                { 2, 10 },
                { 1, 11 },
                { 0, 12 } };
        return intArrtoLocArr(locsAsArray);
    }

    /**
     * Returns a set of all possible moves of a king on an empty board starting at
     * (6, 6)
     * 
     * @return A HashSet of Locations of all possible moves from (6, 6)
     */
    private static Set<Location> initKing() {
        int[][] locsAsArray = {
                { 6, 7 },
                { 5, 7 },
                { 5, 6 },
                { 5, 5 },
                { 6, 5 },
                { 7, 5 } };
        return intArrtoLocArr(locsAsArray);
    }

    /**
     * Returns a set of all possible moves of a rook on an empty board starting at
     * (6, 6)
     * 
     * @return A HashSet of Locations of all possible moves from (6, 6)
     */
    private static Set<Location> initRook() {
        int[][] locsAsArray = { { 6, 5 },
                { 6, 7 },
                { 6, 8 },
                { 6, 9 },
                { 6, 10 },
                { 6, 11 },
                { 6, 12 },
                { 6, 13 },
                { 5, 6 },
                { 4, 6 },
                { 3, 6 },
                { 2, 6 },
                { 1, 6 },
                { 0, 6 } };
        return intArrtoLocArr(locsAsArray);
    }

    /**
     * Returns a set of all possible moves of a camel on an empty board starting at
     * (6, 6)
     * 
     * @return A HashSet of Locations of all possible moves from (6, 6)
     */
    private static Set<Location> initCamel() {
        int[][] locsAsArray = { { 9, 7 },
                { 5, 9 },
                { 3, 7 },
                { 3, 5 },
                { 5, 3 },
                { 7, 3 },
                { 9, 5 } };
        return intArrtoLocArr(locsAsArray);
    }

    /**
     * Returns a combination of a and b without changing either set
     * 
     * @param a a set to be merged
     * @param b a set to be merged
     * @return the merged set
     */
    private static Set<Location> combineSets(Set<Location> a, Set<Location> b) {
        Set<Location> res = new HashSet<Location>(a);
        res.addAll(b);
        return res;
    }

}
