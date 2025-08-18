package mod.chloeprime.apotheosismodernragnarok.common.gem.framework;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public interface GunGemBonus {
    default void onGunFire(ItemStack gun, ItemStack gem, GemInstance instance, GunFireEvent event) {}
    default void onGunshotPre(ItemStack gun, ItemStack gem, GemInstance instance, EntityHurtByGunEvent.Pre event) {}
    default void onGunshotPost(ItemStack gun, ItemStack gem, GemInstance instance, EntityHurtByGunEvent.Post event) {}
    default void onGunshotKill(ItemStack gun, ItemStack gem, GemInstance instance, EntityKillByGunEvent event) {}
    default void onBulletCreated(ItemStack gun, ItemStack gem, GemInstance instance, BulletCreateEvent event) {}
    default void clientOnBulletCreated(ItemStack gun, ItemStack gem, GemInstance instance, BulletCreateEvent event) {}
}
