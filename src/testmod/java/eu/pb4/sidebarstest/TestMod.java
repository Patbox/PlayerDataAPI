package eu.pb4.sidebarstest;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class TestMod implements ModInitializer {
    public static final PlayerDataStorage<TestClass> DATA_STORAGE = new JsonDataStorage<>("test_gson", TestClass.class);

    private static int test(CommandContext<CommandSourceStack> objectCommandContext) {
        try {
            ServerPlayer player = objectCommandContext.getSource().getPlayer();

            var testObj = new TestClass();
            testObj.testString = "Hello Custom World " + Math.random() * 100;
            testObj.position = player.position();
            testObj.itemStack = player.getMainHandItem();
            testObj.item = player.getMainHandItem().getItem();
            testObj.text = player.getDisplayName();
            testObj.id = Identifier.tryParse("test:hello");
            PlayerDataApi.setCustomDataFor(player, DATA_STORAGE, testObj);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test2(CommandContext<CommandSourceStack> objectCommandContext) {
        try {
            ServerPlayer player = objectCommandContext.getSource().getPlayer();
            var data = PlayerDataApi.getCustomDataFor(player, DATA_STORAGE);
            player.sendSystemMessage(Component.literal(data.testString), false);
            player.sendSystemMessage(Component.literal(data.position.toString()), false);
            player.sendSystemMessage(Component.literal(data.itemStack.toString()), false);
            player.sendSystemMessage(Component.literal(data.item.toString()), false);
            player.sendSystemMessage(data.text, false);
            player.sendSystemMessage(Component.literal(data.id.toString()), false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test3(CommandContext<CommandSourceStack> objectCommandContext) {
        try {
            ServerPlayer player = objectCommandContext.getSource().getPlayer();
            PlayerDataApi.setGlobalDataFor(player, Identifier.tryParse("test"), StringTag.valueOf("Hello Global World! " + Math.random() * 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test4(CommandContext<CommandSourceStack> objectCommandContext) {
        try {
            ServerPlayer player = objectCommandContext.getSource().getPlayer();
            var element = PlayerDataApi.getGlobalDataFor(player, Identifier.tryParse("test"));
            player.sendSystemMessage(Component.literal(element.toString()), false);
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
