package mod.chloeprime.apotheosismodernragnarok.network;

import io.netty.buffer.ByteBuf;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.client.ClientNetworkHandler;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record S2CPerfectBlockTriggered(
        int id
) implements CustomPacketPayload {
    public static final Type<S2CPerfectBlockTriggered> TYPE = new Type<>(ApotheosisModernRagnarok.loc("perfect_block_triggered"));

    public static final StreamCodec<ByteBuf, S2CPerfectBlockTriggered> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, S2CPerfectBlockTriggered::id, S2CPerfectBlockTriggered::new
    );

    public void handle(IPayloadContext ignored) {
        ClientNetworkHandler.handlePerfectBlockTriggered(this);
    }

    @Override
    public @Nonnull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
