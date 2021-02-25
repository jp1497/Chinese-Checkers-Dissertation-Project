import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;

public class AI {

    Board board;

    AI(Board board) {
        this.board = board;
    }

    // ******************************* MCTS ***************************************

    public ArrayList<Move> MCTS(Board rootstate, float itermax){

        double explorationConst = 2;

        Node rootNode = new Node(null, null, rootstate);

        for (int i = 0; i < itermax; i++) {
            Node node = rootNode;
            Board state = rootstate.clone();
            Player initialPlayer = state.currentPlayer;

            //System.out.println("\n ROOT STATE: \n" + rootstate);

            //System.out.println("\n CLONED STATE: \n" + state);

            node = selection(node, state, explorationConst, 0);
            //System.out.println("\n SELECTED STATE: \n" + state);

            node = expansion(node, state);
            //System.out.println("\n EXPANDED STATE: \n" + state);

            simulationGuided(state);
            //System.out.println("\n END SIMULATED STATE: \n" + state);

            backpropagation(node, state);

        }
        return actionSelection(rootNode);
    }

    double UCB1(double childWins, double childVisits, int currentVisits, double explorationConst){
        return childWins / childVisits + explorationConst * Math.sqrt(Math.log(currentVisits) / childVisits);
    }

    Node selection(Node node, Board state, double explorationConst, int count){

        if (!node.isFullyExpanded() || node.childNodes.isEmpty()){
            //System.out.println("\n selection state \n" + state);
            //System.out.println("future moves from state:\n" + node.futureMoves);
            return node;
        }

        Node bestChild = null;
        double bestVal = -1;

        for (Node child: node.childNodes) {
            double val = UCB1(child.wins, child.visits, node.visits, explorationConst);

            if (val > bestVal){
                bestVal = val;
                bestChild = child;
            }

        }

        Node selectedNode = bestChild;

        state.movePieceSwitchPlayer(selectedNode.move);
        return selection(selectedNode, state, explorationConst,count+1);
    }

    Node expansion(Node node, Board state){

        if (!node.futureMoves.isEmpty()){

            int random =  new Random().nextInt(node.futureMoves.size());

            Move move = node.futureMoves.get(random);
            node = node.addChild(move, state);

            //System.out.println(state.currentPlayer);

            state.movePieceSwitchPlayer(move);

        }
        //System.out.println("EXPANDED STATE:\n" + state);
        return node;
    }

    void simulation(Board state) {

        while (!state.hasWon(state.lastPlayer)) {
            ArrayList<Move> moves = state.getAllMovesForState();
            int random = new Random().nextInt(moves.size());
            Move chosenMove = moves.get(random);
            //System.out.println("chosen move: " + chosenMove);
            //System.out.println(state);
            state.movePieceSwitchPlayer(chosenMove);
        }
    }

    void simulationGuidedRandom(Board state, float randomFactor) {

        while (!state.hasWon(state.currentPlayer)) {
            ArrayList<Move> moves = state.getAllMovesForState();

            int maxMoveValue = -100;
            Move bestMove = null;

            for (Move move: moves){
                int moveValue = state.evaluateBoardAfterMove(move);
                if(moveValue > maxMoveValue){
                    maxMoveValue = moveValue;
                    bestMove = move;
                }
            }

            float randomDecisionGen = new Random().nextFloat();
            //if randomly generated number is greater than the random factor (e.g if factor is 0.2,
            //there is a 0.8 chance of choosing the best move, rather than a random move)
            if (randomDecisionGen > randomFactor){
                state.movePieceSwitchPlayer(bestMove);
                //System.out.println("best move chosen" + state);
            }
            else {
                int randomMoveGen = new Random().nextInt(moves.size());
                Move chosenMove = moves.get(randomMoveGen);
                state.movePieceSwitchPlayer(chosenMove);
                //System.out.println("random move chosen" + state);
            }
        }
    }

