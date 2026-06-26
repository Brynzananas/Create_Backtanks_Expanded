package com.brynzananas.create_backtanks_expanded;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;

public class BacktankUpgradeStationBlock extends Block {
    public BacktankUpgradeStationBlock(Properties properties) {
        super(properties);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED});
    }
    private static final int MAX_BLOCKS = 1024;
    private static final int MAX_DISTANCE = 1024;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    @Override
    @Deprecated
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
        if (level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }
    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            BlockState blockstate = state;
            if (!(Boolean)state.getValue(POWERED)) {
                boolean hasPower = level.hasNeighborSignal(pos);
                if (hasPower) {
                    BlockEntity blockEntity = level.getBlockEntity(pos.above());
                    if (!(blockEntity instanceof BacktankBlockEntity backtankBlockEntity)) return;
                    runNetworkScan(level, pos, backtankBlockEntity);
                }
            }
            level.setBlock(pos, (BlockState)blockstate.setValue(POWERED, flag), 3);
        }

    }
    private void runNetworkScan(Level level, BlockPos startPos, BacktankBlockEntity backtank) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> foundBlocks = new ArrayList<>();

        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;;
            BlockPos neighbor = startPos.relative(direction);
            if (level.getBlockEntity(neighbor) instanceof DepotBlockEntity) {
                queue.add(neighbor);
                visited.add(neighbor);
            }
        }

        while (!queue.isEmpty() && foundBlocks.size() < MAX_BLOCKS) {
            BlockPos current = queue.poll();
            foundBlocks.add(current);

            for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
                if (direction == Direction.UP || direction == Direction.DOWN) continue;;
                BlockPos nextPos = current.relative(direction);

                if (!visited.contains(nextPos)
                        && (level.getBlockEntity(nextPos) instanceof DepotBlockEntity)
                        && startPos.distManhattan(nextPos) <= MAX_DISTANCE) {

                    visited.add(nextPos);
                    queue.add(nextPos);
                }
            }
        }

        boolean hasUpgrades = false;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtank);
        for (int i = 0; i < itemStacks.size(); i++){
            ItemStack itemStack= itemStacks.get(i);
            if (!itemStack.isEmpty()){
                hasUpgrades = true;
                break;
            }
        }
        int i = 0;
        for (BlockPos foundPos : foundBlocks) {
            DepotBlockEntity depotBlockEntity = (DepotBlockEntity) level.getBlockEntity(foundPos);
            if (hasUpgrades){
                RemoveUpgrade(backtank, depotBlockEntity, i, level);
            }else{
                AddUpgrade(backtank, depotBlockEntity, level);
            }
            i++;
        }
    }
    private void RemoveUpgrade(BacktankBlockEntity backtankBlockEntity, DepotBlockEntity depotBlockEntity, int id, Level level){
        if (depotBlockEntity.getHeldItem() != ItemStack.EMPTY) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtankBlockEntity);
        if (id >= itemStacks.size()) return;
        ItemStack itemStack = itemStacks.get(id);
        depotBlockEntity.setHeldItem(itemStack.copy());
        itemStacks.set(id, ItemStack.EMPTY);
        backtankBlockEntity.setData(CreateBacktanksExpanded.BACKTANK_UPGRADES, itemStacks);
        level.sendBlockUpdated(backtankBlockEntity.getBlockPos(), backtankBlockEntity.getBlockState(), backtankBlockEntity.getBlockState(), 3);
        level.sendBlockUpdated(depotBlockEntity.getBlockPos(), depotBlockEntity.getBlockState(), depotBlockEntity.getBlockState(), 3);
        level.playSound(null, backtankBlockEntity.getBlockPos(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, .75f, 1);
    }
    private void AddUpgrade(BacktankBlockEntity backtankBlockEntity, DepotBlockEntity depotBlockEntity, Level level){
        if (!(depotBlockEntity.getHeldItem().getItem() instanceof BacktankUpgradeItem)) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(backtankBlockEntity);
        boolean set = false;
        int itemsToTake = 0;
        for (int i = 0; i < itemStacks.size(); i++){
            ItemStack itemStack = itemStacks.get(i);
            if (itemStack.isEmpty()){
                itemsToTake = depotBlockEntity.getHeldItem().getCount();
                itemStacks.set(i, depotBlockEntity.getHeldItem().copyWithCount(itemsToTake));
                set = true;
                break;
            }else if (itemStack.getItem().equals(depotBlockEntity.getHeldItem().getItem())){
                itemsToTake = depotBlockEntity.getHeldItem().getCount();
                itemStacks.set(i, depotBlockEntity.getHeldItem().copyWithCount(itemsToTake + itemStack.getCount()));
                set = true;
                break;
            }
        }
        if (set)
        {
            backtankBlockEntity.setData(CreateBacktanksExpanded.BACKTANK_UPGRADES, itemStacks);
            int itemCount = depotBlockEntity.getHeldItem().getCount();
            itemCount -= itemsToTake;
            if (itemCount <= 0){
                depotBlockEntity.setHeldItem(ItemStack.EMPTY);
            }else{
                depotBlockEntity.setHeldItem(depotBlockEntity.getHeldItem().copyWithCount(itemCount));
            }
            level.sendBlockUpdated(backtankBlockEntity.getBlockPos(), backtankBlockEntity.getBlockState(), backtankBlockEntity.getBlockState(), 3);
            level.sendBlockUpdated(depotBlockEntity.getBlockPos(), depotBlockEntity.getBlockState(), depotBlockEntity.getBlockState(), 3);
            level.playSound(null, backtankBlockEntity.getBlockPos(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, .75f, 1);
        }
    }
}
