package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.world.item.ItemStack;

public interface GunAffix {
    default void onGunshotPre(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Pre event) {}
    default void onGunshotPost(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Post event) {}
    default void onGunshotKill(ItemStack gun, AffixInstance instance, EntityKillByGunEvent event) {}
    default void onBulletCreated(ItemStack gun, AffixInstance instance, BulletCreateEvent event) {}
}