    void simulationGuided(Board state) {

        int moveCount = 0;
        boolean maxedSimulations = false;

        while (!state.hasWon(state.lastPlayer) && !maxedSimulations) {
            if (moveCount >= 1000){
                maxedSimulations = true;
                break;
            }

            ArrayList<Move> moves = state.getAllMovesForState();

            int bestMoveValue = -1000;

            ArrayList<Move> bestMoves = new ArrayList<>();

            for(Move move: moves){
                int moveValue = state.evaluateBoardAfterMove(move);

                if(moveValue > bestMoveValue){
                    bestMoveValue = moveValue;
                }
            }

            for(Move move: moves) {
                int moveValue = state.evaluateBoardAfterMove(move);

                if (moveValue == bestMoveValue){
                    bestMoves.add(move);
                }
            }

            int random = new Random().nextInt(bestMoves.size());
            Move chosenMove = bestMoves.get(random);

            state.movePieceSwitchPlayer(chosenMove);
            moveCount ++;
            //System.out.println("move chosen: " + chosenMove + "\nsimulation\n" + state);
        }
    }


    void backpropagation(Node node, Board state){
        if (node != null){
            //System.out.println(state);
            //System.out.println(node.playerJustMoved);
            node.Update(state.hasWon(node.playerJustMoved));
            //System.out.println("wins: " + node.wins);
            //System.out.println("visits:" + node.visits);
            backpropagation(node.parentNode, state);
        }
    }

    ArrayList<Move> actionSelection(Node rootnode){

        ArrayList<Move> bestMoves = new ArrayList<>();
        double bestVal = -1;

        for(Node child: rootnode.childNodes){
            double val = (double)child.wins / child.visits;

            if (val >= bestVal){
                bestVal = val;
            }
        }

        for(Node child: rootnode.childNodes){
            double val = (double)child.wins / child.visits;

            if (val == bestVal){
                bestMoves.add(child.move);
            }
        }

        return bestMoves;
    }

    // ******************************* 2 PLAYERS ***************************************

    ArrayList<Move> generateAllBestGreedyMoves(Player player) {

        ArrayList<Move> bestMoves = new ArrayList<>();
        int maxMoveValue = -1000;

        for (Move move : board.getAllMovesForColour(player.getColour())) {
            int moveValue = board.evaluateBoardAfterMove(move);

            if (moveValue > maxMoveValue) {
                maxMoveValue = moveValue;
            }
        }

        for (Move move : board.getAllMovesForColour(player.getColour())) {
            int moveValue = board.evaluateBoardAfterMove(move);

            if (moveValue == maxMoveValue) {
                bestMoves.add(move);
            }
        }
        //System.out.println(bestMoves);
        return bestMoves;

    }

    private int minimaxAlphaBeta(int depth, int alpha, int beta, Player player) {

        if (depth == 0 || board.hasWon(player))
            return board.evaluateTwoPlayers();

        if(player.getColour().equals(PieceType.WHITE)) {

            for (Move move : board.getAllMovesForState()) {
                board.movePiece(move);
                int eval = minimaxAlphaBeta(depth - 1, alpha, beta, player);
                alpha = Math.max(eval, alpha);
                board.undoMove(move);
                if (alpha >= beta)
                    break;
            }
            return alpha;
        }
        else {

            for (Move move : board.getAllMovesForState()) {
                board.movePiece(move);
                int eval = minimaxAlphaBeta(depth - 1, alpha, beta, player);
                beta = Math.min(eval, beta);
                board.undoMove(move);
                if (alpha >= beta)
                    break;
            }
            return beta;
        }
    }

