public abstract class Player {

    private PieceType colour;
    private Boolean isHuman;
    private int numMoves;
    private int distanceFromWin;
    private double totalThinkingTime;
    private Boolean isWinner = false;

    Player(PieceType colour) {
        this.colour = colour;
    }

    PieceType getColour() {
        return colour;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public void addMove(){
        numMoves += 1 ;
    }

    public void addThinkingTime(double nanotime){
        totalThinkingTime += nanotime / 1_000_000_000.0;
    }

    public double getThinkingTime(){
        return totalThinkingTime;
    }

    public double getAverageThinkingTime(){
        double val = 1000 * totalThinkingTime / numMoves;
        val = Math.round(val);
        val = val / 1000;

        return val;
    }

    Boolean isHuman() {
        return isHuman;
    }


    void setIsHuman(Boolean isHuman) {
        this.isHuman = isHuman;
    }

    Boolean isWinner(){
        return isWinner;
    }

    void makeWinner(){
        this.isWinner = true;
    }

    void setDistanceFromWin(int distance){
        distanceFromWin = distance;
    }

    int getDistanceFromWin(){
        return distanceFromWin;
    }

    @Override
    public String toString() {
        return "player: " + colour;
    }
}
