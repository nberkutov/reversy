package models.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.SneakyThrows;
import services.BoardUtils;

import java.lang.reflect.Type;

public class GameBoardSerializer implements JsonSerializer<GameBoard> {

    @SneakyThrows
    @Override
    public JsonElement serialize(GameBoard gameBoard, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(BoardUtils.toString(gameBoard));
    }
}
