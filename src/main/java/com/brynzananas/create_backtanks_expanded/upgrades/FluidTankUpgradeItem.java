package com.brynzananas.create_backtanks_expanded.upgrades;

import com.brynzananas.create_backtanks_expanded.BacktankUpgradeItem;
import com.brynzananas.create_backtanks_expanded.Config;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FluidTankUpgradeItem extends BacktankUpgradeItem {
    public final int capacity;

    public FluidTankUpgradeItem(Properties properties, int capacity) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
    }
    @Override
    public String ModifyTooltipString(String string, int count, ItemStack itemStack){
        IFluidHandlerItem capability = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null) return string;
        FluidStack fluidStack = capability.getFluidInTank(1);
        int amount = fluidStack.getAmount();
        String fluidName = fluidStack.isEmpty() ? I18n.get("item.create_backtanks_expanded.fluid_tank_upgrade.tooltip.empty") : fluidStack.getHoverName().getString();
        int capacity = capability.getTankCapacity(1);
        return string.replaceAll("#fluid_name#", fluidName).replaceAll("#value#", String.valueOf(amount)).replaceAll("#max_value#", String.valueOf(capacity));
    }
}
