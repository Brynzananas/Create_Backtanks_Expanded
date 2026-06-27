package com.brynzananas.create_backtanks_expanded.upgrades;

import com.brynzananas.create_backtanks_expanded.BacktankUpgradeItem;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.List;

public class FluidTankUpgradeItem extends BacktankUpgradeItem {
    public final int capacity;

    public FluidTankUpgradeItem(Properties properties, int capacity) {
        // Fluid items that dynamically store varying amounts should have a max stack size of 1
        super(properties.stacksTo(1));
        this.capacity = capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // FluidUtil provides an easy way to get the current fluid details from the item
        FluidUtil.getFluidContained(stack).ifPresentOrElse(
                fluidStack -> {
                    if (!fluidStack.isEmpty()) {
                        tooltip.add(Component.literal(fluidStack.getHoverName().getString() + ": "
                                + fluidStack.getAmount() + " / " + this.capacity + " mB"));
                    } else {
                        tooltip.add(Component.literal("Empty (" + this.capacity + " mB)"));
                    }
                },
                () -> tooltip.add(Component.literal("Empty (" + this.capacity + " mB)"))
        );
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
