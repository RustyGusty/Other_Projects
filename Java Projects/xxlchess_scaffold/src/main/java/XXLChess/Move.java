package XXLChess;

/**
 * Represents a direction vector of a possible move. If continueFlag == false, then only the
 * specified distance is allowed, but if continueFlag == true, then all positive scalar multiples
 * are allowed.
 */
public class Move {
    
    public final int x; // x-direction of movement
    public final int y; // y-direction of movement
    public final boolean continueFlag; //true if continuous movement, false if point movement

    /**
     * Initializes a new Move with the given parameters
     * @param x - x-direction of movement
     * @param y - y-direction of movement
     * @param continueFlag - true if continuous movement, false if point movement
     */
    public Move(int x, int y, boolean continueFlag){
        this.x = x;
        this.y = y;
        this.continueFlag = continueFlag;
    }

    /**
     * For aid in initialization, converts a 2D array arr into corresponding move values
     * all with the same continue flag
     * @param arr a 2D int[i][2] array, where arr[i]={x,y} is the 2-int pair
     * representing the direction of movement
     * @param continueFlag {@code true} if positive scalar multiples are allowed, {@code false}
     * if only this specified distance is allowed
     * @return a Move[] array with all the moves specified in arr
     */
    public static Move[] intArrToMoveArr(int[][] arr, boolean continueFlag) {
        Move[] res = new Move[arr.length];
        for(int i = 0; i < arr.length; i++) {
            int[] dir = arr[i];
            res[i] = new Move(dir[0], dir[1], continueFlag); 
        }
        return res;
    }
}