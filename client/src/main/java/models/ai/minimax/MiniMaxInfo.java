package models.ai.minimax;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.ai.minimax.tree.Tree;
import models.base.PlayerColor;
import models.strategies.base.Algorithm;

@Data
@AllArgsConstructor
public class MiniMaxInfo {
    private final Tree branch;
    private final int maxDepth;
    private final PlayerColor myColor;
    private final Algorithm algoPlayer;
    private final Algorithm algoBot;
}
