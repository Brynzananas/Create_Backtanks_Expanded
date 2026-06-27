package com.brynzananas.create_backtanks_expanded;

import com.brynzananas.create_backtanks_expanded.upgrades.FluidTankUpgradeItem;
import com.brynzananas.create_backtanks_expanded.upgrades.HoverUpgradeItem;
import com.brynzananas.create_backtanks_expanded.upgrades.PressurizedAirRegenerationUpgradeItem;
import com.brynzananas.create_backtanks_expanded.upgrades.SpeedUpgradeItem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(CreateBacktanksExpanded.MODID)
public class CreateBacktanksExpanded {
    public static final String MODID = "create_backtanks_expanded";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<BacktankUpgradeItem> GENERIC_UPGRADE = ITEMS.registerItem("generic_upgrade", BacktankUpgradeItem::new);
    public static final DeferredItem<SpeedUpgradeItem> SPEED_UPGRADE = ITEMS.registerItem("speed_upgrade", SpeedUpgradeItem::new);
    public static final DeferredItem<HoverUpgradeItem> HOVER_UPGRADE = ITEMS.registerItem("hover_upgrade", HoverUpgradeItem::new);
    public static final DeferredItem<PressurizedAirRegenerationUpgradeItem> AIR_REGENERATION_UPGRADE = ITEMS.registerItem("pressurized_air_regeneration_upgrade", PressurizedAirRegenerationUpgradeItem::new);
    public static final DeferredItem<BacktankUpgradeItem> ELYTRA_UPGRADE = ITEMS.registerItem("elytra_upgrade", BacktankUpgradeItem::new);
    public static final int FLUID_TANK_UPGRADE_CAPACITY = 1000;
    public static final DeferredItem<FluidTankUpgradeItem> FLUID_TANK_UPGRADE = ITEMS.registerItem("fluid_tank_upgrade", new Function<Item.Properties, FluidTankUpgradeItem>() {
        @Override
        public FluidTankUpgradeItem apply(Item.Properties properties) {
            return new FluidTankUpgradeItem(properties, FLUID_TANK_UPGRADE_CAPACITY);
        }
    });
    public static final DeferredBlock<BacktankUpgradeStationBlock> BACKTANK_UPGRADE_STATION = BLOCKS.register("backtank_upgrade_station", () -> new BacktankUpgradeStationBlock(AllBlocks.DEPOT.get().properties()));
    public static final DeferredItem<BlockItem> BACKTANK_UPGRADE_STATION_ITEM = ITEMS.registerSimpleBlockItem(BACKTANK_UPGRADE_STATION);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final Supplier<CreativeModeTab> CREATE_BACKTANKS_EXPANDED_TAB = CREATIVE_MODE_TAB.register("create_backtanks_expanded_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(GENERIC_UPGRADE.get())).title(Component.translatable("create_backtanks_expanded.creativetab")
                    ).displayItems((itemDisplayParameters, output) -> {
                output.accept(BACKTANK_UPGRADE_STATION);
                output.accept(HOVER_UPGRADE);
                output.accept(ELYTRA_UPGRADE);
                output.accept(SPEED_UPGRADE);
            }).build());
    public static final DeferredHolder<Attribute, Attribute> BACKTANK_PRESSURIZED_AIR_REGENERATION = ATTRIBUTES.register(
            "backtank_pressurized_air_regeneration",
            () -> new RangedAttribute(
                    "attribute.create_backtanks_expanded.backtank_pressurized_air_regeneration",
                    0,
                    -Double.MAX_VALUE,
                    Double.MAX_VALUE
            ).setSyncable(true)
    );
    public static final DeferredHolder<Attribute, Attribute> HOVER_REACH = ATTRIBUTES.register(
            "hover_reach",
            () -> new RangedAttribute(
                    "attribute.create_backtanks_expanded.hover_reach",
                    0,
                    0,
                    Double.MAX_VALUE
            ).setSyncable(true)
    );
    public static final Supplier<AttachmentType<Boolean>> CAN_HOVER = ATTACHMENT_TYPES.register(
            "can_hover",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<Boolean>> HOVER_NEARBY_BLOCKS = ATTACHMENT_TYPES.register(
            "hover_nearby_blocks",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build()
    );
    private static final Codec<NonNullList<ItemStack>> ITEM_LIST_CODEC =
            ItemStack.OPTIONAL_CODEC.listOf().xmap(
                    list -> NonNullList.of(ItemStack.EMPTY, list.toArray(new ItemStack[0])),
                    ArrayList::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<ItemStack>> ITEM_LIST_STREAM_CODEC =
            ItemStack.OPTIONAL_STREAM_CODEC
                    .apply(ByteBufCodecs.list())
                    .map(
                            list -> NonNullList.of(ItemStack.EMPTY, list.toArray(new ItemStack[0])),
                            ArrayList::new
                    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> BACKTANK_UPGRADES_2 =
            DATA_COMPONENT_TYPES.register("backtank_upgrades", () -> DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> BACKTANK_FLUID_TANK_2 =
            DATA_COMPONENT_TYPES.register("fluid_tank_2", () -> DataComponentType.<SimpleFluidContent>builder()
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
                    .build());
    public static final int MAX_UPGRADE_SLOTS = 1024;
    public static final Supplier<AttachmentType<NonNullList<ItemStack>>> BACKTANK_UPGRADES =
            ATTACHMENT_TYPES.register("upgrades", () -> AttachmentType.builder(
                    () -> NonNullList.withSize(MAX_UPGRADE_SLOTS, ItemStack.EMPTY)
            ).serialize(ITEM_LIST_CODEC).build());
    public static final int MAX_FLUID_CAPACITY = 1000;
    public static final Supplier<AttachmentType<SerializableFluidTank>> BACKTANK_FLUID_TANK =
            ATTACHMENT_TYPES.register("fluid_tank", () -> AttachmentType.serializable(() ->
                    new SerializableFluidTank(MAX_FLUID_CAPACITY)
            ).build());
    public static boolean isSableInstalled;

    public CreateBacktanksExpanded(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
        ATTRIBUTES.register(modEventBus);
        CREATIVE_MODE_TAB.register(modEventBus);
        isSableInstalled = ModList.get().isLoaded("sable");

        NeoForge.EVENT_BUS.addListener(this::onBlockPlaced);
        NeoForge.EVENT_BUS.addListener(this::onEquipmentChange);
        NeoForge.EVENT_BUS.addListener(this::onItemTooltip);
        modEventBus.addListener(this::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (itemStack, context) -> {
                    // Cast the item safely to extract its custom capacity property
                    int capacity = ((FluidTankUpgradeItem) itemStack.getItem()).capacity;
                    return new FluidHandlerItemStack(new Supplier<DataComponentType<SimpleFluidContent>>() {
                        @Override
                        public DataComponentType<SimpleFluidContent> get() {
                            return new DataComponentType<SimpleFluidContent>() {
                                @Override
                                public @Nullable Codec<SimpleFluidContent> codec() {
                                    return SimpleFluidContent.CODEC;
                                }

                                @Override
                                public StreamCodec<? super RegistryFriendlyByteBuf, SimpleFluidContent> streamCodec() {
                                    return SimpleFluidContent.STREAM_CODEC;
                                }
                            };
                        }
                    }, itemStack, capacity);
                },
                FLUID_TANK_UPGRADE.get()
        );
        // Register the capability for your custom Block Entity Type
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AllBlockEntityTypes.BACKTANK.get(),
                (blockEntity, direction) -> {
                    SerializableFluidTank tank = blockEntity.getData(BACKTANK_FLUID_TANK);

                    // Intercept the onContentsChanged to ensure changes save to the chunk
                    return new SerializableFluidTank(tank.getCapacity()) {
                        @Override
                        public int fill(net.neoforged.neoforge.fluids.FluidStack resource, FluidAction action) {
                            int filled = tank.fill(resource, action);
                            if (filled > 0 && action.execute()) {
                                blockEntity.setChanged(); // Marks chunk dirty for serialization
                            }
                            return filled;
                        }

                        @Override
                        public net.neoforged.neoforge.fluids.FluidStack drain(int maxDrain, FluidAction action) {
                            net.neoforged.neoforge.fluids.FluidStack drained = tank.drain(maxDrain, action);
                            if (!drained.isEmpty() && action.execute()) {
                                blockEntity.setChanged();
                            }
                            return drained;
                        }

                        // Proxy all other necessary FluidTank methods directly to the attachment instance
                        @Override
                        public net.neoforged.neoforge.fluids.FluidStack getFluid() { return tank.getFluid(); }
                        @Override
                        public int getFluidAmount() { return tank.getFluidAmount(); }
                        @Override
                        public int getSpace() { return tank.getSpace(); }
                    };
                }
        );
    }
    private void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem().equals(BACKTANK_UPGRADE_STATION_ITEM.get())){
            List<Component> tooltip = event.getToolTip();
            int targetIndex = Utils.FindInsertionIndex(tooltip, event.getFlags().isAdvanced());
            tooltip.add(targetIndex, Component.translatable("item.create_backtanks_expanded.backtank_upgrade_station.tooltip.4").withStyle(ChatFormatting.GOLD));
            tooltip.add(targetIndex, Component.translatable("item.create_backtanks_expanded.backtank_upgrade_station.tooltip.3").withStyle(ChatFormatting.GRAY));
            tooltip.add(targetIndex, Component.translatable("item.create_backtanks_expanded.backtank_upgrade_station.tooltip.2").withStyle(ChatFormatting.GOLD));
            tooltip.add(targetIndex, Component.translatable("item.create_backtanks_expanded.backtank_upgrade_station.tooltip.1").withStyle(ChatFormatting.GRAY));
        }
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(event.getItemStack());
        if (itemStacks.isEmpty()) return;
        List<Component> tooltip = event.getToolTip();

        int targetIndex = Utils.FindInsertionIndex(tooltip);
        Map<Item, Integer> itemCounts = new HashMap<>();
        List<Item> items = new ArrayList<>();
        for (ItemStack itemStack : itemStacks){
            Item item = itemStack.getItem();
            if (itemCounts.containsKey(item)){
                itemCounts.replace(item, itemCounts.get(item) + itemStack.getCount());
            }else{
                itemCounts.put(item, itemStack.getCount());
                items.add(item);
            }
        }

        int airRegeneration = 0;
        for (int i = 0; i < itemCounts.size(); i++){
            Item item = items.get(i);
            if (!(item instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
            int itemCount = itemCounts.get(item);
            airRegeneration += backtankUpgradeItem.ModifyAirRegeneration(itemCount);
            String descriptionId = item.getDescriptionId() + ".tooltip";
            if (descriptionId.equals("item.create_backtanks_expanded.pressurized_air_regeneration_upgrade.tooltip")) continue;
            Component component = Component.translatable(descriptionId);
            String literalText = component.getString();
            if (literalText.equals(descriptionId)) continue;
            String text = backtankUpgradeItem.ModifyTooltipString(literalText, itemCount);
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
    private void onBlockPlaced(EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        BlockEntity be = event.getLevel().getBlockEntity(event.getPos());
        if (!(be instanceof BacktankBlockEntity)) return;
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            ItemStack itemInHand = player.getItemInHand(player.getUsedItemHand());
            SimpleFluidContent simpleFluidContent = itemInHand.get(BACKTANK_FLUID_TANK_2);
            if (simpleFluidContent != null){
                SerializableFluidTank tank = be.getData(CreateBacktanksExpanded.BACKTANK_FLUID_TANK);
                tank.setFluid(simpleFluidContent.copy());
            }
            NonNullList<ItemStack> itemData = NonNullList.withSize(MAX_UPGRADE_SLOTS, ItemStack.EMPTY);
            NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(itemInHand);
            for (int i = 0; i < itemData.size() && i < itemStacks.size(); i++){
                itemData.set(i, itemStacks.get(i));
            }
            be.setData(BACKTANK_UPGRADES, itemData);
            be.setChanged();
        }
    }

    private void onEquipmentChange(LivingEquipmentChangeEvent event){
        if (event.getSlot() != EquipmentSlot.CHEST) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(event.getFrom());
        if (itemStacks != null){
            for (ItemStack itemStack : itemStacks){
                if (itemStack.isEmpty() || !(itemStack.getItem() instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
                backtankUpgradeItem.OnUnequip(event);
            }
        }
        NonNullList<ItemStack> itemStacks2 = Utils.GetUpgrades(event.getTo());
        if (itemStacks != null){
            for (ItemStack itemStack : itemStacks2){
                if (itemStack.isEmpty() || !(itemStack.getItem() instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
                backtankUpgradeItem.OnEquip(event);
            }
        }

    }
}
