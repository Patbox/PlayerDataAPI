package eu.pb4.playerdata.api.storage;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.impl.PMI;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public record NbtCodecDataStorage<T>(String path, Codec<T> codec) implements PlayerDataStorage<T> {

    @Override
    public boolean save(MinecraftServer server, UUID player, T settings) {
        Path path = PlayerDataApi.getPathFor(server, player);

        if (settings == null) {
            try {
                return Files.deleteIfExists(path.resolve(this.path + ".dat"));
            } catch (Throwable ignored) {
                return false;
            }
        }

        try {
            Files.createDirectories(path);

            NbtCompound out;
            var value = this.codec.encodeStart(NbtOps.INSTANCE, settings).result().get();
            if (value instanceof NbtCompound compound) {
                out = compound;
            } else {
                out = new NbtCompound();
                out.put("", value);
            }

            NbtIo.writeCompressed(out, path.resolve(this.path + ".dat"));
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
            Path path = PlayerDataApi.getPathFor(server, player).resolve(this.path + ".dat");
            if (!Files.exists(path)) {
                return null;
            }
            var nbt = NbtIo.readCompressed(path, NbtTagSizeTracker.ofUnlimitedBytes());
            NbtElement element;
            if (nbt.contains("")) {
                element = nbt.get("");
            } else {
                element = nbt;
            }

            return this.codec.decode(NbtOps.INSTANCE, element).result().map(Pair::getFirst).orElse(null);
        } catch (Exception e) {
            PMI.LOGGER.error(String.format("Couldn't load player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return null;
        }
    }
}
