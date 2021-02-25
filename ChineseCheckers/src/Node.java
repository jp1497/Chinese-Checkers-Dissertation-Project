import java.util.ArrayList;

public class Node {

    Move move;
    Node parentNode;
    Player playerJustMoved;
    ArrayList<Node> childNodes;

    int wins, visits;

    ArrayList<Move> futureMoves;

    Node(Move move, Node parent, Board board){
        this.move = move; // move taken to reach this state
        this.parentNode = parent;
        this.childNodes = new ArrayList<Node>();

        this.wins = 0;
        this.visits = 0;

        this.futureMoves = board.getAllMovesForState();
        this.playerJustMoved = board.lastPlayer;
    }

    Boolean isFullyExpanded(){
        return futureMoves.isEmpty();
    }

    Node addChild(Move move, Board board){
        Node node = new Node(move, this, board);
        this.futureMoves.remove(move);
        this.childNodes.add(node);
        return node;
    }

    void Update(boolean result){
        this.visits += 1;

        if (result)
            this.wins ++;
    }
}
