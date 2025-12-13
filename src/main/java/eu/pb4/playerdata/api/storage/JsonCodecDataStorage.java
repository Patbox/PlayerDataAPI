package eu.pb4.playerdata.api.storage;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.impl.PMI;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public record JsonCodecDataStorage<T>(String path, Codec<T> codec) implements PlayerDataStorage<T> {

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

            var value = this.codec.encodeStart(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), settings).result().get();
            Files.writeString(path.resolve(this.path + ".json"), value.toString(), StandardCharsets.UTF_8);
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
            var element = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));

            return this.codec.decode(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), element).result().map(Pair::getFirst).orElse(null);
        } catch (Exception e) {
            PMI.LOGGER.error(String.format("Couldn't load player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return null;
        }
    }
}
