package org.example.models.ai.traversal.tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.models.base.PlayerColor;
import org.example.models.base.interfaces.GameBoard;

@Data
@AllArgsConstructor
public class Tree {
    private Node root;

    public Tree(GameBoard board, PlayerColor color) {
        this.root = new Node(board, color);
    }
}
