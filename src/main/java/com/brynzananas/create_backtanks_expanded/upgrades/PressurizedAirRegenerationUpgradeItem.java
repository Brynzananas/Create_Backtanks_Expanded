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
    public PressurizedAirRegenerationUpgradeItem(Properties properties) {
        super(properties);
    }
    private static final ResourceLocation PRESSURIZED_AIR_REGEN = ResourceLocation.fromNamespaceAndPath(CreateBacktanksExpanded.MODID, "backtank_pressurized_air_regen");
    @Override
    public void OnEquip(LivingEquipmentChangeEvent event){
        LivingEntity livingEntity = event.getEntity();
        int count = Utils.GetUpgradeCount(livingEntity, CreateBacktanksExpanded.AIR_REGENERATION_UPGRADE.get());
        Utils.AddAirRegenerationAttribute(livingEntity, PRESSURIZED_AIR_REGEN, Config.PRESSURIZED_AIR_REGENERATION_UPGRADE_PRESSURIZED_AIR_REGENERATION.get() * count);
    }
    @Override
    public void OnUnequip(LivingEquipmentChangeEvent event){
        LivingEntity livingEntity = event.getEntity();
        Utils.RemoveAirRegenerationAttribute(livingEntity, PRESSURIZED_AIR_REGEN);
    }
    @Override
    public String ModifyTooltipString(String string, int count){
        return string.replaceAll("#value#", "+" + (Config.PRESSURIZED_AIR_REGENERATION_UPGRADE_PRESSURIZED_AIR_REGENERATION.get() * count));
    }
    public int ModifyAirRegeneration(int count){
        return Config.PRESSURIZED_AIR_REGENERATION_UPGRADE_PRESSURIZED_AIR_REGENERATION.get() * count;
    }
}
