package com.brynzananas.create_backtanks_expanded.ponder;


import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.IndexExclusionHelper;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CreateBacktanksExpandedPonderPlugin extends CreatePonderPlugin {
    public CreateBacktanksExpandedPonderPlugin() {
    }

    public String getModId() {
        return CreateBacktanksExpanded.MODID;
    }

    @Override
    public void registerScenes(final PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CreateBacktanksExpandedPonderScenes.register(helper);
    }

    @Override
    public void registerTags(final PonderTagRegistrationHelper<ResourceLocation> helper) {
    }

    @Override
    public void registerSharedText(final SharedTextRegistrationHelper helper) {

    }

    @Override
    public void onPonderLevelRestore(final PonderLevel ponderLevel) {

    }

    @Override
    public void indexExclusions(final IndexExclusionHelper helper) {

    }
}
