package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonService {
    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .create();

    public static <T> String toJson(T t) {
        synchronized (GSON) {
            return GSON.toJson(t);
        }
    }


    public static <T> T fromJson(String msg, Class<T> nameClass) {
        synchronized (GSON) {
            return GSON.fromJson(msg, nameClass);
        }
    }
}
