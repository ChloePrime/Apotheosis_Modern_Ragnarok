package mod.chloeprime.apotheosismodernragnarok.common.gem.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.GemView;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.framework.AffixBaseUtility;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GunGemBonus;
import mod.chloeprime.apotheosismodernragnarok.mixin.minecraft.LivingEntityAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import org.apache.commons.compress.utils.Lists;

import java.util.Map;

public class DictatorGemBonus extends GemBonus implements GunGemBonus {
    public static final ResourceLocation ID = ApotheosisModernRagnarok.loc("dictator");

    public static final Codec<DictatorGemBonus> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    gemClass(),
                    Purity.mapCodec(Codec.FLOAT).fieldOf("damage").forGetter(instance -> instance.damage)
            ).apply(inst, DictatorGemBonus::new));

    private final Object2FloatMap<Purity> damage;

    public DictatorGemBonus(
            GemClass gemClass,
            Map<Purity, Float> damage
    ) {
        super(gemClass);
        this.damage = new Object2FloatLinkedOpenHashMap<>(damage);
    }

    @Override
    public void onGunshotPre(ItemStack gun, ItemStack gem, GemInstance instance, EntityHurtByGunEvent.Pre event) {
        Entity victim = event.getHurtEntity();
        if (victim instanceof Enemy || victim instanceof NeutralMob) {
            return;
        }
        event.setBaseAmount(this.damage.getOrDefault(instance.purity(), event.getBaseAmount()));
    }

    @Override
    public void onGunshotPost(ItemStack gun, ItemStack gem, GemInstance instance, EntityHurtByGunEvent.Post event) {
        Entity victim = event.getHurtEntity();
        if (victim instanceof Enemy) {
            return;
        }
        if (victim instanceof LivingEntityAccessor living) {
            victim.captureDrops(Lists.newArrayList());
            // 生成掉落物
            if (living.callShouldDropLoot() && victim.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                DamageSource damageSource = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
                living.callDropFromLootTable(damageSource, damageSource.getEntity() instanceof Player);
            }
            // 掉落经验
            living.callDropExperience(event.getAttacker());
            // 生成掉落物实体
            var drops = victim.captureDrops(null);
            if (drops != null) {
                drops.forEach(victim.level()::addFreshEntity);
            }
        }
    }

    public float getDamage(LootRarity rarity) {
        return this.damage.getOrDefault(rarity, 0);
    }

    @Override
    public boolean supports(Purity rarity) {
        return damage.containsKey(rarity);
    }

    @Override
    public Component getSocketBonusTooltip(GemView gem, AttributeTooltipContext ctx) {
        var damage = AffixBaseUtility.fmt(this.damage.getFloat(gem.purity()));
        return Component
                .translatable("bonus.apotheosis_modern_ragnarok.dictator.desc", damage)
                .withStyle(AffixBaseUtility.BRIGHT_RED);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }
}
