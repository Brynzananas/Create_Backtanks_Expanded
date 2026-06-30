package com.brynzananas.create_backtanks_expanded;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public class Utils {
    public static boolean HasUpgrade(LivingEntity livingEntity, BacktankUpgradeItem backtankUpgradeItem){
        return GetUpgradeCount(livingEntity, backtankUpgradeItem) > 0;
    }
    public static int GetUpgradeCount(LivingEntity livingEntity, BacktankUpgradeItem backtankUpgradeItem){
        ItemStack itemStack = GetBacktank(livingEntity);
        if (itemStack == ItemStack.EMPTY) return 0;
        NonNullList<ItemStack> itemStacks = GetUpgrades(itemStack);
        if (itemStacks == null) return 0;
        int count = 0;
        for (ItemStack itemStack1 : itemStacks){
            if (!itemStack1.isEmpty() && itemStack1.is(backtankUpgradeItem)){
                count += itemStack1.getCount();
            }
        }
        return count;
    }
    public static NonNullList<ItemStack> GetUpgrades(ItemStack itemStack){
        ItemContainerContents currentContents = itemStack.getOrDefault(
                CreateBacktanksExpanded.BACKTANK_UPGRADES_2.get(),
                ItemContainerContents.EMPTY);
        return NonNullList.copyOf(currentContents.stream().toList());
    }
    public static ItemStack GetFirstMatchingUpgrade(ItemStack itemStack, Item item){
        NonNullList<ItemStack> itemStacks = GetUpgrades(itemStack);
        ItemStack itemStack2 = ItemStack.EMPTY;
        for (ItemStack itemStack1 : itemStacks) {
            if (itemStack1.getItem().equals(item)) {
                itemStack2 = itemStack1;
                return itemStack2;
            }
        }
        return itemStack2;
    }
    public static NonNullList<ItemStack> GetUpgrades(BlockEntity blockEntity){
        return blockEntity.getData(CreateBacktanksExpanded.BACKTANK_UPGRADES);
    }
    public static ItemStack GetFirstMatchingUpgrade(BlockEntity blockEntity, Item item){
        NonNullList<ItemStack> itemStacks = GetUpgrades(blockEntity);
        ItemStack itemStack2 = ItemStack.EMPTY;
        for (ItemStack itemStack1 : itemStacks) {
            if (itemStack1.getItem().equals(item)) {
                itemStack2 = itemStack1;
                return itemStack2;
            }
        }
        return itemStack2;
    }
    public static int FindInsertionIndex(List<Component> tooltip) {
        boolean found = false;
        for (int i = 0; i < tooltip.size(); i++) {
            Component comp = tooltip.get(i);
            if (found){
                assert ChatFormatting.DARK_GRAY.getColor() != null;
                if (Objects.requireNonNull(comp.getStyle().getColor()).getValue() == ChatFormatting.DARK_GRAY.getColor()){
                return i;
                }
            }else if (comp.getContents() instanceof TranslatableContents translatable) {
                String key = translatable.getKey();
                if (key.startsWith("attribute.modifier") || key.startsWith("item.modifiers")) {
                    found = true;
                }
            }
        }

        return tooltip.isEmpty() ? 0 : tooltip.size() - 1;
    }
    public static int FindInsertionIndex(List<Component> tooltip, boolean isAdvanced) {
        if (isAdvanced && !tooltip.isEmpty()) {
            for (int i = tooltip.size() - 1; i >= 0; i--) {
                String text = tooltip.get(i).getString();
                if (text.contains(":")) {
                    return i;
                }
            }
        }
        return Math.max(tooltip.isEmpty() ? 0 : tooltip.size() - 1, 1);
    }
    public static List<BlockPos> GetNearbySolidBlocks(LivingEntity entity, int radiusX, int radiusZ, int radiusY) {
        List<BlockPos> solidBlocks = new ArrayList<>();
        Level level = entity.level();

        if (level == null) return solidBlocks;

        BlockPos centerPos = entity.blockPosition();

        BlockPos minPos = centerPos.offset(-radiusX, -radiusY, -radiusZ);
        BlockPos maxPos = centerPos.offset(radiusX, radiusY, radiusZ);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            BlockState state = level.getBlockState(pos);

            if (state.isSolid()) {
                solidBlocks.add(pos.immutable());
            }
        }
        if (CreateBacktanksExpanded.isSableInstalled) solidBlocks.addAll(SableCompatibility.GetNearbySolidBlocks(entity, radiusX, radiusZ, radiusY));
        return solidBlocks;
    }
    public static void AddAirRegenerationAttribute(LivingEntity livingEntity, ResourceLocation resourceLocation, double value){
        if (value == 0) return;
        AttributeInstance airAttribute = livingEntity.getAttribute(CreateBacktanksExpanded.BACKTANK_PRESSURIZED_AIR_REGENERATION);
        if (airAttribute == null || airAttribute.hasModifier(resourceLocation)) return;
        AttributeModifier modifier = new AttributeModifier(
                resourceLocation,
                value,
                AttributeModifier.Operation.ADD_VALUE
        );
        airAttribute.addTransientModifier(modifier);
    }
    public static void RemoveAirRegenerationAttribute(LivingEntity livingEntity, ResourceLocation resourceLocation){
        AttributeInstance speedAttribute = livingEntity.getAttribute(CreateBacktanksExpanded.BACKTANK_PRESSURIZED_AIR_REGENERATION);
        if (speedAttribute != null && speedAttribute.hasModifier(resourceLocation)) {
            speedAttribute.removeModifier(resourceLocation);
        }
    }
    public static ItemStack GetBacktank(LivingEntity livingEntity){
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.getItem() instanceof BacktankItem) return itemStack;
        return ItemStack.EMPTY;
    }
}
