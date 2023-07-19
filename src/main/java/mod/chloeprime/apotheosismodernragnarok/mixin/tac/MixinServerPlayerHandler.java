package mod.chloeprime.apotheosismodernragnarok.mixin.tac;

import com.tac.guns.common.Gun;
import com.tac.guns.common.network.ServerPlayHandler;
import com.tac.guns.entity.ProjectileEntity;
import com.tac.guns.interfaces.IProjectileFactory;
import com.tac.guns.item.GunItem;
import com.tac.guns.network.message.MessageBulletTrail;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import mod.chloeprime.apotheosismodernragnarok.common.entity.MagicLaser;
import mod.chloeprime.apotheosismodernragnarok.common.internal.LaserProjectile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.apotheosis.Apoth;
import shadows.apotheosis.adventure.affix.AffixHelper;

import java.util.Arrays;
import java.util.Optional;

@Mixin(value = ServerPlayHandler.class, remap = false)
public class MixinServerPlayerHandler {
    @Redirect(
            method = "handleShoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z", ordinal = 1)
    )
    private static boolean trySaveBullet(ServerPlayer player) {
        if (player.isCreative()) {
            return true;
        }

        if (!BulletSaverAffix.INSTANCE.isPresent()) {
            return false;
        }

        var affix = BulletSaverAffix.INSTANCE.get();
        var gun = player.getMainHandItem();
        return Optional.ofNullable(AffixHelper.getAffixes(gun).get(affix)).map(
                instance -> player.getRandom().nextFloat() <= affix.getValue(gun, instance)
        ).orElse(false);
    }

    @Redirect(
            method = "handleShoot",
            at = @At(value = "INVOKE", target = "Lcom/tac/guns/interfaces/IProjectileFactory;create(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lcom/tac/guns/item/GunItem;Lcom/tac/guns/common/Gun;FF)Lcom/tac/guns/entity/ProjectileEntity;")
    )
    private static ProjectileEntity magicShootsLaser(IProjectileFactory muggleFactory, Level level, LivingEntity shooter, ItemStack stack, GunItem item, Gun gun, float rot0, float rot1) {
        boolean isMagic = Optional.ofNullable(Apoth.Affixes.MAGICAL.get())
                .map(affix -> AffixHelper.getAffixes(stack).containsKey(affix))
                .orElse(false);

        var factory = isMagic ? MagicLaser.Factory.INSTANCE : muggleFactory;
        return factory.create(level, shooter, stack, item, gun, rot0, rot1);
    }

    @Redirect(
            method = "handleShoot",
            at = @At(value = "NEW", target = "([Lcom/tac/guns/entity/ProjectileEntity;Lcom/tac/guns/common/Gun$Projectile;IF)Lcom/tac/guns/network/message/MessageBulletTrail;")
    )
    private static MessageBulletTrail magicShootNoMuggleTrail0(ProjectileEntity[] originalProjectiles, Gun.Projectile projectileProps, int shooterId, float size) {

        var filtered = Arrays.stream(originalProjectiles)
                .filter(it -> !(it instanceof LaserProjectile))
                .toArray(ProjectileEntity[]::new);

        return filtered.length == 0
                ? apotheosis_modern_ragnarok$SKIP_TRAIL
                : new MessageBulletTrail(filtered, projectileProps, shooterId, size);
    }

    @Unique
    private static final MessageBulletTrail apotheosis_modern_ragnarok$SKIP_TRAIL = new MessageBulletTrail(
            new Vec3[0], new Vec3[0],new float[0], new float[0], new int[0], ItemStack.EMPTY, 0xFFFFFFFF, 1, 0, 1, 0, 1
    );

    @Redirect(
            method = "handleShoot",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/simple/SimpleChannel;send(Lnet/minecraftforge/network/PacketDistributor$PacketTarget;Ljava/lang/Object;)V", ordinal = 0)
    )
    private static <MSG> void magicShootNoMuggleTrail1(SimpleChannel channel, PacketDistributor.PacketTarget target, MSG message) {
        if (message == apotheosis_modern_ragnarok$SKIP_TRAIL) {
            return;
        }
        channel.send(target, message);
    }
}
