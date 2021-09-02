package eu.pb4.playerdata.api.storage;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerDataStorage<T> {
    default boolean save(ServerPlayerEntity player, T settings) {
        return this.save(player.server, player.getUuid(), settings);
    }
    boolean save(MinecraftServer server, UUID player, T settings);

    @Nullable
    default T load(ServerPlayerEntity player) {
        return this.load(player.server, player.getUuid());
    }
    @Nullable
    T load(MinecraftServer server, UUID player);
}
