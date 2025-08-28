package mod.chloeprime.apotheosismodernragnarok.network;

import io.netty.buffer.ByteBuf;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.client.ClientNetworkHandler;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record S2CExecutionFeedback(
        int victimId,
        int attackerId
) implements CustomPacketPayload {
    public static final Type<S2CExecutionFeedback> TYPE = new Type<>(ApotheosisModernRagnarok.loc("execution_feedback"));

    public static final StreamCodec<ByteBuf, S2CExecutionFeedback> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, S2CExecutionFeedback::victimId, ByteBufCodecs.VAR_INT, S2CExecutionFeedback::attackerId, S2CExecutionFeedback::new
    );

    public void handle(IPayloadContext ignored) {
        ClientNetworkHandler.handleExecutionFeedback(this);
    }

    @Override
    public @Nonnull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
