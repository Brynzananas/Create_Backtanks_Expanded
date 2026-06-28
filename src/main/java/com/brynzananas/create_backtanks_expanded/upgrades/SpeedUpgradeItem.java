package com.brynzananas.create_backtanks_expanded.upgrades;

import com.brynzananas.create_backtanks_expanded.BacktankUpgradeItem;
import com.brynzananas.create_backtanks_expanded.Config;
import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpeedUpgradeItem extends BacktankUpgradeItem {
    private static final ResourceLocation SPEED_BOOST_ID = ResourceLocation.fromNamespaceAndPath(CreateBacktanksExpanded.MODID, "backtank_move_speed");
    public SpeedUpgradeItem(Properties properties) {
        super(properties);
    }
    @Override
    public void OnEquip(LivingEquipmentChangeEvent event){
        LivingEntity livingEntity = event.getEntity();
        int count = Utils.GetUpgradeCount(livingEntity, CreateBacktanksExpanded.SPEED_UPGRADE.get());
        Utils.AddAirRegenerationAttribute(livingEntity, SPEED_BOOST_ID, Config.SPEED_UPGRADE_PRESSURIZED_AIR_REGENERATION.get() * count);
        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null || speedAttribute.hasModifier(SPEED_BOOST_ID)) return;
        AttributeModifier modifier = new AttributeModifier(
                SPEED_BOOST_ID,
                Config.SPEED_UPGRADE_SPEED_MULTIPLIER.get() * count,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        speedAttribute.addTransientModifier(modifier);
    }
    @Override
    public void OnUnequip(LivingEquipmentChangeEvent event){
        LivingEntity livingEntity = event.getEntity();
        Utils.RemoveAirRegenerationAttribute(livingEntity, SPEED_BOOST_ID);
        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SPEED_BOOST_ID)) {
            speedAttribute.removeModifier(SPEED_BOOST_ID);
        }
    }
    @Override
    public String ModifyTooltipString(String string, int count, ItemStack itemStack){
        return string.replaceAll("#value#", String.valueOf(((int)(Config.SPEED_UPGRADE_SPEED_MULTIPLIER.get() * count * 100f))));
    }
    @Override
    public int ModifyAirRegeneration(int count, ItemStack itemStack){
        return Config.SPEED_UPGRADE_PRESSURIZED_AIR_REGENERATION.get() * count;
    }
}
