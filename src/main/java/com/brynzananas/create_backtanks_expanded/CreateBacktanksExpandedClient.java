package com.brynzananas.create_backtanks_expanded;

import com.brynzananas.create_backtanks_expanded.ponder.CreateBacktanksExpandedPonderPlugin;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.Debug;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.*;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateBacktanksExpanded.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CreateBacktanksExpanded.MODID, value = Dist.CLIENT)
public class CreateBacktanksExpandedClient {
    public CreateBacktanksExpandedClient(IEventBus modEventBus, ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        AddItemTooltips();
    }
    private static void AddItemTooltips(){
//        Item item = CreateBacktanksExpanded.BACKTANK_UPGRADE_STATION_ITEM.get();
//        com.simibubi.create.foundation.item.TooltipModifier tooltipModifier = new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE).andThen(TooltipModifier.mapNull(KineticStats.create(item)));
//        TooltipModifier.REGISTRY.register(item, tooltipModifier);
        PonderIndex.addPlugin(new CreateBacktanksExpandedPonderPlugin());
    }
    static class TooltipFluidInfo{
        public TooltipFluidInfo(int totalAmount, int totalCapacity, String fluidName){
            this.totalCapacity = totalCapacity;
            this.totalAmount = totalAmount;
            this.fluidName = fluidName;
        }
        public int totalCapacity;
        public int totalAmount;
        public String fluidName;
    }
    @SubscribeEvent
    private static void onItemTooltip(ItemTooltipEvent event) { // TODO: This is so shit
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(event.getItemStack());
        if (itemStacks.isEmpty()) return;
        List<Component> tooltip = event.getToolTip();

        int targetIndex = Utils.FindInsertionIndex(tooltip);
        Map<Item, Integer> itemCounts = new HashMap<>();
        List<ItemStack> items = new ArrayList<>();
        List<Fluid> fluidTanks = new ArrayList<>();
        Map<Fluid, TooltipFluidInfo> fluidTanks2 = new HashMap<>();
        for (ItemStack itemStack : itemStacks){
            Item item = itemStack.getItem();
            IFluidHandlerItem capability = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability != null){
                FluidStack fluidStack = capability.getFluidInTank(1);
                if (fluidStack.isEmpty()) continue;
                Fluid fluid = fluidStack.getFluid();
                if (fluidTanks2.containsKey(fluid)){
                    TooltipFluidInfo tooltipFluidInfo = fluidTanks2.get(fluid);
                    tooltipFluidInfo.totalAmount += fluidStack.getAmount();
                    tooltipFluidInfo.totalCapacity += capability.getTankCapacity(1);
                    fluidTanks2.replace(fluid, tooltipFluidInfo);
                }else{
                    fluidTanks2.put(fluid, new TooltipFluidInfo(capability.getTankCapacity(1), fluidStack.getAmount(), fluidStack.isEmpty() ? I18n.get("item.create_backtanks_expanded.fluid_tank_upgrade.tooltip.empty") : fluidStack.getHoverName().getString()));
                    fluidTanks.add(fluid);
                }
            }else{
                if (itemCounts.containsKey(item)){
                    itemCounts.replace(item, itemCounts.get(item) + itemStack.getCount());
                }else{
                    itemCounts.put(item, itemStack.getCount());
                    items.add(itemStack);
                }
            }
        }
        int airRegeneration = 0;
        for (int i = 0; i < itemCounts.size(); i++){
            ItemStack itemStack = items.get(i);
            Item item = itemStack.getItem();
            if (!(item instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
            int itemCount = itemCounts.get(item);
            airRegeneration += backtankUpgradeItem.ModifyAirRegeneration(itemCount, itemStack);
            String descriptionId = item.getDescriptionId() + ".tooltip";
            if (descriptionId.equals("item.create_backtanks_expanded.pressurized_air_regeneration_upgrade.tooltip")) continue;
            Component component = Component.translatable(descriptionId);
            String literalText = component.getString();
            if (literalText.equals(descriptionId)) continue;
            String text = backtankUpgradeItem.ModifyTooltipString(literalText, itemCount, itemStack);
            if (targetIndex >= 0 && targetIndex <= tooltip.size()){
                tooltip.add(targetIndex, Component.literal(text).withStyle(ChatFormatting.BLUE));
            }else{
                tooltip.add(Component.literal(text).withStyle(ChatFormatting.BLUE));
            }
        }
        for (int i = 0; i < fluidTanks2.size(); i++){
            Fluid fluid = fluidTanks.get(i);
            TooltipFluidInfo tooltipFluidInfo = fluidTanks2.get(fluid);
            int amount = tooltipFluidInfo.totalAmount;
            String fluidName = tooltipFluidInfo.fluidName;
            int capacity = tooltipFluidInfo.totalCapacity;
            String theString = I18n.get("item.create_backtanks_expanded.fluid_tank_upgrade.tooltip").replaceAll("#fluid_name#", fluidName).replaceAll("#value#", String.valueOf(amount)).replaceAll("#max_value#", String.valueOf(capacity));
            if (targetIndex >= 0 && targetIndex <= tooltip.size()){
                tooltip.add(targetIndex, Component.literal(theString).withStyle(ChatFormatting.BLUE));
            }else{
                tooltip.add(Component.literal(theString).withStyle(ChatFormatting.BLUE));
            }
        }
        if (airRegeneration != 0){
            String descriptionId = "item.create_backtanks_expanded.pressurized_air_regeneration_upgrade.tooltip";
            Component component = Component.translatable(descriptionId);
            boolean positive = airRegeneration > 0;
            String literalText = component.getString().replaceAll("#value#", (positive ? "+" : "-") + Math.abs(airRegeneration));
            if (targetIndex >= 0 && targetIndex <= tooltip.size()){
                tooltip.add(targetIndex, Component.literal(literalText).withStyle((positive ? ChatFormatting.BLUE : ChatFormatting.RED)));
            }else{
                tooltip.add(Component.literal(literalText).withStyle((positive ? ChatFormatting.BLUE : ChatFormatting.RED)));
            }
        }
    }
}
