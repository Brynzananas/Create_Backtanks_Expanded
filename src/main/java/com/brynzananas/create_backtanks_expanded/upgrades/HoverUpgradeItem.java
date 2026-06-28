package com.brynzananas.create_backtanks_expanded.upgrades;

import com.brynzananas.create_backtanks_expanded.BacktankUpgradeItem;
import com.brynzananas.create_backtanks_expanded.Config;
import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HoverUpgradeItem extends BacktankUpgradeItem {
    public static final ResourceLocation HOVER = ResourceLocation.fromNamespaceAndPath(CreateBacktanksExpanded.MODID, "backtank_hover");
    public HoverUpgradeItem(Properties properties) {
        super(properties);
    }
    @Override
    public void OnEquip(LivingEquipmentChangeEvent event){
        LivingEntity livingEntity = event.getEntity();
        int count = Utils.GetUpgradeCount(livingEntity, CreateBacktanksExpanded.HOVER_UPGRADE.get());
        if (count == 0) return;
        Utils.AddAirRegenerationAttribute(livingEntity, HOVER, Config.HOVER_UPGRADE_PRESSURIZED_AIR_REGENERATION.get() * count);
        AttributeInstance crouchingAttribute = livingEntity.getAttribute(Attributes.SNEAKING_SPEED);
        if (crouchingAttribute != null && !crouchingAttribute.hasModifier(HOVER)){
            double value = crouchingAttribute.getValue();
            AttributeModifier modifier = new AttributeModifier(
                    HOVER,
                    1f - value,
                    AttributeModifier.Operation.ADD_VALUE
            );
            crouchingAttribute.addTransientModifier(modifier);
        }
        AttributeInstance speedAttribute = livingEntity.getAttribute(CreateBacktanksExpanded.HOVER_REACH);
        if (speedAttribute == null || speedAttribute.hasModifier(HOVER)) return;
        AttributeModifier modifier = new AttributeModifier(
                HOVER,
                Math.min(Config.HOVER_UPGRADE_HOVER_REACH_RADIUS.get() * count, Config.HOVER_UPGRADE_MAX_HOVER_REACH_RADIUS.get()) ,
                AttributeModifier.Operation.ADD_VALUE
        );
        speedAttribute.addTransientModifier(modifier);
    }
    @Override
    public void OnUnequip(LivingEquipmentChangeEvent event){
        LivingEntity livingEntity = event.getEntity();
        Utils.RemoveAirRegenerationAttribute(livingEntity, HOVER);
        AttributeInstance crouchingAttribute = livingEntity.getAttribute(Attributes.SNEAKING_SPEED);
        if (crouchingAttribute != null && crouchingAttribute.hasModifier(HOVER)){
            crouchingAttribute.removeModifier(HOVER);
        }
        AttributeInstance speedAttribute = livingEntity.getAttribute(CreateBacktanksExpanded.HOVER_REACH);
        if (speedAttribute != null && speedAttribute.hasModifier(HOVER)) {
            speedAttribute.removeModifier(HOVER);
        }
    }
    @Override
    public String ModifyTooltipString(String string, int count, ItemStack itemStack){
        return string.replaceAll("#value#", String.valueOf((Math.min(Config.HOVER_UPGRADE_HOVER_REACH_RADIUS.get() * count, Config.HOVER_UPGRADE_MAX_HOVER_REACH_RADIUS.get())))).replaceAll("#max_value#", String.valueOf(Config.HOVER_UPGRADE_MAX_HOVER_REACH_RADIUS.get()));
    }
}
