package XXLChess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

import XXLChess.Piece.Piece;
import XXLChess.Piece.PieceIndex;

import processing.event.MouseEvent;

public class AppTest {
    
    static App app;

    @BeforeAll
    static void init() {
        app = new App("src/test/java/XXLChess/testConfig.json");
    }

    /**
     * Tests all the setup things (reading in config file for stats and board state)
     */
    @Test
    void setup() {
        app.setup();
        assertFalse(app.playerIsWhite);
        assertEquals(90000, app.whiteTimer.milliseconds);
        assertEquals(60000, app.blackTimer.milliseconds);
        assertEquals(3, App.speed);
        assertEquals(0.5, App.maxMoveTime);

        ChessTest.checkStandardBoard(app.board);
    }

    /**
     * Checks timers (increment, decrement, and out of time)
     */
    @Test
    void checkTimers() {
        app.setup();
        app.gameOver = false;
        app.whiteMove = true;
        // Check that after 1 second passes, the timer reduces by 1 second for white but not for black
        for(int i = 0; i < App.FPS; i++) {
            app.draw();
        }
        assertEquals(89000, app.whiteTimer.milliseconds, 1);
        assertEquals(60000, app.blackTimer.milliseconds);

        // Test increment timer
        app.whiteTimer.incrementTimer();
        app.blackTimer.incrementTimer();
        assertEquals(90000, app.whiteTimer.milliseconds, 1);
        assertEquals(60000, app.blackTimer.milliseconds);

        // Repeat for black
        for(int i = 0; i < App.FPS; i++) {
            app.draw();
        }
        assertEquals(90000, app.whiteTimer.milliseconds, 1);
        assertEquals(59000, app.blackTimer.milliseconds, 1);

        app.whiteTimer.incrementTimer();
        app.blackTimer.incrementTimer();
        assertEquals(90000, app.whiteTimer.milliseconds, 1);
        assertEquals(64000, app.blackTimer.milliseconds, 1);

        // Test out of time
        app.whiteTimer.milliseconds = 0;
        app.draw();
        assertFalse(app.whiteTimer.decrementTimer()); // White out of time
        assertTrue(app.blackTimer.decrementTimer()); // Black inactive with time
        assertTrue(app.gameOver); // Game over should be called
        app.whiteTimer.milliseconds = 1000;
        app.blackTimer.milliseconds = 0;
        app.whiteTimer.isActive = false;
        app.blackTimer.isActive = true;
        app.draw();
        assertTrue(app.whiteTimer.decrementTimer()); // White with time left
        assertFalse(app.blackTimer.decrementTimer()); // Black out of time

        // Test string formatting
        app.whiteTimer.milliseconds = 2500;
        assertEquals("0:02.5", app.formatTime(app.whiteTimer));
        app.whiteTimer.milliseconds = 50000;
        assertEquals("0:50", app.formatTime(app.whiteTimer));
        app.whiteTimer.milliseconds = 61000;
        assertEquals("1:01", app.formatTime(app.whiteTimer));
        app.blackTimer.milliseconds = 3660000;
        assertEquals("1:01", app.formatTime(app.blackTimer));
    }
    
    @Test
    void moveAndSpeedCalculations(){
        // First, do one where maxMoveTime is enforced
        app.moveAndCalculateSpeed(new Location(13, 13), app.board.getPiece(0, 0));
        assertEquals(new Location(0, 0), app.lastOriginLoc);
        assertEquals(new Location(13, 13), app.lastDestLoc);

        MovementHelper mover = app.mainMover;
        assertEquals(0, mover.curX);
        assertEquals(0, mover.curY);

        // Make the move, and it should reach the final position in 0.5s
        int numFrames = 1;
        while(!mover.move())
            numFrames++;
        
        assertEquals(App.FPS / 2, numFrames, 1);

        // Now, do one where maxMoveTime is not enforced
        app.moveAndCalculateSpeed(new Location(12, 12), app.board.getPiece(13, 13));
        // Check that the speed is the given speed
        assertEquals(3.0, Math.sqrt(mover.xSpeed * mover.xSpeed + mover.ySpeed * mover.ySpeed), 1);
        // Check that it completes the move in the given number of frames
        int numFramesX = (int) ((mover.finalX - mover.curX) / mover.xSpeed);
        int numFramesY = (int) ((mover.finalY - mover.curY) / mover.ySpeed);
        assertTrue(numFramesX == numFramesY);
        assertTrue(numFramesX / App.FPS < App.maxMoveTime); // Time is less than maxMoveTime
        for(int i = 0; i < numFramesX; i++) {
            assertFalse(mover.move());
        }
        assertTrue(mover.move());

        // Now test castling
        app.board = new Board(14, "src/test/java/XXLChess/test_files/castle.txt", false);
        app.moveAndCalculateSpeed(new Location(5, 0), app.board.getKing(false));
        assertEquals(new Location(0, 0), app.auxMover.startLocation);
        assertEquals(new Location(6, 0), app.auxMover.finalLocation);
        assertEquals(new Location(7, 0), app.mainMover.startLocation);
        assertEquals(new Location(5, 0), app.mainMover.finalLocation);
        // Test movement from draw
        for(int i = 0; i < App.FPS; i++) {
            app.draw();
        }
        assertTrue(app.mainMover.move());
        assertTrue(app.auxMover.move());
        restoreStandardBoard();
    }

    private void restoreStandardBoard() {
        app.board = new Board(14, "src/test/java/XXLChess/test_files/startBoard.txt", true);
        app.gameOver = false;
        app.isMoving = false;
        app.whiteMove = true;
    }