    ArrayList<Move> generateAllBestMiniMaxMoves(int depth, Player player) {

        if(player.getColour() == PieceType.WHITE) {
            ArrayList<Move> allMoves = new ArrayList<>();
            ArrayList<Move> bestMoves = new ArrayList<>();
            int maxMoveValue = -100;

            for (Move move : board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int moveValue = minimaxAlphaBeta(depth, -1000, 1000, player);
                if (board.hasWon(player)) {
                    bestMoves.add(move);
                    board.undoMove(move);
                    return bestMoves;
                }
                board.undoMove(move);
                if (moveValue > maxMoveValue)
                    maxMoveValue = moveValue;
                move.setMoveValue(moveValue);
                allMoves.add(move);
            }

            for (Move move : allMoves) {
                if (move.getMoveValue() == maxMoveValue)
                    bestMoves.add(move);
            }
            return bestMoves;
        }
        else {
            ArrayList<Move> allMoves = new ArrayList<>();
            ArrayList<Move> bestMoves = new ArrayList<>();
            int minMoveValue = 100;

            for (Move move : board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int moveValue = minimaxAlphaBeta(depth, -1000, 1000, player);
                if (board.hasWon(player)) {
                    bestMoves.add(move);
                    board.undoMove(move);
                    return bestMoves;
                }
                board.undoMove(move);
                if (moveValue < minMoveValue)
                    minMoveValue = moveValue;
                move.setMoveValue(moveValue);
                allMoves.add(move);
            }

            for (Move move : allMoves) {
                if (move.getMoveValue() == minMoveValue)
                    bestMoves.add(move);
            }
            return bestMoves;
        }
    }

    // ******************************* 3 PLAYERS ***************************************


    private int[] maxN3(int depth, Player player) {

        int[] maxEval = {-1000,-1000,-1000};
        //System.out.println(Arrays.toString(maxEval));

        if (depth == 0 || board.hasWon(player))
                return board.evaluateThreePlayers();

        if (player.getColour().equals(PieceType.WHITE)) {

            for (Move move: board.getAllMovesForColour(PieceType.WHITE)) {
                board.movePiece(move);
                int[] eval = maxN3(depth - 1, player);

                maxEval[0] = Math.max(maxEval[0], eval[0]);
                board.undoMove(move);
            }

        }
        if (player.getColour().equals(PieceType.YELLOW)) {

            for (Move move: board.getAllMovesForColour(PieceType.YELLOW)) {
                board.movePiece(move);

                int[] eval = maxN3(depth - 1,player);

                maxEval[1] = Math.max(maxEval[1], eval[1]);
                board.undoMove(move);
            }
        }
        if (player.getColour().equals(PieceType.RED)) {

            for (Move move: board.getAllMovesForColour(PieceType.RED)) {
                board.movePiece(move);
                int[] eval = maxN3(depth - 1,player);

                maxEval[2] = Math.max(maxEval[2], eval[2]);
                board.undoMove(move);
            }
        }
        return maxEval;
    }

    ArrayList<Move> generateAllBestMaxN3Moves(int depth, Player player) {
        ArrayList<Move> allMoves = new ArrayList<>();
        ArrayList<Move> bestMoves = new ArrayList<>();
        int[] maxMoveValue = {-1000, -1000, -1000};

        for (Move move: board.getAllMovesForColour(player.getColour())) {
            board.movePiece(move);
            int[] moveValue = maxN3(depth, player);

            if(board.hasWon(player)){
                bestMoves.add(move);
                board.undoMove(move);
                return bestMoves;
            }

            board.undoMove(move);

            switch(player.getColour()) {
                case WHITE:
                    if (moveValue[0] > maxMoveValue[0])
                        maxMoveValue[0] = moveValue[0];
                    move.setMoveValue(moveValue[0]);
                    break;
                case YELLOW:
                    if (moveValue[1] > maxMoveValue[1])
                        maxMoveValue[1] = moveValue[1];
                    move.setMoveValue(moveValue[1]);
                    break;
                case RED:
                    if (moveValue[2] > maxMoveValue[2])
                        maxMoveValue[2] = moveValue[2];
                    move.setMoveValue(moveValue[2]);
                    break;
            }
            allMoves.add(move);
        }

        for (Move move: allMoves) {

            switch (player.getColour()) {
                case WHITE:
                    if (move.getMoveValue() == maxMoveValue[0])
                        bestMoves.add(move);
                case YELLOW:
                    if (move.getMoveValue() == maxMoveValue[1])
                        bestMoves.add(move);
                case RED:
                    if (move.getMoveValue() == maxMoveValue[2])
                        bestMoves.add(move);
            }

        }

        return bestMoves;
    }

