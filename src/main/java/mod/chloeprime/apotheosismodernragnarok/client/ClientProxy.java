package mod.chloeprime.apotheosismodernragnarok.client;

import com.tac.guns.client.handler.AimingHandler;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicLaser;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class ClientProxy {
    private static final Minecraft THE_CLIENT = Minecraft.getInstance();
    private static final Vec3 UP = new Vec3(0, 1, 0);

    public static void fixLaserPos(MagicLaser laser) {
        Optional.<Entity>ofNullable(THE_CLIENT.player).or(
                () -> Optional.ofNullable(THE_CLIENT.level).map(level -> level.getEntity(laser.getShooterId()))
        ).ifPresent(shooter -> fixLaserPos(laser, shooter, 1));
    }

    public static void stickLaserToMuzzle(MagicLaser laser, float partialTicks) {
        Optional.ofNullable(laser.getLevel().getEntity(laser.getShooterId())).ifPresent(shooter -> {
            laser.setPos(shooter.getPosition(partialTicks).add(0, shooter.getEyeHeight(), 0));
            ClientProxy.fixLaserPos(laser, shooter, partialTicks);
        });
    }

    public static void fixLaserPos(MagicLaser laser, Entity shooter, float partialTicks) {
        boolean fps = THE_CLIENT.options.getCameraType() == CameraType.FIRST_PERSON;
        float adsProgress = shooter.equals(THE_CLIENT.player) && fps
                ? (float) AimingHandler.get().getNormalisedAdsProgress()
                : 0;

        var axisZ = shooter.getViewVector(partialTicks);
        var axisX = axisZ.cross(UP);
        var axisY = axisX.cross(axisZ);

        var x = Mth.lerp(adsProgress,0.06F, 0);
        var y = Mth.lerp(adsProgress, -0.06F, 0);
        var z = Mth.lerp(adsProgress, 0.8F, 0.6F);
        var bobCompensation = fps && THE_CLIENT.options.bobView && shooter instanceof LivingEntity living
                ? 0.05F * max(abs(living.xxa), abs(living.zza))
                : 0;

        laser.setPos(laser.position().add(
                axisX.scale(x).add(axisY.scale(y + bobCompensation)).add(axisZ.scale(z))
        ));
        laser.lookAt(EntityAnchorArgument.Anchor.FEET, laser.getHitLocation());
    }
}
