package mod.chloeprime.apotheosismodernragnarok.network;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class ModNetwork {
    public static final String VERSION = "1.0.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ApotheosisModernRagnarok.loc("play_channel"),
            () -> VERSION, VERSION::equals, VERSION::equals
    );

    public static void sendToNearby(Object message, Entity center) {
        sendToNearby(message, () -> center);
    }

    public static void sendToNearby(Object message, Supplier<Entity> center) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(center), message);
    }

    private static final AtomicInteger ID_COUNT = new AtomicInteger(1);

    public static void init() {
        var playToClient = Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        CHANNEL.registerMessage(ID_COUNT.getAndIncrement(), S2CPerfectBlockTriggered.class, S2CPerfectBlockTriggered::encode, S2CPerfectBlockTriggered::decode, S2CPerfectBlockTriggered::handle, playToClient);
        CHANNEL.registerMessage(ID_COUNT.getAndIncrement(), S2CSyncStartRecoverPostureTime.class, S2CSyncStartRecoverPostureTime::encode, S2CSyncStartRecoverPostureTime::decode, S2CSyncStartRecoverPostureTime::handle, playToClient);
        CHANNEL.registerMessage(ID_COUNT.getAndIncrement(), S2CExecutionFeedback.class, S2CExecutionFeedback::encode, S2CExecutionFeedback::decode, S2CExecutionFeedback::handle, playToClient);
    }

    private ModNetwork() {}
}
