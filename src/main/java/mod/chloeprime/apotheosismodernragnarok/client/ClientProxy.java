package mod.chloeprime.apotheosismodernragnarok.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.tac.guns.client.handler.AimingHandler;
import com.tac.guns.common.Gun;
import com.tac.guns.item.GunItem;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicLaser;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

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
        var y = Mth.lerp(adsProgress, -0.06F, hasScope(shooter) ? -0.2F : 0);
        var z = Mth.lerp(adsProgress, 0.8F, 0.6F);

        Vec3 offset;
        if (fps && THE_CLIENT.options.bobView && shooter instanceof LocalPlayer player) {
            offset = bobCompensation(new Vec3(x, y, z), laser, player, partialTicks);
        } else {
            offset = new Vec3(x, y, z);
        }

        laser.setPos(laser.position().add(
                axisX.scale(offset.x).add(axisY.scale(offset.y)).add(axisZ.scale(offset.z))
        ));
        laser.lookAt(EntityAnchorArgument.Anchor.FEET, laser.getHitLocation());
    }

    private static Vec3 bobCompensation(Vec3 original, Entity laser, LocalPlayer player, float pPartialTicks) {
        float f = player.walkDist - player.walkDistO;
        float f1 = -(player.walkDist + f * pPartialTicks);
        float f2 = Mth.lerp(pPartialTicks, player.oBob, player.bob);

        var matrix = new PoseStack();
        matrix.mulPose(Vector3f.ZP.rotationDegrees(-(Mth.sin(f1 * (float)Math.PI) * f2 * 3.0F)));
        matrix.mulPose(Vector3f.XP.rotationDegrees(-(Math.abs(Mth.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F)));
        matrix.translate(-(Mth.sin(f1 * (float)Math.PI) * f2 * 0.5F), Math.abs(Mth.cos(f1 * (float)Math.PI) * f2), 0.0D);

        var affineVec = new Vector4f(new Vector3f(original));
        affineVec.transform(matrix.last().pose());
        return new Vec3(new Vector3f(affineVec));
    }

    private static boolean hasScope(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        var gun =living.getMainHandItem();
        if (!(gun.getItem() instanceof GunItem)) {
            return false;
        }
        return Gun.getScope(gun) != null;
    }
}
