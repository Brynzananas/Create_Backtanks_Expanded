package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import com.brynzananas.create_backtanks_expanded.upgrades.HoverUpgradeItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "getGravity", at = @At("HEAD"), cancellable = true)
    private void onGetGravity(CallbackInfoReturnable<Double> info){
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.onGround()) return;
        double value = livingEntity.getAttributeValue(CreateBacktanksExpanded.HOVER_REACH);
        if (!livingEntity.getData(CreateBacktanksExpanded.CAN_HOVER)) return;
        if (!livingEntity.getData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS)) return;
        info.setReturnValue(0d);
    }
}
