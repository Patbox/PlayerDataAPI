package eu.pb4.playerdata.api.storage;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerDataStorage<T> {
    default boolean save(ServerPlayer player, T settings) {
        return this.save(player.level().getServer(), player.getUUID(), settings);
    }
    boolean save(MinecraftServer server, UUID player, T settings);

    @Nullable
    default T load(ServerPlayer player) {
        return this.load(player.level().getServer(), player.getUUID());
    }
    @Nullable
    T load(MinecraftServer server, UUID player);
}
