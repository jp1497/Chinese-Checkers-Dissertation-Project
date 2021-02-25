public class Piece {

    private PieceType colour;

    Piece(PieceType colour) {
        this.colour = colour;
    }

    PieceType getColour() {
        return colour;
    }

    public void setColour(PieceType colour) {
        this.colour = colour;
    }

    @Override
    public String toString() {

        if (colour == PieceType.WHITE)
            return "W ";

        if (colour == PieceType.BLACK)
            return "B ";

        if (colour == PieceType.RED)
            return "R ";

        if (colour == PieceType.GREEN)
            return "G ";

        if (colour == PieceType.YELLOW)
            return "Y ";

        if (colour == PieceType.PURPLE)
            return "P ";

        if (colour == PieceType.EMPTY)
            return ". ";

        else
            return null;
    }
}