    private int paranoid3(int depth, int alpha, int beta, Player player, PieceType maximisingPlayer) {

        if (depth == 0 || board.hasWon(player))
            switch(player.getColour()){
                case WHITE:
                    return board.evaluateSixPlayers()[0];
                case RED:
                    return board.evaluateSixPlayers()[1];
                case YELLOW:
                    return board.evaluateSixPlayers()[2];
                default:
                    return 0;
            }

        // red maximising
        if (player.getColour().equals(maximisingPlayer)) {

            for (Move move: board.getAllMovesForColour(maximisingPlayer)) {
                board.movePiece(move);
                int eval = paranoid3(depth - 1, alpha, beta, player, maximisingPlayer);
                alpha = Math.max(eval, alpha);
                board.undoMove(move);
                if (alpha >= beta)
                    break;
            }
            return alpha;

        }
        else {
            for (Move move: board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int eval = paranoid3(depth - 1, alpha, beta, player, maximisingPlayer);
                beta = Math.min(eval, beta);
                board.undoMove(move);
                if (alpha >= beta)
                    break;
            }
            return beta;

        }
    }

    ArrayList<Move> generateAllParanoid3Moves(int depth, Player player, PieceType maximisingPlayer) {

        if(player.getColour() == maximisingPlayer) {
            ArrayList<Move> allMoves = new ArrayList<>();
            ArrayList<Move> bestMoves = new ArrayList<>();
            int maxMoveValue = -100;

            for (Move move : board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int moveValue = paranoid3(depth, -1000, 1000, player, maximisingPlayer);
                if (board.hasWon(player)) {
                    bestMoves.add(move);
                    board.undoMove(move);
                    return bestMoves;
                }
                board.undoMove(move);
                if (moveValue > maxMoveValue)
                    maxMoveValue = moveValue;
                move.setMoveValue(moveValue);
                allMoves.add(move);
            }

            for (Move move : allMoves) {
                if (move.getMoveValue() == maxMoveValue)
                    bestMoves.add(move);
            }
            return bestMoves;
        }
        else {
            ArrayList<Move> allMoves = new ArrayList<>();
            ArrayList<Move> bestMoves = new ArrayList<>();
            int minMoveValue = 100;

            for (Move move : board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int moveValue = paranoid3(depth, -1000, 1000, player, maximisingPlayer);
                if (board.hasWon(player)) {
                    bestMoves.add(move);
                    board.undoMove(move);
                    return bestMoves;
                }
                board.undoMove(move);
                if (moveValue < minMoveValue)
                    minMoveValue = moveValue;
                move.setMoveValue(moveValue);
                allMoves.add(move);
            }

            for (Move move : allMoves) {
                if (move.getMoveValue() == minMoveValue)
                    bestMoves.add(move);
            }
            return bestMoves;
        }

    }

    // ******************************* 4 PLAYERS ***************************************

