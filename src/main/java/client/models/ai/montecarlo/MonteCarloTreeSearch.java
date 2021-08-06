package client.models.ai.montecarlo;

import client.models.ai.montecarlo.tree.Node;
import client.models.ai.montecarlo.tree.Tree;
import exception.GameException;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Point;
import services.BoardService;

import java.util.List;

public class MonteCarloTreeSearch {
    private static final int WIN_SCORE = 10;
    private int time;
    private PlayerColor opponent;

    public MonteCarloTreeSearch(int time) {
        this.time = time;
    }

    private static boolean isAvailableMovesPlayer(final GameBoard board, final PlayerColor color) throws GameException {
        return BoardService.getAvailableMoves(board, color).isEmpty();
    }

    private int getMillisSec() {
        return time * 1000;
    }

    public Point findNextMove(GameBoard board, PlayerColor playerNo) throws GameException {
        long start = System.currentTimeMillis();
        long end = start + getMillisSec();

        opponent = playerNo.getOpponent();
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setColorMove(playerNo);

        while (System.currentTimeMillis() < end) {
            Node promisingNode = selectPromisingNode(rootNode);

            if (isNotCriticalStateForGame(promisingNode.getState().getBoard())) {
                expandNode(promisingNode);
            }

            Node nodeToExplore = promisingNode;
            if (!promisingNode.getChildArray().isEmpty()) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            PlayerColor playoutResult = simulateRandomPlayout(nodeToExplore);

            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getMove();
    }

    private boolean isNotCriticalStateForGame(final GameBoard board) {
        return BoardService.getCountEmpty(board) != 0
                && BoardService.getCountWhite(board) != 0
                && BoardService.getCountBlack(board) != 0;
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (!node.getChildArray().isEmpty()) {
            node = UpperConfidenceBoundTrees.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) throws GameException {
        List<State> possibleStates = node.getState().getAllPossibleStates();
        for (State state : possibleStates) {
            Node newNode = new Node(state);
            newNode.setParent(node);
            newNode.getState().setColorMove(node.getState().getOpponent());
            node.getChildArray().add(newNode);
        }
    }

    private void backPropogation(Node nodeToExplore, PlayerColor playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (opponent != playerNo) {
                tempNode.getState().addScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }

    private PlayerColor simulateRandomPlayout(Node node) throws GameException {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        GameBoard board = tempState.getBoard();
        if (isPlayerLose(board, opponent.getOpponent())) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return tempState.getOpponent();
        }
        while (!BoardService.isNotPossiblePlayOnBoard(board)) {
            if (!isAvailableMovesPlayer(board, tempState.getOpponent())) {
                tempState.togglePlayer();
                tempState.randomPlay();
                continue;
            }
            tempState.randomPlay();
        }

        if (isPlayerLose(board, opponent)) {
            return opponent;
        }
        return opponent.getOpponent();
    }

    private boolean isPlayerLose(GameBoard board, PlayerColor color) {
        if (color == PlayerColor.BLACK && board.getCountBlackCells() == 0) {
            return true;
        }
        if (color == PlayerColor.WHITE && board.getCountWhiteCells() == 0) {
            return true;
        }
        if (board.getCountEmpty() == 0) {
            if (color == PlayerColor.BLACK && board.getCountBlackCells() <= board.getCountWhiteCells()) {
                return true;
            }
            return color == PlayerColor.WHITE && board.getCountWhiteCells() < board.getCountBlackCells();
        }
        return false;
    }

}
