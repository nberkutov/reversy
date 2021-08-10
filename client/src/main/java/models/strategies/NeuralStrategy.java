package models.strategies;


import exception.GameErrorCode;
import exception.ServerException;
import models.GameProperties;
import models.ai.neural.Neural;
import models.base.Cell;
import models.base.Move;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Strategy;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeuralStrategy implements Strategy {
    private static final String path = GameProperties.NEURAL_FILE;

    private BasicNetwork network;
    private ResilientPropagation train;


    public NeuralStrategy() {
        network = Neural.createNetwork();
        train = new ResilientPropagation(network, new BasicMLDataSet());
        Neural.load(train, path);
    }

    @Override
    public Point getMove(GameBoard board, PlayerColor color) throws ServerException {
        return Neural.getMove(train, board, color);
    }

    private void training() throws ServerException {
//        if (needTrain) {
//            Neural.training(train);
//            Neural.save(train, path);
//            train = Neural.load(boardNeuralScore, path);
//        }
    }

    public void trainByBoards(final List<GameBoard> boards, final PlayerColor filterColor) throws ServerException {
        Map<GameBoard, Move> map = getMovesByBoardAndNeedColor(boards, filterColor);
        MLDataSet dataSet = Neural.generateTraining(map);
        Neural.training(network, dataSet);
//        Neural.save()
    }

    private Map<GameBoard, Move> getMovesByBoardAndNeedColor(final List<GameBoard> boards, final PlayerColor filterColor) throws ServerException {
        final Map<GameBoard, Move> moves = new HashMap<>();
        for (int i = 0; i < boards.size() - 1; i++) {
            final GameBoard first = boards.get(i);
            final GameBoard second = boards.get(i + 1);
            Move move = getMoveByTwoBoards(first, second);
            if (move.getColor() == filterColor) {
                moves.put(first, move);
            }
        }
        return moves;
    }

    private Move getMoveByTwoBoards(final GameBoard first, final GameBoard two) throws ServerException {
        for (int i = 0; i < first.getSize(); i++) {
            for (int j = 0; j < first.getSize(); j++) {
                if (first.getCell(i, j) == Cell.EMPTY && two.getCell(i, j) != Cell.EMPTY) {
                    if (two.getCell(i, j) == Cell.BLACK) {
                        return new Move(PlayerColor.BLACK, new Point(i, j));
                    }
                    return new Move(PlayerColor.WHITE, new Point(i, j));
                }
            }
        }
        throw new ServerException(GameErrorCode.AI_ERROR);
    }
}
