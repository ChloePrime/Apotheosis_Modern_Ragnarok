package mod.chloeprime.apotheosismodernragnarok.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.function.Supplier;

@EventBusSubscriber
public final class ModNetwork {
    public static final String VERSION = String.valueOf(1);

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(VERSION);
        registrar.playToClient(S2CExecutionFeedback.TYPE, S2CExecutionFeedback.STREAM_CODEC, S2CExecutionFeedback::handle);
        registrar.playToClient(S2CPerfectBlockTriggered.TYPE, S2CPerfectBlockTriggered.STREAM_CODEC, S2CPerfectBlockTriggered::handle);
    }

    public static void sendToNearby(CustomPacketPayload message, Entity center) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(center, message);
    }

    public static void sendToNearby(CustomPacketPayload message, Supplier<Entity> center) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(center.get(), message);
    }

    private ModNetwork() {}
}
