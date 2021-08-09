package gui;

import exception.ServerException;
import lombok.NoArgsConstructor;
import models.base.GameState;
import models.base.interfaces.GameBoard;

@NoArgsConstructor
public class EmptyGUI implements GameGUI {
    @Override
    public void updateGUI(GameBoard board, GameState gameState, String opponent) throws ServerException {

    }
}
