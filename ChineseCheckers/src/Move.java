public class Move {

    private Cell initialCell, targetCell;
    private Piece pieceMoved;
    private MoveType moveType;
    private int moveValue;

    Move(Cell initialCell, Cell targetCell, MoveType moveType) {
        this.initialCell = initialCell;
        this.targetCell = targetCell;
        this.moveType = moveType;
        this.pieceMoved = initialCell.getPiece();
    }

    Move(Cell initialCell, Cell targetCell, MoveType moveType, int moveValue) {
        this.initialCell = initialCell;
        this.targetCell = targetCell;
        this.moveType = moveType;
        this.pieceMoved = initialCell.getPiece();
        this.moveValue = moveValue;
    }

    Cell getInitialCell() {
        return initialCell;
    }

    Cell getTargetCell() {
        return targetCell;
    }

    Piece getPieceMoved() {
        return pieceMoved;
    }

    MoveType getMoveType() {
        return moveType;
    }

    int getMoveValue() {
        return moveValue;
    }

    void setMoveValue(int moveValue) {
        this.moveValue = moveValue;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Move))
            return false;

        Move that = (Move) other;

        return this.initialCell.equals(that.initialCell) &&
                this.targetCell.equals(that.targetCell) &&
                this.pieceMoved.equals(that.pieceMoved);
    }

    @Override
    public String toString() {
        return "Move from: " + initialCell.getRow() + ", " + initialCell.getCol() +
                " to: " + targetCell.getRow() + ", " + targetCell.getCol() +
                " with a value of: " + moveValue;
    }
}
