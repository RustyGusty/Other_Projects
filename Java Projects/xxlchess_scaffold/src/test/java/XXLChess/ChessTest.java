package XXLChess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import XXLChess.Piece.Piece;
import XXLChess.Piece.PieceIndex;

import java.util.*;

public class ChessTest {

    /*
     * Testing whether it properly reads the basic board and places down
     * all the pieces properly
     */
    @Test
    void buildStandardBoard() {
        
        Board board = new Board(14, "src/test/java/XXLChess/test_files/startBoard.txt", true);
        checkStandardBoard(board);
    }

    public static void checkStandardBoard(Board board) {
        PieceIndex pieceIndexList[] = { PieceIndex.ROOK, PieceIndex.KNIGHT, PieceIndex.BISHOP, PieceIndex.ARCHBISHOP,
                PieceIndex.CAMEL, PieceIndex.GENERAL, PieceIndex.AMAZON, PieceIndex.KING, PieceIndex.GENERAL,
                PieceIndex.CAMEL, PieceIndex.CHANCELLOR, PieceIndex.BISHOP, PieceIndex.KNIGHT, PieceIndex.ROOK };

        for (int j = 0; j < 14; j++) {
            // Test non-pawn pieces
            for (int i = 0; i < 14; i += 13) {
                assertEquals(pieceIndexList[j], board.getPiece(i, j).pieceIndex);
            }

            // Test pawns
            for (int i = 1; i < 13; i += 11) {
                assertEquals(PieceIndex.PAWN, board.getPiece(i, j).pieceIndex);
            }

            // Test empty spaces
            for (int i = 2; i < 12; i++) {
                assertEquals(null, board.getPiece(i, j));
            }
        }
    }

    /**
     * Tests all non-pawn pieces and their movements for both black and white,
     * including blocks and (for non-king pieces) captures, and actual board movement
     */
    @Test
    void testPieceMovement() {
        boolean foo[] = {true, false};

        Location newLoc = new Location(6, 6);
        for(boolean isWhite : foo)
            for(PieceIndex ind : PieceIndex.values()) {
                if(ind == PieceIndex.PAWN)
                    continue;
                Board board = createSinglePieceBlockedBoard(ind, isWhite, newLoc);
                Piece curPiece = board.getPiece(6, 6);
                assertEquals(ind, curPiece.pieceIndex);
                if(curPiece != null)
                    assertEquals(InitHelper.moveList.get(ind.ordinal()), curPiece.validMoves, "Failed at PieceIndex " + ind.toString());
                Location oldLocation = curPiece.location;
                Location targetLocation = curPiece.validMoves.iterator().next();
                curPiece.makeMove(targetLocation);
                assertEquals(null, board.getPiece(oldLocation));
                assertEquals(curPiece, board.getPiece(targetLocation));
            }
    }

    /**
     * For assistance in testing. Returns a new board with the placedPiece
     * in the given position and the possible moves initialized with white to move
     * with kings out of the way at (0, 4) and (0, 8) (unless a king is initialized),
     * four friendly knights to block movement to the right at (7, 6), (7, 7), (7, 8), and (7, 9)),
     * and four opponent knights at (6, 5), (5, 5), (4, 5), and (3, 5) to check captures
     * 
     * @param placedPieceIndex PieceIndex to use to get the Piece to be placed
     * @param isWhite {@code true} if the piece is white's, {@code false} if the piece is black's
     * @param newLoc Location of the place to have the piece 
     * @return the initialized board as given above
     */
    private Board createSinglePieceBlockedBoard(PieceIndex placedPieceIndex, boolean isWhite, Location newLoc) {
        Board res = createSinglePieceBoard(placedPieceIndex, isWhite, newLoc, new Location(0, 8), new Location(0, 4));
        for(int i = 6; i <= 9; i++)
            res.addPieceFromPieceIndex(PieceIndex.KNIGHT, isWhite, new Location(7, i));
        if (placedPieceIndex != PieceIndex.KING)
            for(int i = 6; i >= 3; i--)
                res.addPieceFromPieceIndex(PieceIndex.KNIGHT, !isWhite, new Location(i, 5));
        res.initializeMoves(isWhite);
        return res;
    }

