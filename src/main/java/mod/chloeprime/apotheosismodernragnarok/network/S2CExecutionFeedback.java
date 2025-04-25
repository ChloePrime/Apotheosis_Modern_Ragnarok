package mod.chloeprime.apotheosismodernragnarok.network;

import mod.chloeprime.apotheosismodernragnarok.client.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CExecutionFeedback(
        int victimId,
        int attackerId
) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(victimId);
        buf.writeVarInt(attackerId);
    }

    public static S2CExecutionFeedback decode(FriendlyByteBuf buf) {
        var victimId = buf.readVarInt();
        var attackerId = buf.readVarInt();
        return new S2CExecutionFeedback(victimId, attackerId);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientNetworkHandler.handleExecutionFeedback(this));
        context.get().setPacketHandled(true);
    }
}
