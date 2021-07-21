package gui;

import exception.GameException;
import models.base.GameState;
import models.base.interfaces.GameBoard;

public interface GameGUI {
    void updateGUI(GameBoard board, GameState gameState) throws GameException;
}
