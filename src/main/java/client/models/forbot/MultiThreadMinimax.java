package client.models.forbot;

import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

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

    private static boolean isAvailableMovesPlayer(final GameBoard board, final PlayerColor color) throws GameException {
        return !BoardService.getAvailableMoves(board, color).isEmpty();
    }

    @SneakyThrows
    @Override
    protected Integer compute() {
        if (depth >= info.getMaxDepth()
                || criticalStateForGame(board)
                || moreTriggers(board, moveColor, move, depth)) {
            return funcEvaluation(board, moveColor, move, depth);
        }

        List<Point> listMoves = BoardService.getAvailableMoves(board, moveColor);

        if (moveColor == info.getMyColor()) {
            int maxEval = Integer.MIN_VALUE;
            List<MultiThreadMinimax> list = new LinkedList<>();
            for (Point point : listMoves) {
                MultiThreadMinimax minimax = simulationMove(point, moveColor);
                minimax.fork();
                list.add(minimax);
            }
            for (MultiThreadMinimax m : list) {
                int eval = m.join();
                if (maxEval <= eval) {
                    maxEval = eval;
                    info.getBranch().addNode(depth + 1, board, m.move, eval);
                }

//                alpha = Math.max(alpha, eval);
//                if (beta <= alpha) {
//                    break;
//                }
            }

            return maxEval;
        }

        int minEval = Integer.MAX_VALUE;
        List<MultiThreadMinimax> list = new LinkedList<>();
        for (Point point : listMoves) {
            MultiThreadMinimax minimax = simulationMove(point, moveColor);
            minimax.fork();
            list.add(minimax);
        }
        for (MultiThreadMinimax m : list) {
            int eval = m.join();

            minEval = Math.min(minEval, eval);
//            beta = Math.min(beta, eval);
//            if (beta <= alpha) {
//                break;
//            }
        }

        return minEval;
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move, final int depth) throws GameException {
        if (color == info.getMyColor()) {
            return info.getStrategy().triggerEvaluationCall(board, color, move, depth, info.getMaxDepth());
        }
        return info.getStrategyBot().triggerEvaluationCall(board, color, move, depth, info.getMaxDepth());
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move, int depth) throws GameException {
        if (color == info.getMyColor()) {
            return info.getStrategy().funcEvaluation(board, color, move, depth, info.getMaxDepth());
        }
        return info.getStrategyBot().funcEvaluation(board, color, move, depth, info.getMaxDepth());
    }

    private MultiThreadMinimax simulationMove(final Point point, final PlayerColor color) throws GameException {
        final GameBoard newBoard = board.clone();
        final PlayerColor opponentColor = PlayerColor.getOpponentColor(color);
        BoardService.makeMove(newBoard, point, Cell.valueOf(color));
        PlayerColor nextMove = color;
        if (isAvailableMovesPlayer(newBoard, opponentColor)) {
            nextMove = opponentColor;
        }

        return new MultiThreadMinimax(newBoard, nextMove, point, depth + 1, info, alpha, beta);
    }

    private boolean criticalStateForGame(final GameBoard board) {
        return BoardService.getCountEmpty(board) == 0
                || BoardService.getCountWhite(board) == 0
                || BoardService.getCountBlack(board) == 0;
    }
}
