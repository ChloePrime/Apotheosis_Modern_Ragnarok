package mod.chloeprime.apotheosismodernragnarok.common.entity;

import com.tac.guns.Config;
import com.tac.guns.common.Gun;
import com.tac.guns.entity.ProjectileEntity;
import com.tac.guns.item.GunItem;
import mod.chloeprime.apotheosismodernragnarok.api.MagicProjectileFactory;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.internal.MagicProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Optional;

/**
 * 魔法火球
 */
public class MagicFireball extends ProjectileEntity implements MagicProjectile, IAnimatable, IAnimationTickable {
    public static final float DEFAULT_POWER = 1.5F;
    private final float power = DEFAULT_POWER;

    public MagicFireball(EntityType<? extends Entity> type, Level worldIn) {
        super(type, worldIn);
        gecko = GeckoLibUtil.createFactory(this);
    }

    public MagicFireball(EntityType<? extends Entity> type, Level level, ProjectileBuilder builder) {
        super(type, level, builder.shooter, builder.weapon, builder.gunItem, builder.data, builder.rrp, builder.rry);
        gecko = GeckoLibUtil.createFactory(this);
    }

    public final AnimationFactory gecko;

    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot) {
        createExplosion(this, this.power * Config.COMMON.missiles.explosionRadius.get().floatValue(), true);
    }

    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z) {
        createExplosion(this, this.power * Config.COMMON.missiles.explosionRadius.get().floatValue(), true);
        this.life = 0;
    }

    public void onExpired() {
        createExplosion(this, this.power * Config.COMMON.missiles.explosionRadius.get().floatValue(), true);
    }

    private PlayState flyAnim(AnimationEvent<MagicFireball> e) {
        e.getController().setAnimation(new AnimationBuilder().addAnimation("speed_3", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState ringAnim(AnimationEvent<MagicFireball> e) {
        e.getController().setAnimation(new AnimationBuilder().addAnimation("fly", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "fly", 0, this::flyAnim));
        data.addAnimationController(new AnimationController<>(this, "ring", 0, this::ringAnim));
    }

    @Override
    public AnimationFactory getFactory() {
        return gecko;
    }

    @Override
    public int tickTimer() {
        return tickCount;
    }

    public static class Factory implements MagicProjectileFactory {
        public static final Factory INSTANCE = new Factory();

        @Override
        public ProjectileEntity create(Level level, LivingEntity shooter, ItemStack itemStack, GunItem gunItem, Gun gun, float rrp, float rry) {
            return new MagicFireball(
                    ModContent.Entities.MAGIC_FIREBALL.get(), level,
                    new ProjectileBuilder().shooter(shooter).weapon(itemStack).item(gunItem).data(gun).recoil(rrp, rry)
            );
        }

        @Override
        public Optional<SoundEvent> getShootSound(ItemStack weapon) {
            return Optional.of(ModContent.Sounds.MAGIC_FIREBALL.get());
        }

        private Factory() {}
    }
}
