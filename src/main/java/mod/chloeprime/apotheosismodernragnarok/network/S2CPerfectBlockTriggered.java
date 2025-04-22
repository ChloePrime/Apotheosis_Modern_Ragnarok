package mod.chloeprime.apotheosismodernragnarok.network;

import mod.chloeprime.apotheosismodernragnarok.client.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CPerfectBlockTriggered(
        int id
) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(id);
    }

    public static S2CPerfectBlockTriggered decode(FriendlyByteBuf buf) {
        var id = buf.readVarInt();
        return new S2CPerfectBlockTriggered(id);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientNetworkHandler.handlePerfectBlockTriggered(this));
        context.get().setPacketHandled(true);
    }
}
