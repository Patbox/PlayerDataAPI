package eu.pb4.playerdata.impl;

import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
@ApiStatus.Internal
public interface PMI {
    Logger LOGGER = LoggerFactory.getLogger("Player Data API");

    Map<PlayerDataStorage<Object>, Object> pda_getStorageMap(UUID uuid);
    <T> T pda_getStorageValue(UUID uuid, PlayerDataStorage<T> storage);
    <T> void pda_setStorageValue(UUID uuid, PlayerDataStorage<T> storage, T value);
    boolean pda_isStored(UUID uuid);
}
