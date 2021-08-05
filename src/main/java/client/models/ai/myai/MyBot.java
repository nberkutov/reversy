package client.models.ai.myai;

import client.models.ai.minimax.tree.Tree;
import client.models.strategies.Strategy;
import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;

public class MyBot {
    private final int depth;
    private final PlayerColor myColor;
    private final Strategy strategy;


    public MyBot(int depth, PlayerColor myColor, Strategy strategy) {
        this.depth = depth;
        this.myColor = myColor;
        this.strategy = strategy;
    }

    public int calculateMove(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws GameException {
        if (depth >= this.depth
                || BoardService.isNotPossiblePlayOnBoard(board)
                || moreTriggers(board, moveColor, move, depth)) {
            return funcEvaluation(board, moveColor, move, depth);
        }

        List<Point> listMoves = BoardService.getAvailableMoves(board, moveColor);

        int maxEval = Integer.MIN_VALUE;
        for (Point point : listMoves) {
            int eval = simulationMove(branch, board, point, myColor, depth + 1);

            if (maxEval <= eval) {
                maxEval = eval;

                branch.addNode(depth + 1, board, point, eval);
            }

        }
        return maxEval;
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move, final int depth) throws GameException {
        return strategy.triggerEvaluationCall(board, color, move, depth, this.depth);
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move, int depth) throws GameException {
        return strategy.funcEvaluation(board, color, move, depth, this.depth);
    }

    private int simulationMove(final Tree branch,
                               final GameBoard board,
                               final Point point,
                               final PlayerColor color,
                               int depth) throws GameException {
        GameBoard newBoard = board.clone();
        BoardService.makeMove(newBoard, point, Cell.valueOf(color));

        return calculateMove(branch, newBoard, point, depth, color);
    }
}
