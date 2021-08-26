package org.example.models.players;


import org.example.exception.ServerException;
import org.example.logic.BoardLogic;
import org.example.models.SmartPlayer;
import org.example.models.base.GameState;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.MyStrategy;
import org.example.models.strategies.algorithms.HardAlgorithm;
import org.example.models.strategies.algorithms.StrangeAlgorithm;
import org.example.models.strategies.base.Algorithm;
import org.example.models.strategies.base.Strategy;

import java.util.Arrays;
import java.util.List;

public class AutoSmartMyBot extends SmartPlayer {
    private final Strategy strategy;
    private final List<Algorithm> algList;
    private int lose;

    public AutoSmartMyBot(final String nickname) {
        super(nickname);
        algList = Arrays.asList(new StrangeAlgorithm(), new HardAlgorithm());
        this.strategy = new MyStrategy(3, algList.get(0));

        lose = 0;
    }

    @Override
    public Point move(final GameBoard board) throws ServerException {
        return strategy.getMove(board, color);
    }

    @Override
    public void triggerMoveOpponent(final GameBoard board) throws ServerException {

    }

    @Override
    public void triggerGameEnd(final GameState state, final GameBoard board) throws ServerException {
        if (BoardLogic.getCountCellByPlayerColor(board, color)
                < BoardLogic.getCountCellByPlayerColor(board, color.getOpponent())) {
            lose++;
            if (lose % 3 == 0) {
                ((MyStrategy) strategy).incrementDeath();
            }
        }

    }
}