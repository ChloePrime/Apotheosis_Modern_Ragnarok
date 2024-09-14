package mod.chloeprime.apotheosismodernragnarok.common.util;

import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import mod.chloeprime.apotheosismodernragnarok.mixin.apoth.GemInstanceAccessor;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;
import java.util.stream.Stream;

public class SocketHelper2 {
    @SuppressWarnings("DataFlowIssue")
    public static Stream<Pair<GemBonus, GemInstance>> streamGemBonuses(ItemStack item) {
        return SocketHelper.getGems(item).streamValidGems()
                .flatMap(gi -> ((GemInstanceAccessor) (Object) gi).callMap(Function.identity()).map(gb -> Pair.of(gb, gi)).stream());
    }
}
