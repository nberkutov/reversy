package client.models.forbot;

import lombok.Data;
import models.base.interfaces.GameBoard;
import models.board.Point;

import java.util.Map;
import java.util.TreeMap;

@Data
public class Tree {
    private TreeMap<Integer, Node> tree;

    public Tree() {
        tree = new TreeMap<>();
    }

    public void addNode(Integer depth, GameBoard last, GameBoard past, Point point) {
        tree.put(depth, new Node(last, past, point));
    }

    public Point getMove() {
        Map.Entry<Integer, Node> entry = tree.lastEntry();
        Node node = entry.getValue();
        return node.getMove();
    }

}
