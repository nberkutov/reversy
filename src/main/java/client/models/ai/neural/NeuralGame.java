package client.models.ai.neural;

import exception.GameException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.util.List;

public class NeuralGame {
    private final BasicNetwork network;
    private final NormalizedField color;
    private final NormalizedField cell;
    private final PlayerColor myColor;
    private final GameBoard startBoard;

    public NeuralGame(BasicNetwork network, PlayerColor myColor, GameBoard startBoard) {
        this.network = network;
        this.color = new NormalizedField(NormalizationAction.Normalize, "color", 1, -1, 1, -1);
        this.cell = new NormalizedField(NormalizationAction.Normalize, "cell", 1, -1, 1, -1);
        this.myColor = myColor;
        this.startBoard = startBoard;
    }

    public double scoreGame() throws GameException {
        final SimulationBoard sim = new SimulationBoard(startBoard, myColor);
        while (!sim.isGameEnd()) {
            final MLData input = getInput(sim.getBoard(), sim.getMoveColor());
            final Point move = getNeuralMove(sim, input);
            sim.move(move);
        }
        return sim.getScore(myColor);
    }

    public Point getNeuralMove() throws GameException {
        final SimulationBoard sim = new SimulationBoard(startBoard, myColor);
        final MLData input = getInput(sim.getBoard(), sim.getMoveColor());
        return getNeuralMove(sim, input);
    }

    private Point getNeuralMove(SimulationBoard sim, MLData input) throws GameException {
        final MLData output = this.network.compute(input);
        final double value = output.getData(0);
        final List<Point> points = sim.getCanMoves();
        final NormalizedField normPoint = new NormalizedField(NormalizationAction.Normalize,
                null, points.size() - 1, 0, 1, -1);
        final int index = (int) normPoint.deNormalize(value);

        return points.get(index);
    }

    private MLData getInput(GameBoard board, PlayerColor moveColor) throws GameException {
        final MLData input = new BasicMLData(1 + board.getSize() * board.getSize());
        input.setData(0, color.normalize(moveColor.getId()));
        int k = 1;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                input.setData(k++, cell.normalize(board.getCell(i, j).getId()));
            }
        }
        return input;
    }


}
