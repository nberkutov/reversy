package client.models.forbot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.base.interfaces.GameBoard;
import models.board.Point;

@Data
@EqualsAndHashCode
public class Node {
    private GameBoard last;
    private GameBoard past;
    private Point move;

    public Node(GameBoard last, GameBoard past, Point move) {
        this.last = last;
        this.past = past;
        this.move = move;
    }
}
