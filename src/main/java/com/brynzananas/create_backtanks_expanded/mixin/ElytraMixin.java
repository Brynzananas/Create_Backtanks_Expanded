package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.NonNullList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IItemStackExtension.class)
public interface ElytraMixin {
    @Inject(method = "canElytraFly", at = @At("HEAD"), cancellable = true)
    private void onCanElytraFly(LivingEntity entity, CallbackInfoReturnable<Boolean> info){
        if (!Utils.HasUpgrade(entity, CreateBacktanksExpanded.ELYTRA_UPGRADE.get())) return;
        info.setReturnValue(true);
    }
    @Inject(method = "elytraFlightTick", at = @At("HEAD"), cancellable = true)
    private void onElytraFlightTick(LivingEntity entity, int flightTicks, CallbackInfoReturnable<Boolean> info){
        if (!Utils.HasUpgrade(entity, CreateBacktanksExpanded.ELYTRA_UPGRADE.get())) return;
        info.setReturnValue(true);
    }
}
