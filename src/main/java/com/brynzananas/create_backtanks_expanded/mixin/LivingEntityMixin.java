package com.brynzananas.create_backtanks_expanded.mixin;

import com.brynzananas.create_backtanks_expanded.CreateBacktanksExpanded;
import com.brynzananas.create_backtanks_expanded.Utils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin{

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info){
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        double value = livingEntity.getAttributeValue(CreateBacktanksExpanded.HOVER_REACH);
        if (!livingEntity.getData(CreateBacktanksExpanded.CAN_HOVER) && value > 0){
                livingEntity.setData(CreateBacktanksExpanded.CAN_HOVER, true);

        }else if (value <= 0){
            livingEntity.setData(CreateBacktanksExpanded.CAN_HOVER, false);
        }
        boolean canHover = livingEntity.getData(CreateBacktanksExpanded.CAN_HOVER);
        if (canHover && !livingEntity.getData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS) && !Utils.GetNearbySolidBlocks(livingEntity, (int) value, (int) value, (int) value).isEmpty()){
                livingEntity.setData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS, true);
        }else if (livingEntity.getData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS) && Utils.GetNearbySolidBlocks(livingEntity, (int) value, (int) value, (int) value).isEmpty()){
            livingEntity.setData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS, false);
        }
        double air_regeneration = livingEntity.getAttributeValue(CreateBacktanksExpanded.BACKTANK_PRESSURIZED_AIR_REGENERATION);
        if (air_regeneration != 0 && livingEntity.level().getGameTime() % 20 == 0){
            ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
            BacktankUtil.consumeAir(livingEntity, itemStack, (int)-air_regeneration);
        }
        if (!canHover) return;
        if (!livingEntity.getData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS)) return;
        boolean up = livingEntity.jumping;
        boolean down = livingEntity.isShiftKeyDown();
        Vec3 deltaMovement = livingEntity.getDeltaMovement();
        if (!livingEntity.onGround()) livingEntity.setOnGround(true);
        livingEntity.setDeltaMovement(deltaMovement.x, ((up && down ? 0f : (up ? 1f : (down ? -1f : 0f))) * livingEntity.getSpeed()), deltaMovement.z);
    }
    @Inject(method = "getJumpPower", at = @At("HEAD"), cancellable = true)
    private void onGetJumpPower(CallbackInfoReturnable<Float> info){
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.getData(CreateBacktanksExpanded.CAN_HOVER)) return;
        if (!livingEntity.getData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS)) return;
        info.setReturnValue(0f);
    }
    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder createLivingAttributes(AttributeSupplier.Builder original) {
        original.add(CreateBacktanksExpanded.HOVER_REACH);
        original.add(CreateBacktanksExpanded.BACKTANK_PRESSURIZED_AIR_REGENERATION);
        return original;
    }
    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void onCauseFallDamage(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> info){
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.getData(CreateBacktanksExpanded.CAN_HOVER)) return;
        info.setReturnValue(false);
    }
    @Inject(method = "getFlyingSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetFlyingSpeed(CallbackInfoReturnable<Float> info){
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.getData(CreateBacktanksExpanded.CAN_HOVER)) return;
        if (!livingEntity.getData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS)) return;
        info.setReturnValue(livingEntity.getSpeed());
    }
    /*@ModifyVariable(
            method = "handleRelativeFrictionAndCalculateMovement",
            at = @At("HEAD"),
            ordinal = 0, // 0 means the first argument of type 'int'
            argsOnly = true // Ensures we only look at method parameters, not local variables
    )
    private float modifyTargetArgument(float originalValue) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.getData(CreateBacktanksExpanded.CAN_HOVER)) return originalValue;
        if (!livingEntity.getData(CreateBacktanksExpanded.HOVER_NEARBY_BLOCKS)) return originalValue;
        return 1f;
    }*/
}
