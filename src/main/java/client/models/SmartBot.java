package client.models;


import client.models.forbot.Tree;
import client.models.strategies.SimpleStrategy;
import client.models.strategies.Strategy;
import exception.GameException;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;

public class SmartBot extends Player {
    private final int depth;
    private final Strategy strategyPlayer;
    private final Strategy strategyBot;
    private int alpha;
    private int beta;

    public SmartBot(final String nickname) {
        this(nickname, 3);
    }

    public SmartBot(final String nickname, final int depth) {
        this(nickname, depth, new SimpleStrategy());
    }

    public SmartBot(String nickname, int depth, Strategy strategyPlayer) {
        super(nickname);
        this.depth = depth;
        this.strategyPlayer = strategyPlayer;
        this.strategyBot = new SimpleStrategy();
    }

    private static boolean isAvailableMovesPlayer(final GameBoard board, final PlayerColor color) throws GameException {
        return BoardService.getAvailableMoves(board, color).isEmpty();
    }

    private boolean moreTriggers(final GameBoard board, final PlayerColor color, final Point move, final int depth) throws GameException {
        if (color == this.color) {
            return strategyPlayer.additionTriggerEvaluationCall(board, color, move, depth, this.depth);
        }
        return strategyBot.additionTriggerEvaluationCall(board, color, move, depth, this.depth);
    }

    private int funcEvaluation(final GameBoard board, final PlayerColor color, final Point move, int depth) throws GameException {
        if (color == this.color) {
            return strategyPlayer.funcEvaluation(board, color, move, depth, this.depth);
        }
        return strategyBot.funcEvaluation(board, color, move, depth, this.depth);
    }

    private int simulationMove(final Tree branch,
                               final GameBoard board,
                               final Point point,
                               final PlayerColor color,
                               int depth) throws GameException {
        GameBoard newBoard = board.clone();
        BoardService.makeMove(newBoard, point, Cell.valueOf(color));
        PlayerColor opponentColor = PlayerColor.getOpponentColor(color);
        int eval;
        if (isAvailableMovesPlayer(newBoard, opponentColor)) {
            eval = minimax(branch, newBoard, point, depth, opponentColor);
        } else {
            eval = minimax(branch, newBoard, point, depth, color);
        }
        return eval;
    }

    @Override
    public Point move(final GameBoard board) throws GameException {
        Tree branch = new Tree();
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        minimax(branch, board, null, 0, color);
        return branch.getMove();
    }

    private boolean criticalStateForGame(final GameBoard board) {
        return BoardService.getCountEmpty(board) == 0
                || BoardService.getCountWhite(board) == 0
                || BoardService.getCountBlack(board) == 0;
    }

    private int minimax(final Tree branch, final GameBoard board, Point move, int depth, PlayerColor moveColor) throws GameException {
        if (depth >= this.depth
                || criticalStateForGame(board)
                || moreTriggers(board, moveColor, move, depth)) {
            return funcEvaluation(board, moveColor, move, depth);
        }

        List<Point> listMoves = BoardService.getAvailableMoves(board, moveColor);

        if (moveColor == color) {
            int maxEval = Integer.MIN_VALUE;
            for (Point point : listMoves) {
                int eval = simulationMove(branch, board, point, color, depth + 1);

                if (maxEval <= eval) {
                    maxEval = eval;
                    branch.addNode(depth + 1, board, point, eval);
                }

                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }

        int minEval = Integer.MAX_VALUE;
        PlayerColor opponentColor = PlayerColor.getOpponentColor(color);
        for (Point point : listMoves) {
            int eval = -1 * simulationMove(branch, board, point, opponentColor, depth + 1);

            minEval = Math.min(minEval, eval);

            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }

}
