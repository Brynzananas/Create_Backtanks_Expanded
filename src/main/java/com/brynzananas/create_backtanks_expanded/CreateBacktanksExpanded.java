package com.brynzananas.create_backtanks_expanded;

import com.brynzananas.create_backtanks_expanded.upgrades.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.codec.CreateCodecs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.Debug;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(CreateBacktanksExpanded.MODID)
public class CreateBacktanksExpanded {
    public static final String MODID = "create_backtanks_expanded";
    public static final Logger LOGGER = LogUtils.getLogger();
//    private static final CreateRegistrate REGISTRATE = ((CreateRegistrate)CreateRegistrate.create(MODID).setTooltipModifierFactory((item) -> (new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)).andThen(TooltipModifier.mapNull(KineticStats.create(item)))));
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<BacktankUpgradeItem> GENERIC_UPGRADE = ITEMS.registerItem("generic_upgrade", BacktankUpgradeItem::new);
    private static final ResourceLocation SPEED_RESOURCE_ID = ResourceLocation.fromNamespaceAndPath(CreateBacktanksExpanded.MODID, "backtank_move_speed");
    public static final DeferredItem<SpeedUpgradeItem> SPEED_UPGRADE = ITEMS.registerItem("speed_upgrade", new Function<Item.Properties, SpeedUpgradeItem>() {
        @Override
        public SpeedUpgradeItem apply(Item.Properties properties) {
            return new SpeedUpgradeItem(properties, SPEED_RESOURCE_ID, 0, 0, 0, 0);
        }
    });
    public static final ResourceLocation HOVER_RESOURCE_ID = ResourceLocation.fromNamespaceAndPath(CreateBacktanksExpanded.MODID, "backtank_hover");
    public static final DeferredItem<HoverUpgradeItem> HOVER_UPGRADE = ITEMS.registerItem("hover_upgrade", new Function<Item.Properties, HoverUpgradeItem>() {
        @Override
        public HoverUpgradeItem apply(Item.Properties properties) {
            return new HoverUpgradeItem(properties, HOVER_RESOURCE_ID,0, 0, 0, 0);
        }
    });
    private static final ResourceLocation PRESSURIZED_AIR_REGEN_RESOURCE_ID = ResourceLocation.fromNamespaceAndPath(CreateBacktanksExpanded.MODID, "backtank_pressurized_air_regen");
    public static final DeferredItem<PressurizedAirRegenerationUpgradeItem> AIR_REGENERATION_UPGRADE = ITEMS.registerItem("pressurized_air_regeneration_upgrade", new Function<Item.Properties, PressurizedAirRegenerationUpgradeItem>() {
        @Override
        public PressurizedAirRegenerationUpgradeItem apply(Item.Properties properties) {
            return new PressurizedAirRegenerationUpgradeItem(properties, PRESSURIZED_AIR_REGEN_RESOURCE_ID, 0, 0);
        }
    });
    public static final DeferredItem<BacktankUpgradeItem> ELYTRA_UPGRADE = ITEMS.registerItem("elytra_upgrade", BacktankUpgradeItem::new);
    public static final int FLUID_TANK_UPGRADE_CAPACITY = 1000;
    public static final DeferredItem<FluidTankUpgradeItem> FLUID_TANK_UPGRADE = ITEMS.registerItem("fluid_tank_upgrade", new Function<Item.Properties, FluidTankUpgradeItem>() {
        @Override
        public FluidTankUpgradeItem apply(Item.Properties properties) {
            return new FluidTankUpgradeItem(properties, FLUID_TANK_UPGRADE_CAPACITY);
        }
    });
    public static final DeferredItem<AutoDrinkUpgradeItem> AUTO_DRINK_UPGRADE = ITEMS.registerItem("auto_drink_upgrade", AutoDrinkUpgradeItem::new);
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
                output.accept(FLUID_TANK_UPGRADE);
                output.accept(AUTO_DRINK_UPGRADE);
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
    public static final Supplier<AttachmentType<Boolean>> CAN_AUTO_DRINK = ATTACHMENT_TYPES.register(
            "can_auto_drink",
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
    public static final Supplier<DataComponentType<SimpleFluidContent>> STORED_FLUID
            = DATA_COMPONENTS.registerComponentType("stored_fluid", builder -> builder
            .persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
    );
    public static final Supplier<AttachmentType<SerializableFilteringBehaviour>> BACKTANK_CONSUME_FILTER =
            ATTACHMENT_TYPES.register("consume_filtering", () -> AttachmentType.serializable(() ->
                    (SerializableFilteringBehaviour)new SerializableFilteringBehaviour(null, new BacktankUpgradeStationBlock.BacktankValueBox()).forFluids()
            ).build());
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FilterData>> BACKTANK_CONSUME_FILTER_2 =
//            DATA_COMPONENT_TYPES.register("consume_filtering_2", () -> DataComponentType.<FilterData>builder()
//                    .persistent(FilterData.CODEC)
//                    .networkSynchronized(FilterData.STREAM_CODEC)
//                    .build());
//    public static final BlockCapability<FilteringBehaviour, Void> BACKTANK_CONSUME_FILTER =
//            BlockCapability.createVoid(ResourceLocation.fromNamespaceAndPath(MODID, "consume_filtering"), FilteringBehaviour.class);

    public static boolean isSableInstalled;

    public CreateBacktanksExpanded(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
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
        NeoForge.EVENT_BUS.addListener(this::onEntityTick);
        NeoForge.EVENT_BUS.addListener(this::onServerLoad);
//        NeoForge.EVENT_BUS.addListener(this::onCanEffectBeAdded);

        modEventBus.addListener(this::registerCapabilities);


    }

    private void onServerLoad(ServerAboutToStartEvent event){
        SPEED_UPGRADE.get().speedValue = Config.SPEED_UPGRADE_SPEED_MULTIPLIER.get();
        SPEED_UPGRADE.get().max_value = Config.SPEED_UPGRADE_MAX_SPEED_MULTIPLIER.get();
        SPEED_UPGRADE.get().air_regeneration_value = Config.SPEED_UPGRADE_PRESSURIZED_AIR_REGENERATION.get();
        SPEED_UPGRADE.get().max_air_regeneration_value = Config.SPEED_UPGRADE_MAX_PRESSURIZED_AIR_REGENERATION.get();
        HOVER_UPGRADE.get().hoverValue = Config.HOVER_UPGRADE_HOVER_REACH_RADIUS.get();
        HOVER_UPGRADE.get().max_value = Config.HOVER_UPGRADE_MAX_HOVER_REACH_RADIUS.get();
        HOVER_UPGRADE.get().air_regeneration_value = Config.HOVER_UPGRADE_PRESSURIZED_AIR_REGENERATION.get();
        HOVER_UPGRADE.get().max_air_regeneration_value = Config.HOVER_UPGRADE_MAX_PRESSURIZED_AIR_REGENERATION.get();
        AIR_REGENERATION_UPGRADE.get().air_regeneration_value = Config.PRESSURIZED_AIR_REGENERATION_UPGRADE_PRESSURIZED_AIR_REGENERATION.get();
        AIR_REGENERATION_UPGRADE.get().max_air_regeneration_value = Config.PRESSURIZED_AIR_REGENERATION_UPGRADE_MAX_PRESSURIZED_AIR_REGENERATION.get();
        FLUID_TANK_UPGRADE.get().capacity = Config.FLUID_TANK_UPGRADE_MAX_CAPACITY.get();
    }

    private void onCanEffectBeAdded(MobEffectEvent.Applicable event){
        MobEffectInstance mobEffectInstance = event.getEffectInstance();
        if (mobEffectInstance.getEffect().value().getCategory() != MobEffectCategory.HARMFUL) return;
        LivingEntity livingEntity = event.getEntity();
        ItemStack itemStack = Utils.GetBacktank(livingEntity);
        if (itemStack.isEmpty()) return;
        NonNullList<ItemStack> originalUpgrades = Utils.GetUpgrades(itemStack);
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        itemStacks.addAll(originalUpgrades);
        boolean hasAutoDrink = false;
        boolean canCancel = false;
        for (int i = 0; i < itemStacks.size(); i++){
            ItemStack itemStack1 = itemStacks.get(i);
            if (itemStack1.getItem().equals(AUTO_DRINK_UPGRADE.get())){
                hasAutoDrink = true;
            }
            IFluidHandlerItem capability = itemStack1.getCapability(Capabilities.FluidHandler.ITEM);
            if (capability == null) continue;
            FluidStack fluidStack = capability.getFluidInTank(1).copy();
            if (fluidStack.isEmpty() || fluidStack.getTags().noneMatch(b -> b.equals(Tags.Fluids.MILK))) continue;
            int drainAmount = (int)((double)(mobEffectInstance.getDuration() * mobEffectInstance.getDuration()) * 0.1d);
            capability.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
            canCancel = true;
            ItemStack container = capability.getContainer().copy();
            itemStacks.set(i, container);
            break;
        }
        if (canCancel & hasAutoDrink){
            itemStack.set(CreateBacktanksExpanded.BACKTANK_UPGRADES_2, ItemContainerContents.fromItems(itemStacks));
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }
    private void onEntityTick(EntityTickEvent.Post entityTickEvent){
        if (entityTickEvent.getEntity().level().isClientSide) return;
        if (!(entityTickEvent.getEntity() instanceof LivingEntity livingEntity)) return;
        ItemStack itemStack = Utils.GetBacktank(livingEntity);
        if (itemStack.isEmpty()) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(itemStack);
        for (ItemStack itemStack1 : itemStacks){
            if (!(itemStack1.getItem() instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
            backtankUpgradeItem.OnTick(entityTickEvent, itemStack1);
        }
    }
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (itemStack, context) -> {
                    int capacity = ((FluidTankUpgradeItem) itemStack.getItem()).capacity;
                    return new FluidHandlerItemStack(STORED_FLUID, itemStack, capacity);
                },
                FLUID_TANK_UPGRADE.get()
        );
//        event.registerBlockEntity(
//                BACKTANK_CONSUME_FILTER,
//                AllBlockEntityTypes.BACKTANK.get(),
//                (blockEntity, direction) -> {
//                    return new FilteringBehaviour(blockEntity, new BacktankUpgradeStationBlock.BacktankValueBox()).forFluids();
//                }
//        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AllBlockEntityTypes.BACKTANK.get(),
                (blockEntity, direction) -> {
                    SerializableFluidTank tank = blockEntity.getData(BACKTANK_FLUID_TANK);

                    return new SerializableFluidTank(tank.getCapacity()) {
                        @Override
                        public int fill(net.neoforged.neoforge.fluids.FluidStack resource, FluidAction action) {
                            int filled = tank.fill(resource, action);
                            if (filled > 0 && action.execute()) {
                                blockEntity.setChanged();
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
    private void onBlockPlaced(EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        BlockEntity be = event.getLevel().getBlockEntity(event.getPos());
        if (!(be instanceof BacktankBlockEntity backtankBlockEntity)) return;
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
//            FilterData filterData = itemInHand.get(BACKTANK_CONSUME_FILTER_2);
//            if (filterData != null){
//                SerializableFilteringBehaviour serializableFilteringBehaviour = be.getData(BACKTANK_CONSUME_FILTER);
//                if (serializableFilteringBehaviour == null){
//                    serializableFilteringBehaviour = new SerializableFilteringBehaviour(backtankBlockEntity, new BacktankUpgradeStationBlock.BacktankValueBox());
//                }
//                filterData.applyTo(serializableFilteringBehaviour);
//                be.setData(BACKTANK_CONSUME_FILTER, serializableFilteringBehaviour);
//            }
        }
    }

    private void onEquipmentChange(LivingEquipmentChangeEvent event){
        if (event.getEntity().level().isClientSide) return;
        if (event.getSlot() != EquipmentSlot.CHEST) return;
        NonNullList<ItemStack> itemStacks = Utils.GetUpgrades(event.getFrom());
        if (itemStacks != null){
            for (ItemStack itemStack : itemStacks){
                if (itemStack.isEmpty() || !(itemStack.getItem() instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
                backtankUpgradeItem.OnUnequip(event, itemStack);
            }
        }
        NonNullList<ItemStack> itemStacks2 = Utils.GetUpgrades(event.getTo());
        if (itemStacks != null){
            for (ItemStack itemStack : itemStacks2){
                if (itemStack.isEmpty() || !(itemStack.getItem() instanceof BacktankUpgradeItem backtankUpgradeItem)) continue;
                backtankUpgradeItem.OnEquip(event, itemStack);
            }
        }

    }
}