    /**
     * For assistance in testing. Returns a new board with the placedPiece
     * in the given position and the possible moves initialized with white to move
     * with kings out of the way at (0, 4) and (0, 8) (unless a king is initialized)
     * 
     * @param placedPieceIndex PieceIndex to use to get the Piece to be placed
     * @param isWhite {@code true} if the piece is white's, {@code false} if the piece is black's
     * @param newLoc Location of the place to have the piece 
     * @return the initialized board as given above
     */
    private Board createSinglePieceBoard(PieceIndex placedPieceIndex, boolean isWhite, Location newLoc) {
        return createSinglePieceBoard(placedPieceIndex, isWhite, newLoc, new Location(0, 8), new Location(0, 4));
    }

    /**
     * For assistance in testing. Returns a new board with the placedPiece
     * in the given position and the possible moves initialized with white to move
     * with kings in the specified position
     * 
     * @param placedPieceIndex PieceIndex to use to get the Piece to be placed
     * @param isWhite {@code true} if the piece is white's, {@code false} if the piece is black's
     * @param newLoc Location of the place to have the piece 
     * @param ownerKingLoc Location of the owner's king
     * @parma oppKingLoc Location of the opponent's king
     * 
     * @return the initialized board
     */
    private Board createSinglePieceBoard(PieceIndex placedPieceIndex, boolean isWhite, Location newLoc, Location ownerKingLoc, Location oppKingLoc) {
        Board res = new Board(14);
        res.addPieceFromPieceIndex(placedPieceIndex, isWhite, newLoc);
        // Place king outside of any piece's range to make sure they don't interfere with the valid spaces
        if(placedPieceIndex != PieceIndex.KING) 
            res.addPieceFromPieceIndex(PieceIndex.KING, isWhite, ownerKingLoc);
        res.addPieceFromPieceIndex(PieceIndex.KING, !isWhite, oppKingLoc);

        res.initializeMoves(true);
        return res;
    }

