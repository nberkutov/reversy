package gui;

import exception.GameException;
import models.base.Cell;
import models.board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class WindowGUI extends JFrame {
    private static final int CELL_SIZE = 100;
    private static final int OFFSET = 50;
    private JLabel[] cells;
    private Board board;

    public WindowGUI() {
        super("Reversi Client");
        board = new Board();
        int xSize = CELL_SIZE * Board.BOARD_SIZE + 2 * OFFSET;
        int ySize = xSize;
        cells = new JLabel[Board.BOARD_SIZE * Board.BOARD_SIZE];
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(xSize, ySize);
        setVisible(true);
        setBackground(Color.GREEN);
        setLocationRelativeTo(null);
    }

    public void update(Board board) throws GameException {
        this.board = board;
        repaint();
    }

    private String mapCellToChar(Cell cell) {
        switch (cell) {
            case EMPTY:
                return "_";
            case WHITE:
                return "◯";//return "⚪";
            case BLACK:
                return "⬤";
        }
        return "";
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        float length = CELL_SIZE * Board.BOARD_SIZE;

        g2.setColor(Color.decode("#187d47"));
        g2.fillRect(OFFSET, OFFSET, (int) (length), (int) (length));
        g2.setColor(Color.BLACK);
        for (int i = 0; i < Board.BOARD_SIZE + 1; i++) {
            float coordinate = i * CELL_SIZE;
            Line2D verticalLine =
                    new Line2D.Float(coordinate + OFFSET, OFFSET, coordinate + OFFSET, length + OFFSET);
            Line2D horizontalLine =
                    new Line2D.Float(OFFSET, coordinate + OFFSET, length + OFFSET, coordinate + OFFSET);
            g2.draw(verticalLine);
            g2.draw(horizontalLine);
        }

        for (int y = 0; y < Board.BOARD_SIZE; y++) {
            for (int x = 0; x < Board.BOARD_SIZE; x++) {
                try {
                    Cell cell = board.getCell(x, y);
                    if (cell == Cell.EMPTY) {
                        continue;
                    }
                    if (cell == Cell.BLACK) {
                        g2.setColor(Color.BLACK);
                    }
                    if (cell == Cell.WHITE) {
                        g2.setColor(Color.WHITE);
                    }
                    g2.fillOval(
                            x * CELL_SIZE + OFFSET + (int) (CELL_SIZE * 0.125),
                            y * CELL_SIZE + OFFSET + (int) (CELL_SIZE * 0.125),
                            (int) (CELL_SIZE * 0.75),
                            (int) (CELL_SIZE * 0.75)
                    );
                } catch (GameException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new WindowGUI();
    }
}
