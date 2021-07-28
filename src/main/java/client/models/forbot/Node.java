package client.models.forbot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.base.interfaces.GameBoard;
import models.board.Point;

@Data
@EqualsAndHashCode
public class Node {
    private GameBoard last;
    private Point move;
    private int score;

    public Node(GameBoard last, Point move, int score) {
        this.last = last;
        this.move = move;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Node{" +
                "move=" + move +
                ", score=" + score +
                '}';
    }
}
