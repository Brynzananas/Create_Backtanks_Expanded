package com.brynzananas.create_backtanks_expanded.ponder;

import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ButtonBlock;

public class CreateBacktanksExpandedPonderScenes {
    public static void register(final PonderSceneRegistrationHelper<ResourceLocation> registry) {

        registry.forComponents(CreateBacktanksExpanded.BACKTANK_UPGRADE_STATION_ITEM.getId())
                .addStoryBoard("backtank_upgrade_station/intro", CreateBacktanksExpandedPonderScenes::BacktankUpgradeStationIntro)
                .addStoryBoard("backtank_fluid_capacity", CreateBacktanksExpandedPonderScenes::BacktankFluidCapacity);
        registry.forComponents(AllItems.COPPER_BACKTANK.getId())
                .addStoryBoard("backtank_upgrade_station/intro", CreateBacktanksExpandedPonderScenes::BacktankUpgradeStationIntro)
                .addStoryBoard("backtank_fluid_capacity", CreateBacktanksExpandedPonderScenes::BacktankFluidCapacity);
        registry.forComponents(AllItems.NETHERITE_BACKTANK.getId())
                .addStoryBoard("backtank_upgrade_station/intro", CreateBacktanksExpandedPonderScenes::BacktankUpgradeStationIntro)
                .addStoryBoard("backtank_fluid_capacity", CreateBacktanksExpandedPonderScenes::BacktankFluidCapacity);
    }
    public static void BacktankUpgradeStationIntro(SceneBuilder scene, SceneBuildingUtil util) {
        final CreateSceneBuilder createSceneBuilder = new CreateSceneBuilder(scene);
        final CreateSceneBuilder.WorldInstructions world = createSceneBuilder.world();
        final OverlayInstructions overlay = createSceneBuilder.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final CreateSceneBuilder.EffectInstructions effects = createSceneBuilder.effects();

        scene.title("backtank_upgrade_station_intro", "Upgrading Backtanks");
        final BlockPos backtank_upgrade_station = util.grid().at(2, 1, 2);
        final BlockPos depot1 = util.grid().at(2, 1, 1);
        final BlockPos depot2 = util.grid().at(2, 1, 0);
        final BlockPos depot3 = util.grid().at(3, 1, 0);
        final BlockPos depot4 = util.grid().at(4, 1, 0);
        final BlockPos depot5 = util.grid().at(4, 1, 1);
        final BlockPos depot6 = util.grid().at(4, 1, 2);
        final BlockPos target = util.grid().at(1, 1, 2);
        final BlockPos redstone1 = util.grid().at(0, 1, 2);
        final BlockPos redstone2 = util.grid().at(0, 1, 1);
        final BlockPos button = util.grid().at(0, 1, 0);
        final BlockPos backtank = util.grid().at(2, 2, 2);
        scene.showBasePlate();
        scene.idle(20);
        world.showSection(select.position(backtank_upgrade_station), Direction.DOWN);
        overlay.showText(100)
                .attachKeyFrame()
                .text("Backtank Upgrade Station allows to put upgrades on Backtanks from nearby connected depots")
                .placeNearTarget()
                .pointAt(vector.blockSurface(new BlockPos(2, 2, 2), Direction.WEST));
        scene.idle(100);
        world.showSection(select.position(target), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(depot1), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(depot2), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(redstone1), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(depot3), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(depot4), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(redstone2), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(depot5), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(depot6), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(button), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(backtank), Direction.DOWN);
        scene.idle(20);
        world.modifyBlock(button, blockState -> blockState.setValue(ButtonBlock.POWERED, true), false);
        effects.indicateRedstone(redstone1);
        effects.indicateRedstone(redstone2);
        world.modifyBlockEntity(depot1, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        world.modifyBlockEntity(depot2, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        world.modifyBlockEntity(depot3, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        world.modifyBlockEntity(depot4, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        world.modifyBlockEntity(depot5, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        world.modifyBlockEntity(depot6, DepotBlockEntity.class, DepotBlockEntity::clearContent);
        effects.indicateSuccess(backtank);
        scene.idle(20);
        overlay.showText(180)
                .attachKeyFrame()
                .text("When redstone powered, Backtank Upgrade Station picks all upgrades from nearby connected depots and puts them to the Backtank on top of the block")
                .placeNearTarget()
                .pointAt(vector.blockSurface(new BlockPos(2, 2, 2), Direction.WEST));
        scene.idle(20);
        world.modifyBlock(button, blockState -> blockState.setValue(ButtonBlock.POWERED, false), false);
        scene.idle(180);
        world.modifyBlock(button, blockState -> blockState.setValue(ButtonBlock.POWERED, true), false);
        effects.indicateRedstone(redstone1);
        effects.indicateRedstone(redstone2);
        world.modifyBlockEntity(depot1, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(CreateBacktanksExpanded.SPEED_UPGRADE.get(), 2)));
        world.modifyBlockEntity(depot2, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(CreateBacktanksExpanded.SPEED_UPGRADE.get(), 4)));
        world.modifyBlockEntity(depot3, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(CreateBacktanksExpanded.SPEED_UPGRADE.get(), 8)));
        world.modifyBlockEntity(depot4, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(CreateBacktanksExpanded.SPEED_UPGRADE.get(), 16)));
        world.modifyBlockEntity(depot5, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(CreateBacktanksExpanded.SPEED_UPGRADE.get(), 32)));
        world.modifyBlockEntity(depot6, DepotBlockEntity.class, be -> be.setHeldItem(new ItemStack(CreateBacktanksExpanded.SPEED_UPGRADE.get(), 64)));
        effects.indicateSuccess(backtank);
        scene.idle(20);
        overlay.showText(180)
                .attachKeyFrame()
                .text("When redstone powered with equipped upgrades, Backtank Upgrade Station strips all upgrades from the Backtank and returns them to nearby connected depots")
                .placeNearTarget()
                .pointAt(vector.blockSurface(new BlockPos(2, 2, 2), Direction.WEST));
        scene.idle(20);
        world.modifyBlock(button, blockState -> blockState.setValue(ButtonBlock.POWERED, false), false);
        scene.idle(180);
    }
    public static void BacktankFluidCapacity(SceneBuilder scene, SceneBuildingUtil util){
        final CreateSceneBuilder createSceneBuilder = new CreateSceneBuilder(scene);
        final CreateSceneBuilder.WorldInstructions world = createSceneBuilder.world();
        final OverlayInstructions overlay = createSceneBuilder.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final CreateSceneBuilder.EffectInstructions effects = createSceneBuilder.effects();

        scene.title("backtank_fluid_capacity", "Backtank Fluid Capacity");
        final BlockPos backtank_upgrade_station = util.grid().at(2, 1, 2);
        final BlockPos depot = util.grid().at(1, 1, 2);
        final BlockPos cog1 = util.grid().at(3, 1, 2);
        final BlockPos mechanicalPipe = util.grid().at(3, 2, 2);
        final BlockPos tank1 = util.grid().at(4, 1, 2);
        final BlockPos tank2 = util.grid().at(4, 2, 2);
        final BlockPos cog2 = util.grid().at(3, 2, 3);
        final BlockPos shaft = util.grid().at(4, 2, 3);
        final BlockPos cog3 = util.grid().at(5, 2, 3);
        final BlockPos cog4 = util.grid().at(5, 1, 2);
        final BlockPos cog5 = util.grid().at(5, 0, 1);
        final BlockPos backtank = util.grid().at(2, 2, 2);
        scene.showBasePlate();
        world.showSection(select.position(backtank_upgrade_station), Direction.DOWN);
        world.showSection(select.position(cog5), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(depot), Direction.DOWN);
        world.showSection(select.position(cog4), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(cog1), Direction.DOWN);
        world.showSection(select.position(cog3), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(mechanicalPipe), Direction.DOWN);
        world.showSection(select.position(shaft), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(tank1), Direction.DOWN);
        world.showSection(select.position(cog2), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(tank2), Direction.DOWN);
        scene.idle(1);
        world.showSection(select.position(backtank), Direction.DOWN);
        scene.idle(20);
        overlay.showText(80)
                .attachKeyFrame()
                .text("Backtanks can hold fluid content up to 1000mB")
                .placeNearTarget()
                .pointAt(vector.blockSurface(new BlockPos(2, 2, 2), Direction.WEST));
        scene.idle(100);
        overlay.showText(80)
                .attachKeyFrame()
                .text("Backtanks automaticallly distribute fluid content to equpped Fluid Tank Upgrades")
                .placeNearTarget()
                .pointAt(vector.blockSurface(new BlockPos(2, 2, 2), Direction.WEST));
        scene.idle(80);
    }
}
