package com.brynzananas.create_backtanks_expanded;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class SerializableFilteringBehaviour extends FilteringBehaviour  implements INBTSerializable<CompoundTag> {

    public SerializableFilteringBehaviour(SmartBlockEntity smartBlockEntity, ValueBoxTransform valueBoxTransform) {
        super(smartBlockEntity, valueBoxTransform);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        this.write(compoundTag, provider, false);
        this.write(compoundTag, provider, true);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.read(nbt, provider, false);
        this.read(nbt, provider, true);
    }
}