package com.brynzananas.create_backtanks_expanded.upgrades;

import com.brynzananas.create_backtanks_expanded.BacktankUpgradeItem;
import com.brynzananas.create_backtanks_expanded.Config;
import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PressurizedAirRegenerationUpgradeItem extends BacktankUpgradeItem {
    public int air_regeneration_value;
    public int max_air_regeneration_value;
    public ResourceLocation resourceId;
    public PressurizedAirRegenerationUpgradeItem(Properties properties, ResourceLocation resourceId, int air_regeneration_value, int max_air_regeneration_value) {
        super(properties);
        this.resourceId = resourceId;
        this.air_regeneration_value = air_regeneration_value;
        this.max_air_regeneration_value = max_air_regeneration_value;
    }
    @Override
    public void OnEquip(LivingEquipmentChangeEvent event, ItemStack itemStack){
        LivingEntity livingEntity = event.getEntity();
        int count = Utils.GetUpgradeCount(livingEntity, (BacktankUpgradeItem) itemStack.getItem());
        int value2 = air_regeneration_value * count;
        if (max_air_regeneration_value != 0){
            value2 = Math.min(value2, max_air_regeneration_value);
        }
        Utils.AddAirRegenerationAttribute(livingEntity, resourceId, value2);
    }
    @Override
    public void OnUnequip(LivingEquipmentChangeEvent event, ItemStack itemStack){
        LivingEntity livingEntity = event.getEntity();
        Utils.RemoveAirRegenerationAttribute(livingEntity, resourceId);
    }
    @Override
    public String ModifyTooltipString(String string, int count, ItemStack itemStack){
        int value2 = air_regeneration_value * count;
        if (max_air_regeneration_value != 0){
            value2 = Math.min(value2, max_air_regeneration_value);
        }
        return string.replaceAll("#value#", "+" + (value2)).replaceAll("#max_value", String.valueOf(max_air_regeneration_value));
    }
    public int ModifyAirRegeneration(int count, ItemStack itemStack){
        return max_air_regeneration_value * count;
    }
}
