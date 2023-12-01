package eu.pb4.playerdata.api.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.playerdata.impl.BaseGson;
import eu.pb4.playerdata.impl.PMI;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public record JsonDataStorage<T>(String path, Class<T> clazz, Gson gson) implements PlayerDataStorage<T> {
    public JsonDataStorage(String path, Class<T> clazz) {
        this(path, clazz, BaseGson.GSON);
    }

    public JsonDataStorage(String path, Class<T> clazz, Function<GsonBuilder, GsonBuilder> builderConsumer) {
        this(path, clazz, builderConsumer.apply(createGsonBuilder()).create());
    }

    public static GsonBuilder createGsonBuilder() {
        return BaseGson.createBuilder();
    }

    @Override
    public boolean save(MinecraftServer server, UUID player, T settings) {
        Path path = PlayerDataApi.getPathFor(server, player);

        if (settings == null) {
            try {
                return Files.deleteIfExists(path.resolve(this.path + ".json"));
            } catch (Throwable ignored) {
                return false;
            }
        }

        try {
            Files.createDirectories(path);
            Files.writeString(path.resolve(this.path + ".json"), this.gson.toJson(settings), StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            PMI.LOGGER.error(String.format("Couldn't save player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public T load(MinecraftServer server, UUID player) {
        try {
            Path path = PlayerDataApi.getPathFor(server, player).resolve(this.path + ".json");
            if (!Files.exists(path)) {
                return null;
            }

            String json = Files.readString(path, StandardCharsets.UTF_8);
            return this.gson.fromJson(json, this.clazz);
        } catch (Exception e) {
            PMI.LOGGER.error(String.format("Couldn't load player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return null;
        }
    }
}
