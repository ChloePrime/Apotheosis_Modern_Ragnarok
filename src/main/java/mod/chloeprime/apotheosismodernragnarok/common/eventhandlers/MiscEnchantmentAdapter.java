package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import mod.chloeprime.apotheosismodernragnarok.mixin.tacz.EntityKineticBulletAccessor;
import mod.chloeprime.gunsmithlib.proxies.ClientProxy;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class MiscEnchantmentAdapter {
    private static final ItemStack VIRTUAL_ARROW = Items.ARROW.getDefaultInstance();

    @SubscribeEvent
    @SuppressWarnings("UnstableApiUsage")
    private static void onAttachProperty(AttachmentPropertyEvent event) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        var cache = event.getCacheProperty();
        var level = server.overworld();
        var piercing = EnchantmentHelper.getPiercingCount(level, event.getGunItem(), VIRTUAL_ARROW);
        if (piercing > 0) {
            cache.setCache(GunProperties.PIERCE, cache.getCache(GunProperties.PIERCE) + piercing);
        }
        var punch = ClientProxy.getRegistryAccess()
                .flatMap(reg -> reg.registry(Registries.ENCHANTMENT))
                .flatMap(registry -> registry.getHolder(Enchantments.PUNCH))
                .orElse(null);
        if (punch != null) {
            var knockback = event.getGunItem().getEnchantmentLevel(punch);
            if (knockback > 0) {
                cache.setCache(GunProperties.KNOCKBACK, cache.getCache(GunProperties.KNOCKBACK) + knockback);
            }
        }
    }

    /**
     * 1.21.1 后的冲击附魔适配，
     * 实际上并没有作用，因为冲击附魔限制直接伤害来源必须是箭，
     * 所以还是得在上面获取冲击附魔的等级并根据等级加击退才行。
     * 不过这里的代码依然可以让数据包自定义的附魔给子弹加击退。
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    private static void onGunshotPre(EntityHurtByGunEvent.Pre event) {
        if (!(event.getBullet() instanceof EntityKineticBulletAccessor accessor)) {
            return;
        }
        if (!(event.getBullet().level() instanceof ServerLevel level)) {
            return;
        }
        var attacker = event.getAttacker();
        var victim = event.getHurtEntity();
        if (attacker == null || victim == null) {
            return;
        }
        var source = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        var oldKnockback = accessor.getKnockback();
        var newKnockback = EnchantmentHelper.modifyKnockback(level, attacker.getMainHandItem(), victim, source, oldKnockback);
        if (Math.abs(newKnockback - oldKnockback) > 1e-3) {
            accessor.setKnockback(newKnockback);
        }
    }
}
