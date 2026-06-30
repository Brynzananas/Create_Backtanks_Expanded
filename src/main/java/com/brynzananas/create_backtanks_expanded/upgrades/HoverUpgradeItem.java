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
    public int hoverValue;
    public int air_regeneration_value;
    public int max_value;
    public int max_air_regeneration_value;
    public ResourceLocation resourceId;
    public HoverUpgradeItem(Properties properties, ResourceLocation resourceId, int hoverValue, int air_regeneration_value, int max_value, int max_air_regeneration_value) {
        super(properties);
        this.resourceId = resourceId;
        this.hoverValue = hoverValue;
        this.air_regeneration_value = air_regeneration_value;
        this.max_air_regeneration_value = max_air_regeneration_value;
    }
    @Override
    public void OnEquip(LivingEquipmentChangeEvent event, ItemStack itemStack){
        LivingEntity livingEntity = event.getEntity();
        int count = Utils.GetUpgradeCount(livingEntity, CreateBacktanksExpanded.HOVER_UPGRADE.get());
        if (count == 0) return;
        int value2 = air_regeneration_value * count;
        if (max_air_regeneration_value != 0){
            value2 = Math.min(value2, max_air_regeneration_value);
        }
        Utils.AddAirRegenerationAttribute(livingEntity, resourceId, value2);
        AttributeInstance crouchingAttribute = livingEntity.getAttribute(Attributes.SNEAKING_SPEED);
        if (crouchingAttribute != null && !crouchingAttribute.hasModifier(resourceId)){
            double value = crouchingAttribute.getValue();
            AttributeModifier modifier = new AttributeModifier(
                    resourceId,
                    1f - value,
                    AttributeModifier.Operation.ADD_VALUE
            );
            crouchingAttribute.addTransientModifier(modifier);
        }
        AttributeInstance speedAttribute = livingEntity.getAttribute(CreateBacktanksExpanded.HOVER_REACH);
        if (speedAttribute == null || speedAttribute.hasModifier(resourceId)) return;
        int value = hoverValue * count;
        if (max_value != 0){
            value = Math.min(value, max_value);
        }
        AttributeModifier modifier = new AttributeModifier(
                resourceId,
                value,
                AttributeModifier.Operation.ADD_VALUE
        );
        speedAttribute.addTransientModifier(modifier);
    }
    @Override
    public void OnUnequip(LivingEquipmentChangeEvent event, ItemStack itemStack){
        LivingEntity livingEntity = event.getEntity();
        Utils.RemoveAirRegenerationAttribute(livingEntity, resourceId);
        AttributeInstance crouchingAttribute = livingEntity.getAttribute(Attributes.SNEAKING_SPEED);
        if (crouchingAttribute != null && crouchingAttribute.hasModifier(resourceId)){
            crouchingAttribute.removeModifier(resourceId);
        }
        AttributeInstance speedAttribute = livingEntity.getAttribute(CreateBacktanksExpanded.HOVER_REACH);
        if (speedAttribute != null && speedAttribute.hasModifier(resourceId)) {
            speedAttribute.removeModifier(resourceId);
        }
    }
    @Override
    public String ModifyTooltipString(String string, int count, ItemStack itemStack){
        int value = hoverValue * count;
        if (max_value != 0){
            value = Math.min(value, max_value);
        }
        return string.replaceAll("#value#", String.valueOf(value)).replaceAll("#max_value#", String.valueOf(max_value));
    }
}
