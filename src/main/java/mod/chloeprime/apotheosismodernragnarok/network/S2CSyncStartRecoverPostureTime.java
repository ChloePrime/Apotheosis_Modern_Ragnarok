package mod.chloeprime.apotheosismodernragnarok.network;

import mod.chloeprime.apotheosismodernragnarok.client.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CSyncStartRecoverPostureTime(
        int id,
        long eta
) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(id);
        buf.writeVarLong(eta);
    }

    public static S2CSyncStartRecoverPostureTime decode(FriendlyByteBuf buf) {
        var id = buf.readVarInt();
        var eta = buf.readVarLong();
        return new S2CSyncStartRecoverPostureTime(id, eta);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientNetworkHandler.handleSyncStartRecoverPostureTime(this));
        context.get().setPacketHandled(true);
    }
}
