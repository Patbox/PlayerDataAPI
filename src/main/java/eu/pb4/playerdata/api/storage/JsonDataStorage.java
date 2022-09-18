package eu.pb4.playerdata.api.storage;

import com.google.gson.Gson;
import eu.pb4.playerdata.impl.BaseGson;
import eu.pb4.playerdata.impl.PMI;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

public record JsonDataStorage<T>(String path, Class<T> clazz, Gson gson) implements PlayerDataStorage<T> {
    public JsonDataStorage(String path, Class<T> clazz) {
        this(path, clazz, BaseGson.GSON);
    }

    @Override
    public boolean save(MinecraftServer server, UUID player, T settings) {
        if (settings == null) {
            return false;
        }

        try {
            Path path = PlayerDataApi.getPathFor(server, player);
            path.toFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.resolve(this.path + ".json").toFile()), StandardCharsets.UTF_8));
            writer.write(this.gson.toJson(settings));
            writer.close();

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
            if (!path.toFile().exists()) {
                return null;
            }

            String json = IOUtils.toString(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
            return this.gson.fromJson(json, this.clazz);
        } catch (Exception e) {
            PMI.LOGGER.error(String.format("Couldn't load player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return null;
        }
    }
}
