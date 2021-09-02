package eu.pb4.playerdata.api.storage;

import eu.pb4.playerdata.PlayerDataMod;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import java.nio.file.Path;
import java.util.UUID;

public record NbtDataStorage(String path) implements PlayerDataStorage<NbtCompound> {

    @Override
    public boolean save(MinecraftServer server, UUID player, NbtCompound settings) {
        if (settings == null) {
            return false;
        }

        try {
            Path path = PlayerDataApi.getPathFor(server, player);
            path.toFile().mkdirs();

            NbtIo.writeCompressed(settings, path.resolve(this.path + ".dat").toFile());
            return true;
        } catch (Exception e) {
            PlayerDataMod.LOGGER.error(String.format("Couldn't save player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public NbtCompound load(MinecraftServer server, UUID player) {
        try {
            Path path = PlayerDataApi.getPathFor(server, player).resolve(this.path + ".dat");
            if (!path.toFile().exists()) {
                return null;
            }

            return NbtIo.readCompressed(path.toFile());
        } catch (Exception e) {
            PlayerDataMod.LOGGER.error(String.format("Couldn't load player data of %s for path %s", player, this.path));
            e.printStackTrace();
            return null;
        }
    }
}
