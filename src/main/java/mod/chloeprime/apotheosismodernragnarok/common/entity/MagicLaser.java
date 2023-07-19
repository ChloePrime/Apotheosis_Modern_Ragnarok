package mod.chloeprime.apotheosismodernragnarok.common.entity;

import com.google.common.util.concurrent.AtomicDouble;
import com.tac.guns.common.Gun;
import com.tac.guns.entity.ProjectileEntity;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.interfaces.IProjectileFactory;
import com.tac.guns.item.GunItem;
import com.tac.guns.util.math.ExtendedEntityRayTraceResult;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.internal.LaserProjectile;
import mod.chloeprime.apotheosismodernragnarok.mixin.tac.ProjectileEntityAccessor;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * 魔法激光
 * <p/>
 * @see mod.chloeprime.apotheosismodernragnarok.mixin.tac.MixinProjectileEntity 部分实现
 */
@SuppressWarnings("unchecked")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicLaser extends ProjectileEntity implements IEntityAdditionalSpawnData, LaserProjectile {
    public static final int LIVE_DURATION = 10;
    public MagicLaser(EntityType type, Level level, Builder builder) {
        super(type, level, builder.shooter, builder.weapon, builder.gunItem, builder.data, builder.rrp, builder.rry);
        direction = getDeltaMovement().normalize();
        range = builder.data.getProjectile().life * builder.data.getProjectile().getSpeed();
        constructorTail(level);
    }

    public MagicLaser(EntityType type, Level level) {
        super(type, level);
        constructorTail(level);
        direction = UP;
    }

    private static final Vec3 UP = new Vec3(0, 1, 0);

    public static class Builder {
        public Builder shooter(LivingEntity shooter) {
            this.shooter = shooter;
            return this;
        }

        public Builder weapon(ItemStack weapon) {
            this.weapon = weapon;
            return this;
        }

        public Builder item(GunItem item) {
            this.gunItem = item;
            return this;
        }

        public Builder data(Gun data) {
            this.data = data;
            return this;
        }

        public Builder recoil(float pitch, float yaw) {
            rrp = pitch;
            rry = yaw;
            return this;
        }

        private LivingEntity shooter;
        private ItemStack weapon;
        private GunItem gunItem;
        private Gun data;
        private float rrp, rry;
    }

    public float getRoll() {
        return roll;
    }

    public float getLength() {
        return length;
    }

    public long getLocalSpawnTime() {
        return localSpawnTime;
    }

    public void clip() {
        Vec3 pos = position();
        var ctx = new ClipContext(pos, pos.add(direction.scale(range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        int piercing = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.COLLATERAL.get(), getWeapon());
        int entityLimit = piercing + 1;

        var block = level.clip(ctx);
        var entity = clipEntities(ctx.getFrom(), ctx.getTo(), entityLimit);

        // 什么也没打中
        if (block.getType() == HitResult.Type.MISS && entity.isEmpty()) {
            length = (float) range;
            lookAt(EntityAnchorArgument.Anchor.FEET, pos.add(direction.scale(range)));
            return;
        }
        // 伤害实体并统计数量
        var furthestHit = new AtomicReference<HitResult>(null);
        var lengthSqrByEntity = new AtomicDouble(0);
        long entityCount = entity.stream().mapToLong(stream -> stream
                .filter(result -> result.getEntity() != shooter)
                .map(ExtendedEntityRayTraceResult::new)
                .peek(hit -> {
                    onHit(hit, ctx.getFrom(), hit.getLocation());
                    var lengthSqr = hit.getLocation().distanceToSqr(ctx.getFrom());
                    if (lengthSqr > lengthSqrByEntity.get()) {
                        lengthSqrByEntity.set(lengthSqr);
                        furthestHit.set(hit);
                    }
                })
                .count()
        ).findAny().orElse(0);
        Vec3 hitLocation;
        // 击中方块
        if (entityCount < entityLimit && block.getType() != HitResult.Type.MISS) {
            onHit(block, ctx.getFrom(), block.getLocation());
            hitLocation = block.getLocation();
        } else {
            hitLocation = Optional.ofNullable(furthestHit.get())
                    .map(HitResult::getLocation)
                    .orElse(pos);
        }
        length = (float) hitLocation.distanceTo(ctx.getFrom());
        lookAt(EntityAnchorArgument.Anchor.FEET, hitLocation);
    }

    private Optional<Stream<EntityResult>> clipEntities(Vec3 from, Vec3 to, int limit) {
        if (limit == 1) {
            return Optional.ofNullable(findEntityOnPath(from, to)).map(Collections::singletonList).map(List::stream);
        }
        return Optional.ofNullable(findEntitiesOnPath(from, to)).map(list -> list.stream()
                // 阻止伤害到自己和自己的坐骑
                .filter(result -> result.getEntity() != shooter && result.getEntity() != shooter.getVehicle())
                // 按距离排序
                .sorted(Comparator.comparing(result -> result.getHitPos().distanceToSqr(from)))
                .limit(limit)
        );
    }

    private static double distanceSqr(ClipContext from, HitResult to) {
        return from.getFrom().distanceToSqr(to.getLocation());
    }

    private final Vec3 direction;
    private double range;
    private Vec2 rotation = Vec2.ZERO;
    private float roll;
    private float length;
    private long localSpawnTime;

    protected void onHit(HitResult result, Vec3 start, Vec3 end) {
        ((ProjectileEntityAccessor)this).invokeOnHit(result, start, end);
    }

    private void constructorTail(Level level) {
        this.life = LIVE_DURATION;
        this.roll = Mth.TWO_PI * level.getRandom().nextFloat();
    }

    @Override
    public void updateHeading() {

    }

    @Override
    public Vec3 getDeltaMovement() {
        return isAddedToWorld() ? Vec3.ZERO : super.getDeltaMovement();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        localSpawnTime = System.nanoTime();
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat(rotation.x);
        buffer.writeFloat(rotation.y);
        buffer.writeFloat(roll);
        buffer.writeFloat(length);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        super.readSpawnData(buffer);
        rotation = new Vec2(buffer.readFloat(), buffer.readFloat());
        roll = buffer.readFloat();
        length = buffer.readFloat();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        clip();
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class Factory implements IProjectileFactory {
        public static final Factory INSTANCE = new Factory();

        @Override
        public ProjectileEntity create(Level level, LivingEntity shooter, ItemStack stack, GunItem gunItem, Gun gun, float recoilP, float recoilY) {
            var builder = new Builder().shooter(shooter).weapon(stack).item(gunItem).data(gun).recoil(recoilP, recoilY);
            return new MagicLaser(ModContent.Entities.MAGIC_LASER.get(), level, builder);
        }

        private Factory() {}
    }
}