    /**
     * Special case: Pawn movement. Checks for white:
     * 1) Hasn't moved, 13th row, can move twice;
     * 2) Has moved, 13th row, cannot move twice;
     * 3) Hasn't moved, not 13th row, cannot move twice;
     * 4) Hasn't moved, 13th row, cannot move twice because second square blocked by opponent piece
     * 5) Hasn't moved, 13th row, cannot move beceause square blocked by opponent piece
     * 6) Captures allowed if opposing piece placed diagonally
     * 7) Queen promotion when moving to 6th row;
     */
    @Test
    void whitePawnTests() {
        Location rank2 = new Location(12, 12);
        Location rank3 = new Location(12, 11);
        Location rank4 = new Location(12, 10);
        Location promoSquare = new Location(12, 6);

        Board board = createSinglePieceBoard(PieceIndex.PAWN, true, rank2);
        Piece pawn = board.getPiece(rank2);
        // 1) Hasn't moved, 13th row, can move twice;
        assertEquals(new HashSet<Location>(Arrays.asList(rank3, rank4)), pawn.validMoves);
        // 2) Has moved, 13th row, cannot move twice
        pawn.hasMoved = true; // this is set separately in App.java to avoid test move errors, so it is set manually here also
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(rank3)), pawn.validMoves);
        // 3) Hasn't moved, not 13th row, cannot move twice;
        pawn.hasMoved = false;
        pawn.makeMove(rank3);
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(rank4)), pawn.validMoves);
        // 4) Hasn't moved, 13th row, cannot move twice because second square blocked by opponent piece
        pawn.makeMove(rank2);
        board.addPieceFromPieceIndex(PieceIndex.KNIGHT, false, rank4);
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(rank3)), pawn.validMoves);
        // 5) Hasn't moved, 13th row, cannot move beceause square blocked by opponent piece
        Piece knight = board.getPiece(rank4);
        knight.makeMove(rank3);
        board.initializeMoves(true);
        assertTrue(pawn.validMoves.isEmpty());
        // 6) Captures allowed if opposing piece placed diagonally
        board.addPieceFromPieceIndex(PieceIndex.CAMEL, false, new Location(11, 11));
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(11, 11))), pawn.validMoves);
        // 7) Queen promotion when moving to middle rows
        pawn.makeMove(new Location(12, 7));
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(promoSquare)), pawn.validMoves);
        pawn.makeMove(promoSquare);
        Piece queen = board.getPiece(promoSquare);
        assertEquals(PieceIndex.QUEEN, queen.pieceIndex);
    }

    /**
     * Special case: Pawn movement. Checks for black:
     * 1) Hasn't moved, 2nd row, can move twice;
     * 2) Has moved, 2nd row, cannot move twice;
     * 3) Hasn't moved, not 2nd row, cannot move twice;
     * 4) Hasn't moved, 2nd row, cannot move twice because second square blocked by opponent piece
     * 5) Hasn't moved, 2nd row, cannot move beceause square blocked by opponent piece
     * 6) Captures allowed if opposing piece placed diagonally
     * 7) Queen promotion when moving to 7th row;
     */
    @Test
    void blackPawnTests() {
        Location rank13 = new Location(12, 1);
        Location rank12 = new Location(12, 2);
        Location rank11 = new Location(12, 3);
        Location promoSquare = new Location(12, 7);

        Board board = createSinglePieceBoard(PieceIndex.PAWN, false, rank13);
        Piece pawn = board.getPiece(rank13);
        // 1) Hasn't moved, 2nd row, can move twice;
        assertEquals(new HashSet<Location>(Arrays.asList(rank12, rank11)), pawn.validMoves);
        // 2) Has moved, 2nd row, cannot move twice
        pawn.hasMoved = true; // this is set separately in App.java to avoid test move errors, so it is set manually here also
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(rank12)), pawn.validMoves);
        // 3) Hasn't moved, not 2nd row, cannot move twice;
        pawn.hasMoved = false;
        pawn.makeMove(rank12);
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(rank11)), pawn.validMoves);
        // 4) Hasn't moved, 2nd row, cannot move twice because second square blocked by opponent piece
        pawn.makeMove(rank13);
        Piece knight = board.addPieceFromPieceIndex(PieceIndex.KNIGHT, true, rank11);
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(rank12)), pawn.validMoves);
        // 5) Hasn't moved, 2nd row, cannot move beceause square blocked by opponent piece
        knight.makeMove(rank12);
        board.initializeMoves(true);
        assertTrue(pawn.validMoves.isEmpty());
        // 6) Captures allowed if opposing piece placed diagonally
        board.addPieceFromPieceIndex(PieceIndex.CAMEL, true, new Location(11, 2));
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(11, 2))), pawn.validMoves);
        // 7) Queen promotion when moving to middle rows
        pawn.makeMove(new Location(12, 6));
        board.initializeMoves(true);
        assertEquals(new HashSet<Location>(Arrays.asList(promoSquare)), pawn.validMoves);
        pawn.makeMove(promoSquare);
        Piece queen = board.getPiece(promoSquare);
        assertEquals(PieceIndex.QUEEN, queen.pieceIndex);
    }

    /**
     * Tests valid moves when checking: Block, move king, and capture attacking piece
     */
    @Test
    void check() {
        Board board = new Board(14, "src/test/java/XXLChess/test_files/check.txt", false);
        // Knight at (1, 0) should have no valid moves
        Piece knight = board.getPiece(1, 0);
        assertTrue(knight.validMoves.isEmpty());
        // Pawn at (1, 1) should have no valid moves
        Piece pawn = board.getPiece(1, 1);
        assertTrue(pawn.validMoves.isEmpty());
        // Bishop at (0, 3) should be able to block check on king
        Piece bishop = board.getPiece(0, 3);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(4, 1))), bishop.validMoves);
        // King at (0, 4) should be able to move out of check (but not into other king)
        Piece king = board.getPiece(0, 4);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(3, 1))), king.validMoves);
        // Chancellor at (5, 0) should be able to capture attacker
        Piece chancellor = board.getPiece(5, 0);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(4, 5))), chancellor.validMoves);
        // General at (5, 5) should be able to capture attacker or block
        Piece general = board.getPiece(5, 5);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(4, 5), new Location(4, 4), new Location(4, 3))), general.validMoves);
    }

    /**
     * Special Case: Castle rules. Tests for black only:
     * 1) valid short and long castles (pieces attacking rook-moved squares only)
     * 2) Short castle rook or king have moved 
     * 3) Friendly piece in the way of long castle
     * 4) Piece attacking squares in front of short castle
     * 5) Piece checking king
     * 6) Odd starting position (King squished by rooks on edge of board,
     *    can't castle short because OOB but can castle long around rook)
     */
    @Test
    void castle(){
        // 1) valid short and long castles (pieces attacking rook-moved squares only)
        Board board = new Board(14, "src/test/java/XXLChess/test_files/castle.txt", false);
        Piece king = board.getKing(false);
        Piece rook = board.getPiece(0, 0);
        Location shortCastle = new Location(5, 0);
        Location longCastle = new Location(9, 0);
        Location left = new Location(6, 0);
        Location right = new Location(8, 0);
        assertEquals(new HashSet<Location>(Arrays.asList(left, right, shortCastle, longCastle)), king.validMoves);
        // 2) Rook or king have moved
        king.hasMoved = true;
        board.initializeMoves(false);
        assertEquals(new HashSet<Location>(Arrays.asList(left, right)), king.validMoves);
        king.hasMoved = false;
        rook.hasMoved = true;
        board.initializeMoves(false);
        assertEquals(new HashSet<Location>(Arrays.asList(left, right, longCastle)), king.validMoves);
        // 3) Friendly piece in the way of long castle, valid short castle
        Piece knight = board.addPieceFromPieceIndex(PieceIndex.KNIGHT, false, new Location(12, 0));
        rook.hasMoved = false;
        board.initializeMoves(false);
        assertEquals(new HashSet<Location>(Arrays.asList(left, right, shortCastle)), king.validMoves);
        // 4) Piece attacking squares in front of short castle
        board.removePiece(knight);
        rook = board.addPieceFromPieceIndex(PieceIndex.ROOK, true, new Location(5, 10));
        board.initializeMoves(false);
        assertEquals(new HashSet<Location>(Arrays.asList(left, right, longCastle)), king.validMoves);
        rook.makeMove(new Location(6, 10));
        board.initializeMoves(false);
        assertEquals(new HashSet<Location>(Arrays.asList(right, longCastle)), king.validMoves);
        // 5) Piece checking king
        rook.makeMove(new Location(7, 10));
        board.initializeMoves(false);
        assertEquals(new HashSet<Location>(Arrays.asList(left, right)), king.validMoves);
        // 6) Odd starting position (King squished by rooks on edge of board,
        //    can't castle short because OOB but can castle long around rook)
        board = new Board(14, "src/test/java/XXLChess/test_files/squishedCastle.txt", false);
        king = board.getKing(false);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(3, 0))), king.validMoves);
    }

    /**
     * Special Case: Pin. Test for black only:
     * 1) Trying to move a pinned piece away from the pin (impossible)
     * 2) Moving a pinned piece on the same line as pin / capture attacker (possible)
     */
    @Test 
    void pin() {
        Board board = new Board(14, "src/test/java/XXLChess/test_files/pin.txt", false);
        // 1) Trying to move a pinned piece away from the pin (impossible)
        Piece bishop = board.getPiece(0, 1);
        assertTrue(bishop.validMoves.isEmpty());
        // 2) Moving a pinned piece on the same line as pin / capture attacker (possible)
        Piece queen = board.getPiece(2, 2);
        assertEquals(new HashSet<Location>(Arrays.asList(new Location(1, 1), new Location(3, 3), new Location(4, 4))), queen.validMoves);
    }

    /**
     * Checks whether checkmates and stalemates return proper values
     */
    @Test
    void checkmateAndStalemate() {
        Board board = new Board(14, "src/test/java/XXLChess/test_files/checkmate.txt", true);
        // Delviering checkmate with the queen
        Piece queen = board.getPiece(1, 2);
        queen.makeMove(new Location(0, 1));
        assertFalse(board.initializeMoves(false));
        assertTrue(board.isInCheck(false));

        //Delivering stalemate
        queen.makeMove(new Location(2, 1));
        Piece king = board.getKing(true);
        king.makeMove(new Location(0, 2));
        assertFalse(board.initializeMoves(false));
        assertFalse(board.isInCheck(false));
    }
}