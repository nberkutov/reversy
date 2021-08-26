package org.example.models.ai.traversal;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.models.ai.traversal.tree.Node;

@Data
@AllArgsConstructor
public class Task {
    private Node node;

    public static Task create(Node node) {
        return new Task(node);
    }
}
