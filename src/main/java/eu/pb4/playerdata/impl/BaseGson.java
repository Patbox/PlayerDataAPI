package eu.pb4.playerdata.impl;


import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;
import org.joml.*;

import java.lang.reflect.Type;
import java.util.BitSet;

public class BaseGson {
    public static final Gson GSON = createBuilder().setLenient().create();

    public static GsonBuilder createBuilder() {
        return new GsonBuilder().disableHtmlEscaping()
                .registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer())

                .registerTypeHierarchyAdapter(Item.class, new RegistrySerializer<>(Registries.ITEM))
                .registerTypeHierarchyAdapter(Block.class, new RegistrySerializer<>(Registries.BLOCK))
                .registerTypeHierarchyAdapter(Enchantment.class, new RegistrySerializer<>(Registries.ENCHANTMENT))
                .registerTypeHierarchyAdapter(SoundEvent.class, new RegistrySerializer<>(Registries.SOUND_EVENT))
                .registerTypeHierarchyAdapter(StatusEffect.class, new RegistrySerializer<>(Registries.STATUS_EFFECT))
                .registerTypeHierarchyAdapter(EntityType.class, new RegistrySerializer<>(Registries.ENTITY_TYPE))
                .registerTypeHierarchyAdapter(BlockEntityType.class, new RegistrySerializer<>(Registries.BLOCK_ENTITY_TYPE))
                .registerTypeHierarchyAdapter(GameEvent.class, new RegistrySerializer<>(Registries.GAME_EVENT))
                .registerTypeHierarchyAdapter(Fluid.class, new RegistrySerializer<>(Registries.FLUID))
                .registerTypeHierarchyAdapter(VillagerType.class, new RegistrySerializer<>(Registries.VILLAGER_TYPE))
                .registerTypeHierarchyAdapter(VillagerProfession.class, new RegistrySerializer<>(Registries.VILLAGER_PROFESSION))
                .registerTypeHierarchyAdapter(Potion.class, new RegistrySerializer<>(Registries.POTION))
                .registerTypeHierarchyAdapter(ParticleType.class, new RegistrySerializer<>(Registries.PARTICLE_TYPE))
                .registerTypeHierarchyAdapter(PaintingVariant.class, new RegistrySerializer<>(Registries.PAINTING_VARIANT))
                .registerTypeHierarchyAdapter(ChunkStatus.class, new RegistrySerializer<>(Registries.CHUNK_STATUS))
                .registerTypeHierarchyAdapter(ScreenHandlerType.class, new RegistrySerializer<>(Registries.SCREEN_HANDLER))
                .registerTypeHierarchyAdapter(RecipeType.class, new RegistrySerializer<>(Registries.RECIPE_TYPE))
                .registerTypeHierarchyAdapter(RecipeSerializer.class, new RegistrySerializer<>(Registries.RECIPE_SERIALIZER))
                .registerTypeHierarchyAdapter(EntityAttribute.class, new RegistrySerializer<>(Registries.ATTRIBUTE))
                .registerTypeHierarchyAdapter(PositionSourceType.class, new RegistrySerializer<>(Registries.POSITION_SOURCE_TYPE))
                .registerTypeHierarchyAdapter(RuleTestType.class, new RegistrySerializer<>(Registries.RULE_TEST))
                .registerTypeHierarchyAdapter(RuleBlockEntityModifier.class, new RegistrySerializer<>(Registries.RULE_BLOCK_ENTITY_MODIFIER))
                .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
                .registerTypeHierarchyAdapter(Style.class, new CodecSerializer<>(Style.Codecs.CODEC))
                .registerTypeHierarchyAdapter(ItemStack.class, new CodecSerializer<>(ItemStack.CODEC))
                .registerTypeHierarchyAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
                .registerTypeHierarchyAdapter(Vec3d.class, new CodecSerializer<>(Vec3d.CODEC))
                .registerTypeHierarchyAdapter(Vector3f.class, new CodecSerializer<>(Codecs.VECTOR_3F))
                .registerTypeHierarchyAdapter(EntityPredicate.class, new CodecSerializer<>(EntityPredicate.CODEC))
                .registerTypeHierarchyAdapter(AffineTransformation.class, new CodecSerializer<>(AffineTransformation.CODEC))
                .registerTypeHierarchyAdapter(Brightness.class, new CodecSerializer<>(Brightness.CODEC))
                .registerTypeHierarchyAdapter(Quaternionf.class, new CodecSerializer<>(Codecs.QUATERNIONF))
                .registerTypeHierarchyAdapter(AxisAngle4f.class, new CodecSerializer<>(Codecs.AXIS_ANGLE4F))
                .registerTypeHierarchyAdapter(Matrix4f.class, new CodecSerializer<>(Codecs.MATRIX4F))
                .registerTypeHierarchyAdapter(BitSet.class, new CodecSerializer<>(Codecs.BIT_SET))
                .registerTypeHierarchyAdapter(GameProfile.class, new CodecSerializer<>(Codecs.GAME_PROFILE_WITH_PROPERTIES))

                ;
    }

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
