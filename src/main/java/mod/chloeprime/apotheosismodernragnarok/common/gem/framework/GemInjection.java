package mod.chloeprime.apotheosismodernragnarok.common.gem.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import net.minecraft.resources.ResourceLocation;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class GemInjection implements CodecProvider<GemInjection> {
    public static final Codec<GemInjection> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    ResourceLocation.CODEC.fieldOf("gem").forGetter(GemInjection::getInjectionTarget),
                    GemBonus.CODEC.listOf().fieldOf("bonuses").forGetter(GemInjection::getBonuses))
            .apply(inst, GemInjection::new));

    public final long hash = HASH_GENERATOR.nextLong() ^ 1L;

    public GemInjection(ResourceLocation target, List<GemBonus> bonuses) {
        this.target = target;
        this.bonuses = bonuses;
    }

    public List<GemBonus> getBonuses() {
        return bonuses;
    }

    public ResourceLocation getInjectionTarget() {
        return target;
    }

    public static final Random HASH_GENERATOR = new SecureRandom();
    private final ResourceLocation target;
    private final List<GemBonus> bonuses;

    @Override
    public Codec<? extends GemInjection> getCodec() {
        return CODEC;
    }
}