    private int[] maxN4(int depth, Player player) {

        int[] maxEval = {-1000,-1000,-1000,-1000};

        if (depth == 0 || board.hasWon(player))
            return board.evaluateFourPlayers();

        if (player.getColour().equals(PieceType.PURPLE)) {

            for (Move move: board.getAllMovesForColour(PieceType.PURPLE)) {
                board.movePiece(move);
                int[] eval = maxN4(depth - 1, player);
                maxEval[0] = Math.max(maxEval[0], eval[0]);
                board.undoMove(move);
            }

        }
        if (player.getColour().equals(PieceType.GREEN)) {

            for (Move move: board.getAllMovesForColour(PieceType.GREEN)) {
                board.movePiece(move);
                int[] eval = maxN4(depth - 1,player);
                maxEval[1] = Math.max(maxEval[1], eval[1]);
                board.undoMove(move);
            }
        }
        if (player.getColour().equals(PieceType.YELLOW)) {

            for (Move move: board.getAllMovesForColour(PieceType.YELLOW)) {
                board.movePiece(move);
                int[] eval = maxN4(depth - 1,player);
                maxEval[2] = Math.max(maxEval[2], eval[2]);
                board.undoMove(move);
            }
        }
        if (player.getColour().equals(PieceType.RED)) {

            for (Move move: board.getAllMovesForColour(PieceType.RED)) {
                board.movePiece(move);
                int[] eval = maxN4(depth - 1,player);
                maxEval[3] = Math.max(maxEval[3], eval[3]);
                board.undoMove(move);
            }
        }
        return maxEval;
    }

    ArrayList<Move> generateAllBestMaxN4Moves(int depth, Player player) {
        ArrayList<Move> allMoves = new ArrayList<>();
        ArrayList<Move> bestMoves = new ArrayList<>();
        int[] maxMoveValue = {-1000, -1000, -1000, -1000};

        for (Move move: board.getAllMovesForColour(player.getColour())) {
            board.movePiece(move);
            int[] moveValue = maxN4(depth, player);

            if(board.hasWon(player)){
                bestMoves.add(move);
                board.undoMove(move);
                return bestMoves;
            }

            board.undoMove(move);

            switch(player.getColour()) {
                case PURPLE:
                    if (moveValue[0] > maxMoveValue[0])
                        maxMoveValue[0] = moveValue[0];
                    move.setMoveValue(moveValue[0]);
                    break;
                case GREEN:
                    if (moveValue[1] > maxMoveValue[1])
                        maxMoveValue[1] = moveValue[1];
                    move.setMoveValue(moveValue[1]);
                    break;
                case YELLOW:
                    if (moveValue[2] > maxMoveValue[2])
                        maxMoveValue[2] = moveValue[2];
                    move.setMoveValue(moveValue[2]);
                    break;
                case RED:
                    if (moveValue[3] > maxMoveValue[3])
                        maxMoveValue[3] = moveValue[3];
                    move.setMoveValue(moveValue[3]);
                    break;
            }
            allMoves.add(move);
        }

        for (Move move: allMoves) {

            switch (player.getColour()) {
                case PURPLE:
                    if (move.getMoveValue() == maxMoveValue[0])
                        bestMoves.add(move);
                case GREEN:
                    if (move.getMoveValue() == maxMoveValue[1])
                        bestMoves.add(move);
                case YELLOW:
                    if (move.getMoveValue() == maxMoveValue[2])
                        bestMoves.add(move);
                case RED:
                    if (move.getMoveValue() == maxMoveValue[3])
                        bestMoves.add(move);
            }

        }

        return bestMoves;
    }

    // ******************************* 6 PLAYERS ***************************************

