package mod.chloeprime.apotheosismodernragnarok.mixin.minecraft;

import com.tac.guns.entity.DamageSourceProjectile;
import mod.chloeprime.apotheosismodernragnarok.common.internal.ExtendedDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = DamageSource.class, priority = 1005)
public class MixinDamageSource implements ExtendedDamageSource {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ItemStack apotheosis_modern_ragnarok$getWeapon() {
        return Optional.ofNullable(apotheosis_modern_ragnarok$weapon)
                .orElse(apotheosis_modern_ragnarok$weapon = ((DamageSourceProjectile)(Object)this).getWeapon());
    }

    @Override
    public void apotheosis_modern_ragnarok$setWeapon(ItemStack weapon) {
        apotheosis_modern_ragnarok$weapon = weapon;
    }

    @Override
    public boolean apotheosis_modern_ragnarok$isGunshot() {
        return apotheosis_modern_ragnarok$gunshot;
    }

    @Override
    public void apotheosis_modern_ragnarok$setGunshot(boolean value) {
        apotheosis_modern_ragnarok$gunshot = value;
    }

    @Override
    public boolean apotheosis_modern_ragnarok$isGunshotFirstPart() {
        return apotheosis_modern_ragnarok$gunshotFirst;
    }

    @Override
    public void apotheosis_modern_ragnarok$setGunshotFirstPart(boolean value) {
            this.apotheosis_modern_ragnarok$gunshotFirst = value;
    }

    @Override
    public boolean apotheosis_modern_ragnarok$isHeadshot() {
        return apotheosis_modern_ragnarok$headshot;
    }

    @Override
    public void apotheosis_modern_ragnarok$setHeadshot(boolean value) {
        apotheosis_modern_ragnarok$headshot = value;
    }

    @Unique private boolean apotheosis_modern_ragnarok$headshot;
    @Unique private boolean apotheosis_modern_ragnarok$gunshot;
    @Unique private boolean apotheosis_modern_ragnarok$gunshotFirst;
    @Unique private ItemStack apotheosis_modern_ragnarok$weapon;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(String pMessageId, CallbackInfo ci) {
        apotheosis_modern_ragnarok$gunshot = apotheosis_modern_ragnarok$gunshotFirst = (Object)this instanceof DamageSourceProjectile;

        if ((Object)this instanceof DamageSourceProjectile) {
            // 从枪械伤害来源里复制武器
            // 对于 DamageSourceProjectile，此时武器参数是还未被赋值给字段。
            // 所以得等调用 getter 的时候再获取
            apotheosis_modern_ragnarok$weapon = null;
        } else if ((Object)this instanceof EntityDamageSource source) {
            // 假定玩家主手所持武器即所持武器
            apotheosis_modern_ragnarok$weapon = Optional.ofNullable(source.getEntity())
                    .map(e -> e instanceof LivingEntity living ? living.getMainHandItem() : null)
                    .orElse(ItemStack.EMPTY);
        } else {
            apotheosis_modern_ragnarok$weapon = ItemStack.EMPTY;
        }
    }

    @Dynamic
    @Inject(
            method = { "copyFrom(Lnet/minecraft/world/damagesource/DamageSource;)V", "apotheosis$copyFrom(Lnet/minecraft/world/damagesource/DamageSource;)V"},
            at = @At("TAIL"), remap = false, require = 1
    )
    @SuppressWarnings("DataFlowIssue")
    private void injectCopy(DamageSource other0, CallbackInfo ci) {
        var other = ((MixinDamageSource)(Object)other0);
        this.apotheosis_modern_ragnarok$gunshot  = other.apotheosis_modern_ragnarok$gunshot;
        this.apotheosis_modern_ragnarok$headshot = other.apotheosis_modern_ragnarok$headshot;
        this.apotheosis_modern_ragnarok$weapon   = other.apotheosis_modern_ragnarok$getWeapon();
    }
}
