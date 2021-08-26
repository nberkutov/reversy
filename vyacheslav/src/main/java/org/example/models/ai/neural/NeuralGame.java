package org.example.models.ai.neural;

import org.encog.ml.data.MLData;
import org.encog.neural.neat.NEATNetwork;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.example.exception.ServerException;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;
import org.example.models.board.Point;
import org.example.models.strategies.MyStrategy;
import org.example.models.strategies.algorithms.HardAlgorithm;
import org.example.models.strategies.base.Strategy;

import java.util.List;

public class NeuralGame {
    private final NEATNetwork network;
    private final PlayerColor myColor;
    private final GameBoard startBoard;
    private final Strategy str = new MyStrategy(3, new HardAlgorithm());

    public NeuralGame(NEATNetwork network, PlayerColor myColor, GameBoard startBoard) {
        this.network = network;
        this.myColor = myColor;
        this.startBoard = startBoard;
    }

    public double scoreGame() throws ServerException {
        final SimulationBoard sim = new SimulationBoard(startBoard, myColor);
        while (!sim.isGameEnd()) {
            if (sim.getMoveColor() == myColor) {
                final MLData input = Neural.getInput(sim.getBoard(), sim.getMoveColor());
                final Point move = getNeuralMove(sim, input);
                sim.move(move);
                continue;
            }

            sim.move(str.getMove(sim.getBoard(), sim.getMoveColor()));
        }
        return sim.getScore(myColor);
    }

    private Point getNeuralMove(SimulationBoard sim, MLData input) throws ServerException {
        final MLData output = this.network.compute(input);
        final double value = output.getData(0);
        final List<Point> points = sim.getCanMoves();
        final NormalizedField normPoint = new NormalizedField(NormalizationAction.Normalize,
                null, points.size() - 1d, 0, 1, -1);
        final int index = (int) normPoint.deNormalize(value);

        return points.get(index);
    }


}
