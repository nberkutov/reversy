package models.base.modifiedSerializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.concurrent.locks.Lock;

public class LockSerializer implements JsonSerializer<Lock> {
    @Override
    public JsonElement serialize(final Lock lock, final Type type, final JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive("Locker");
    }
}
