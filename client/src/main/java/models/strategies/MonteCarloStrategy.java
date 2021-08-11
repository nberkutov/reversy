package models.strategies;


import exception.ServerException;
import lombok.AllArgsConstructor;
import models.ai.montecarlo.MonteCarloTreeSearch;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;

@AllArgsConstructor
public class MonteCarloStrategy implements Strategy {
    private final int time;

    public MonteCarloStrategy() {
        this(3);
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        MonteCarloTreeSearch monte = new MonteCarloTreeSearch(time);
        return monte.findNextMove(board, color);
    }
}
