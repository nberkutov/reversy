package client.models.ai.montecarlo;

import exception.GameException;
import lombok.Data;
import models.base.Cell;
import models.base.PlayerColor;
import models.base.interfaces.GameBoard;
import models.board.Board;
import models.board.Point;
import services.BoardService;

import java.util.ArrayList;
import java.util.List;

@Data
public class State {
    private GameBoard board;
    private Point move;
    private PlayerColor colorMove;
    private int visitCount;
    private double winScore;

    public State() {
        board = new Board();
    }

    public State(State state) {
        this.board = state.getBoard().clone();
        this.move = state.getMove();
        this.colorMove = state.getColorMove();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
    }

    public State(GameBoard board, Point move) {
        this.board = board.clone();
        this.move = move;
    }


    PlayerColor getOpponent() throws GameException {
        return colorMove.getOpponent();
    }

    public List<State> getAllPossibleStates() throws GameException {
        List<State> possibleStates = new ArrayList<>();
        List<Point> availablePositions = BoardService.getAvailableMoves(board, colorMove);
        for (Point p : availablePositions) {
            State newState = new State(board, p);
            newState.setColorMove(colorMove);
            BoardService.makeMove(newState.getBoard(), p, Cell.valueOf(newState.getColorMove()));
            possibleStates.add(newState);
        }

        return possibleStates;
    }

    void incrementVisit() {
        this.visitCount++;
    }

    void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    void randomPlay() throws GameException {
        List<Point> availablePositions = BoardService.getAvailableMoves(board, colorMove);
        int totalPossibilities = availablePositions.size();
        int selectRandom = (int) (Math.random() * totalPossibilities);
        BoardService.makeMove(board, availablePositions.get(selectRandom), Cell.valueOf(colorMove));
    }


    void maxHeuristicsPlay() throws GameException {
        Point maxEvalMove = null;
        int maxEval = Integer.MIN_VALUE;
        for (Point move : BoardService.getAvailableMoves(board, colorMove)) {
            GameBoard gb = board.clone();
            BoardService.makeMove(gb, move, Cell.valueOf(colorMove));
            int eval = BoardService.getCountCellByPlayerColor(gb, colorMove);
            if (eval > maxEval) {
                maxEval = eval;
                maxEvalMove = move;
            }
        }
        BoardService.makeMove(board, maxEvalMove, Cell.valueOf(colorMove));
    }

    void togglePlayer() throws GameException {
        this.colorMove = colorMove.getOpponent();
    }

    @Override
    public String toString() {
        return "State{" +
                "move=" + move +
                ", playerNo=" + colorMove +
                ", visitCount=" + visitCount +
                ", winScore=" + winScore +
                '}';
    }
}
