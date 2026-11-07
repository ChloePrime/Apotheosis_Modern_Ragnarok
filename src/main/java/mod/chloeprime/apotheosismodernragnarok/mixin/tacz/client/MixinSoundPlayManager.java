package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.pojo.display.SoundData;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import mod.chloeprime.apotheosismodernragnarok.client.ClientCoremodHooks;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(value = SoundPlayManager.class, remap = false)
public class MixinSoundPlayManager {
    /**
     * 借助 GunsmithLib 新版自带的枪械音效播放同名 sound event 功能，
     * 此处只需要修改音效 id 就行了，
     * 不用再像之前那样使用 {@link SoundEngine} 单独播放音效了。
     *
     * @since 7.0.0
     */
    @ModifyExpressionValue(
            method = "playShootSound",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lcom/tacz/guns/client/resource/pojo/display/SoundData;"))
    private static SoundData playModifiedSoundsForMagicGuns(
            SoundData original,
            LivingEntity shooter, GunDisplayInstance display, GunData gunData
    ) {
        var mainhand = shooter.getMainHandItem();
        var isWrongWeapon = TimelessAPI.getGunDisplay(mainhand)
                .filter(mainhandDisplay -> mainhandDisplay != display)
                .isPresent();
        if (isWrongWeapon) {
            return original;
        }
        var adjusted = ClientCoremodHooks.adjustGunSound(mainhand, original::getSound);
        if (Objects.equals(adjusted, original.getSound())) {
            return original;
        }
        return SoundDataAccessor.create(adjusted, null, null, 0, 0.1F);
    }
}
