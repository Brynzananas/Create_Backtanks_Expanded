package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.SerializableFluidTank;
import com.brynzananas.create_backtanks_expanded.Utils;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    }
}
