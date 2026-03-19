package eu.pb4.playerdata.mixin;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.fixes.PlayerStorageFileFix;
import net.minecraft.util.filefix.operations.FileFixOperations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerStorageFileFix.class)
public abstract class PlayerStorageFileFixMixin extends FileFix {
    public PlayerStorageFileFixMixin(Schema schema) {
        super(schema);
    }

    @Inject(method = "makeFixer", at = @At("TAIL"))
    private void moveModdedFiles(CallbackInfo ci) {
        this.addFileFixOperation(FileFixOperations.move("player-mod-data", "players/mod_data"));
    }
}
