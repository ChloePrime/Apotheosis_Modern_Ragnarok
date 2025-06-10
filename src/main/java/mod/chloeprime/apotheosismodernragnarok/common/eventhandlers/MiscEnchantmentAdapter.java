package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MiscEnchantmentAdapter {
    @SubscribeEvent
    @SuppressWarnings("UnstableApiUsage")
    public static void onAttachProperty(AttachmentPropertyEvent event) {
        var cache = event.getCacheProperty();
        var piercing = event.getGunItem().getEnchantmentLevel(Enchantments.PIERCING);
        if (piercing > 0) {
            cache.setCache(GunProperties.PIERCE, cache.getCache(GunProperties.PIERCE) + piercing);
        }
        var knockback = event.getGunItem().getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
        if (knockback > 0) {
            cache.setCache(GunProperties.KNOCKBACK, cache.getCache(GunProperties.KNOCKBACK) + knockback);
        }
    }
}
