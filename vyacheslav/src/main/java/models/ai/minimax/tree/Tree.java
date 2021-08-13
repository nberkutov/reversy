package models.ai.minimax.tree;

import lombok.Data;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.Map;
import java.util.TreeMap;

@Data
public class Tree {
    private final TreeMap<Vertex, Node> map;

    public Tree() {
        map = new TreeMap<>();
    }

    public synchronized void addNode(Integer depth, GameBoard last, Point point, float score) {
        map.put(new Vertex(depth, score), new Node(last, point, score));
    }

    public Point getMove() {
        Map.Entry<Vertex, Node> entry = map.firstEntry();
        Node node = entry.getValue();
        return node.getMove();
    }
}
