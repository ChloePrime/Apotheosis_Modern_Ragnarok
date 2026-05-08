package mod.chloeprime.apotheosismodernragnarok.mixin.tacz.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import mod.chloeprime.apotheosismodernragnarok.client.ClientCoremodHooks;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(value = SoundPlayManager.class, remap = false)
public class MixinSoundPlayManager {
    /**
     * 借助 GunsmithLib 新版自带的枪械音效播放同名 sound event 功能，
     * 此处只需要修改音效 id 就行了，
     * 不用再像之前那样使用 {@link SoundEngine} 单独播放音效了。
     *
     * @since 7.0.0
     */
    @WrapOperation(
            method = "playShootSound",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation playModifiedSoundsForMagicGuns(
            GunDisplayInstance display, String name, Operation<ResourceLocation> original,
            LivingEntity entity, GunDisplayInstance gunIndex, GunData gunData
    ) {
        var mainhand = entity.getMainHandItem();
        var fallback = (Supplier<ResourceLocation>) () -> original.call(display, name);
        var isWrongWeapon = TimelessAPI.getGunDisplay(mainhand)
                .filter(mainhandDisplay -> mainhandDisplay != display)
                .isPresent();
        if (isWrongWeapon) {
            return fallback.get();
        }
        return ClientCoremodHooks.adjustGunSound(mainhand, fallback);
    }
}
