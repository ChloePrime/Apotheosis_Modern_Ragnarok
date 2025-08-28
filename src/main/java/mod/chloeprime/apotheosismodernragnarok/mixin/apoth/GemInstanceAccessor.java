package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.function.Function;

@Mixin(value = GemInstance.class, remap = false)
public interface GemInstanceAccessor {
    @Invoker
    <T> Optional<T> callMap(Function<GemBonus, T> function);
}
