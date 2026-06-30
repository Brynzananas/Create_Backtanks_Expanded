package com.brynzananas.create_backtanks_expanded.upgrades;

import com.brynzananas.create_backtanks_expanded.BacktankUpgradeItem;
import com.brynzananas.create_backtanks_expanded.Config;
import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

public class SpeedUpgradeItem extends BacktankUpgradeItem {
    public double speedValue;
    public int air_regeneration_value;
    public double max_value;
    public int max_air_regeneration_value;
    public ResourceLocation resourceId;
    public SpeedUpgradeItem(Properties properties, ResourceLocation resourceId, double speedValue, int air_regeneration_value, double max_value, int max_air_regeneration_value) {
        super(properties);
        this.resourceId = resourceId;
        this.speedValue = speedValue;
        this.air_regeneration_value = air_regeneration_value;
        this.max_value = max_value;
        this.max_air_regeneration_value = max_air_regeneration_value;
    }
    @Override
    public void OnEquip(LivingEquipmentChangeEvent event, ItemStack itemStack){
        LivingEntity livingEntity = event.getEntity();
        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null || speedAttribute.hasModifier(resourceId)) return;
        int count = Utils.GetUpgradeCount(livingEntity, (BacktankUpgradeItem) itemStack.getItem());
        int value2 = air_regeneration_value * count;
        if (max_air_regeneration_value != 0){
            value2 = Math.min(value2, max_air_regeneration_value);
        }
        Utils.AddAirRegenerationAttribute(livingEntity, resourceId, value2);
        double value = speedValue * (double)count;
        if (max_value != 0){
            value = Math.min(value, max_value);
        }
        AttributeModifier modifier = new AttributeModifier(
                resourceId,
                value,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        speedAttribute.addTransientModifier(modifier);
    }
    @Override
    public void OnUnequip(LivingEquipmentChangeEvent event, ItemStack itemStack){
        LivingEntity livingEntity = event.getEntity();
        Utils.RemoveAirRegenerationAttribute(livingEntity, resourceId);
        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(resourceId)) {
            speedAttribute.removeModifier(resourceId);
        }
    }
    @Override
    public String ModifyTooltipString(String string, int count, ItemStack itemStack){
        double value = speedValue * (double)count;
        if (max_value != 0){
            value = Math.min(value, max_value);
        }
        return string.replaceAll("#value#", String.valueOf(((int)(value * 100d)))).replaceAll("#max_value#", String.valueOf((int)(max_value * 100d)));
    }
    @Override
    public int ModifyAirRegeneration(int count, ItemStack itemStack){
        int value2 = air_regeneration_value * count;
        if (max_air_regeneration_value != 0){
            value2 = Math.min(value2, max_air_regeneration_value);
        }
        return value2;
    }
}
