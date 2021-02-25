import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {

    private ArrayList<Player> players;
    private Board board;
    private GameState currentState;
    private AI ai;

    private static final int NUM_PLAYERS = 2;

    private static Scanner input = new Scanner(System.in);

    private Game(){
        initGame(NUM_PLAYERS);

        do { //game loop
            System.out.println("\n" + board.toString());
            System.out.println(board.currentPlayer.toString());

            Move move;

            if(board.currentPlayer.isHuman()) {
               move = getPlayerMove(board.currentPlayer);
            } else {
                move = generateRandomMove(board.currentPlayer);
                System.out.println("you have chosen: " + move.toString());
            }

            if (isValidMove(move, board.currentPlayer)){
                board.currentPlayer.addMove();
                board.movePieceSwitchPlayer(move);
                updateGame(board.currentPlayer);
            }
            else {
                System.out.println("invalid move");
            }

        } while (currentState == GameState.PLAYING);
    }

    private void initGame(int numPlayers) {
        board = new Board(numPlayers, true);
        ai = new AI(board);
        currentState = GameState.PLAYING;
    }


    private Move generateMove(int initialRow, int initialCol, int targetRow, int targetCol) {
        Cell initialCell = board.getCell(initialRow, initialCol);
        Cell targetCell = board.getCell(targetRow, targetCol);

        int absRow = Math.abs(initialRow - targetRow);
        int absCol = Math.abs(initialCol - targetCol);

        if (absRow == 2 || absCol == 2) {
            return new Move(initialCell, targetCell, MoveType.STEP);
        } else {
            return new Move(initialCell, targetCell, MoveType.JUMP);
        }

    }

    private boolean isValidMove(Move move, Player player) {

        Piece sourcePiece = move.getInitialCell().getPiece();
        Cell sourceCell = move.getInitialCell();

        if (sourcePiece == null){
            System.out.println("selected cell does not have a piece.");
            return false;
        }

        if (sourcePiece.getColour() != player.getColour()) {
            System.out.println("source piece at: " + sourceCell.getRow() + ", " + sourceCell.getRow());
            System.out.println("selected cell has a piece of the invalid colour:" + sourcePiece.getColour());
            return false;
        }

        // if move is not found in available moves
        for (Move availablemove: board.getAllMoves(sourceCell)) {
            if (availablemove.equals(move))
                return true;
        }
        return false;
    }

    /**
     * Generates a move for the current player
     * @param player represents a Player object
     * @return a Move object based on the player's input
     */
    private Move getPlayerMove(Player player) {

        if (player == board.currentPlayer)
            System.out.println("Player: " + player.getColour() +
                    " enter your selected piece (row[0-16], column[0-24]):");

        int selectedRow = input.nextInt();
        int selectedCol = input.nextInt();

        //print available steps (update to print all available moves)
        System.out.println("Available moves:");
        for (Move move: board.getAllMoves(board.getCell(selectedRow,selectedCol)))
            System.out.println(move.toString());

        System.out.println("you have chosen piece: " + selectedRow + ", " + selectedCol +
                    " enter your selected move (row[0-16], column[0-24]):");

        int nextRow = input.nextInt();
        int nextCol = input.nextInt();

        return generateMove(selectedRow, selectedCol, nextRow, nextCol);
    }

    private Move generateRandomMove(Player player) {

        ArrayList<Move> allBestMoves = null;

        if (NUM_PLAYERS == 2)
            switch (player.getColour()) {
                case BLACK:
                    allBestMoves = ai.generateAllBestMiniMaxMoves(2, player);
                    break;
                case WHITE:
                    //System.out.println(ai.MCTS(board, 10, Math.sqrt(2)));
                    allBestMoves = ai.generateAllBestMiniMaxMoves(2,player);
                    //allBestMoves = ai.generateAllBestMiniMaxMoves(2, player);
                    break;
            }
        if (NUM_PLAYERS == 3)
            switch (player.getColour()){
                case WHITE:
                    allBestMoves = ai.generateAllBestMaxN3Moves(2,player);
                    break;
                case YELLOW:
                    //allBestMoves = ai.generateAllBestMaxN3Moves(2,player);
                    allBestMoves = ai.generateAllBestMaxN3Moves(2,player);
                    break;
                case RED:
                    //allBestMoves = ai.MCTS(board,100);
                    allBestMoves = ai.generateAllBestMaxN3Moves(2,player);
                    break;
                default:
                    allBestMoves = null;
            }
        if (NUM_PLAYERS == 4)
            switch (player.getColour()) {
                case PURPLE:
                    allBestMoves = ai.generateAllBestMaxN4Moves(2, player);
                    break;
                case GREEN:
                    allBestMoves = ai.generateAllBestMaxN4Moves(2, player);
                    break;
                case YELLOW:
                    allBestMoves = ai.generateAllBestMaxN4Moves(2, player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllBestMaxN4Moves(2, player);
                    break;
                default:
                    allBestMoves = null;
            }
        if (NUM_PLAYERS == 6)
            allBestMoves = ai.generateAllBestMaxN6Moves(2, player);


        for (Move move: allBestMoves)
            System.out.println(move);

        if (allBestMoves.size() == 0)
            System.out.println("no moves?");

        int random =  new Random().nextInt(allBestMoves.size());

        return allBestMoves.get(random);


    }

    private void updateGame(Player player) {
        if (board.hasWon(player)) {
            switch (player.getColour()) {
                case RED:
                    currentState = GameState.RED_WIN;
                    break;
                case WHITE:
                    currentState = GameState.WHITE_WIN;
                    break;
                case YELLOW:
                    currentState = GameState.YELLOW_WIN;
                    break;
                case GREEN:
                    currentState = GameState.GREEN_WIN;
                    break;
                case BLACK:
                    currentState = GameState.BLACK_WIN;
                    break;
                case PURPLE:
                    currentState = GameState.PURPLE_WIN;
                    break;
                default:
                    break;
            }
        }
        if (currentState != GameState.PLAYING) {
            System.out.println("\n" + board.toString());
            System.out.println(board.currentPlayer.getColour() + " WINS with " +
                    board.currentPlayer.getNumMoves() + " moves");
        }
    }


    public static void main(String[] args){
        new Game();
    }
}
