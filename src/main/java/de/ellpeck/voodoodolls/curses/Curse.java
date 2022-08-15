package de.ellpeck.voodoodolls.curses;

import de.ellpeck.voodoodolls.Packets;
import de.ellpeck.voodoodolls.VoodooDollBlockEntity;
import de.ellpeck.voodoodolls.VoodooDolls;
import de.ellpeck.voodoodolls.curses.events.CurseEvent;
import de.ellpeck.voodoodolls.curses.triggers.CurseTrigger;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Curse implements INBTSerializable<CompoundTag> {

    public UUID sourceDoll;
    public String dollName;
    public CurseTrigger trigger;
    public CurseEvent event;
    public UUID playerId;
    public String playerName;
    public boolean isInactive;

    public Curse(UUID sourceDoll, String dollName, UUID playerId, String playerName, CurseTrigger trigger, CurseEvent event) {
        this.sourceDoll = sourceDoll;
        this.dollName = dollName;
        this.playerId = playerId;
        this.playerName = playerName;
        this.trigger = trigger;
        this.event = event;
    }

    public Curse(CompoundTag nbt) {
        this.deserializeNBT(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        nbt.putString("trigger", this.trigger.id);
        nbt.putString("event", this.event.id);
        nbt.putUUID("source", this.sourceDoll);
        nbt.putString("doll_name", this.dollName);
        nbt.putUUID("player", this.playerId);
        nbt.putString("player_name", this.playerName);
        nbt.putBoolean("inactive", this.isInactive);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.trigger = CurseTrigger.TRIGGERS.get(nbt.getString("trigger"));
        this.event = CurseEvent.EVENTS.get(nbt.getString("event"));
        this.sourceDoll = nbt.getUUID("source");
        this.dollName = nbt.getString("doll_name");
        this.playerId = nbt.getUUID("player");
        this.playerName = nbt.getString("player_name");
        this.isInactive = nbt.getBoolean("inactive");
    }

    public void occurRandomly(Level level) {
        if (this.isInactive || !this.event.isEnabled())
            return;
        var player = this.getPlayer(level);
        if (player != null && player.getRandom().nextFloat() <= this.event.chance.get())
            this.forceOccur(level);
    }

    public void forceOccur(Level level) {
        var player = this.getPlayer(level);
        if (player != null) {
            this.event.occur(player, this);
            if (!player.level.isClientSide) {
                Packets.sendTo(player, new Packets.CurseOccurs(this.sourceDoll));
                player.displayClientMessage(new TranslatableComponent("info." + VoodooDolls.ID + ".curse_triggered", this.dollName, this.event.getDisplayName()).withStyle(ChatFormatting.RED), false);
            }
        }
    }

    public Player getPlayer(Level level) {
        return level.getPlayerByUUID(this.playerId);
    }

    public TranslatableComponent getDisplayName() {
        return new TranslatableComponent("info." + VoodooDolls.ID + ".curse", this.event.getDisplayName(), this.trigger.getDisplayName());
    }

    public static Curse create(Player player, VoodooDollBlockEntity source) {
        var triggers = CurseTrigger.TRIGGERS.values().stream()
                .filter(CurseTrigger::isEnabled)
                .toArray(CurseTrigger[]::new);
        if (triggers.length <= 0)
            return null;
        var events = CurseEvent.EVENTS.values().stream()
                .filter(CurseEvent::isEnabled)
                .filter(e -> source.getTier().isBadnessAllowed.apply(e.badness.get()))
                .toArray(CurseEvent[]::new);
        if (events.length <= 0)
            return null;

        var event = events[player.getRandom().nextInt(events.length)];
        var trigger = triggers[player.getRandom().nextInt(triggers.length)];
        return new Curse(source.dollId, source.getName().getString(), player.getUUID(), player.getGameProfile().getName(), trigger, event);
    }
}
