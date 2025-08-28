package mod.chloeprime.apotheosismodernragnarok.common.util.debug;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class DamageAmountDebug {
    private static final Logger LOGGER = LoggerFactory.getLogger("AMR Damage Debug");
    private final Map<Pair<LivingEntity, LivingEntity>, Float> CACHE = new WeakHashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public final void preGunHurt(EntityHurtByGunEvent.Pre event) {
        Optional.ofNullable(event.getAttacker()).ifPresent(attacker -> {
            if (!(event.getHurtEntity() instanceof LivingEntity victim)) {
                return;
            }
            CACHE.put(Pair.of(attacker, victim), victim.getHealth());
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public final void postGunHurt(EntityHurtByGunEvent.Post event) {
        postHurtOrKill(event.getAttacker(), event.getHurtEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public final void postGunKill(EntityKillByGunEvent event) {
        postHurtOrKill(event.getAttacker(), event.getKilledEntity());
    }

    private void postHurtOrKill(LivingEntity attacker, Entity victim) {
        if (attacker == null) {
            return;
        }
        if (!(victim instanceof LivingEntity lv)) {
            return;
        }
        var cache = CACHE.remove(Pair.of(attacker, lv));
        if (cache == null) {
            return;
        }
        var amount = cache - lv.getHealth();
        LOGGER.debug("Gunshot {} {}, causes {} damage", lv.isDeadOrDying() ? "killed" : "hurt", lv, amount);
    }
}