    private int[] maxN6(int depth, Player player) {

        int[] maxEval = {-1000,-1000,-1000,-1000,-1000,-1000};

        if (depth == 0 || board.hasWon(player))
            return board.evaluateSixPlayers();

        if (player.getColour().equals(PieceType.PURPLE)) {

            for (Move move: board.getAllMovesForColour(PieceType.PURPLE)) {
                board.movePiece(move);
                int[] eval = maxN6(depth - 1, player);
                maxEval[0] = Math.max(maxEval[0], eval[0]);
                board.undoMove(move);
            }

        }
        if (player.getColour().equals(PieceType.GREEN)) {

            for (Move move: board.getAllMovesForColour(PieceType.GREEN)) {
                board.movePiece(move);
                int[] eval = maxN6(depth - 1,player);
                maxEval[1] = Math.max(maxEval[1], eval[1]);
                board.undoMove(move);
            }
        }
        if (player.getColour().equals(PieceType.YELLOW)) {

            for (Move move: board.getAllMovesForColour(PieceType.YELLOW)) {
                board.movePiece(move);
                int[] eval = maxN6(depth - 1,player);
                maxEval[2] = Math.max(maxEval[2], eval[2]);
                board.undoMove(move);
            }
        }
        if (player.getColour().equals(PieceType.RED)) {

            for (Move move: board.getAllMovesForColour(PieceType.RED)) {
                board.movePiece(move);
                int[] eval = maxN6(depth - 1,player);
                maxEval[3] = Math.max(maxEval[3], eval[3]);
                board.undoMove(move);
            }
        }

        if (player.getColour().equals(PieceType.WHITE)) {

            for (Move move: board.getAllMovesForColour(PieceType.WHITE)) {
                board.movePiece(move);
                int[] eval = maxN6(depth - 1,player);
                maxEval[4] = Math.max(maxEval[4], eval[4]);
                board.undoMove(move);
            }
        }

        if (player.getColour().equals(PieceType.BLACK)) {

            for (Move move: board.getAllMovesForColour(PieceType.BLACK)) {
                board.movePiece(move);
                int[] eval = maxN6(depth - 1,player);
                maxEval[5] = Math.max(maxEval[5], eval[5]);
                board.undoMove(move);
            }
        }
        return maxEval;
    }

    ArrayList<Move> generateAllBestMaxN6Moves(int depth, Player player) {
        ArrayList<Move> allMoves = new ArrayList<>();
        ArrayList<Move> bestMoves = new ArrayList<>();
        int[] maxMoveValue = {-1000, -1000, -1000, -1000, -1000, -1000};

        for (Move move: board.getAllMovesForColour(player.getColour())) {
            board.movePiece(move);
            int[] moveValue = maxN6(depth, player);

            if(board.hasWon(player)){
                bestMoves.add(move);
                board.undoMove(move);
                return bestMoves;
            }

            board.undoMove(move);

            switch(player.getColour()) {
                case PURPLE:
                    if (moveValue[0] > maxMoveValue[0])
                        maxMoveValue[0] = moveValue[0];
                    move.setMoveValue(moveValue[0]);
                    break;
                case GREEN:
                    if (moveValue[1] > maxMoveValue[1])
                        maxMoveValue[1] = moveValue[1];
                    move.setMoveValue(moveValue[1]);
                    break;
                case YELLOW:
                    if (moveValue[2] > maxMoveValue[2])
                        maxMoveValue[2] = moveValue[2];
                    move.setMoveValue(moveValue[2]);
                    break;
                case RED:
                    if (moveValue[3] > maxMoveValue[3])
                        maxMoveValue[3] = moveValue[3];
                    move.setMoveValue(moveValue[3]);
                    break;
                case WHITE:
                    if (moveValue[4] > maxMoveValue[4])
                        maxMoveValue[4] = moveValue[4];
                    move.setMoveValue(moveValue[4]);
                    break;
                case BLACK:
                    if (moveValue[5] > maxMoveValue[5])
                        maxMoveValue[5] = moveValue[5];
                    move.setMoveValue(moveValue[5]);
                    break;
            }
            allMoves.add(move);
        }

        for (Move move: allMoves) {

            switch (player.getColour()) {
                case PURPLE:
                    if (move.getMoveValue() == maxMoveValue[0])
                        bestMoves.add(move);
                case GREEN:
                    if (move.getMoveValue() == maxMoveValue[1])
                        bestMoves.add(move);
                case YELLOW:
                    if (move.getMoveValue() == maxMoveValue[2])
                        bestMoves.add(move);
                case RED:
                    if (move.getMoveValue() == maxMoveValue[3])
                        bestMoves.add(move);
                case WHITE:
                    if (move.getMoveValue() == maxMoveValue[4])
                        bestMoves.add(move);
                case BLACK:
                    if (move.getMoveValue() == maxMoveValue[5])
                        bestMoves.add(move);
            }

        }

        return bestMoves;
    }

