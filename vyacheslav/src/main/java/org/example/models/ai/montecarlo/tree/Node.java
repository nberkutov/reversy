package org.example.models.ai.montecarlo.tree;

import lombok.Data;
import org.example.models.ai.montecarlo.State;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Data
public class Node {
    private final State state;
    private Node parent;
    private final List<Node> childArray;

    public Node(final GameBoard board, final PlayerColor color) {
        this.state = new State(board, color);
        childArray = new ArrayList<>();
    }

    public Node(final State state) {
        this.state = state;
        childArray = new ArrayList<>();
    }

    public Node(final State state, final Node parent, final List<Node> childArray) {
        this.state = state;
        this.parent = parent;
        this.childArray = childArray;
    }

    public Node(Node node) {
        this.childArray = new ArrayList<>();
        this.state = new State(node.getState());
        if (node.getParent() != null) {
            this.parent = node.getParent();
        }
        final List<Node> childArray = node.getChildArray();
        for (final Node child : childArray) {
            this.childArray.add(new Node(child));
        }
    }

    public Node getRandomChildNode() {
        final int noOfPossibleMoves = childArray.size();
        final int selectRandom = (int) (Math.random() * noOfPossibleMoves);
        return this.childArray.get(selectRandom);
    }

    public Node getChildWithMaxScore() {
        return Collections.max(childArray, Comparator.comparing(c -> c.getState().getVisitCount()));
    }

    @Override
    public String toString() {
        return "Node{" +
                "state=" + state +
                ", parent=" + parent +
                ", childArraySize=" + childArray.size() +
                '}';
    }
}
