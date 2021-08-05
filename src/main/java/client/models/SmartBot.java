package client.models;


import client.models.strategies.Strategy;
import exception.GameException;
import models.base.GameState;
import models.base.interfaces.GameBoard;
import models.board.Point;

public class SmartBot extends Player {
    private final int perem;
    private AiEnum ai;
    private final Strategy strategyPlayer;

    public SmartBot(String nickname, AiEnum ai, int perem, Strategy strategyPlayer) {
        super(nickname);
        this.ai = ai;
        this.perem = perem;
        this.strategyPlayer = strategyPlayer;
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        return AiUtils.getPointByAi(board, ai, color, perem, strategyPlayer);
    }

    @Override
    public void triggerAfterGameEnd(GameState state, GameBoard board) {

    }

}
