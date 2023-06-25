package XXLChess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import XXLChess.Piece.*;

public class Ai extends Thread {
    private App app;
    private List<Piece> pieces;
    private boolean isWhite;
    private Board board;
    public boolean disableAi;


    public Ai(App app, boolean isWhite, Board board) {
        this.app = app;
        this.pieces = new ArrayList<Piece>();
        this.isWhite = isWhite;
        this.board = board;
        this.disableAi = false;
    }

    // Make this into its own method, so you can make smarter methods later and implement them easier
    
    // Note that it's returning a boolean. This is how you should make your smarter methods,
    // where if a move is not made it returns false, so you know to try another algorithm if needed
    public boolean makeRandomMove() {
        Random random = new Random();
        Piece pieceToMove;
        // do-while looop to avoid recursion
        do{
            int randomIndex = random.nextInt(pieces.size());
            pieceToMove = pieces.get(randomIndex);
        } while(pieceToMove.validMoves.isEmpty());
        int randomMoveIndex = random.nextInt(pieceToMove.validMoves.size());
        Location targetLocation = (Location) pieceToMove.validMoves.toArray()[randomMoveIndex];
        app.moveAndCalculateSpeed(targetLocation, pieceToMove);
        
        return true;
    }

    @Override
    public void run() {
        while(true) {
            boolean isTurn = app.whiteMove == this.isWhite;
            if(!isTurn || app.isMoving) {
                try{
                    sleep(100);
                } catch(Exception e){}
                continue;
            }
            pieces.clear();
        
            for (Piece piece : board.pieceList) {
                if (piece.isWhite == this.isWhite) {
                    pieces.add(piece);
                }
            }
            
            try {
                sleep(1000);
            } catch (Exception e){}

            if(disableAi)
                continue;

            makeRandomMove();
        }
    }
    

}