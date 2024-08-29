package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import mod.chloeprime.apotheosismodernragnarok.common.affix.GunAffix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber
public class GunAffixTrigger {
    @SubscribeEvent
    public static void hurt(EntityHurtByGunEvent event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        var shooter = event.getAttacker();
        if (shooter == null) {
            return;
        }
        var gun = shooter.getMainHandItem();
        if (!checkGun(gun, event.getGunId())) {
            return;
        }

        var affixes = AffixHelper.streamAffixes(gun);
        if (event instanceof EntityHurtByGunEvent.Pre pre) {
            affixes.forEach(instance -> {
                if (instance.affix().get() instanceof GunAffix affix) {
                    affix.onGunshotPre(gun, instance, pre);
                }
            });
        }
        if (event instanceof EntityHurtByGunEvent.Post post) {
            affixes.forEach(instance -> {
                if (instance.affix().get() instanceof GunAffix affix) {
                    affix.onGunshotPost(gun, instance, post);
                }
            });
        }
    }

    @SubscribeEvent
    public static void kill(EntityKillByGunEvent event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        var shooter = event.getAttacker();
        if (shooter == null) {
            return;
        }
        var gun = shooter.getMainHandItem();
        if (!checkGun(gun, event.getGunId())) {
            return;
        }

        AffixHelper.streamAffixes(gun).forEach(instance -> {
            if (instance.affix().get() instanceof GunAffix affix) {
                affix.onGunshotKill(gun, instance, event);
            }
        });
    }

    private static boolean checkGun(ItemStack weapon, ResourceLocation gunId) {
        return Optional.ofNullable(IGun.getIGunOrNull(weapon))
                .filter(ig -> ig.getGunId(weapon).equals(gunId))
                .isPresent();
    }
}
