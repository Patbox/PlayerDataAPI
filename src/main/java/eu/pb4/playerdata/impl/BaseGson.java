package eu.pb4.playerdata.impl;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Brightness;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.reflect.Type;
import java.util.BitSet;

public class BaseGson {
    public static final Gson GSON = createBuilder().setLenient().create();
    private static final ThreadLocal<HolderLookup.Provider> LOOKUP = new ThreadLocal<>();
    private static final HolderLookup.Provider FALLBACK_LOOKUP = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    public static GsonBuilder createBuilder() {
        return new GsonBuilder().disableHtmlEscaping()
                .registerTypeHierarchyAdapter(Item.class, new RegistrySerializer<>(BuiltInRegistries.ITEM))
                .registerTypeHierarchyAdapter(Block.class, new RegistrySerializer<>(BuiltInRegistries.BLOCK))
                .registerTypeHierarchyAdapter(SoundEvent.class, new RegistrySerializer<>(BuiltInRegistries.SOUND_EVENT))
                .registerTypeHierarchyAdapter(MobEffect.class, new RegistrySerializer<>(BuiltInRegistries.MOB_EFFECT))
                .registerTypeHierarchyAdapter(EntityType.class, new RegistrySerializer<>(BuiltInRegistries.ENTITY_TYPE))
                .registerTypeHierarchyAdapter(BlockEntityType.class, new RegistrySerializer<>(BuiltInRegistries.BLOCK_ENTITY_TYPE))
                .registerTypeHierarchyAdapter(GameEvent.class, new RegistrySerializer<>(BuiltInRegistries.GAME_EVENT))
                .registerTypeHierarchyAdapter(Fluid.class, new RegistrySerializer<>(BuiltInRegistries.FLUID))
                .registerTypeHierarchyAdapter(VillagerType.class, new RegistrySerializer<>(BuiltInRegistries.VILLAGER_TYPE))
                .registerTypeHierarchyAdapter(VillagerProfession.class, new RegistrySerializer<>(BuiltInRegistries.VILLAGER_PROFESSION))
                .registerTypeHierarchyAdapter(Potion.class, new RegistrySerializer<>(BuiltInRegistries.POTION))
                .registerTypeHierarchyAdapter(ParticleType.class, new RegistrySerializer<>(BuiltInRegistries.PARTICLE_TYPE))
                .registerTypeHierarchyAdapter(ChunkStatus.class, new RegistrySerializer<>(BuiltInRegistries.CHUNK_STATUS))
                .registerTypeHierarchyAdapter(MenuType.class, new RegistrySerializer<>(BuiltInRegistries.MENU))
                .registerTypeHierarchyAdapter(RecipeType.class, new RegistrySerializer<>(BuiltInRegistries.RECIPE_TYPE))
                .registerTypeHierarchyAdapter(RecipeSerializer.class, new RegistrySerializer<>(BuiltInRegistries.RECIPE_SERIALIZER))
                .registerTypeHierarchyAdapter(Attribute.class, new RegistrySerializer<>(BuiltInRegistries.ATTRIBUTE))
                .registerTypeHierarchyAdapter(PositionSourceType.class, new RegistrySerializer<>(BuiltInRegistries.POSITION_SOURCE_TYPE))
                .registerTypeHierarchyAdapter(RuleTestType.class, new RegistrySerializer<>(BuiltInRegistries.RULE_TEST))
                .registerTypeHierarchyAdapter(RuleBlockEntityModifier.class, new RegistrySerializer<>(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER))
                .registerTypeHierarchyAdapter(Identifier.class, new CodecSerializer<>(Identifier.CODEC))
                .registerTypeHierarchyAdapter(Component.class, new CodecSerializer<>(ComponentSerialization.CODEC))
                .registerTypeHierarchyAdapter(Style.class, new CodecSerializer<>(Style.Serializer.CODEC))
                .registerTypeHierarchyAdapter(ItemStack.class, new CodecSerializer<>(ItemStack.CODEC))
                .registerTypeHierarchyAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
                .registerTypeHierarchyAdapter(Vec3.class, new CodecSerializer<>(Vec3.CODEC))
                .registerTypeHierarchyAdapter(Vector3f.class, new CodecSerializer<>(ExtraCodecs.VECTOR3F))
                .registerTypeHierarchyAdapter(EntityPredicate.class, new CodecSerializer<>(EntityPredicate.CODEC))
                .registerTypeHierarchyAdapter(Transformation.class, new CodecSerializer<>(Transformation.CODEC))
                .registerTypeHierarchyAdapter(Brightness.class, new CodecSerializer<>(Brightness.CODEC))
                .registerTypeHierarchyAdapter(Quaternionf.class, new CodecSerializer<>(ExtraCodecs.QUATERNIONF_COMPONENTS))
                .registerTypeHierarchyAdapter(AxisAngle4f.class, new CodecSerializer<>(ExtraCodecs.AXISANGLE4F))
                .registerTypeHierarchyAdapter(Matrix4f.class, new CodecSerializer<>(ExtraCodecs.MATRIX4F))
                .registerTypeHierarchyAdapter(BitSet.class, new CodecSerializer<>(ExtraCodecs.BIT_SET))
                .registerTypeHierarchyAdapter(GameProfile.class, new CodecSerializer<>(ExtraCodecs.AUTHLIB_GAME_PROFILE))

                .registerTypeAdapter(new TypeToken<Holder<Enchantment>>() {}.getType(), new CodecSerializer<>(Enchantment.CODEC))
                .registerTypeAdapter(new TypeToken<Holder<Biome>>() {}.getType(), new CodecSerializer<>(Biome.CODEC))
                .registerTypeAdapter(new TypeToken<Holder<PaintingVariant>>() {}.getType(), new CodecSerializer<>(PaintingVariant.CODEC))
                ;
    }

    public static void withRegistries(HolderLookup.Provider lookup) {
        if (lookup != null) {
            LOOKUP.set(lookup);
        } else {
            LOOKUP.remove();
        }
    }

    public static HolderLookup.Provider getLookup() {
        var lookup = LOOKUP.get();
        if (lookup == null) {
            return FALLBACK_LOOKUP;
        }
        return lookup;
    }

    private record RegistrySerializer<T>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.registry.getValue(Identifier.tryParse(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("" + this.registry.getKey(src));
        }
    }

    private record CodecSerializer<T>(Codec<T> codec) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return this.codec.decode(getLookup().createSerializationContext(JsonOps.INSTANCE), json).getOrThrow().getFirst();
            } catch (Throwable e) {
                return null;
            }
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                return src != null ? this.codec.encodeStart(getLookup().createSerializationContext(JsonOps.INSTANCE), src).getOrThrow() : JsonNull.INSTANCE;
            } catch (Throwable e) {
                return JsonNull.INSTANCE;
            }
        }
    }
}
