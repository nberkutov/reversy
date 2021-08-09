package gui;


import exception.ServerException;
import models.base.GameState;
import models.base.interfaces.GameBoard;

public interface GameGUI {
    void updateGUI(GameBoard board, GameState gameState, String opponent) throws ServerException;

}
