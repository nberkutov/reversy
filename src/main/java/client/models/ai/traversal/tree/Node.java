package client.models.ai.traversal.tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;

import java.util.concurrent.CopyOnWriteArrayList;

@Data
@AllArgsConstructor
public class Node {
    private final int death;
    private SimGame state;
    private Node parent;
    private CopyOnWriteArrayList<Node> child;

    public Node(SimGame state, Node parent) {
        this.state = state;
        this.parent = parent;
        child = new CopyOnWriteArrayList();
        death = 0;
    }

    public Node(SimGame state, Node parent, int death) {
        this.state = state;
        this.parent = parent;
        this.death = death;
        child = new CopyOnWriteArrayList();
    }

    public Node(GameBoard board, PlayerColor color) {
        this(new SimGame(board, color), null);
    }

    public void addChild(Node node) {
        child.add(node);
    }

    @Override
    public String toString() {
        return "Node{" +
                "state=" + state +
                ", death=" + death +
                '}';
    }
}
