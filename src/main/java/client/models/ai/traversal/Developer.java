package client.models.ai.traversal;


import client.models.ai.traversal.tree.Node;
import client.models.ai.traversal.tree.SimGame;
import exception.GameException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.concurrent.BlockingDeque;

@AllArgsConstructor
public class Developer extends Thread {
    private final BlockingDeque<Task> tasks;
    private final BlockingDeque<Node> result;
    private final PlayerColor myColor;
    private final DeveloperOptions options;
    @Getter
    @Setter
    private boolean stopWork;
    @Getter
    private int maxDeath;

    public Developer(BlockingDeque<Task> tasks, BlockingDeque<Node> result, PlayerColor myColor, DeveloperOptions options) {
        this.tasks = tasks;
        this.result = result;
        this.myColor = myColor;
        this.options = options;
        stopWork = false;
        maxDeath = -1;
    }

    private static boolean canMove(final GameBoard board, final PlayerColor color) throws GameException {
        return !BoardService.getAvailableMoves(board, color).isEmpty();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Task t = tasks.takeFirst();
                Node n = t.getNode();
                if (n == null) {
                    break;
                }
                simulation(n);
                if (stopWork) {
                    result.putLast(n);
                    break;
                }
            }
        } catch (GameException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        stopWork = true;
    }

    private void simulation(Node node) throws GameException, InterruptedException {
        SimGame state = node.getState();
        PlayerColor color = state.getColorMove();
        GameBoard board = state.getBoard();
        if (BoardService.isNotPossiblePlayOnBoard(board)) {
            state.setGameEnd(true);
        }

        if (node.getDeath() >= options.getMaxDeath() || state.isGameEnd()) {
            result.putLast(node);
            return;
        }

        for (Point p : BoardService.getAvailableMoves(board, color)) {
            GameBoard newB = board.clone();
            BoardService.makeMove(newB, p, Cell.valueOf(color));
            PlayerColor nextMoveColor = color.getOpponent();
            if (!canMove(newB, nextMoveColor)) {
                nextMoveColor = color;
            }
            int score = BoardService.getCountCellByPlayerColor(board, myColor);
            SimGame newState = new SimGame(newB, p, nextMoveColor, score, false);
            Node newN = new Node(newState, node, node.getDeath() + 1);
            node.addChild(newN);
            if (options.getOption() == TraversalEnum.DEEP) {
                tasks.putFirst(new Task(newN));
            } else {
                tasks.putLast(new Task(newN));
            }
        }
        maxDeath = Math.max(maxDeath, node.getDeath());
    }

}
