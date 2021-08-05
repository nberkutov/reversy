package client.models;

import client.models.strategies.StrangeStrategy;
import client.models.strategies.Strategy;
import exception.GameException;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;

public class AutoSmartMyBot extends Player {
    private AiEnum ai;
    private Strategy strategy;
    private int lose;
    private int death;

    public AutoSmartMyBot(String nickname) {
        super(nickname);
        ai = AiEnum.MY_BOT;
        strategy = new StrangeStrategy();
        lose = 0;
        death = 2;
    }

    @Override
    public Point move(GameBoard board) throws GameException {
        return AiUtils.getPointByAi(board, ai, color, death, strategy);
    }

    @Override
    public void triggerAfterGameEnd(GameState state, GameBoard board) {

    }
}
