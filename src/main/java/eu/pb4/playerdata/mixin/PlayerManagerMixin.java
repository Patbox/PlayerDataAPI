package eu.pb4.playerdata.mixin;

import eu.pb4.playerdata.impl.PMI;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(value = PlayerManager.class, priority = 500)
public class PlayerManagerMixin implements PMI {
    @Unique
    private final Map<UUID, Map<PlayerDataStorage<Object>, Object>> pda_playerDataMap = new Object2ObjectOpenHashMap<>();

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void loadData(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        var map = new Object2ObjectOpenHashMap<PlayerDataStorage<Object>, Object>();
        for (PlayerDataStorage<?> storage : PlayerDataApi.getDataStorageSet()) {
            try {
                map.put(((PlayerDataStorage<Object>) storage), storage.load(player));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.pda_playerDataMap.put(player.getUuid(), map);
    }

    @Inject(method = "savePlayerData", at = @At("HEAD"))
    private void pda_saveData(ServerPlayerEntity player, CallbackInfo ci) {
        var map = this.pda_playerDataMap.get(player.getUuid());
        if (map != null) {
            for (var entry : map.entrySet()) {
                try {
                    entry.getKey().save(player, entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Inject(method = "remove", at = @At("TAIL"))
    private void pda_dontHoldOfflineData(ServerPlayerEntity player, CallbackInfo ci) {
        this.pda_playerDataMap.remove(player.getUuid());
    }

    @Override
    public Map<PlayerDataStorage<Object>, Object> pda_getStorageMap(UUID uuid) {
        return this.pda_playerDataMap.get(uuid);
    }

    @Override
    public <T> T pda_getStorageValue(UUID uuid, PlayerDataStorage<T> storage) {
        var map = this.pda_playerDataMap.get(uuid);
        if (map != null) {
            return (T) map.get(storage);
        }

        return null;
    }

    @Override
    public <T> void pda_setStorageValue(UUID uuid, PlayerDataStorage<T> storage, T value) {
        var map = this.pda_playerDataMap.get(uuid);
        if (map != null) {
            map.put((PlayerDataStorage<Object>) storage, value);
        }
    }

    @Override
    public boolean pda_isStored(UUID uuid) {
        return this.pda_playerDataMap.containsKey(uuid);
    }
}
