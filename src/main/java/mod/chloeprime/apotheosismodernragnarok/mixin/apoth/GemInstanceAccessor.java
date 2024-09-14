package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.function.Function;

@Mixin(GemInstance.class)
public interface GemInstanceAccessor {
    @Invoker
    <T> Optional<T> callMap(Function<GemBonus, T> function);
}
