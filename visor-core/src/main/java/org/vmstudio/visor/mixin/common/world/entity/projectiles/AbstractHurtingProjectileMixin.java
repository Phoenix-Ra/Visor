package org.vmstudio.visor.mixin.common.world.entity.projectiles;

import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.server.player.VRServerPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if >=1.20.5 {
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

@Mixin(AbstractHurtingProjectile.class)
public abstract class AbstractHurtingProjectileMixin {

    //? if >=1.20.5 {
    // AIM_DEFLECT has already stored the look angle, onDeflection turns it into the powers
    @Inject(at = @At("HEAD"), method = "onDeflection")
    public void visor$onDeflectByVRPlayer(Entity instance, boolean attack, CallbackInfo ci) {
        if (!attack || !(instance instanceof ServerPlayer player)) {
            return;
        }
        VRServerPlayer vrPlayer = VisorAPI.server()
                .getVRPlayer(player);
        if (vrPlayer == null) {
            return;
        }
        ((AbstractHurtingProjectile) (Object) this).setDeltaMovement(
                vrPlayer.getPoseData()
                        .getHmd()
                        .getDirectionVec3()
                        .normalize()
        );
    }
    //?} else {
    /*@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    public Vec3 visor$onDeflectByVRPlayer(Entity instance) {
        if (!(instance instanceof ServerPlayer player)) {
            return instance.getLookAngle();
        }
        VRServerPlayer vrPlayer = VisorAPI.server()
                .getVRPlayer(player);
        if (vrPlayer == null) {
            return instance.getLookAngle();
        }

        return vrPlayer.getPoseData()
                .getHmd()
                .getDirectionVec3();
    }
    *///?}
}
