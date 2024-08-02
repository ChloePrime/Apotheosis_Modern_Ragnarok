package mod.chloeprime.apotheosismodernragnarok.api.events;

import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ArmorSquashAffixTakeEffectEvent extends LivingEvent {
    private final Entity shooter;
    private final Affix affix;

    private final ItemStack armorToDestroy;

    public ArmorSquashAffixTakeEffectEvent(LivingEntity victim, Entity shooter, Affix affix, ItemStack armorToDestroy) {
        super(victim);
        this.shooter = shooter;
        this.affix = affix;
        this.armorToDestroy = armorToDestroy;
    }

    public final LivingEntity getVictim() {
        return super.getEntity();
    }

    public final Entity getShooter() {
        return shooter;
    }

    public final Affix getAffix() {
        return affix;
    }

    public final ItemStack getArmorToDestroy() {
        return armorToDestroy;
    }
}
