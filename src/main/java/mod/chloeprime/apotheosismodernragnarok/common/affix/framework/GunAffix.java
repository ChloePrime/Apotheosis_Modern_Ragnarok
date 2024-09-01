package mod.chloeprime.apotheosismodernragnarok.common.affix.framework;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import mod.chloeprime.apotheosismodernragnarok.common.util.BulletCreateEvent;
import net.minecraft.world.item.ItemStack;

public interface GunAffix {
    default void onGunshotPre(ItemStack stack, AffixInstance instance, EntityHurtByGunEvent.Pre event) {}
    default void onGunshotPost(ItemStack stack, AffixInstance instance, EntityHurtByGunEvent.Post event) {}
    default void onGunshotKill(ItemStack stack, AffixInstance instance, EntityKillByGunEvent event) {}
    default void onBulletCreated(ItemStack stack, AffixInstance instance, BulletCreateEvent event) {}
}
