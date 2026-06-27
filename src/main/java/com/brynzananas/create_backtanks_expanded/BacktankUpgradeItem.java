package com.brynzananas.create_backtanks_expanded;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BacktankUpgradeItem extends Item {
    public BacktankUpgradeItem(Properties properties) {
        super(properties);
    }
    public void OnEquip(LivingEquipmentChangeEvent event){
    }
    public void OnUnequip(LivingEquipmentChangeEvent event){
    }
    public String ModifyTooltipString(String string, int count){
        return string;
    }
    public int ModifyAirRegeneration(int count) {return 0;}
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        String descriptionId = stack.getDescriptionId() + ".tooltip.item";
        Component component = Component.translatable(descriptionId);
        String literalText = component.getString();
        if (literalText.equals(descriptionId)){
            descriptionId = stack.getDescriptionId() + ".tooltip";
            component = Component.translatable(descriptionId);
        }
        literalText = ModifyTooltipString(component.getString(), 1);
        int airRegeneration = ModifyAirRegeneration(1);
        if (airRegeneration != 0 && !stack.getItem().equals(CreateBacktanksExpanded.AIR_REGENERATION_UPGRADE.get())){
            String descriptionId2 = "item.create_backtanks_expanded.pressurized_air_regeneration_upgrade.tooltip";
            Component component2 = Component.translatable(descriptionId2);
            boolean positive = airRegeneration > 0;
            String literalText2 = component2.getString().replaceAll("#value#", (positive ? "+" : "-") + Math.abs(airRegeneration));
            literalText += ". " + literalText2;
        }
        component = Component.literal(literalText).withStyle(ChatFormatting.GOLD);
        tooltipComponents.add(component);
    }
}
