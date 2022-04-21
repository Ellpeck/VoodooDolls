package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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

public class Packets {

    private static final String VERSION = "1.0";
    private static SimpleChannel network;

    public static void setup(FMLCommonSetupEvent ignoredEvent) {
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(VoodooDolls.ID, "network"), () -> VERSION, VERSION::equals, VERSION::equals);
        network.registerMessage(0, VoodooDollName.class, VoodooDollName::toBytes, VoodooDollName::fromBytes, VoodooDollName::onMessage);
        network.registerMessage(1, Curses.class, Curses::toBytes, Curses::fromBytes, Curses::onMessage);
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

    public static class VoodooDollName {

        private final BlockPos pos;
        private final String name;

        public VoodooDollName(BlockPos pos, String name) {
            this.pos = pos;
            this.name = name;
        }

        public static VoodooDollName fromBytes(PacketBuffer buf) {
            return new VoodooDollName(buf.readBlockPos(), buf.readUtf());
        }

        public static void toBytes(VoodooDollName packet, PacketBuffer buf) {
            buf.writeBlockPos(packet.pos);
            buf.writeUtf(packet.name);
        }

        @SuppressWarnings("Convert2Lambda")
        public static void onMessage(VoodooDollName message, Supplier<NetworkEvent.Context> ctx) {
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

    public static class Curses {

        private final CompoundNBT data;

        public Curses(CompoundNBT data) {
            this.data = data;
        }

        public static Curses fromBytes(PacketBuffer buf) {
            return new Curses(buf.readNbt());
        }

        public static void toBytes(Curses packet, PacketBuffer buf) {
            buf.writeNbt(packet.data);
        }

        @SuppressWarnings("Convert2Lambda")
        public static void onMessage(Curses message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    CurseData data = CurseData.get(Minecraft.getInstance().level);
                    data.deserializeNBT(message.data);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
