package org.example.models.ai.minimax.tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Node {
    private final GameBoard last;
    private final Point move;
    private final float score;

    @Override
    public String toString() {
        return "Node{" +
                "move=" + move +
                ", score=" + score +
                '}';
    }
}
