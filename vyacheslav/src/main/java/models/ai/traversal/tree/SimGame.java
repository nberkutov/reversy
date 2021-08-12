package models.ai.traversal.tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;

@Data
@AllArgsConstructor
public class SimGame {
    private GameBoard board;
    private Point move;
    private PlayerColor colorMove;
    private int score;
    private boolean gameEnd;

    public SimGame(GameBoard board, PlayerColor colorMove) {
        this.board = board;
        this.colorMove = colorMove;
        gameEnd = false;
    }

    @Override
    public String toString() {
        return "SimGame{" +
                "move=" + move +
                ", colorMove=" + colorMove +
                ", score=" + score +
                ", gameEnd=" + gameEnd +
                '}';
    }
}
