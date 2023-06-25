package XXLChess;

public class MovementHelper {
    public boolean isMoving;
    public Location startLocation;
    public Location finalLocation; 
    public double curX;
    public double curY;
    public double finalX;
    public double finalY;
    public double xSpeed;
    public double ySpeed;

    private int xDir;
    private int yDir;

    public MovementHelper() {
        isMoving = false;
    }

    public void initialize(Location startLocation, Location finalLocation) {
        this.startLocation = startLocation;
        this.finalLocation = finalLocation;

        curX = startLocation.x * App.CELLSIZE;
        curY = startLocation.y * App.CELLSIZE;
        finalX = finalLocation.x * App.CELLSIZE;
        finalY = finalLocation.y * App.CELLSIZE;
        isMoving = true;
        
        double distanceX = finalX-curX;
        double distanceY = finalY-curY;

        xDir = distanceX < 0 ? -1 : 1;
        yDir = distanceY < 0 ? -1 : 1;

        double distance = Math.sqrt(distanceX*distanceX+distanceY*distanceY);
        double timeTaken = (distance/App.speed)/App.FPS;
        double curSpeed;
        if(timeTaken > App.maxMoveTime){
            curSpeed = distance/(App.maxMoveTime*App.FPS);
        }
        else{
            curSpeed = App.speed;
        }

        // if the x-position is negative, then the returned arctan value needs to be inverted
        // since arctan's range is from -PI/2 to +PI/2 and not all directions
        int modifier = xDir;
        double angle = Math.atan(distanceY/distanceX);
        // If straight vertical / horizontal movement, handle these cases separately
        // (Modifier == 1 since 0 is not less than 0, so it's ignored)
        if(distanceX == 0){
            angle = distanceY>0?Math.PI/2:Math.PI/-2;
        }
        xSpeed = curSpeed*Math.cos(angle)*modifier;
        ySpeed = curSpeed*Math.sin(angle)*modifier;
    }

    public boolean isMovedPiece(Location curLocation) {
        return this.isMoving && curLocation.equals(this.finalLocation);
    }

    public boolean move() {
        curX += xSpeed;
        curY += ySpeed;
        // If it's overshot
        if(xDir * (curX - finalX) > 0 || yDir * (curY - finalY) > 0){
            curX = finalX;
            curY = finalY;
            isMoving = false;
            return true;
        }
        return false;
    }
}
