package mod.chloeprime.apotheosismodernragnarok.client;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.sound.GunSoundInstance;
import com.tacz.guns.init.ModSounds;
import com.tacz.guns.sound.SoundManager;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.MagicalShotAffix;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client.AbstractSoundInstanceAccessor;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client.EntityBoundSoundInstanceAccessor;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.client.MixinSoundEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
@OnlyIn(Dist.CLIENT)
public class ClientCoremodHooks {
    private ClientCoremodHooks() {
    }

    /**
     * @see MixinSoundEngine 调用点
     */
    public static void adjustGunSound(SoundInstance instance0, Runnable canceller) {
        if (!(instance0 instanceof GunSoundInstance instance)) {
            return;
        }
        if (!ModSounds.GUN.getId().equals(instance.getLocation())) {
            return;
        }
        if (!(((EntityBoundSoundInstanceAccessor) instance).getEntity() instanceof LivingEntity shooter)) {
            return;
        }
        var gun = shooter.getMainHandItem();
        var isShootSound = Optional.ofNullable(IGun.getIGunOrNull(gun))
                .flatMap(kun -> TimelessAPI.getClientGunIndex(kun.getGunId(gun)))
                .filter(index -> {
                    var sound = instance.getRegistryName();
                    return sound != null && (
                            sound.equals(index.getSounds(SoundManager.SHOOT_SOUND)) ||
                            sound.equals(index.getSounds(SoundManager.SILENCE_SOUND)) ||
                            sound.equals(index.getSounds(SoundManager.SHOOT_3P_SOUND)) ||
                            sound.equals(index.getSounds(SoundManager.SILENCE_3P_SOUND)));
                }).isPresent();
        if (isShootSound) {
            adjustGunSound(shooter, shooter.getMainHandItem(), instance, canceller);
        }
    }

    public static void adjustGunSound(LivingEntity shooter, ItemStack item, SoundInstance oldInstance, Runnable canceller) {
        Optional<SoundEvent> newSound = Optional.empty();
        newSound = newSound.or(() -> MagicalShotAffix.getSoundFor(item));

        newSound.ifPresent(newSE -> {
            if (ModSounds.GUN.getId().equals(newSE.getLocation())) {
                return;
            }
            canceller.run();
            var vol = oldInstance instanceof AbstractSoundInstanceAccessor old ? old.amr$getVolume() : 1;
            var pit = oldInstance instanceof AbstractSoundInstanceAccessor old ? old.amr$getPitch() : 1;
            Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(
                    newSE, oldInstance.getSource(), vol, pit, shooter, System.currentTimeMillis()
            ));
        });
    }
}
