package com.brynzananas.create_backtanks_expanded;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.Debug;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

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
        Item item = CreateBacktanksExpanded.BACKTANK_UPGRADE_STATION_ITEM.get();
        com.simibubi.create.foundation.item.TooltipModifier tooltipModifier = new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE).andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        TooltipModifier.REGISTRY.register(item, tooltipModifier);
    }
    @SubscribeEvent
    private static void onItemTooltip(ItemTooltipEvent event) { // TODO: This is so shit
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(event.getItemStack());
        if (itemStacks.isEmpty()) return;
        List<Component> tooltip = event.getToolTip();

        int targetIndex = Utils.FindInsertionIndex(tooltip);
        Map<Item, Integer> itemCounts = new HashMap<>();
        List<ItemStack> items = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();
        List<ItemStack> fluidTanks = new ArrayList<>();
        for (ItemStack itemStack : itemStacks){
            Item item = itemStack.getItem();
            if (item.equals(CreateBacktanksExpanded.FLUID_TANK_UPGRADE.get())){
                fluidTanks.add(itemStack);
            }else{
                if (itemCounts.containsKey(item)){
                    itemCounts.replace(item, itemCounts.get(item) + itemStack.getCount());
                }else{
                    itemCounts.put(item, itemStack.getCount());
                    items.add(itemStack);
                    items2.add(item);
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
        for (ItemStack itemStack : fluidTanks){
            Item item = itemStack.getItem();
            if (!(item instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
            airRegeneration += backtankUpgradeItem.ModifyAirRegeneration(1, itemStack);
            String descriptionId = item.getDescriptionId() + ".tooltip";
            Component component = Component.translatable(descriptionId);
            String literalText = component.getString();
            if (literalText.equals(descriptionId)) continue;
            String text = backtankUpgradeItem.ModifyTooltipString(literalText, 1, itemStack);
            if (targetIndex >= 0 && targetIndex <= tooltip.size()){
                tooltip.add(targetIndex, Component.literal(text).withStyle(ChatFormatting.BLUE));
            }else{
                tooltip.add(Component.literal(text).withStyle(ChatFormatting.BLUE));
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
