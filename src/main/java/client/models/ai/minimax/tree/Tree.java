package client.models.ai.minimax.tree;

import lombok.Data;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.Map;
import java.util.TreeMap;

@Data
public class Tree {
    private TreeMap<Vertex, Node> tree;

    public Tree() {
        tree = new TreeMap<>();
    }

    public synchronized void addNode(Integer depth, GameBoard last, Point point, float score) {
        tree.put(new Vertex(depth, score), new Node(last, point, score));
    }

    public Point getMove() {
        Map.Entry<Vertex, Node> entry = tree.firstEntry();
        Node node = entry.getValue();
        return node.getMove();
    }
}
