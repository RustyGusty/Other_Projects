package XXLChess;

public class ChessTimer {
    
    public double milliseconds;
    public boolean isActive;
    private double increment;
    private static final double MILLIS_PER_FRAME;

    static {
        MILLIS_PER_FRAME = 1000.0/App.FPS;
    }
    
    public ChessTimer(long milliseconds, long increment){
        this.milliseconds = milliseconds;
        this.increment = increment;
        isActive = false;
    }

    public boolean decrementTimer(){
        if(!isActive)
            return true;
        milliseconds -= MILLIS_PER_FRAME;
        return milliseconds > 0;
    }

    public void incrementTimer(){
        if(!isActive)
            isActive = true;
        else{
            isActive = false;
            milliseconds += increment;
        }
    }
}
