package XXLChess;

//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.MouseEvent;
import XXLChess.Piece.*;

import java.util.concurrent.TimeUnit;


import java.io.*;
/**
 * Control and View: Deals with reading player inputs and displaying the result to the user 
 */
public class App extends PApplet {

    public static final int SPRITESIZE = 480;
    public static final int CELLSIZE = 45;
    public static final int SIDEBAR = 120;
    public static final int BOARD_WIDTH = 14;

    public static final int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static final int HEIGHT = BOARD_WIDTH*CELLSIZE;    

    public static double speed;
    public static double maxMoveTime;
     
    
    Ai ai;
    boolean playerIsWhite;

    public static final int FPS = 60;
    PImage[] pieces = new PImage[PieceIndex.size * 2];

    public Board board;
    public String configPath;

    public App() {
        this.configPath = "config.json";
    }

    /**
     * For testing purposes, uses a custom config file and initializes
     * the surface separately from PApplet
     * @param configPath location of the Json config file
     */
    public App(String configPath) {
        this.configPath = configPath;
        surface = initSurface();
        sketchPath();
        setup();
        handleDraw();
        noLoop();
        ai.disableAi = true;
    }

    /**
     * Initialise the setting of the window size.
    */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images.
     * Initialise the elements such as the player, enemies and map elements.
    */    
    public void setup() {
        frameRate(FPS);

        // Load images during setup and place them into the pieces array
        // Black pieces are the first PieceIndex.size elements, 
        // White pieces are the last PieceIndex.size elements
        pieces[PieceIndex.ROOK.ordinal()] = loadImage("src/main/resources/XXLChess/b-rook.png");
        pieces[PieceIndex.KNIGHT.ordinal()] = loadImage("src/main/resources/XXLChess/b-knight.png");
        pieces[PieceIndex.BISHOP.ordinal()] = loadImage("src/main/resources/XXLChess/b-bishop.png");
        pieces[PieceIndex.ARCHBISHOP.ordinal()] = loadImage("src/main/resources/XXLChess/b-archbishop.png");
        pieces[PieceIndex.CAMEL.ordinal()] = loadImage("src/main/resources/XXLChess/b-camel.png");
        pieces[PieceIndex.GENERAL.ordinal()] = loadImage("src/main/resources/XXLChess/b-knight-king.png");
        pieces[PieceIndex.AMAZON.ordinal()] = loadImage("src/main/resources/XXLChess/b-amazon.png");
        pieces[PieceIndex.KING.ordinal()] = loadImage("src/main/resources/XXLChess/b-king.png");
        pieces[PieceIndex.CHANCELLOR.ordinal()] = loadImage("src/main/resources/XXLChess/b-chancellor.png");
        pieces[PieceIndex.PAWN.ordinal()] = loadImage("src/main/resources/XXLChess/b-pawn.png");
        pieces[PieceIndex.QUEEN.ordinal()] = loadImage("src/main/resources/XXLChess/b-queen.png");
        pieces[PieceIndex.PAWN.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-pawn.png");
        pieces[PieceIndex.ROOK.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-rook.png");
        pieces[PieceIndex.KNIGHT.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-knight.png");
        pieces[PieceIndex.BISHOP.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-bishop.png");
        pieces[PieceIndex.ARCHBISHOP.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-archbishop.png");
        pieces[PieceIndex.CAMEL.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-camel.png");
        pieces[PieceIndex.GENERAL.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-knight-king.png");
        pieces[PieceIndex.AMAZON.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-amazon.png");
        pieces[PieceIndex.KING.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-king.png");
        pieces[PieceIndex.CHANCELLOR.ordinal() + PieceIndex.size] = loadImage("src/main/resources/XXLChess/w-chancellor.png");
        pieces[PieceIndex.QUEEN.ordinal() + PieceIndex.size]=loadImage("src/main/resources/XXLChess/w-queen.png");

        // load config
        JSONObject conf = loadJSONObject(new File(this.configPath));

        playerIsWhite = conf.getString("player_colour").equals("white");

        board = new Board(BOARD_WIDTH, conf.getString("layout"), true);
        ai = new Ai(this, !playerIsWhite, board);
        ai.start();
        initializeTimers((JSONObject)conf.get("time_controls"), playerIsWhite);

        speed = conf.getDouble("piece_movement_speed");
        maxMoveTime = conf.getDouble("max_movement_time");
        
    }

    public ChessTimer whiteTimer;
    public ChessTimer blackTimer;

    public void initializeTimers(JSONObject config, boolean playerIsWhite){
        JSONObject playerObj = (JSONObject)config.get("player");
        ChessTimer playerTime = new ChessTimer(TimeUnit.SECONDS.toMillis(playerObj.getLong("seconds")), (TimeUnit.SECONDS.toMillis(playerObj.getLong("increment"))) );
        JSONObject cpuObj = (JSONObject)config.get("cpu");
        ChessTimer cpuTime = new ChessTimer(TimeUnit.SECONDS.toMillis(cpuObj.getLong("seconds")), (TimeUnit.SECONDS.toMillis(cpuObj.getLong("increment"))) );;
        whiteTimer = playerIsWhite?playerTime:cpuTime;
        blackTimer = playerIsWhite?cpuTime:playerTime;
        whiteTimer.isActive = true;
    }

    /**
     * Receive key pressed signal from the keyboard.
    */
    public void keyPressed(){
        if (key == 'e') {
            resign = true;
            ai.disableAi = true;
          }
    }
    
    public Location lastOriginLoc = null; // For displaying last move, Location of last origin of movement
    public Location lastDestLoc = null; // For displaying last move, Location of last destination of movement
    public boolean pickUp=false; // Used to show if a piece has been selected or not
    public boolean whiteMove=true; // Used to determine whose move it is
    public Piece selectedPiece = null; // Refers to the currently selected piece to check for valid movement
    public boolean gameOver = false; // Returns true when the timer hits 0:00
    public String middleString = "";

    public MovementHelper mainMover = new MovementHelper();
    public MovementHelper auxMover = new MovementHelper(); // For if a castle happens and two pieces move at once
    public boolean isMoving = false;
    public boolean resign = false;


    /**
     * Handles all logic relating to when a player presses the mouse. This involves "picking up"
     * a piece, checking if a move is valid, moving the piece, and reinitializing the valid moves
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        if (gameOver){
            return;
        }

        if (isMoving){
            return;
        }

        if(whiteMove != playerIsWhite)
            return;

        int x = e.getX();
        int y = e.getY();
        
        int row = (int) Math.floor(y / CELLSIZE);
        int col = (int) Math.floor(x / CELLSIZE);

        if (row < 0 || row >= BOARD_WIDTH || col < 0 || col >= BOARD_WIDTH) {
            pickUp = false;
            return; 
        }

        Location targetLocation = new Location(col, row);
        Piece targetPiece = board.getPiece(row, col);

        if (!pickUp) {
            if(targetPiece != null && whiteMove == targetPiece.isWhite) {
                selectedPiece = targetPiece;
                pickUp=true;
            }

        }
        else {
            if(selectedPiece.isValidMove(targetLocation)) {
                moveAndCalculateSpeed(targetLocation, selectedPiece);
                pickUp = false;

            }
            else if(targetPiece == selectedPiece) {
                pickUp = false;
            }
            // Added a function where if you had a piece picked up, you can pick up
            // another piece instead of always dropping it
            else if(targetPiece != null && targetPiece.isWhite == whiteMove) {
                selectedPiece = targetPiece;
            }
            else {
                pickUp = false;
            }
        }
        
    }


    /**
     * Makes the move of the selectedPiece to targetLocation, updating all parameters for
     * drawing accordingly (such as speed, yellow markers, timers, and checking
     * for end conditions)
     * @param targetLocation The location the piece should be moved to (should be valid)
     * @param selectedPiece The piece that is going to be moved
     */
    public void moveAndCalculateSpeed(Location targetLocation, Piece selectedPiece) {
        lastOriginLoc = selectedPiece.location;
        selectedPiece.makeMove(targetLocation);
        lastDestLoc = targetLocation;

        mainMover.initialize(lastOriginLoc, lastDestLoc);
        
        selectedPiece.hasMoved = true;

        // If this was a castle
        int distanceX = lastDestLoc.x - lastOriginLoc.x;
        if(selectedPiece instanceof King && Math.abs(distanceX) >= 2) {
            int sign = distanceX > 0 ? 1 : -1; // Extract just the sign as 1 or -1
            Location curLoc = new Location(selectedPiece.location.x + sign, selectedPiece.location.y);
            Move move = new Move(sign, 0, true);
            // Shift over by sign until we find the piece (should be a rook)
            while(board.getPiece(curLoc) == null) 
                curLoc.stepForward(move);
            Piece rook = board.getPiece(curLoc);
            rook.makeMove(new Location(lastDestLoc.x - sign, lastDestLoc.y));
            auxMover.initialize(curLoc, rook.location);
            rook.hasMoved = true;
        }

        isMoving = true;
        
        whiteTimer.incrementTimer();
        blackTimer.incrementTimer();

        whiteMove = !whiteMove;
        if(!board.initializeMoves(whiteMove)){
            gameOver = true;
            //if player who's about to move has no valid moves when they still have a king, returns stalemate
            if(!board.isInCheck(whiteMove)){
                middleString = ("Stalemate\n –\n draw");
            }
            // If the player was supposed to move there, you lost
            else if(whiteMove == playerIsWhite){
                middleString = ("You lost\n by\n checkmate");
            }
            else{
                middleString = ("You won\n by\n checkmate");
            }
        }
    }

    /**
     * formats timer according to chess.com rules
     * @param timer
     * @return
     */
    public String formatTime(ChessTimer timer){
        String formatted = "";
        
        long milliseconds = (long) timer.milliseconds;
        if(milliseconds <= 0){
            gameOver = true;
            return "0:00";
        }
        long seconds = milliseconds/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        minutes %= 60;
        seconds %= 60;
        milliseconds %= 1000;

        

        if(hours > 0){
            formatted = hours + ":";
            if (minutes < 10){
                formatted += "0";
            }
            formatted += minutes;
        }
        else{
            formatted = minutes + ":";
            if (seconds < 10){
                formatted += "0";
            }
            formatted += seconds;
            if (minutes == 0 && seconds < 20){
                formatted += "." + milliseconds/100;
            }
        }

        return formatted;
    }

    public void draw() {
        if (!gameOver){
            whiteTimer.decrementTimer();
            blackTimer.decrementTimer();
        }

        //chess board
        background(200); //grey
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if ((i + j) % 2 == 0) {
                    fill(255, 204, 153); //light brown
                } else {
                    fill(153, 102, 51); //dark brown
                }
                rect(j * CELLSIZE, i * CELLSIZE, CELLSIZE, CELLSIZE);
            }
        }

        //chess pieces
        Piece mainMovingPiece = null;
        Piece auxMovingPiece = null;
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Piece targetPiece = board.getPiece(i, j);

                if(targetPiece != null) {
                    int modifier = 0;
                    if(targetPiece.isWhite) modifier = PieceIndex.size;
                    if(mainMover.isMovedPiece(new Location(j, i)))
                        mainMovingPiece = targetPiece;
                    else if(auxMover.isMovedPiece(new Location(j, i)))
                        auxMovingPiece = targetPiece;
                    else 
                        image(pieces[targetPiece.pieceIndex.ordinal() + modifier], j * CELLSIZE, i * CELLSIZE, CELLSIZE, CELLSIZE);
                }
            }
        } 

        if(mainMovingPiece != null){
            if(mainMover.move()) {
                isMoving = mainMover.isMoving || auxMover.isMoving; //Only update to false if both are not moving
            }
            int modifier = 0;
            if(mainMovingPiece.isWhite) modifier = PieceIndex.size;
            image(pieces[mainMovingPiece.pieceIndex.ordinal() + modifier], (float) mainMover.curX, (float) mainMover.curY, CELLSIZE, CELLSIZE);
        }
        if(auxMovingPiece != null){
            if(auxMover.move()) {
                isMoving = mainMover.isMoving || auxMover.isMoving; //Only update to false if both are not moving
            }
            int modifier = 0;
            if(auxMovingPiece.isWhite) modifier = PieceIndex.size;
            image(pieces[auxMovingPiece.pieceIndex.ordinal() + modifier], (float) auxMover.curX, (float) auxMover.curY, CELLSIZE, CELLSIZE);
        }

        // Gets the string format of each timer
        String checkBlack = formatTime(blackTimer);
        String checkWhite = formatTime(whiteTimer);

        // Prints black and white timer at top and bottom
        textAlign(RIGHT, TOP);
        textSize(20);
        fill(255);
        text(checkBlack, WIDTH-25, 20);
        textAlign(RIGHT, BOTTOM);
        textSize(20);
        fill(255);
        text(checkWhite, WIDTH-25, HEIGHT-20);

        // If black timer hits zero, player wins by time
        if(checkBlack.equals("0:00")){
            gameOver = true;
            textAlign(RIGHT, CENTER);
            textSize(20);
            fill(255);
            text("You won\n on time", WIDTH-25, HEIGHT/2); 

        }

        // If white timer hits zero, player wins by time
        if(checkWhite.equals("0:00")){
            gameOver = true;
            textAlign(RIGHT, CENTER);
            textSize(20);
            fill(255);
            text("You lost\n on time", WIDTH-25, HEIGHT/2); 
        }


        if (pickUp == true) {
            fill(0, 255, 0, 127); // Green
            rect(selectedPiece.location.x * CELLSIZE, selectedPiece.location.y * CELLSIZE, CELLSIZE, CELLSIZE); // Highlight current selected piece green
            for (int i = 0; i < BOARD_WIDTH; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if (selectedPiece.isValidMove(new Location(i, j))) {
                        if (board.getPiece(j, i) == null) {
                            fill(135, 206, 250, 127); // Blue
                            rect(i * CELLSIZE, j * CELLSIZE, CELLSIZE, CELLSIZE); // Highlight valid moves blue
                        } else {
                            fill(255, 0, 0, 128); // Red
                            rect(i * CELLSIZE, j * CELLSIZE, CELLSIZE, CELLSIZE); // Highlight captures red
                        }
                    }
                }
            }
        }

        //highlight checked king
        if(board.isInCheck(whiteMove)){
            King targetKing = board.getKing(whiteMove);
            fill(128, 0, 0, 128); //dark red
            rect(targetKing.location.x * CELLSIZE, targetKing.location.y * CELLSIZE, CELLSIZE, CELLSIZE);
            textAlign(RIGHT, CENTER);
            textSize(20);
            fill(255);
            if(!gameOver)
                text("Check!", WIDTH-25, HEIGHT/2); 
        }

        fill(255, 255, 0, 127); //yellow
        //highlight old move yellow
        if(lastOriginLoc != null){
            rect(lastOriginLoc.x * CELLSIZE, lastOriginLoc.y * CELLSIZE, CELLSIZE, CELLSIZE);
            rect(lastDestLoc.x * CELLSIZE, lastDestLoc.y * CELLSIZE, CELLSIZE, CELLSIZE);
        }

        textAlign(RIGHT, CENTER);
        textSize(20);
        fill(255);
        text(middleString, WIDTH-25, HEIGHT/2);

        if (resign){
            gameOver = true;
            textAlign(RIGHT, CENTER);
            textSize(20);
            fill(255);
            text("You chose\n to\n resign", WIDTH-25, HEIGHT/2); 
        }

    }
    
    public static void main(String[] args) {
        PApplet.main("XXLChess.App");
    }

}