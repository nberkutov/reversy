package client.models.ai.minimax;

import client.models.ai.minimax.tree.Tree;
import client.models.strategies.Strategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import models.base.PlayerColor;

@Data
@AllArgsConstructor
public class MiniMaxInfo {
    private final Tree branch;
    private final int maxDepth;
    private final PlayerColor myColor;
    private final Strategy strategy;
    private final Strategy strategyBot;


}
