package models.base.modifiedSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import logic.BoardUtils;
import lombok.SneakyThrows;
import models.base.interfaces.GameBoard;

import java.lang.reflect.Type;

public class GameBoardSerializer implements JsonSerializer<GameBoard> {

    @SneakyThrows
    @Override
    public JsonElement serialize(final GameBoard gameBoard, final Type type, final JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(BoardUtils.toString(gameBoard));
    }
}
