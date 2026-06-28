package com.brynzananas.create_backtanks_expanded.upgrades;

import com.brynzananas.create_backtanks_expanded.*;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.BuildersTeaItem;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class AutoDrinkUpgradeItem extends BacktankUpgradeItem {
    public AutoDrinkUpgradeItem(Properties properties) {
        super(properties);
    }
    @Override
    public void OnTick(EntityTickEvent entityTickEvent, ItemStack itemStack){
        if (!(entityTickEvent.getEntity() instanceof LivingEntity livingEntity)) return;
        ItemStack itemStack2 = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        NonNullList<ItemStack> originalUpgrades = Utils.GetUpgrades(itemStack2);
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        itemStacks.addAll(originalUpgrades);
//        FilterData filterData = itemStack.get(CreateBacktanksExpanded.BACKTANK_CONSUME_FILTER_2);
//        FilterItemStack filterItemStack = null;
//        if (filterData != null){
//            filterItemStack = FilterItemStack.of(filterData.filterStack());
//        }
        boolean updateArray = false;
        for (int i = 0; i < itemStacks.size(); i++){
            ItemStack itemStack1 = itemStacks.get(i);
            IFluidHandlerItem capability = itemStack1.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability == null) continue;
            FluidStack fluidStack = capability.getFluidInTank(1).copy();
            if (fluidStack.isEmpty()) continue;
            boolean updateFluidStack = false;
            // TODO: Unhardcode it or something
            if (fluidStack.getTags().anyMatch(b -> b.equals(AllTags.AllFluidTags.TEA.tag))){
                if (livingEntity.getEffect(MobEffects.DIG_SPEED) == null || livingEntity.getEffect(MobEffects.DIG_SPEED).getDuration() < 100){
                    int duration = 3600;
                    if (fluidStack.getAmount() < Config.AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT.get()){
                        duration = (int)((double)duration / (Math.min(fluidStack.getAmount(), Config.AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT.get()) / (double)Config.AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT.get()));
                    }
                    if (livingEntity.hasEffect(MobEffects.DIG_SPEED)){
                        Objects.requireNonNull(livingEntity.getEffect(MobEffects.DIG_SPEED)).duration += duration;
                    }else {
                        MobEffectInstance mobEffectInstance2 = new MobEffectInstance(MobEffects.DIG_SPEED, duration, 0);
                        livingEntity.addEffect(mobEffectInstance2);
                    }
                    updateFluidStack = true;
                }
            }
//            if (filterItemStack != null && !filterItemStack.test(livingEntity.level(), fluidStack)) continue;
            PotionContents contents = fluidStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (!contents.equals(PotionContents.EMPTY) && contents.potion().isPresent()) {
                List<MobEffectInstance> mobEffectInstances = contents.potion().get().value().getEffects();
                for (MobEffectInstance mobEffectInstance : mobEffectInstances) {
                    MobEffectInstance mobEffectInstance1 = livingEntity.getEffect(mobEffectInstance.getEffect());
                    if (mobEffectInstance1 != null && mobEffectInstance1.getDuration() > 100) continue;
                    int duration = mobEffectInstance.getDuration();
                    if (fluidStack.getAmount() < Config.AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT.get()) {
                        duration = (int) ((double) duration / (Math.min(fluidStack.getAmount(), Config.AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT.get()) / (double) Config.AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT.get()));
                    }
                    if (mobEffectInstance1 != null && mobEffectInstance1.getAmplifier() == mobEffectInstance.getAmplifier()) {
                        mobEffectInstance1.duration += duration;
                    } else {
                        MobEffectInstance mobEffectInstance2 = new MobEffectInstance(mobEffectInstance.getEffect(), duration, mobEffectInstance.getAmplifier());
                        livingEntity.addEffect(mobEffectInstance2);
                    }
                    updateFluidStack = true;
                }
            }
            if (updateFluidStack) {
                capability.drain(Config.AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT.get(), IFluidHandler.FluidAction.EXECUTE);
                ItemStack container = capability.getContainer().copy();
                itemStacks.set(i, container);
                updateArray = true;
            }
        }
        if (updateArray){
            itemStack2.set(CreateBacktanksExpanded.BACKTANK_UPGRADES_2, ItemContainerContents.fromItems(itemStacks));
        }
    }
}
