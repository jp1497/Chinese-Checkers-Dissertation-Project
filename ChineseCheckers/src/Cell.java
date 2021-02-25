public class Cell {

    private Piece piece;
    private int row, col;

    Cell(int row, int col, Piece piece) {
        this.piece = piece;
        this.row = row;
        this.col = col;
    }

    void clearCell() {
        piece = null;
    }

    Piece getPiece() {
        return piece;
    }

    void setPiece(Piece piece) {
        this.piece = piece;
    }

    int getRow() {
        return row;
    }

    void setRow(int row) {
        this.row = row;
    }

    int getCol() {
        return col;
    }

    void setCol(int col) {
        this.col = col;
    }

    @Override
    public String toString() {
        if (piece == null)
            return "  ";
        else
            return piece.toString();
    }
}
