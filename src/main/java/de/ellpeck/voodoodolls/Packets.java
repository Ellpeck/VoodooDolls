package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.UUID;
import java.util.function.Supplier;

public class Packets {

    private static final String VERSION = "1.0";
    private static SimpleChannel network;

    public static void setup(FMLCommonSetupEvent ignoredEvent) {
        Packets.network = NetworkRegistry.newSimpleChannel(new ResourceLocation(VoodooDolls.ID, "network"), () -> Packets.VERSION, Packets.VERSION::equals, Packets.VERSION::equals);
        Packets.network.registerMessage(0, VoodooDollName.class, VoodooDollName::toBytes, VoodooDollName::fromBytes, VoodooDollName::onMessage);
        Packets.network.registerMessage(1, Curses.class, Curses::toBytes, Curses::fromBytes, Curses::onMessage);
        Packets.network.registerMessage(2, CurseOccurs.class, CurseOccurs::toBytes, CurseOccurs::fromBytes, CurseOccurs::onMessage);
    }

    public static void sendToAll(Object message) {
        Packets.network.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendTo(Player player, Object message) {
        Packets.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }

    public static void sendToServer(Object message) {
        Packets.network.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static class VoodooDollName {

        private final BlockPos pos;
        private final String name;

        public VoodooDollName(BlockPos pos, String name) {
            this.pos = pos;
            this.name = name;
        }

        public static VoodooDollName fromBytes(FriendlyByteBuf buf) {
            return new VoodooDollName(buf.readBlockPos(), buf.readUtf());
        }

        public static void toBytes(VoodooDollName packet, FriendlyByteBuf buf) {
            buf.writeBlockPos(packet.pos);
            buf.writeUtf(packet.name);
        }

        @SuppressWarnings("Convert2Lambda")
        public static void onMessage(VoodooDollName message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    Player player = ctx.get().getSender();
                    var entity = player.level.getBlockEntity(message.pos);
                    if (entity instanceof VoodooDollBlockEntity) {
                        ((VoodooDollBlockEntity) entity).setCustomName(new TextComponent(message.name), true);
                        entity.getLevel().sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 3);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static class Curses {

        private final CompoundTag data;

        public Curses(CompoundTag data) {
            this.data = data;
        }

        public static Curses fromBytes(FriendlyByteBuf buf) {
            return new Curses(buf.readNbt());
        }

        public static void toBytes(Curses packet, FriendlyByteBuf buf) {
            buf.writeNbt(packet.data);
        }

        @SuppressWarnings("Convert2Lambda")
        public static void onMessage(Curses message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    var data = CurseData.get(Minecraft.getInstance().level);
                    data.load(message.data);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static class CurseOccurs {

        private final UUID sourceDoll;

        public CurseOccurs(UUID sourceDoll) {
            this.sourceDoll = sourceDoll;
        }

        public static CurseOccurs fromBytes(FriendlyByteBuf buf) {
            return new CurseOccurs(buf.readUUID());
        }

        public static void toBytes(CurseOccurs packet, FriendlyByteBuf buf) {
            buf.writeUUID(packet.sourceDoll);
        }

        @SuppressWarnings("Convert2Lambda")
        public static void onMessage(CurseOccurs message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    var mc = Minecraft.getInstance();
                    var data = CurseData.get(mc.level);
                    var curse = data.getCurse(message.sourceDoll);
                    if (curse != null)
                        curse.forceOccur(mc.level);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
