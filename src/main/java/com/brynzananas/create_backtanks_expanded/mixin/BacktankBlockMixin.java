package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BacktankBlock.class)
public class BacktankBlockMixin {

    /*@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> info){
        BacktankBlock backtankBlock = (BacktankBlock) (Object) this;
        if (player != null && !player.isShiftKeyDown() && player.getMainHandItem()
                .getItem() instanceof BacktankUpgradeItem){
            BlockEntity blockEntity = level.getBlockEntity(pos);
            NonNullList<ItemStack> itemStacks = blockEntity.getData(CreateBacktanksExpanded.BACKTANK_UPGRADES);
//            BacktankUpgradeData backtankUpgradeData = level.getCapability(CreateBacktanksExpanded.UPGRADES, pos);
//            NonNullList<ItemStack> itemStacks = backtankUpgradeData.getUpgrades();
            boolean set = false;
            for (int i = 0; i < itemStacks.size(); i++){
                ItemStack itemStack = itemStacks.get(i);
                if (itemStack.isEmpty()){
                    itemStacks.set(i, player.getMainHandItem().copyWithCount(1));
                    set = true;
                    break;
                }
            }
            if (set)
            {
                blockEntity.setData(CreateBacktanksExpanded.BACKTANK_UPGRADES, itemStacks);
                level.sendBlockUpdated(pos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                player.getMainHandItem().shrink(1);
                level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, .75f, 1);
                info.setReturnValue(ItemInteractionResult.SUCCESS);
            }
        }
    }*/
    @Inject(method = "getCloneItemStack", at = @At("TAIL"))
    private void onGetCloneItemStack(LevelReader pLevel, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> info){
        ItemStack itemStack = info.getReturnValue();
        BlockEntity blockEntity = pLevel.getBlockEntity(pos);
        if (blockEntity == null) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(blockEntity);
        ItemContainerContents itemContainerContents = ItemContainerContents.fromItems(itemStacks);
        itemStack.set(CreateBacktanksExpanded.BACKTANK_UPGRADES_2, itemContainerContents);
    }
}
