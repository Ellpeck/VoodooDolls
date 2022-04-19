package de.ellpeck.voodoodolls;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class Network {

    private static final String VERSION = "1.0";
    private static SimpleChannel network;

    public static void setup(FMLCommonSetupEvent ignoredEvent) {
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(VoodooDolls.ID, "network"), () -> VERSION, VERSION::equals, VERSION::equals);
        network.registerMessage(0, ChangeVoodooDollName.class, ChangeVoodooDollName::toBytes, ChangeVoodooDollName::fromBytes, ChangeVoodooDollName::onMessage);
    }

    public static void sendToAll(Object message) {
        network.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendTo(PlayerEntity player, Object message) {
        network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }

    public static void sendToServer(Object message) {
        network.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static class ChangeVoodooDollName {

        private final BlockPos pos;
        private final String name;

        public ChangeVoodooDollName(BlockPos pos, String name) {
            this.pos = pos;
            this.name = name;
        }

        public static ChangeVoodooDollName fromBytes(PacketBuffer buf) {
            return new ChangeVoodooDollName(buf.readBlockPos(), buf.readUtf());
        }

        public static void toBytes(ChangeVoodooDollName packet, PacketBuffer buf) {
            buf.writeBlockPos(packet.pos);
            buf.writeUtf(packet.name);
        }

        @SuppressWarnings("Convert2Lambda")
        public static void onMessage(ChangeVoodooDollName message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    PlayerEntity player = ctx.get().getSender();
                    TileEntity entity = player.level.getBlockEntity(message.pos);
                    if (entity instanceof VoodooDollBlockEntity) {
                        ((VoodooDollBlockEntity) entity).customName = new StringTextComponent(message.name);
                        entity.getLevel().sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 3);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
