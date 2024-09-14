package mod.chloeprime.apotheosismodernragnarok.common.gem.framework;

import com.google.common.collect.ImmutableList;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import mod.chloeprime.apotheosismodernragnarok.mixin.apoth.GemAccessor;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class GemInjector {
    public static void doInjections() {
        for (GemInjection injection : GemInjectionRegistry.INSTANCE.getValues()) {
            var gem = GemRegistry.INSTANCE.getValue(injection.getInjectionTarget());
            if (gem != null) {
                injectGem(injection, gem);
            }
        }
    }

    private static void injectGem(@Nonnull GemInjection injection, @Nonnull Gem gem) {
        var newBonuses = Stream.concat(gem.getBonuses().stream(), injection.getBonuses().stream())
                .distinct()
                .collect(ImmutableList.toImmutableList());
        var newBonusMap = newBonuses.stream()
                .<Pair<LootCategory, GemBonus>>mapMulti((gemData, mapper) -> gemData.getGemClass().types().forEach(c -> mapper.accept(Pair.of(c, gemData))))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        var uuidsNeeded = newBonuses.stream()
                .mapToInt(GemBonus::getNumberOfUUIDs)
                .max()
                .orElse(0);

        var accessor = (GemAccessor) gem;
        accessor.setBonuses(newBonuses);
        accessor.setBonusMap(newBonusMap);
        accessor.setUuidsNeeded(uuidsNeeded);
    }
}
