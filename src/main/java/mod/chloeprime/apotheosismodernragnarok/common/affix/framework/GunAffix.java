package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public interface GunAffix {
    default void onGunFire(ItemStack gun, AffixInstance instance, GunFireEvent event) {}
    default void onGunshotPre(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Pre event) {}
    default void onGunshotPost(ItemStack gun, AffixInstance instance, EntityHurtByGunEvent.Post event) {}
    default void onGunshotKill(ItemStack gun, AffixInstance instance, EntityKillByGunEvent event) {}
    default void onBulletCreated(ItemStack gun, AffixInstance instance, BulletCreateEvent event) {}
    default void clientOnBulletCreated(ItemStack gun, AffixInstance instance, BulletCreateEvent event) {}
}
