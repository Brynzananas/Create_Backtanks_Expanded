package com.brynzananas.create_backtanks_expanded;

import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Debug;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class BacktankHandleFluidsBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<BacktankHandleFluidsBehaviour> TYPE = new BehaviourType<>();

    public BacktankHandleFluidsBehaviour(SmartBlockEntity be) {
        super(be);
    }
        @Override
        public void tick(){
        if (blockEntity == null) return;
        SerializableFluidTank tank = blockEntity.getData(CreateBacktanksExpanded.BACKTANK_FLUID_TANK);
        if (tank.isEmpty()) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(blockEntity);
        boolean changed = false;
        for (int i = 0; i < itemStacks.size(); i++){
            ItemStack itemStack = itemStacks.get(i);
            IFluidHandlerItem capability = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability == null) continue;
            FluidStack toFill = tank.getFluid().copy();
            int filled = capability.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
            if (filled <= 0) continue;
            tank.getFluid().shrink(filled);
            ItemStack container = capability.getContainer().copy();
            changed = true;
            itemStacks.set(i, container);
            if (tank.isEmpty()) break;
        }
        if (changed){
            blockEntity.setChanged();
            blockEntity.setData(CreateBacktanksExpanded.BACKTANK_UPGRADES, itemStacks);
        }
    }
    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
