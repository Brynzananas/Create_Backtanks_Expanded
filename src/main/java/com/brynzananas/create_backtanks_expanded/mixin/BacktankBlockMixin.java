package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.*;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BacktankBlock.class)
public class BacktankBlockMixin {
    @Inject(method = "getCloneItemStack", at = @At("TAIL"))
    private void onGetCloneItemStack(LevelReader pLevel, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> info){
        ItemStack itemStack = info.getReturnValue();
        BlockEntity blockEntity = pLevel.getBlockEntity(pos);
        if (blockEntity == null) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(blockEntity);
        ItemContainerContents itemContainerContents = ItemContainerContents.fromItems(itemStacks);
        itemStack.set(CreateBacktanksExpanded.BACKTANK_UPGRADES_2, itemContainerContents);
        SerializableFluidTank serializableFluidTank = blockEntity.getData(CreateBacktanksExpanded.BACKTANK_FLUID_TANK);
        itemStack.set(CreateBacktanksExpanded.BACKTANK_FLUID_TANK_2, SimpleFluidContent.copyOf(serializableFluidTank.getFluid()));
//        SerializableFilteringBehaviour serializableFilteringBehaviour = blockEntity.getData(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER);
//        itemStack.set(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER_2, FilterData.fromBehavior(serializableFilteringBehaviour));
    }
    @Inject(method = "getDrops", at = @At("TAIL"), cancellable = true)
    private void onGetDrops(BlockState pState, LootParams.Builder pBuilder, CallbackInfoReturnable<List<ItemStack>> info){
        BacktankBlock backtankBlock = (BacktankBlock) (Object) this;
        List<ItemStack> lootDrops = info.getReturnValue();
        BlockEntity blockEntity = pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof BacktankBlockEntity bbe) {
            info.setReturnValue(lootDrops.stream().peek((stack) -> {
                if (stack.getItem() instanceof BacktankItem) {
                    NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(blockEntity);
                    ItemContainerContents itemContainerContents = ItemContainerContents.fromItems(itemStacks);
                    stack.set(CreateBacktanksExpanded.BACKTANK_UPGRADES_2, itemContainerContents);
                    SerializableFluidTank serializableFluidTank = blockEntity.getData(CreateBacktanksExpanded.BACKTANK_FLUID_TANK);
                    stack.set(CreateBacktanksExpanded.BACKTANK_FLUID_TANK_2, SimpleFluidContent.copyOf(serializableFluidTank.getFluid()));
//                    SerializableFilteringBehaviour serializableFilteringBehaviour = blockEntity.getData(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER);
//                    stack.set(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER_2, FilterData.fromBehavior(serializableFilteringBehaviour));
                }
            }).toList());
        }
    }
}
