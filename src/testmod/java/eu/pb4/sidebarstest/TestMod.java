package eu.pb4.sidebarstest;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.literal;

public class TestMod implements ModInitializer {
    public static final PlayerDataStorage<TestClass> DATA_STORAGE = new JsonDataStorage<>("test_gson", TestClass.class);

    private static int test(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();

            var testObj = new TestClass();
            testObj.testString = "Hello Custom World " + Math.random() * 100;
            testObj.position = player.getPos();
            testObj.itemStack = player.getMainHandStack();
            testObj.item = player.getMainHandStack().getItem();
            testObj.text = player.getDisplayName();
            testObj.id = new Identifier("test:hello");
            PlayerDataApi.setCustomDataFor(player, DATA_STORAGE, testObj);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test2(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            var data = PlayerDataApi.getCustomDataFor(player, DATA_STORAGE);
            player.sendMessage(Text.literal(data.testString), false);
            player.sendMessage(Text.literal(data.position.toString()), false);
            player.sendMessage(Text.literal(data.itemStack.toString()), false);
            player.sendMessage(Text.literal(data.item.toString()), false);
            player.sendMessage(data.text, false);
            player.sendMessage(Text.literal(data.id.toString()), false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test3(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            PlayerDataApi.setGlobalDataFor(player, new Identifier("test"), NbtString.of("Hello Global World! " + Math.random() * 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test4(CommandContext<ServerCommandSource> objectCommandContext) {
        try {
            ServerPlayerEntity player = objectCommandContext.getSource().getPlayer();
            NbtElement element = PlayerDataApi.getGlobalDataFor(player, new Identifier("test"));
            player.sendMessage(Text.literal(element.toString()), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void onInitialize() {
        PlayerDataApi.register(DATA_STORAGE);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("test").executes(TestMod::test)
            );
            dispatcher.register(
                    literal("test2").executes(TestMod::test2)
            );
            dispatcher.register(
                    literal("test3").executes(TestMod::test3)
            );
            dispatcher.register(
                    literal("test4").executes(TestMod::test4)
            );
        });
    }

}