    private int paranoid6(int depth, int alpha, int beta, Player player, PieceType maximisingPlayer) {

        if (depth == 0 || board.hasWon(player))
            switch(player.getColour()){
                case WHITE:
                    return board.evaluateSixPlayers()[0];
                case GREEN:
                    return board.evaluateSixPlayers()[1];
                case RED:
                    return board.evaluateSixPlayers()[2];
                case BLACK:
                    return board.evaluateSixPlayers()[3];
                case YELLOW:
                    return board.evaluateSixPlayers()[4];
                case PURPLE:
                    return board.evaluateSixPlayers()[5];
                default:
                    return 0;
            }

        // red maximising
        if (player.getColour().equals(maximisingPlayer)) {

            for (Move move: board.getAllMovesForColour(maximisingPlayer)) {
                board.movePiece(move);
                int eval = paranoid6(depth - 1, alpha, beta, player, maximisingPlayer);
                alpha = Math.max(eval, alpha);
                board.undoMove(move);
                if (alpha >= beta)
                    break;
            }
            return alpha;

        }
        else {
            for (Move move: board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int eval = paranoid6(depth - 1, alpha, beta, player, maximisingPlayer);
                beta = Math.min(eval, beta);
                board.undoMove(move);
                if (alpha >= beta)
                    break;
            }
            return beta;

        }
    }

    ArrayList<Move> generateAllParanoid6Moves(int depth, Player player, PieceType maximisingPlayer) {

        if(player.getColour() == maximisingPlayer) {
            ArrayList<Move> allMoves = new ArrayList<>();
            ArrayList<Move> bestMoves = new ArrayList<>();
            int maxMoveValue = -100;

            for (Move move : board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int moveValue = paranoid6(depth, -1000, 1000, player, maximisingPlayer);
                if (board.hasWon(player)) {
                    bestMoves.add(move);
                    board.undoMove(move);
                    return bestMoves;
                }
                board.undoMove(move);
                if (moveValue > maxMoveValue)
                    maxMoveValue = moveValue;
                move.setMoveValue(moveValue);
                allMoves.add(move);
            }

            for (Move move : allMoves) {
                if (move.getMoveValue() == maxMoveValue)
                    bestMoves.add(move);
            }
            return bestMoves;
        }
        else {
            ArrayList<Move> allMoves = new ArrayList<>();
            ArrayList<Move> bestMoves = new ArrayList<>();
            int minMoveValue = 100;

            for (Move move : board.getAllMovesForColour(player.getColour())) {
                board.movePiece(move);
                int moveValue = paranoid6(depth, -1000, 1000, player, maximisingPlayer);
                if (board.hasWon(player)) {
                    bestMoves.add(move);
                    board.undoMove(move);
                    return bestMoves;
                }
                board.undoMove(move);
                if (moveValue < minMoveValue)
                    minMoveValue = moveValue;
                move.setMoveValue(moveValue);
                allMoves.add(move);
            }

            for (Move move : allMoves) {
                if (move.getMoveValue() == minMoveValue)
                    bestMoves.add(move);
            }
            return bestMoves;
        }

    }

    //for testing
    public static void main(String[] args) {
        Board board = new Board(2, true);
        AI ai = new AI(board);

        while (!board.hasWon(board.lastPlayer)){
            System.out.println(board);

            //ArrayList<Move> moves = ai.generateAllBestMiniMaxMoves(2,board.currentPlayer);

            ArrayList<Move> moves = ai.generateAllBestGreedyMoves(board.currentPlayer);

            int random = new Random().nextInt(moves.size());
            board.movePiece(moves.get(random));
            board.currentPlayer.addMove();
            board.nextPlayer();
        }
        System.out.println("winner = " + board.lastPlayer + ", moves: " + board.lastPlayer.getNumMoves());

    }
}

