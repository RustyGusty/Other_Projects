package XXLChess;

/**
 * Represents a square on the board. It's pretty much just a 2-int array, but this aids in readability
 */
public class Location {
    public int x;
    public int y;

    /**
     * Base constructor for a {0, 0} Location
     */
    public Location(){
        this.x = 0;
        this.y = 0;
    }

    /**
     * Constructor for an {x, y} Location
     * @param x int for the x-coord
     * @param y int for the y-coord
     */
    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Location(int[] locAsArray) {
        this.x = locAsArray[0];
        this.y = locAsArray[1];
    }

    /**
     * Returns true if this is a valid location within the board bounds, false otherwise
     * @return {@code true} if this is a valid location within the board bounds, {@code false} otherwise
     */
    public boolean isValid() {
        return this.x >= 0 && this.x < App.BOARD_WIDTH && this.y >= 0 && this.y < App.BOARD_WIDTH;
    }

    /**
     * Starting at this Location, returns a new Location after moving in the direction of move
     * for multiplier times
     * @param move Move object to represent the direction of movement
     * @param multiplier number of times the move is done
     * @return a new Location object of the final square after moving
     */
    public Location getLocationFromMove(Move move, int multiplier){
        Location res = new Location();
        res.x = this.x + move.x * multiplier;
        res.y = this.y + move.y * multiplier;
        return res;
    }

    /**
     * Changes this Location to have its coordinates moved forward in the direction of move once,
     * returning its validity as a boolean
     * @param move Move object specifying the direction of movement
     * @return {@code true} if the resulting square is valid, {@code false} otherwise
     */
    public boolean stepForward(Move move) {
        this.x += move.x;
        this.y += move.y;
        return this.isValid();
    }

    @Override
    public int hashCode(){
        return this.x * App.BOARD_WIDTH + this.y;
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        } else if (other == null || this.getClass() != other.getClass()){
            return false;
        } else {
            Location loc = (Location) other;
            return this.x == loc.x && this.y == loc.y;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}