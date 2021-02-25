import java.util.*;

public class Board {

    public int width = 19;
    public int height = 13;

    Cell[][] board;
    int[][] boardLayout;
    Boolean isSmallBoard;

    Player currentPlayer;
    Player lastPlayer;

    private ArrayList<Player> players;

    int numPlayers;

    Board(int numPlayers, boolean isSmallBoard) {
        this.numPlayers = numPlayers;
        this.isSmallBoard = isSmallBoard;
        this.boardLayout = generateBoardLayout(numPlayers, isSmallBoard);

        this.initBoard(boardLayout, isSmallBoard);
        this.initPlayers(numPlayers);
    }

    // ******************* general board methods ***************

    private void initBoard(int[][] boardLayout, boolean isSmallBoard) {

        if(isSmallBoard){
            height = 13;
            width = 19;
        }
        else{
            height = 17;
            width = 25;
        }

        this.board = new Cell[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                switch(boardLayout[row][col]){
                    case 0:
                        board[row][col] = new Cell(row, col, new Piece(PieceType.EMPTY));
                        break;
                    case 1:
                        board[row][col] = new Cell(row, col, new Piece(PieceType.WHITE));
                        break;
                    case 2:
                        board[row][col] = new Cell(row, col, new Piece(PieceType.BLACK));
                        break;
                    case 3:
                        board[row][col] = new Cell(row, col, new Piece(PieceType.YELLOW));
                        break;
                    case 4:
                        board[row][col] = new Cell(row, col, new Piece(PieceType.GREEN));
                        break;
                    case 5:
                        board[row][col] = new Cell(row, col, new Piece(PieceType.PURPLE));
                        break;
                    case 6:
                        board[row][col] = new Cell(row, col, new Piece(PieceType.RED));
                        break;
                    case 9:
                        board[row][col] = new Cell(row, col, null);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + boardLayout[col][row]);
                }
            }
        }
    }

    private void initPlayers(int numPlayers) {

        players = new ArrayList<>();

        switch (numPlayers) {

            case 1:
                players.add(new ComputerPlayer(PieceType.WHITE));
                players.add(new ComputerPlayer(PieceType.BLACK));

            case 2:
                players.add(new ComputerPlayer(PieceType.WHITE));
                players.add(new ComputerPlayer(PieceType.BLACK));
                break;
            case 3:
                players.add(new ComputerPlayer(PieceType.WHITE));
                players.add(new ComputerPlayer(PieceType.YELLOW));
                players.add(new ComputerPlayer(PieceType.RED));
                break;
            case 4:
                players.add(new ComputerPlayer(PieceType.PURPLE));
                players.add(new ComputerPlayer(PieceType.GREEN));
                players.add(new ComputerPlayer(PieceType.YELLOW));
                players.add(new ComputerPlayer(PieceType.RED));
                break;
            case 6:
                players.add(new ComputerPlayer(PieceType.WHITE));
                players.add(new ComputerPlayer(PieceType.GREEN));
                players.add(new ComputerPlayer(PieceType.RED));
                players.add(new ComputerPlayer(PieceType.BLACK));
                players.add(new ComputerPlayer(PieceType.YELLOW));
                players.add(new ComputerPlayer(PieceType.PURPLE));
        }

        currentPlayer = players.get(0);
        lastPlayer = players.get(players.size()-1);
    }

    public void nextPlayer() {

        int index = players.indexOf(currentPlayer);

        if (players.size()-1 == index) {
            lastPlayer = currentPlayer;
            currentPlayer = players.get(0);
        }
        else {
            lastPlayer = currentPlayer;
            currentPlayer = players.get(index + 1);
        }
    }

    public Board clone() {

        Board clone = new Board(numPlayers, isSmallBoard);
        clone.players = players;
        clone.currentPlayer = currentPlayer;
        clone.lastPlayer = lastPlayer;
        clone.isSmallBoard = isSmallBoard;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (this.board[row][col].getPiece() != null) {
                    Piece piece = new Piece(board[row][col].getPiece().getColour());
                    clone.board[row][col] = new Cell(row,col,piece);
                    //this.board[row][col];
                }
            }
        }
        return clone;
    }

    void movePiece(Move move) {
        Cell initialCell = move.getInitialCell();
        Cell targetCell = move.getTargetCell();
        Piece pieceMoved = move.getPieceMoved();

        int initialRow = initialCell.getRow();
        int initialCol = initialCell.getCol();
        int targetRow = targetCell.getRow();
        int targetCol = targetCell.getCol();

        board[initialRow][initialCol].setPiece(targetCell.getPiece());
        board[targetRow][targetCol].setPiece(pieceMoved);

    }

    void movePieceSwitchPlayer(Move move) {
        this.nextPlayer();
        Cell initialCell = move.getInitialCell();
        Cell targetCell = move.getTargetCell();
        Piece pieceMoved = move.getPieceMoved();

        int initialRow = initialCell.getRow();
        int initialCol = initialCell.getCol();
        int targetRow = targetCell.getRow();
        int targetCol = targetCell.getCol();

        this.board[initialRow][initialCol].setPiece(targetCell.getPiece());
        this.board[targetRow][targetCol].setPiece(pieceMoved);

    }

    void undoMove(Move move) {
        Cell initialCell = move.getInitialCell();
        Cell targetCell = move.getTargetCell();
        Piece pieceMoved = move.getPieceMoved();

        int initialRow = initialCell.getRow();
        int initialCol = initialCell.getCol();
        int targetRow = targetCell.getRow();
        int targetCol = targetCell.getCol();

        board[targetRow][targetCol].setPiece(initialCell.getPiece());
        board[initialRow][initialCol].setPiece(pieceMoved);


    }

    // ******************* getters ***************

    Cell getCell(int row, int col) {
        return board[row][col];
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    // ******************* board conversion methods ***************

    private int[] convertToCubicCoordinates(int row, int col) {

        int x = (col - row) / 2;
        int z = row;
        int y = -x-z;

        return new int[]{x, y, z};
    }

    private int hexDistanceCubic(int initRow, int initCol, int targRow, int targCol) {

        int initx = convertToCubicCoordinates(initRow, initCol)[0];
        int inity = convertToCubicCoordinates(initRow, initCol)[1];
        int initz = convertToCubicCoordinates(initRow, initCol)[2];

        int targx = convertToCubicCoordinates(targRow, targCol)[0];
        int targy = convertToCubicCoordinates(targRow, targCol)[1];
        int targz = convertToCubicCoordinates(targRow, targCol)[2];

        int dx = Math.abs(initx - targx);
        int dy = Math.abs(inity - targy);
        int dz = Math.abs(initz - targz);

        return Math.max(Math.max(dx,dy),dz);
    }

    // ******************* move generation methods ***************

    private Move generateStep(int initRow, int initCol, int targetRow, int targetCol) {
        if ((targetRow >= 0 && targetRow < height) && (targetCol >= 0 && targetCol < width)) {
            if (board[targetRow][targetCol].getPiece() == null) {
                return null;
            } else if (board[targetRow][targetCol].getPiece().getColour() != PieceType.EMPTY) {
                return null;
            } else {
                return new Move(board[initRow][initCol], board[targetRow][targetCol], MoveType.STEP);
            }
        }
        return null;
    }

    private Move generateJump(int initRow, int initCol, int midRow, int midCol, int targetRow, int targetCol) {
        if ((((midRow >= 0 && midRow < height) && (midCol >= 0 && midCol < width))
                &&((targetRow >= 0 && targetRow < height) && (targetCol >= 0 && targetCol < width)))) {
            //if cells are invalid or end cell is not empty, return null
            //System.out.println("target: " + targetRow + ", " + targetCol + ". mid: " + midRow + ", " + midCol);
            if (board[targetRow][targetCol].getPiece() == null || board[midRow][midCol] == null) {
                return null;
            } else if (board[midRow][midCol].getPiece().getColour() == PieceType.EMPTY) {
                return null;
            } else if (board[targetRow][targetCol].getPiece().getColour() != PieceType.EMPTY) {
                return null;
            } else {
                return new Move(board[initRow][initCol], board[targetRow][targetCol], MoveType.JUMP);
            }
        }
        return null;
    }

    private ArrayList<Cell> getPieceLocations(PieceType colour) {

        ArrayList<Cell> positions = new ArrayList<>();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                if (board[row][col].getPiece() != null)
                    if (board[row][col].getPiece().getColour() == colour)
                        positions.add(board[row][col]);
            }
        }
        return positions;
    }

    private ArrayList<Move> getAvailableSteps(Cell cell) {

        ArrayList<Move> validStepMoves = new ArrayList<>();

        int col = cell.getCol();
        int row = cell.getRow();

        //bottom right (row+1, col+1)
        validStepMoves.add(generateStep(row,col,row+1,col+1));
        //bottom left (row+1, col-1)
        validStepMoves.add(generateStep(row,col,row+1,col-1));
        //far left (row, col-2)
        validStepMoves.add(generateStep(row,col,row,col-2));
        //top left (row-1, col-1)
        validStepMoves.add(generateStep(row,col,row-1,col-1));
        //top right (row-1, col+1)
        validStepMoves.add(generateStep(row,col,row-1,col+1));
        //far right (row, col+2)
        validStepMoves.add(generateStep(row,col,row,col+2));

        validStepMoves.removeIf(Objects::isNull);

        return validStepMoves;
    }

    private ArrayList<Move> getAvailableJumps(Cell cell) {

        ArrayList<Move> validJumpMoves = new ArrayList<>();

        int col = cell.getCol();
        int row = cell.getRow();

        //bottom right (row+2, col+2)
        validJumpMoves.add(generateJump(row, col,row+1, col+1,row+2,col+2));
        //bottom left (row+2, col-2)
        validJumpMoves.add(generateJump(row, col,row+1, col-1,row+2,col-2));
        //far left (row, col-4)
        validJumpMoves.add(generateJump(row, col, row, col - 2, row, col - 4));
        //top left (row-2, col-2)
        validJumpMoves.add(generateJump(row, col,row-1, col-1,row-2,col-2));
        //top right (row-2, col+2)
        validJumpMoves.add(generateJump(row, col,row-1, col+1,row-2,col+2));
        //far right (row, col+4)
        validJumpMoves.add(generateJump(row, col, row, col + 2, row, col + 4));

        validJumpMoves.removeIf(Objects::isNull);

        return validJumpMoves;
    }

    private ArrayList<Move> getAvailableJumpsDepthFive(Cell cell) {

        ArrayList<Move> validJumpMoves = new ArrayList<>();

        //very inefficient way of generating moves, i'm aware. needs refactoring

        for(Move move: getAvailableJumps(cell)){
            if(!validJumpMoves.contains(move)) {
                validJumpMoves.add(move);
            }
            for (Move move2: getAvailableJumps(move.getTargetCell())){
                if(!validJumpMoves.contains(move2)) {
                    validJumpMoves.add(new Move(cell, move2.getTargetCell(), MoveType.JUMP));
                }
                for (Move move3: getAvailableJumps(move2.getTargetCell())){
                    if(!validJumpMoves.contains(move3)) {
                        validJumpMoves.add(new Move(cell, move3.getTargetCell(), MoveType.JUMP));
                    }
                    for (Move move4: getAvailableJumps(move.getTargetCell())){
                        if(!validJumpMoves.contains(move4)) {
                            validJumpMoves.add(new Move(cell, move4.getTargetCell(), MoveType.JUMP));
                        }
                        for (Move move5: getAvailableJumps(move.getTargetCell())){
                            if(!validJumpMoves.contains(move5)) {
                                validJumpMoves.add(new Move(cell, move5.getTargetCell(), MoveType.JUMP));
                            }
                        }
                    }
                }
            }
        }

        ArrayList<Move> uniqueValidJumpMoves = new ArrayList<>();

        //remove duplicates
        for(Move move: validJumpMoves){
            if(!uniqueValidJumpMoves.contains(move))
                uniqueValidJumpMoves.add(move);
        }

        return uniqueValidJumpMoves;
    }

    ArrayList<Move> getAllMoves(Cell cell) {

        ArrayList<Move> allMoves = new ArrayList<>();
        allMoves.addAll(getAvailableJumpsDepthFive(cell));
        allMoves.addAll(getAvailableSteps(cell));

        return allMoves;
    }

    ArrayList<Move> getAllMovesForColour(PieceType colour) {

        ArrayList<Move> allAvailableMoves = new ArrayList<>();
        ArrayList<Move> forwardMoves = new ArrayList<>();

        for (Cell cell: getPieceLocations(colour)) {
            allAvailableMoves.addAll(getAllMoves(cell));
        }

        for (Move move: allAvailableMoves){
            if (hasMovedForward(move)){
                forwardMoves.add(move);
            }
        }
        if (!forwardMoves.isEmpty()){
            return forwardMoves;
        }
        else{
            return allAvailableMoves;
        }
        //System.out.println(allAvailableMoves);

    }

    ArrayList<Move> getAllMovesForState(){
        ArrayList<Move> allAvailableMoves = new ArrayList<>();
        ArrayList<Move> forwardMoves = new ArrayList<>();

        for (Cell cell: getPieceLocations(currentPlayer.getColour())) {
            allAvailableMoves.addAll(getAllMoves(cell));
        }
        for (Move move: allAvailableMoves){
            if (hasMovedForward(move)){
                forwardMoves.add(move);
            }
        }
        if (!forwardMoves.isEmpty()){
            return forwardMoves;
        }
        else{
            return allAvailableMoves;
        }
    }

    boolean hasMovedForward(Move move) {
        int initRow = move.getInitialCell().getRow();
        int initCol = move.getInitialCell().getCol();
        int targetRow = move.getTargetCell().getRow();
        int targetCol = move.getTargetCell().getCol();


        if (board[initRow][initCol].getPiece() != null) {

            int initDistance = distanceToGoal(initRow, initCol);
            int targDistance = distanceToGoal(initRow, initCol);

            if (initDistance <= targDistance) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // ****************** evaluation methods ******************

    int getTargetRow(PieceType colour){

        if (isSmallBoard) {
            if (colour.equals(PieceType.WHITE)) {
                return 12;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 9;
            }
            if (colour.equals(PieceType.RED)) {
                return 3;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 0;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 3;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 9;
            }
            else
                return 0;
        }
        else{
            if (colour.equals(PieceType.WHITE)) {
                return 16;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 12;
            }
            if (colour.equals(PieceType.RED)) {
                return 4;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 0;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 4;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 12;
            }
            else
                return 0;
        }


    }

    int getTargetCol(PieceType colour){
        if (isSmallBoard){
            if (colour.equals(PieceType.WHITE)) {
                return 9;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 0;
            }
            if (colour.equals(PieceType.RED)) {
                return 0;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 9;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 18;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 18;
            }
            else
                return 0;
        }
        else{
            if (colour.equals(PieceType.WHITE)) {
                return 12;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 0;
            }
            if (colour.equals(PieceType.RED)) {
                return 0;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 12;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 24;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 24;
            }
            else
                return 0;
        }

    }

    int getBaseRow(PieceType colour){
        if(isSmallBoard){
            if (colour.equals(PieceType.WHITE)) {
                return 0;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 3;
            }
            if (colour.equals(PieceType.RED)) {
                return 9;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 12;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 9;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 3;
            }
            else
                return 0;
        }
        else{
            if (colour.equals(PieceType.WHITE)) {
                return 0;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 4;
            }
            if (colour.equals(PieceType.RED)) {
                return 12;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 16;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 12;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 4;
            }
            else
                return 0;
        }

    }

    int getBaseCol(PieceType colour){
        if(isSmallBoard){
            if (colour.equals(PieceType.WHITE)) {
                return 9;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 18;
            }
            if (colour.equals(PieceType.RED)) {
                return 18;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 9;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 0;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 0;
            }
            else
                return 0;
        }
        else{
            if (colour.equals(PieceType.WHITE)) {
                return 12;
            }
            if (colour.equals(PieceType.GREEN)) {
                return 24;
            }
            if (colour.equals(PieceType.RED)) {
                return 24;
            }
            if (colour.equals(PieceType.BLACK)) {
                return 12;
            }
            if (colour.equals(PieceType.YELLOW)) {
                return 0;
            }
            if (colour.equals(PieceType.PURPLE)) {
                return 0;
            }
            else
                return 0;
        }

    }

    int distanceToGoal(int row, int col){

        int targRow = getTargetRow(board[row][col].getPiece().getColour());
        int targCol = getTargetCol(board[row][col].getPiece().getColour());

        if(isSmallBoard){
            return 12 - hexDistanceCubic(row,col,targRow,targCol);
        }
        else{
            return 16 - hexDistanceCubic(row,col,targRow,targCol);
        }

    }

    int pieceInGoalValue(int row, int col){

        if (distanceToGoal(row,col) < 4) {
            return 4 - distanceToGoal(row,col);
        }
        else
            return 0;
    }

    int pieceInBaseValue(int row, int col){
        int baseRow = getBaseRow(board[row][col].getPiece().getColour());
        int baseCol = getBaseCol(board[row][col].getPiece().getColour());

        int distanceToBase = hexDistanceCubic(row,col,baseRow,baseCol);

        if (distanceToBase == 3){
            return -1;
        }
        if (distanceToBase == 2){
            return -3;
        }
        if (distanceToBase == 1){
            return -5;
        }
        if (distanceToBase == 0){
            return -10;
        }
        else
            return -0;
    }

    int evaluateTwoPlayers() {
        int verticalDistance = 0;
        int piecesInGoal = 0;
        int piecesInBase = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                if(board[row][col].getPiece() != null) {
                    //white is maximising
                    if (board[row][col].getPiece().getColour().equals(PieceType.WHITE)) {
                        verticalDistance += distanceToGoal(row,col);
                        piecesInGoal += pieceInGoalValue(row,col);
                        piecesInBase += pieceInBaseValue(row,col);
                    }

                    //black is minimising
                    if (board[row][col].getPiece().getColour().equals(PieceType.BLACK)) {
                        verticalDistance -= distanceToGoal(row,col);
                        piecesInGoal -= pieceInGoalValue(row,col);
                        piecesInBase -= pieceInBaseValue(row,col);
                    }
                }
            }
        }

        int eval = Math.round(5 * piecesInGoal + 3 * verticalDistance + 5* piecesInBase);
        return eval;
    }

    int[] evaluateTwoPlayersIndependently() {

        int[] eval = {0, 0};
        int whiteDistance = 0, blackDistance = 0;
        int whitePiecesInGoal = 0, blackPiecesInGoal = 0;
        int whitePiecesInBase = 0, blackPiecesInBase = 0;


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                if (board[row][col].getPiece() != null) {
                    if (board[row][col].getPiece().getColour().equals(PieceType.WHITE)) {

                        whiteDistance += distanceToGoal(row,col);

                        whitePiecesInGoal += pieceInGoalValue(row,col);

                        whitePiecesInBase += pieceInBaseValue(row,col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.BLACK)) {

                        blackDistance += distanceToGoal(row,col);

                        blackPiecesInGoal += pieceInGoalValue(row,col);

                        blackPiecesInBase += pieceInBaseValue(row,col);
                    }
                }
            }
        }
        eval[0] = Math.round(5 * whitePiecesInGoal + 3 * whiteDistance + 5 * whitePiecesInBase);
        eval[1] = Math.round(5 * blackPiecesInGoal + 3 * blackDistance + 5 * blackPiecesInBase);
        return eval;
    }

    int[] evaluateThreePlayers() {

        int[] eval = {0, 0, 0};
        int whiteDistance = 0, yellowDistance = 0, redDistance = 0;
        int whitePiecesInGoal = 0, yellowPiecesInGoal = 0, redPiecesInGoal = 0;
        int whitePiecesInBase = 0, yellowPiecesInBase = 0, redPiecesInBase = 0;


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                if (board[row][col].getPiece() != null) {
                    if (board[row][col].getPiece().getColour().equals(PieceType.WHITE)) {

                        whiteDistance += distanceToGoal(row,col);
                        whitePiecesInGoal += pieceInGoalValue(row,col);
                        whitePiecesInBase += pieceInBaseValue(row,col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.YELLOW)) {
                        yellowDistance += distanceToGoal(row,col);
                        yellowPiecesInGoal += pieceInGoalValue(row,col);
                        yellowPiecesInBase += pieceInBaseValue(row,col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.RED)) {
                        //target = (4,0)
                        redDistance += distanceToGoal(row,col);
                        redPiecesInGoal += pieceInGoalValue(row,col);
                        redPiecesInBase += pieceInBaseValue(row,col);
                    }
                }
            }
        }
        eval[0] = Math.round(5 * whitePiecesInGoal + 3 * whiteDistance + 5 * whitePiecesInBase);
        eval[1] = Math.round(5 * yellowPiecesInGoal + 3 * yellowDistance + 5 * yellowPiecesInBase);
        eval[2] = Math.round(5 * redPiecesInGoal + 3 * redDistance + 5 * redPiecesInBase);

        return eval;
    }

    int[] evaluateFourPlayers() {

        int[] eval = {0, 0, 0, 0};
        int purpleDistance = 0, greenDistance = 0, yellowDistance = 0, redDistance = 0;
        int purplePiecesInGoal = 0, greenPiecesInGoal = 0, yellowPiecesInGoal = 0, redPiecesInGoal = 0;
        int purplePiecesInBase = 0, greenPiecesInBase = 0, yellowPiecesInBase = 0, redPiecesInBase = 0;


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                if (board[row][col].getPiece() != null) {
                    if (board[row][col].getPiece().getColour().equals(PieceType.PURPLE)) {

                        purpleDistance += distanceToGoal(row,col);
                        purplePiecesInGoal += pieceInGoalValue(row,col);
                        purplePiecesInBase += pieceInBaseValue(row,col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.GREEN)) {

                        greenDistance += distanceToGoal(row,col);
                        greenPiecesInGoal += pieceInGoalValue(row,col);
                        greenPiecesInBase += pieceInBaseValue(row,col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.YELLOW)) {
                        yellowDistance += distanceToGoal(row,col);
                        yellowPiecesInGoal += pieceInGoalValue(row,col);
                        yellowPiecesInBase += pieceInBaseValue(row,col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.RED)) {
                        //target = (4,0)
                        redDistance += distanceToGoal(row,col);
                        redPiecesInGoal += pieceInGoalValue(row,col);
                        redPiecesInBase += pieceInBaseValue(row,col);
                    }
                }
            }
        }
        eval[0] = Math.round(5 * purplePiecesInGoal + 3 * purpleDistance + 5 * purplePiecesInBase);
        eval[1] = Math.round(5 * greenPiecesInGoal + 3 * greenDistance + 5 * greenPiecesInBase);
        eval[2] = Math.round(5 * yellowPiecesInGoal + 3 * yellowDistance + 5 * yellowPiecesInBase);
        eval[3] = Math.round(5* redPiecesInGoal + 3 * redDistance + 5 * redPiecesInBase);

        return eval;
    }

    int[] evaluateSixPlayers() {

        int[] eval = {0, 0, 0, 0, 0, 0};
        int purpleDistance = 0, greenDistance = 0, yellowDistance = 0,
                redDistance = 0, whiteDistance = 0, blackDistance = 0;
        int purplePiecesInGoal = 0, greenPiecesInGoal = 0, yellowPiecesInGoal = 0,
                redPiecesInGoal = 0, whitePiecesInGoal = 0, blackPiecesInGoal = 0;

        int purplePiecesInBase = 0, greenPiecesInBase = 0, yellowPiecesInBase = 0,
                redPiecesInBase = 0, whitePiecesInBase = 0, blackPiecesInBase = 0;


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                if (board[row][col].getPiece() != null) {
                    if (board[row][col].getPiece().getColour().equals(PieceType.WHITE)) {

                        whiteDistance += distanceToGoal(row, col);
                        whitePiecesInGoal += pieceInGoalValue(row, col);
                        whitePiecesInBase += pieceInBaseValue(row, col);
                    }


                    if (board[row][col].getPiece().getColour().equals(PieceType.PURPLE)) {

                        purpleDistance += distanceToGoal(row, col);
                        purplePiecesInGoal += pieceInGoalValue(row, col);
                        purplePiecesInBase += pieceInBaseValue(row, col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.GREEN)) {

                        greenDistance += distanceToGoal(row, col);
                        greenPiecesInGoal += pieceInGoalValue(row, col);
                        greenPiecesInBase += pieceInBaseValue(row, col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.YELLOW)) {
                        yellowDistance += distanceToGoal(row, col);
                        yellowPiecesInGoal += pieceInGoalValue(row, col);
                        yellowPiecesInBase += pieceInBaseValue(row, col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.RED)) {
                        //target = (4,0)
                        redDistance += distanceToGoal(row, col);
                        redPiecesInGoal += pieceInGoalValue(row, col);
                        redPiecesInBase += pieceInBaseValue(row, col);
                    }

                    if (board[row][col].getPiece().getColour().equals(PieceType.BLACK)) {
                        //target = (4,0)
                        blackDistance += distanceToGoal(row, col);
                        blackPiecesInGoal += pieceInGoalValue(row, col);
                        blackPiecesInBase += pieceInBaseValue(row, col);
                    }
                }
            }
        }
        eval[0] = Math.round(5 * whitePiecesInGoal + 3 * whiteDistance + 5 * whitePiecesInBase);
        eval[1] = Math.round(5 * greenPiecesInGoal + 3 * greenDistance + 5 * greenPiecesInBase);
        eval[2] = Math.round(5 * redPiecesInGoal + 3 * redDistance + 5 * redPiecesInBase);
        eval[3] = Math.round(5 * blackPiecesInGoal + 3 * blackDistance + 5 * blackPiecesInBase);
        eval[4] = Math.round(5 * yellowPiecesInGoal + 3 * yellowDistance + 5 * yellowPiecesInBase);
        eval[5] = Math.round(5 * purplePiecesInGoal + 3 * purpleDistance + 5 * purplePiecesInBase);




        return eval;
    }

    int evaluateBoardAfterMove(Move move){
        int eval = 0;
        movePiece(move);

        if (numPlayers == 2) {
            switch (move.getPieceMoved().getColour()) {
                case BLACK:
                    eval = evaluateTwoPlayersIndependently()[1];
                    break;
                case WHITE:
                    eval = evaluateTwoPlayersIndependently()[0];
                    break;

            }
        }
        if (numPlayers == 3) {
            switch (move.getPieceMoved().getColour()) {
                case RED:
                    eval = evaluateThreePlayers()[2];
                    break;
                case WHITE:
                    eval = evaluateThreePlayers()[0];
                    break;
                case YELLOW:
                    eval = evaluateThreePlayers()[1];
                    break;
            }
        }
        if (numPlayers == 4) {
                switch (move.getPieceMoved().getColour()) {
                    case RED:
                        eval = evaluateFourPlayers()[3];
                        break;
                    case YELLOW:
                        eval = evaluateFourPlayers()[2];
                        break;
                    case GREEN:
                        eval = evaluateFourPlayers()[1];
                        break;
                    case PURPLE:
                        eval = evaluateFourPlayers()[0];
                        break;
                }
            }
            if (numPlayers == 6) {
                switch (move.getPieceMoved().getColour()) {
                    case WHITE:
                        eval = evaluateSixPlayers()[0];
                        break;
                    case GREEN:
                        eval = evaluateSixPlayers()[1];
                        break;
                    case RED:
                        eval = evaluateSixPlayers()[2];
                        break;
                    case BLACK:
                        eval = evaluateSixPlayers()[3];
                        break;
                    case YELLOW:
                        eval = evaluateSixPlayers()[4];
                        break;
                    case PURPLE:
                        eval = evaluateSixPlayers()[5];
                        break;
                }
        }
        undoMove(move);
        return eval;
    }

    int distanceFromWin(Player player) {
        int distance;

        if (isSmallBoard){
            distance = -8;
        } else {
            distance = -20;
        }

        for (Cell cell : getPieceLocations(player.getColour())) {
            int row = cell.getRow();
            int col = cell.getCol();

            if (isSmallBoard){
                distance += 12 - distanceToGoal(row,col);
            } else {
                distance += 16 - distanceToGoal(row, col);
            }

        }

//            switch (player.getColour()) {
//                case PURPLE:
//                    distance += hexDistanceCubic(row, col, 12, 24);
//                    break;
//                case GREEN:
//                    distance += hexDistanceCubic(row, col, 12, 0);
//                    break;
//                case YELLOW:
//                    distance += hexDistanceCubic(row, col, 4, 24);
//                    break;
//                case RED:
//                    distance += hexDistanceCubic(row, col, 4, 0);
//                    break;
//                case WHITE:
//                    distance += hexDistanceCubic(row, col, 16, 12);
//                    break;
//                case BLACK:
//                    distance += hexDistanceCubic(row, col, 0, 12);
//                    break;
//            }
//        }
        return distance;
    }

    // ******************* win condition methods **********************

    private boolean whiteWin() {

        return  board[16][12].getPiece().getColour().equals(PieceType.WHITE) &&
                board[15][11].getPiece().getColour().equals(PieceType.WHITE) &&
                board[15][13].getPiece().getColour().equals(PieceType.WHITE) &&
                board[14][10].getPiece().getColour().equals(PieceType.WHITE) &&
                board[14][12].getPiece().getColour().equals(PieceType.WHITE) &&
                board[14][14].getPiece().getColour().equals(PieceType.WHITE) &&
                board[13][ 9].getPiece().getColour().equals(PieceType.WHITE) &&
                board[13][11].getPiece().getColour().equals(PieceType.WHITE) &&
                board[13][13].getPiece().getColour().equals(PieceType.WHITE) &&
                board[13][15].getPiece().getColour().equals(PieceType.WHITE);

    }

    private boolean redWin() {

        return  board[4][0].getPiece().getColour().equals(PieceType.RED) &&
                board[4][2].getPiece().getColour().equals(PieceType.RED) &&
                board[4][4].getPiece().getColour().equals(PieceType.RED) &&
                board[4][6].getPiece().getColour().equals(PieceType.RED) &&
                board[5][1].getPiece().getColour().equals(PieceType.RED) &&
                board[5][3].getPiece().getColour().equals(PieceType.RED) &&
                board[5][5].getPiece().getColour().equals(PieceType.RED) &&
                board[6][2].getPiece().getColour().equals(PieceType.RED) &&
                board[6][4].getPiece().getColour().equals(PieceType.RED) &&
                board[7][3].getPiece().getColour().equals(PieceType.RED);

    }

    private boolean yellowWin() {

        return  board[4][18].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[4][20].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[4][22].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[4][24].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[5][19].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[5][21].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[5][23].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[6][20].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[6][22].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[7][21].getPiece().getColour().equals(PieceType.YELLOW);
    }

    private boolean purpleWin() {

        return  board[12][18].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[12][20].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[12][22].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[12][24].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[11][19].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[11][21].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[11][23].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[10][20].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[10][22].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[9][21].getPiece().getColour().equals(PieceType.PURPLE);
    }

    private boolean greenWin() {

        return  board[12][0].getPiece().getColour().equals(PieceType.GREEN) &&
                board[12][2].getPiece().getColour().equals(PieceType.GREEN) &&
                board[12][4].getPiece().getColour().equals(PieceType.GREEN) &&
                board[12][6].getPiece().getColour().equals(PieceType.GREEN) &&
                board[11][1].getPiece().getColour().equals(PieceType.GREEN) &&
                board[11][3].getPiece().getColour().equals(PieceType.GREEN) &&
                board[11][5].getPiece().getColour().equals(PieceType.GREEN) &&
                board[10][2].getPiece().getColour().equals(PieceType.GREEN) &&
                board[10][4].getPiece().getColour().equals(PieceType.GREEN) &&
                board[9][3].getPiece().getColour().equals(PieceType.GREEN);
    }

    private boolean blackWin() {

        return  board[0][12].getPiece().getColour().equals(PieceType.BLACK) &&
                board[1][11].getPiece().getColour().equals(PieceType.BLACK) &&
                board[1][13].getPiece().getColour().equals(PieceType.BLACK) &&
                board[2][10].getPiece().getColour().equals(PieceType.BLACK) &&
                board[2][12].getPiece().getColour().equals(PieceType.BLACK) &&
                board[2][14].getPiece().getColour().equals(PieceType.BLACK) &&
                board[3][ 9].getPiece().getColour().equals(PieceType.BLACK) &&
                board[3][11].getPiece().getColour().equals(PieceType.BLACK) &&
                board[3][13].getPiece().getColour().equals(PieceType.BLACK) &&
                board[3][15].getPiece().getColour().equals(PieceType.BLACK);

    }

    private boolean whiteMiniWin() {

        return  board[12][9].getPiece().getColour().equals(PieceType.WHITE) &&

                board[11][8].getPiece().getColour().equals(PieceType.WHITE) &&
                board[11][10].getPiece().getColour().equals(PieceType.WHITE) &&

                board[10][7].getPiece().getColour().equals(PieceType.WHITE) &&
                board[10][9].getPiece().getColour().equals(PieceType.WHITE) &&
                board[10][11].getPiece().getColour().equals(PieceType.WHITE);
    }

    private boolean redMiniWin() {

        return  board[3][0].getPiece().getColour().equals(PieceType.RED) &&
                board[3][2].getPiece().getColour().equals(PieceType.RED) &&
                board[3][4].getPiece().getColour().equals(PieceType.RED) &&

                board[4][1].getPiece().getColour().equals(PieceType.RED) &&
                board[4][3].getPiece().getColour().equals(PieceType.RED) &&

                board[5][2].getPiece().getColour().equals(PieceType.RED);

    }

    private boolean yellowMiniWin() {

        return  board[3][14].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[3][16].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[3][18].getPiece().getColour().equals(PieceType.YELLOW) &&

                board[4][15].getPiece().getColour().equals(PieceType.YELLOW) &&
                board[4][17].getPiece().getColour().equals(PieceType.YELLOW) &&

                board[5][16].getPiece().getColour().equals(PieceType.YELLOW);
    }

    private boolean purpleMiniWin() {

        return  board[7][16].getPiece().getColour().equals(PieceType.PURPLE) &&

                board[8][15].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[8][17].getPiece().getColour().equals(PieceType.PURPLE) &&

                board[9][14].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[9][16].getPiece().getColour().equals(PieceType.PURPLE) &&
                board[9][18].getPiece().getColour().equals(PieceType.PURPLE);
    }

    private boolean greenMiniWin() {

        return  board[7][2].getPiece().getColour().equals(PieceType.GREEN) &&

                board[8][1].getPiece().getColour().equals(PieceType.GREEN) &&
                board[8][3].getPiece().getColour().equals(PieceType.GREEN) &&

                board[9][0].getPiece().getColour().equals(PieceType.GREEN) &&
                board[9][2].getPiece().getColour().equals(PieceType.GREEN) &&
                board[9][4].getPiece().getColour().equals(PieceType.GREEN);
    }

    private boolean blackMiniWin() {

        return  board[0][9].getPiece().getColour().equals(PieceType.BLACK) &&

                board[1][8].getPiece().getColour().equals(PieceType.BLACK) &&
                board[1][10].getPiece().getColour().equals(PieceType.BLACK) &&

                board[2][7].getPiece().getColour().equals(PieceType.BLACK) &&
                board[2][9].getPiece().getColour().equals(PieceType.BLACK) &&
                board[2][11].getPiece().getColour().equals(PieceType.BLACK);

    }

    boolean hasWon(Player player) {

        if(isSmallBoard) {
            switch (player.getColour()) {
                case RED:
                    return redMiniWin();
                case WHITE:
                    return whiteMiniWin();
                case YELLOW:
                    return yellowMiniWin();
                case GREEN:
                    return greenMiniWin();
                case BLACK:
                    return blackMiniWin();
                case PURPLE:
                    return purpleMiniWin();
                default:
                    return false;
            }
        }
        else{
                switch (player.getColour()) {
                    case RED:
                        return redWin();
                    case WHITE:
                        return whiteWin();
                    case YELLOW:
                        return yellowWin();
                    case GREEN:
                        return greenWin();
                    case BLACK:
                        return blackWin();
                    case PURPLE:
                        return purpleWin();
                    default:
                        return false;
            }

        }

    }

    // ******************* board layout methods **********************

    private int[][] generateBoardLayout(int numPlayers, boolean isSmallBoard) {

        if (!isSmallBoard){
            switch(numPlayers) {

                case 1:
                    return testBoardLayout();

                case 2:
                    return twoPlayerBoardLayout();

                case 3:
                    return threePlayerBoardLayout();

                case 4:
                    return fourPlayerBoardLayout();

                case 6:
                    return sixPlayerBoardLayout();

                default:
                    throw new IllegalStateException("Invalid number of players: " + numPlayers);
            }
        }
        else{
            switch(numPlayers) {

                case 1:
                    return testBoardLayout();

                case 2:
                    return twoPlayerMiniBoardLayout();

                case 3:
                    return threePlayerMiniBoardLayout();

                case 4:
                    return fourPlayerMiniBoardLayout();

                case 6:
                    return sixPlayerMiniBoardLayout();

                default:
                    throw new IllegalStateException("Invalid number of players: " + numPlayers);
            }

        }

    }

    private int[][] testBoardLayout() {
        boardLayout = new int[height][width];
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 1, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 1, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0},
                {9, 0, 9, 0, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 0, 9, 4, 9, 0, 9, 0, 9},
                {9, 9, 0, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 0, 9, 9},
                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 0, 9, 0, 9, 9, 9},
                {9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9},
                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},
                {9, 9, 0, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9},
                {9, 0, 9, 0, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9},
                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 2, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 2, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
        };
        return boardLayout;
    }

    private int[][] twoPlayerBoardLayout() {
        boardLayout = new int[height][width];
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0},
                {9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9},
                {9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9},
                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},
                {9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9},
                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},
                {9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9},
                {9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9},
                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
        };
        return boardLayout;
    }

    private int[][] threePlayerBoardLayout() {
        boardLayout = new int[height][width];
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0},
                {9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9},
                {9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9},
                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},
                {9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9},
                {9, 9, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 9, 9},
                {9, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 9},
                {9, 3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6, 9},
                {3, 9, 3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6, 9, 6},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
        };
        return boardLayout;
    }

    private int[][] fourPlayerBoardLayout() {
        boardLayout = new int[height][width];
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {5, 9, 5, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 4, 9, 4},
                {9, 5, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 4, 9},
                {9, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 9},
                {9, 9, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 9, 9},
                {9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9},
                {9, 9, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 9, 9},
                {9, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 9},
                {9, 3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6, 9},
                {3, 9, 3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6, 9, 6},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
        };
        return boardLayout;
    }

    private int[][] sixPlayerBoardLayout() {
        boardLayout = new int[height][width];
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {5, 9, 5, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 4, 9, 4},
                {9, 5, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 4, 9},
                {9, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 9},
                {9, 9, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 9, 9},
                {9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9},
                {9, 9, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 9, 9},
                {9, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 9},
                {9, 3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6, 9},
                {3, 9, 3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6, 9, 6},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
        };
        return boardLayout;
    }

    // ******************* mini board layout methods **********************

    private int[][] generateMiniBoardLayout(int numPlayers) {

        switch (numPlayers) {

            case 2:
                return twoPlayerMiniBoardLayout();

            case 3:
                return threePlayerMiniBoardLayout();

            case 4:
                return fourPlayerMiniBoardLayout();

            case 6:
                return sixPlayerMiniBoardLayout();

            default:
                throw new IllegalStateException("Invalid number of players: " + numPlayers);
        }
    }

    private int[][] twoPlayerMiniBoardLayout() {
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9},

                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0},
                {9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9},
                {9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9},

                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},

                {9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9},
                {9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9},
                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0},

                {9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9},
        };
        return boardLayout;
    }

    private int[][] threePlayerMiniBoardLayout() {
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9},

                {0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0},
                {9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9},
                {9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9},

                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},

                {9, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 9},
                {9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9},
                {3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6},

                {9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
        };
        return boardLayout;
    }

    private int[][] fourPlayerMiniBoardLayout() {
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9},

                {5, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 4},
                {9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9},
                {9, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 9},

                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},

                {9, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 9},
                {9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9},
                {3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6},

                {9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9},
        };
        return boardLayout;
    }

    private int[][] sixPlayerMiniBoardLayout() {
        boardLayout = new int[][]{
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 1, 9, 1, 9, 1, 9, 9, 9, 9, 9, 9, 9},

                {5, 9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9, 4},
                {9, 5, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 4, 9},
                {9, 9, 5, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 4, 9, 9},

                {9, 9, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 9, 9},

                {9, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 9},
                {9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9},
                {3, 9, 3, 9, 3, 9, 0, 9, 0, 9, 0, 9, 0, 9, 6, 9, 6, 9, 6},

                {9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9},
                {9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 9, 9, 9, 9, 9, 9, 9, 9, 9},

        };
        return boardLayout;
    }

    @Override
    public String toString() {
        String output = null;
        // prints numbers before each line for referencing of coordinates
        if(!isSmallBoard){
            output = "                        1 1 1 1 1 1 1 1 1 1 2 2 2 2 2\n" +
                    "    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4\n";
        }
        else if(isSmallBoard){
            output = "                        1 1 1 1 1 1 1 1 1\n" +
                    "    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8\n";
        }
        for (int row = 0; row < height; row++) {

            if(row < 10)
                output += row + ":  ";
            else
                output += row + ": ";

            for (int col = 0; col < width; col++) {
                output += board[row][col].toString();
            }
            output += "\n";
        }
        return output;
    }

    //for testing
    public static void main(String[] args) {

        Board board = new Board(2, true);
        AI ai = new AI(board);

        System.out.println(board);

        ArrayList<Move> moves = ai.generateAllBestGreedyMoves(board.currentPlayer);
        Move move = moves.get(0);

        System.out.println(Arrays.toString(board.evaluateTwoPlayersIndependently()));
        System.out.println(move);
        board.movePiece(move);
        System.out.println(board);
        System.out.println(Arrays.toString(board.evaluateTwoPlayersIndependently()));

        moves = ai.generateAllBestGreedyMoves(board.currentPlayer);
        move = moves.get(0);

        System.out.println(Arrays.toString(board.evaluateTwoPlayersIndependently()));
        System.out.println(move);
        board.movePiece(move);
        System.out.println(board);
        System.out.println(Arrays.toString(board.evaluateTwoPlayersIndependently()));

        moves = ai.generateAllBestGreedyMoves(board.currentPlayer);
        move = moves.get(0);

        System.out.println(Arrays.toString(board.evaluateTwoPlayersIndependently()));
        System.out.println(move);
        board.movePiece(move);
        System.out.println(board);
        System.out.println(Arrays.toString(board.evaluateTwoPlayersIndependently()));



    }
}
