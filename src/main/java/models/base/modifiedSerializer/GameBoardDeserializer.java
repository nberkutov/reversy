package models.base.modifiedSerializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.SneakyThrows;
import models.base.interfaces.GameBoard;
import services.BoardUtils;

import java.lang.reflect.Type;

public class GameBoardDeserializer implements JsonDeserializer<GameBoard> {
    @SneakyThrows
    @Override
    public GameBoard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        return BoardUtils.fromString(jsonElement.getAsString());
    }
}
