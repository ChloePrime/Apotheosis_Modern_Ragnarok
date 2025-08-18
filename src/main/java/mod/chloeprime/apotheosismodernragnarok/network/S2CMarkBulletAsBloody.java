package mod.chloeprime.apotheosismodernragnarok.network;

import mod.chloeprime.apotheosismodernragnarok.client.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CMarkBulletAsBloody(
        int bulletId
) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(bulletId);
    }

    public static S2CMarkBulletAsBloody decode(FriendlyByteBuf buf) {
        var bulletId = buf.readVarInt();
        return new S2CMarkBulletAsBloody(bulletId);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientNetworkHandler.handleMarkBulletAsBloody(this));
        context.get().setPacketHandled(true);
    }
}
