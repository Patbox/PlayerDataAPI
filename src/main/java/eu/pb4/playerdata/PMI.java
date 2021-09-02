package eu.pb4.playerdata;

import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.UUID;
@ApiStatus.Internal
public interface PMI {
    Map<PlayerDataStorage<Object>, Object> pda_getStorageMap(UUID uuid);
    <T> T pda_getStorageValue(UUID uuid, PlayerDataStorage<T> storage);
    <T> void pda_setStorageValue(UUID uuid, PlayerDataStorage<T> storage, T value);
    boolean pda_isStored(UUID uuid);
}
