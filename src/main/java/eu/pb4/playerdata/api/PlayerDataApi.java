package eu.pb4.playerdata.api;

import com.google.common.collect.ImmutableSet;
import eu.pb4.playerdata.PMI;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PlayerDataApi {
    private static final PlayerDataStorage<NbtCompound> GLOBAL_DATA_STORAGE = new NbtDataStorage("general");
    private static final Set<PlayerDataStorage<?>> STORAGE = new HashSet<>();

    static {
        register(GLOBAL_DATA_STORAGE);
    }

    private PlayerDataApi() {
    }

    public static <T extends PlayerDataStorage<?>> boolean register(T dataStorage) {
        return STORAGE.add(dataStorage);
    }

    /**
     * Main method for getting data stored in general nbt file
     * This should be used, if you don't have too much data while being able to store it as nbt
     * Use only for online players, as offline state might be incorrect
     *
     * @param player     your target player
     * @param identifier identifier representing data
     * @return NbtElement of this data or null
     */
    @Nullable
    public static NbtElement getGlobalDataFor(ServerPlayerEntity player, Identifier identifier) {
        var data = getCustomDataFor(player, GLOBAL_DATA_STORAGE);
        return data != null ? data.get(identifier.toString()) : null;
    }

    /**
     * Main method for getting data stored in general nbt file
     * This should be used, if you don't have too much data while being able to store it as nbt
     * Use only for online players, as offline state might be incorrect
     *
     * @param player     your target player
     * @param identifier identifier representing data
     * @return NbtElement of this data or null
     */
    @Nullable
    public static <T extends NbtElement> T getGlobalDataFor(ServerPlayerEntity player, Identifier identifier, NbtType<T> type) {
        var data = getGlobalDataFor(player, identifier);
        return data != null && data.getNbtType() == type ? (T) data : null;
    }

    /**
     * Main method for setting data stored in general nbt file
     * This should be used, if you don't have too much data while being able to store it as nbt
     * Use only for online players, as it won't save for offline players!
     *
     * @param player     your target player
     * @param identifier identifier representing data
     * @param element    nbt element to be stored
     */
    public static void setGlobalDataFor(ServerPlayerEntity player, Identifier identifier, NbtElement element) {
        var data = getCustomDataFor(player, GLOBAL_DATA_STORAGE);
        if (data == null) {
            data = new NbtCompound();
            setCustomDataFor(player, GLOBAL_DATA_STORAGE, data);
        }
        if (element != null) {
            data.put(identifier.toString(), element);
        } else {
            data.remove(identifier.toString());
        }
    }

    @Nullable
    public static <T> T getCustomDataFor(ServerPlayerEntity player, PlayerDataStorage<T> storage) {
        return getCustomDataFor(player.server, player.getUuid(), storage);
    }

    public static <T> void setCustomDataFor(ServerPlayerEntity player, PlayerDataStorage<T> storage, T value) {
        setCustomDataFor(player.server, player.getUuid(), storage, value);
    }

    @Nullable
    public static <T> T getCustomDataFor(MinecraftServer server, UUID uuid, PlayerDataStorage<T> storage) {
        var pmi = ((PMI) server.getPlayerManager());

        if (pmi.pda_isStored(uuid)) {
            return pmi.pda_getStorageValue(uuid, storage);
        } else {
            return storage.load(server, uuid);
        }
    }

    public static <T> void setCustomDataFor(MinecraftServer server, UUID uuid, PlayerDataStorage<T> storage, T value) {
        var pmi = ((PMI) server.getPlayerManager());

        if (pmi.pda_isStored(uuid)) {
            pmi.pda_setStorageValue(uuid, storage, value);
        } else {
            storage.save(server, uuid, value);
        }
    }

    public static ImmutableSet<PlayerDataStorage<?>> getDataStorageSet() {
        return ImmutableSet.copyOf(STORAGE);
    }

    public static Path getPathFor(ServerPlayerEntity player) {
        return getPathFor(player.server, player.getUuid());
    }

    public static Path getPathFor(MinecraftServer server, UUID uuid) {
        return server.getSavePath(WorldSavePath.ROOT).resolve("player-mod-data").resolve(uuid.toString());
    }
}
