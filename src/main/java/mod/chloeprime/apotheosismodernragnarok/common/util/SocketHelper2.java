package mod.chloeprime.apotheosismodernragnarok.common.util;

import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import mod.chloeprime.apotheosismodernragnarok.mixin.apoth.GemInstanceAccessor;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class SocketHelper2 {
    public static void forEachValidAffix(ItemStack item, BiConsumer<DynamicHolder<? extends Affix>, AffixInstance> code) {
        AffixHelper.getAffixes(item).forEach((holder, instance) -> {
            if (instance.isValid()) {
                code.accept(holder, instance);
            }
        });
    }

    public static void forEachGemBonus(ItemStack item, BiConsumer<GemBonus, GemInstance> code) {
        for (var instance : SocketHelper.getGems(item)) {
            if (instance.isValid()) {
                ((GemInstanceAccessor) (Object) instance)
                        .callMap(Function.identity())
                        .ifPresent(bonus -> code.accept(bonus, instance));
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static Stream<Pair<GemBonus, GemInstance>> streamGemBonuses(ItemStack item) {
        return SocketHelper.getGems(item).streamValidGems()
                .flatMap(gi -> ((GemInstanceAccessor) (Object) gi).callMap(Function.identity()).map(gb -> Pair.of(gb, gi)).stream());
    }
}
