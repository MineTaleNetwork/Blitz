package cc.minetale.blitz.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;

public class ConfigLoader {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T loadConfig(T config, File file) throws IOException {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
        }

        if (file.createNewFile()) {
            String json = gson.toJson(JsonParser.parseString(gson.toJson(config)));
            try (PrintWriter out = new PrintWriter(file)) {
                out.println(json);
            }
        } else {
            return gson.fromJson(new String(Files.readAllBytes(file.toPath())), (Type) config.getClass());
        }

        return null;
    }

    public static void saveConfig(Object config, File file) throws IOException {
        file.createNewFile();
        String json = gson.toJson(JsonParser.parseString(gson.toJson(config)));
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(json);
        }
    }

}
