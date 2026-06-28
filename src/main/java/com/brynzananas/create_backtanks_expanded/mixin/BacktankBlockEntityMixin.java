package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.*;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.Debug;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.Console;
import java.util.List;

@Mixin(BacktankBlockEntity.class)
public class BacktankBlockEntityMixin{

    @Inject(method = "addBehaviours", at = @At("TAIL"))
    private void onAddBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo info){
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        behaviours.add(new BacktankHandleFluidsBehaviour(backtankBlockEntity));
//        SerializableFilteringBehaviour serializableFilteringBehaviour = backtankBlockEntity.getData(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER);
//        if (serializableFilteringBehaviour == null){
//            serializableFilteringBehaviour = (SerializableFilteringBehaviour)new SerializableFilteringBehaviour(backtankBlockEntity, new BacktankUpgradeStationBlock.BacktankValueBox()).forFluids();
//        }
//        if (serializableFilteringBehaviour.blockEntity == null){
//            serializableFilteringBehaviour.blockEntity = backtankBlockEntity;
//        }
//        behaviours.add(serializableFilteringBehaviour);
//        Level level = backtankBlockEntity.getLevel();
//        if (level == null) return;
//        behaviours.add(level.getCapability(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER, backtankBlockEntity.getBlockPos()));
    }
    @Inject(method = "write", at = @At("TAIL"))
    private void onWrite(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo info){
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        Level level = backtankBlockEntity.getLevel();
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtankBlockEntity);
        ContainerHelper.saveAllItems(compound, itemStacks, registries);
//        SerializableFilteringBehaviour serializableFilteringBehaviour = backtankBlockEntity.getData(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER);
//        if (serializableFilteringBehaviour != null){
//            serializableFilteringBehaviour.write(compound, registries, false);
//            serializableFilteringBehaviour.write(compound, registries, true);
//        }
    }
    @Inject(method = "read", at = @At("TAIL"))
    private void onRead(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo info){
        BacktankBlockEntity backtankBlockEntity = (BacktankBlockEntity) (Object) this;
        Level level = backtankBlockEntity.getLevel();
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtankBlockEntity);
        ContainerHelper.loadAllItems(compound, itemStacks, registries);
        backtankBlockEntity.setData(CreateBacktanksExpanded.BACKTANK_UPGRADES, itemStacks);
//        SerializableFilteringBehaviour serializableFilteringBehaviour = backtankBlockEntity.getData(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER);
//        if (serializableFilteringBehaviour != null){
//            serializableFilteringBehaviour.read(compound, registries, false);
//            serializableFilteringBehaviour.read(compound, registries, true);
//            backtankBlockEntity.setData(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER, serializableFilteringBehaviour);
//        }
    }
}