    /**
     * Tests mouse-clicking functions (picking up pieces, dropping them, and making moves)
     */
    @Test
    void mouseClicking() {
        app.isMoving = false;
        app.gameOver = false;
        app.whiteMove = app.playerIsWhite;

        // Need a situation allowing for captures
        Piece amazon = app.board.getPiece(0, 6);
        amazon.makeMove(new Location(5, 2));
        clickMouse(new Location(5, 2));
        assertEquals(app.board.getPiece(2, 5), app.selectedPiece);
        // Check that highlight branch is taken
        app.draw();
        assertTrue(app.pickUp);

        // Invalid location check
        clickMouse(new Location(17, 14));
        assertFalse(app.pickUp);

        // Select a piece, then select another piece check
        clickMouse(new Location(0, 0));
        clickMouse(new Location(0, 1));
 
        assertEquals(app.board.getPiece(1, 0), app.selectedPiece);

        // Click on invalid move check
        clickMouse(new Location(10, 6));
        assertTrue(app.board.getPiece(1, 0) != null); // Piece has not moved
        assertFalse(app.pickUp);

        // Make valid move check
        clickMouse(new Location(0, 1)); // Move a pawn down 2 tiles
        clickMouse(new Location(0, 3));
        assertTrue(app.isMoving);
        assertTrue(app.board.getPiece(1, 0) == null);
        assertTrue(app.board.getPiece(3, 0) != null);
        assertEquals(new Location(0, 3), app.mainMover.finalLocation);

        // Check that after the move is done, play switched to other side
        for(int i = 0 ; i < App.FPS / 2; i++) {
            app.draw();
        }
        assertTrue(app.whiteMove != app.playerIsWhite);

        // Check that mouse clicks are ignored when necessary
        clickMouse(new Location(0, 0));
        assertFalse(app.pickUp);
        app.whiteMove = app.playerIsWhite;
        app.isMoving = true;
        clickMouse(new Location(0, 0));
        assertFalse(app.pickUp);
        app.isMoving = false;
        app.gameOver = true;
        clickMouse(new Location(0, 0));
        assertFalse(app.pickUp);

    }

    private void clickMouse(Location loc){
        app.mouseClicked(new MouseEvent(null, System.currentTimeMillis(), MouseEvent.CLICK,
        0, (int) ((loc.x + 0.5) * App.CELLSIZE), (int) ((loc.y + 0.5) * App.CELLSIZE), 37, 1));
    }

    @Test
    void checksAndCheckmates() {
        app.whiteMove = true;
        app.gameOver = false;
        app.board = new Board(14, "src/test/java/XXLChess/test_files/checkmate.txt", true);
        // AI delivers checkmate with the queen
        Piece queen = app.board.getPiece(1, 2);
        app.moveAndCalculateSpeed(new Location(1, 1), queen);
        assertTrue(app.gameOver);
        assertEquals("You lost\n by\n checkmate", app.middleString);
        // Check that the highlight branch is taken
        app.draw();
        assertTrue(app.board.isInCheck(app.whiteMove));
        

        // AI stalemates
        queen.makeMove(new Location(1, 2));
        app.whiteMove = true;
        app.gameOver = false;
        Piece king = app.board.getKing(true);
        app.moveAndCalculateSpeed(new Location(0, 2), king);
        assertTrue(app.gameOver);
        assertEquals("Stalemate\n –\n draw", app.middleString);
        
        // Player checkmates
        app.gameOver = false;
        app.board.removePiece(queen);
        queen = app.board.addPieceFromPieceIndex(PieceIndex.QUEEN, false, new Location(1, 2));
        app.board.addPieceFromPieceIndex(PieceIndex.ROOK, false, new Location(10, 3));
        app.moveAndCalculateSpeed(new Location(1, 1), queen);
        assertTrue(app.gameOver);
        assertEquals("You won\n by\n checkmate", app.middleString);

        restoreStandardBoard();
    }

    @Test
    void keyPressing() {
        app.key = 'e';
        app.keyPressed();
        app.draw();
        assertTrue(app.resign);
        app.resign = false;
    }

    @Test
    void ai() {
        Piece[][] oldBoard = new Piece[App.BOARD_WIDTH][App.BOARD_WIDTH];
        for(int j = 0; j < App.BOARD_WIDTH; j++){
            for(int k = 0; k < App.BOARD_WIDTH; k++) {
                oldBoard[j][k] = app.board.board[j][k];
            }
        }
        app.board.initializeMoves(!app.playerIsWhite);
        app.isMoving = false;
        app.whiteMove = !app.playerIsWhite;
        app.ai.disableAi = false;
        // Wait for AI to make a move
        while(app.whiteMove != app.playerIsWhite){
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }
        try{
            Thread.sleep(500);
        } catch (Exception e) {}
        // Reverse move and check that it was a valid move
        Piece movedPiece = oldBoard[app.mainMover.startLocation.y][app.mainMover.startLocation.x];
        movedPiece.makeMove(app.mainMover.startLocation);
        Piece removedPiece = oldBoard[app.mainMover.finalLocation.y][app.mainMover.finalLocation.x];
        if(removedPiece != null)
            app.board.addPieceFromPieceIndex(removedPiece.pieceIndex, removedPiece.isWhite, removedPiece.location);
        app.board.initializeMoves(!app.playerIsWhite);
        assertTrue(movedPiece.validMoves.contains(app.mainMover.finalLocation));
    }
}
