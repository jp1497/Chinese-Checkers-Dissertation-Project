import java.io.FileWriter;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;

public class Experiment {

    private Board board;
    private AI ai;
    private GameState currentState;

    private Player winner;
    private double experimentTime;

    public Experiment(int experimentNum){
        initGame(experimentNum);

        double gameStartTime = System.nanoTime();

        while (currentState == GameState.PLAYING){

            double startTime = System.nanoTime();

            Move move = generateRandomMove(board.currentPlayer,experimentNum);

            double finishTime = System.nanoTime();

            board.currentPlayer.addThinkingTime(finishTime - startTime);

            board.currentPlayer.addMove();

            board.movePieceSwitchPlayer(move);

            System.out.println(board);
            updateGame(board.currentPlayer);
        }

        double gameFinishTime = System.nanoTime();

        double val = 1000 * (gameFinishTime - gameStartTime) / 1_000_000_000.0;
        val = Math.round(val);
        experimentTime = val / 1000;
    }

    private void initGame(int experimentNum) {

        int numPlayers = 0;

        // minimax / mcts
        if (experimentNum == 1 || experimentNum == 2 || experimentNum == 3)
            numPlayers = 2;


        if (experimentNum == 4 || experimentNum == 5 || experimentNum == 6 ||
                experimentNum == 7 || experimentNum == 8 || experimentNum == 9)
            numPlayers = 3;

        if (experimentNum == 10 || experimentNum == 11 || experimentNum == 12)
            numPlayers = 6;

        board = new Board(numPlayers, true);
        ai = new AI(board);
        currentState = GameState.PLAYING;
    }

