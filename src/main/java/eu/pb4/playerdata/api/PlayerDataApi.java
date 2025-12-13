package eu.pb4.playerdata.api;

import com.google.common.collect.ImmutableSet;
import eu.pb4.playerdata.impl.PMI;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class PlayerDataApi {
    private static final PlayerDataStorage<CompoundTag> GLOBAL_DATA_STORAGE = new NbtDataStorage("general");
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
    public static Tag getGlobalDataFor(ServerPlayer player, Identifier identifier) {
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
    public static <T extends Tag> T getGlobalDataFor(ServerPlayer player, Identifier identifier, TagType<T> type) {
        var data = getGlobalDataFor(player, identifier);
        return data != null && data.getType() == type ? (T) data : null;
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
    public static void setGlobalDataFor(ServerPlayer player, Identifier identifier, Tag element) {
        var data = getCustomDataFor(player, GLOBAL_DATA_STORAGE);
        if (data == null) {
            data = new CompoundTag();
            setCustomDataFor(player, GLOBAL_DATA_STORAGE, data);
        }
        if (element != null) {
            data.put(identifier.toString(), element);
        } else {
            data.remove(identifier.toString());
        }
    }

    @Nullable
    public static <T> T getCustomDataFor(ServerPlayer player, PlayerDataStorage<T> storage) {
        return getCustomDataFor(Objects.requireNonNull(player.level().getServer()), player.getUUID(), storage);
    }

    public static <T> void setCustomDataFor(ServerPlayer player, PlayerDataStorage<T> storage, T value) {
        setCustomDataFor(Objects.requireNonNull(player.level().getServer()), player.getUUID(), storage, value);
    }

    @Nullable
    public static <T> T getCustomDataFor(MinecraftServer server, UUID uuid, PlayerDataStorage<T> storage) {
        var pmi = ((PMI) server.getPlayerList());

        if (pmi.pda_isStored(uuid)) {
            return pmi.pda_getStorageValue(uuid, storage);
        } else {
            return storage.load(server, uuid);
        }
    }

    public static <T> void setCustomDataFor(MinecraftServer server, UUID uuid, PlayerDataStorage<T> storage, T value) {
        var pmi = ((PMI) server.getPlayerList());

        if (pmi.pda_isStored(uuid)) {
            pmi.pda_setStorageValue(uuid, storage, value);
        } else {
            storage.save(server, uuid, value);
        }
    }

    public static ImmutableSet<PlayerDataStorage<?>> getDataStorageSet() {
        return ImmutableSet.copyOf(STORAGE);
    }

    public static Path getPathFor(ServerPlayer player) {
        return getPathFor(Objects.requireNonNull(player.level().getServer()), player.getUUID());
    }

    public static Path getPathFor(MinecraftServer server, UUID uuid) {
        return server.getWorldPath(LevelResource.ROOT).resolve("player-mod-data").resolve(uuid.toString());
    }
}
