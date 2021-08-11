package models.base.modifiedSerializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import logic.BoardUtils;
import lombok.SneakyThrows;
import models.base.interfaces.GameBoard;

import java.lang.reflect.Type;

public class GameBoardDeserializer implements JsonDeserializer<GameBoard> {
    @SneakyThrows
    @Override
    public GameBoard deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return BoardUtils.fromString(jsonElement.getAsString());
    }
}
