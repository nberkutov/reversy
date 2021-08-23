package org.example.models.ai.minimax;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.models.ai.minimax.tree.Tree;
import org.example.models.base.PlayerColor;
import org.example.models.strategies.base.Algorithm;

@Data
@AllArgsConstructor
public class MiniMaxInfo {
    private final Tree branch;
    private final int maxDepth;
    private final PlayerColor myColor;
    private final Algorithm algoPlayer;
    private final Algorithm algoBot;
}
