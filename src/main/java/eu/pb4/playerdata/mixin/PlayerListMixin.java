package eu.pb4.playerdata.mixin;

import eu.pb4.playerdata.impl.PMI;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;

@Mixin(value = PlayerList.class, priority = 500)
public class PlayerListMixin implements PMI {
    @Unique
    private final Map<UUID, Map<PlayerDataStorage<Object>, Object>> pda_playerDataMap = new Object2ObjectOpenHashMap<>();

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void loadData(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        var map = new Object2ObjectOpenHashMap<PlayerDataStorage<Object>, Object>();
        for (PlayerDataStorage<?> storage : PlayerDataApi.getDataStorageSet()) {
            try {
                map.put(((PlayerDataStorage<Object>) storage), storage.load(player));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.pda_playerDataMap.put(player.getUUID(), map);
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void pda_saveData(ServerPlayer player, CallbackInfo ci) {
        var map = this.pda_playerDataMap.get(player.getUUID());
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
    private void pda_dontHoldOfflineData(ServerPlayer player, CallbackInfo ci) {
        this.pda_playerDataMap.remove(player.getUUID());
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
