package com.brynzananas.create_backtanks_expanded;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record FilterData(ItemStack filterStack, int count) {

    public static final Codec<FilterData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.OPTIONAL_CODEC.fieldOf("filter").forGetter(FilterData::filterStack),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(FilterData::count)
            ).apply(instance, FilterData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterData> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, FilterData::filterStack,
            ByteBufCodecs.VAR_INT, FilterData::count,
            FilterData::new
    );

    /**
     * Factory method to extract serializable data directly from Create's FilteringBehavior.
     */
    public static FilterData fromBehavior(FilteringBehaviour behavior) {
        if (behavior == null) {
            return new FilterData(ItemStack.EMPTY, 1);
        }
        return new FilterData(behavior.getFilter(), behavior.getAmount());
    }

    /**
     * Applies this state back into a target FilteringBehavior instance.
     */
    public void applyTo(FilteringBehaviour behavior) {
        if (behavior != null) {
            behavior.setFilter(this.filterStack);
            behavior.count = this.count;
        }
    }
}