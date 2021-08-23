package org.example.models;

import org.example.models.board.ArrayBoard;
import org.example.models.board.Board;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class BaseAllBoards {
    public static Stream<Arguments> getAllBoards() {
        return Stream.of(
                Arguments.of(new ArrayBoard()),
                Arguments.of(new Board())
        );
    }


}
