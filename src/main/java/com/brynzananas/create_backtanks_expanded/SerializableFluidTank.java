package com.brynzananas.create_backtanks_expanded;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class SerializableFluidTank extends FluidTank implements INBTSerializable<CompoundTag> {

    public SerializableFluidTank(int capacity) {
        super(capacity);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return this.writeToNBT(provider, new CompoundTag());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.readFromNBT(provider, nbt);
    }
}