    private Move generateRandomMove(Player player, int experimentNum){

        ArrayList<Move> allBestMoves = null;

        //testing minimax self play depth 1/2
        if (experimentNum == 1){
            switch(player.getColour()){
                case WHITE:
                    //allBestMoves = ai.generateAllBestMiniMaxMoves(2,player);
                    allBestMoves = ai.generateAllBestMiniMaxMoves(2,player);
                    break;
                case BLACK:
                    allBestMoves = ai.generateAllBestMiniMaxMoves(3,player);
                    //allBestMoves = ai.generateAllBestMiniMaxMoves(3,player);
                    break;
            }
        }

        //testing minimax self play depth 1/3
        if (experimentNum == 2){
            switch(player.getColour()){
                case WHITE:
                    allBestMoves = ai.generateAllBestMiniMaxMoves(1,player);
                    break;
                case BLACK:
                    allBestMoves = ai.generateAllBestMiniMaxMoves(3,player);
                    break;
            }
        }

        //testing mcts
        if (experimentNum == 3){
            switch(player.getColour()){
                    case WHITE:
                        allBestMoves = ai.MCTS(board,100);
                        break;
                    case BLACK:
                        allBestMoves = ai.MCTS(board, 250);
                        break;
                }
            }


        //testing maxN depth 1/2/3 self play
        if (experimentNum == 4){
            switch(player.getColour()){
                case WHITE:
                    allBestMoves = ai.generateAllBestMaxN3Moves(1,player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllBestMaxN3Moves(2,player);
                    break;
                case YELLOW:
                    allBestMoves = ai.generateAllBestMaxN3Moves(3,player);
                    break;
            }
        }

        //testing paranoid depth 1/2/3 self play
        if (experimentNum == 5){
            switch(player.getColour()) {
                case WHITE:
                    allBestMoves = ai.generateAllParanoid3Moves(1, player, PieceType.WHITE);
                    break;
                case RED:
                    allBestMoves = ai.generateAllParanoid3Moves(2, player, PieceType.RED);
                    break;
                case YELLOW:
                    allBestMoves = ai.generateAllParanoid3Moves(3, player, PieceType.YELLOW);
                    break;
            }
        }

        //testing greedy self play
        if (experimentNum == 6){
            switch(player.getColour()){
                case WHITE:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
                case YELLOW:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
            }
        }

        //comparative 3 player depth 1
        if (experimentNum == 7){
            switch(player.getColour()){
                case WHITE:
                    allBestMoves = ai.generateAllBestMaxN3Moves(1,player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllParanoid3Moves(1,player,PieceType.RED);
                    break;
                case YELLOW:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
            }
        }

        //comparative 3 player depth 2
        if (experimentNum == 8) {
            switch (player.getColour()) {
                case WHITE:
                    allBestMoves = ai.generateAllBestMaxN3Moves(2, player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllParanoid3Moves(2, player, PieceType.RED);
                    break;
                case YELLOW:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
            }
        }

        //comparative 3 player depth 3
        if (experimentNum == 9) {
            switch (player.getColour()) {
                case WHITE:
                    allBestMoves = ai.generateAllBestMaxN3Moves(3, player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllParanoid3Moves(3, player, PieceType.RED);
                    break;
                case YELLOW:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
            }
        }

        //comparative 6 player depth 1
        if (experimentNum == 10){
            switch(player.getColour()){
                case WHITE:
                case GREEN:
                    allBestMoves = ai.generateAllBestMaxN6Moves(1,player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllParanoid6Moves(1,player,PieceType.RED);
                    break;
                case BLACK:
                    allBestMoves = ai.generateAllParanoid6Moves(1,player,PieceType.BLACK);
                    break;
                case YELLOW:
                case PURPLE:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
            }
        }

        //comparative 6 player depth 2
        if (experimentNum == 11){
            switch(player.getColour()){
                case WHITE:
                case GREEN:
                    allBestMoves = ai.generateAllBestMaxN6Moves(2,player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllParanoid6Moves(2,player,PieceType.RED);
                    break;
                case BLACK:
                    allBestMoves = ai.generateAllParanoid6Moves(2,player,PieceType.BLACK);
                    break;
                case YELLOW:
                case PURPLE:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
            }
        }

        //comparative 6 player depth 3
        if (experimentNum == 12){
            switch(player.getColour()){
                case WHITE:
                case GREEN:
                    allBestMoves = ai.generateAllBestMaxN6Moves(3,player);
                    break;
                case RED:
                    allBestMoves = ai.generateAllParanoid6Moves(3,player,PieceType.RED);
                    break;
                case BLACK:
                    allBestMoves = ai.generateAllParanoid6Moves(3,player,PieceType.BLACK);
                    break;
                case YELLOW:
                case PURPLE:
                    allBestMoves = ai.generateAllBestGreedyMoves(player);
                    break;
            }
        }
        // three player mcts
        if (experimentNum ==13){
            switch (player.getColour()) {
                case WHITE:
                    allBestMoves = ai.MCTS(board,50);
                    break;
                case RED:
                    allBestMoves = ai.MCTS(board,100);
                    break;
                case YELLOW:
                    allBestMoves = ai.MCTS(board,250);
                    break;
            }
        }




        ArrayList<Move> finalAllBestMoves = new ArrayList<>();
        int bestval = -1000;
        for(Move move: allBestMoves) {
            int val = board.evaluateBoardAfterMove(move);
            if (val > bestval) {
                bestval = val;
            }
        }
        for (Move move: allBestMoves) {
            int val = board.evaluateBoardAfterMove(move);
            if (val == bestval){
                finalAllBestMoves.add(move);
            }
        }

        int random = new Random().nextInt(finalAllBestMoves.size());

        return finalAllBestMoves.get(random);
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
            board.currentPlayer.makeWinner();
            winner = board.currentPlayer;

            for(Player finishedplayer: board.getPlayers()){
                finishedplayer.setDistanceFromWin(board.distanceFromWin(finishedplayer));
            }
        }
    }

    public ArrayList<Player> getFinishedPlayers(){
        return board.getPlayers();
    }

    public Player getWinner(){
        return winner;
    }

    public double getExperimentTime() {
        return experimentTime;
    }
}
