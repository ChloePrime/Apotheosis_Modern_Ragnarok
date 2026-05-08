package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("DATA_HEALTH_ID")
    static EntityDataAccessor<Float> getDataHealthId() {
        throw new AbstractMethodError();
    }

    @Accessor boolean isDead();

    @Invoker boolean callShouldDropLoot();
    @Invoker void callDropFromLootTable(DamageSource pDamageSource, boolean pHitByPlayer);
    @Invoker void callDropExperience(Entity attacker);

    // 下面几个字段 Shadow 会犯病，
    // 所以用 accessor
    @Accessor int getNoJumpDelay();
    @Accessor void setNoJumpDelay(int value);
}
