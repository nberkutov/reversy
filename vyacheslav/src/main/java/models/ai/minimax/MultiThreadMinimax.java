package models.ai.minimax;

import exception.ServerException;
import logic.BoardLogic;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import models.strategies.base.Algorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

@AllArgsConstructor
public class MultiThreadMinimax extends RecursiveTask<Integer> {
    private final GameBoard board;
    private final PlayerColor moveColor;
    private final Point move;
    private final int depth;
    private final MiniMaxInfo info;
    private int alpha;
    private int beta;

    public MultiThreadMinimax(GameBoard board, PlayerColor moveColor, Point move, int depth, MiniMaxInfo info) {
        this.board = board;
        this.moveColor = moveColor;
        this.move = move;
        this.depth = depth;
        this.info = info;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws ServerException {
        return !BoardLogic.getAvailableMoves(board, color).isEmpty();
    }

    @SneakyThrows
    @Override
    protected Integer compute() {
        if (depth >= info.getMaxDepth()
                || BoardLogic.isNotPossiblePlayOnBoard(board)
                || moreTriggers(board, moveColor, move)) {
            return funcEvaluation(board, moveColor, move);
        }

        final List<Point> listMoves = BoardLogic.getAvailableMoves(board, moveColor);
        final List<MultiThreadMinimax> list = new LinkedList<>();

        if (moveColor == info.getMyColor()) {
            int maxEval = Integer.MIN_VALUE;
            for (final Point point : listMoves) {
                final MultiThreadMinimax minimax = simulationMove(point, moveColor);
                minimax.fork();
                list.add(minimax);
            }

            for (final MultiThreadMinimax m : list) {
                final int eval = m.join();
                if (maxEval <= eval) {
                    maxEval = eval;
                    info.getBranch().addNode(depth + 1, board, m.move, eval);
                }

                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }

            return maxEval;
        }

        int minEval = Integer.MAX_VALUE;
        for (final Point point : listMoves) {
            final MultiThreadMinimax minimax = simulationMove(point, moveColor);
            minimax.fork();
            list.add(minimax);
        }

        for (final MultiThreadMinimax m : list) {
            final int eval = -1 * m.join();

            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }

        return minEval;
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return getAlgoByPlayerColor(color).triggerEvaluationCall(board, color, move);
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move) throws ServerException {
        return getAlgoByPlayerColor(color).funcEvaluation(board, color, move);
    }

    private Algorithm getAlgoByPlayerColor(final PlayerColor color) {
        Algorithm algo = info.getAlgoBot();
        if (color == info.getMyColor()) {
            algo = info.getAlgoPlayer();
        }
        return algo;
    }

    private MultiThreadMinimax simulationMove(final Point point, final PlayerColor color) throws ServerException {
        final GameBoard newBoard = board.clone();
        final PlayerColor opponentColor = color.getOpponent();
        BoardLogic.makeMove(newBoard, point, Cell.valueOf(color));
        PlayerColor nextMove = color;
        if (canMove(newBoard, opponentColor)) {
            nextMove = opponentColor;
        }

        return new MultiThreadMinimax(newBoard, nextMove, point, depth + 1, info, alpha, beta);
    }

}
