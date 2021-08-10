package gui;


import exception.ServerException;
import models.base.GameState;
import models.base.interfaces.GameBoard;

public interface GameGUI {
    void updateGUI(final GameBoard board, final GameState gameState, final String opponent) throws ServerException;

}
