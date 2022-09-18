package eu.pb4.playerdata.impl;


import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class BaseGson {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
            .registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer())

            .registerTypeHierarchyAdapter(Item.class, new RegistrySerializer<>(Registry.ITEM))
            .registerTypeHierarchyAdapter(Block.class, new RegistrySerializer<>(Registry.BLOCK))
            .registerTypeHierarchyAdapter(Enchantment.class, new RegistrySerializer<>(Registry.ENCHANTMENT))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistrySerializer<>(Registry.SOUND_EVENT))
            .registerTypeHierarchyAdapter(StatusEffect.class, new RegistrySerializer<>(Registry.STATUS_EFFECT))
            .registerTypeHierarchyAdapter(EntityType.class, new RegistrySerializer<>(Registry.ENTITY_TYPE))
            .registerTypeHierarchyAdapter(BlockEntityType.class, new RegistrySerializer<>(Registry.BLOCK_ENTITY_TYPE))

            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())

            .registerTypeHierarchyAdapter(ItemStack.class, new CodecSerializer<>(ItemStack.CODEC))
            .registerTypeHierarchyAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
            .registerTypeHierarchyAdapter(Vec3d.class, new CodecSerializer<>(Vec3d.CODEC))
            .setLenient().create();


    private record RegistrySerializer<T>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.registry.get(Identifier.tryParse(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("" + this.registry.getId(src));
        }
    }

    private record CodecSerializer<T>(Codec<T> codec) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return this.codec.decode(JsonOps.INSTANCE, json).getOrThrow(false, (x) -> {}).getFirst();
            } catch (Throwable e) {
                return null;
            }
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                return src != null ? this.codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false, (x) -> {}) : JsonNull.INSTANCE;
            } catch (Throwable e) {
                return JsonNull.INSTANCE;
            }
        }
    }
}
