package mod.chloeprime.apotheosismodernragnarok.common.eventhandlers;

import com.tacz.guns.entity.EntityKineticBullet;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TelepathicAffixAdapter {
    @SubscribeEvent
    public static void onLivingDrop(LivingDropsEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof EntityKineticBullet)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof LivingEntity shooter)) {
            return;
        }

        var weapon = shooter.getMainHandItem();
        var canTeleport = AffixHelper.streamAffixes(weapon).anyMatch(AffixInstance::enablesTelepathy);
        var targetPos = shooter.position();
        if (canTeleport) {
            for (ItemEntity item : event.getDrops()) {
                item.setPos(targetPos.x, targetPos.y, targetPos.z);
                item.setPickUpDelay(0);
            }
        }
    }
}
