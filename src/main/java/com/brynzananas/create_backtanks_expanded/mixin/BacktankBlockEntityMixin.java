package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.SerializableFluidTank;
import com.brynzananas.create_backtanks_expanded.Utils;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BacktankBlockEntity.class)
public class BacktankBlockEntityMixin extends KineticBlockEntity {
    public BacktankBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void onWrite(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo info){
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        Level level = backtankBlockEntity.getLevel();
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtankBlockEntity);
        ContainerHelper.saveAllItems(compound, itemStacks, registries);
    }
    @Inject(method = "read", at = @At("TAIL"))
    private void onRead(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo info){
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        Level level = backtankBlockEntity.getLevel();
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtankBlockEntity);
        ContainerHelper.loadAllItems(compound, itemStacks, registries);
        backtankBlockEntity.setData(CreateBacktanksExpanded.BACKTANK_UPGRADES, itemStacks);
    }

    @Inject(method = "applyImplicitComponents", at = @At("TAIL"))
    private void onApplyImplicitComponents(BlockEntity.DataComponentInput componentInput, CallbackInfo info) {
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        backtankBlockEntity.setData(CreateBacktanksExpanded.BACKTANK_UPGRADES, NonNullList.copyOf(componentInput.getOrDefault(CreateBacktanksExpanded.BACKTANK_UPGRADES_2, ItemContainerContents.EMPTY) .stream().toList()));
    }
    @Inject(method = "collectImplicitComponents", at = @At("TAIL"))
    private void onCollectImplicitComponents(DataComponentMap.Builder components, CallbackInfo info) {
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        components.set(CreateBacktanksExpanded.BACKTANK_UPGRADES_2, ItemContainerContents.fromItems(Utils.GetUpgrades(backtankBlockEntity)));
    }
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info){
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        SerializableFluidTank tank = backtankBlockEntity.getData(CreateBacktanksExpanded.BACKTANK_FLUID_TANK);
        if (tank.isEmpty()) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtankBlockEntity);
        boolean changed = false;
        for (ItemStack itemStack : itemStacks){
            FluidActionResult fluidActionResult = FluidUtil.tryFillContainer(itemStack, tank, tank.getFluidAmount(), null, true);
            if (!fluidActionResult.success) continue;
            changed = true;
            if (tank.isEmpty()) break;
        }
        if (changed) backtankBlockEntity.setChanged();
    }
}
