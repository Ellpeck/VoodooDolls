package de.ellpeck.voodoodolls.curses;

import de.ellpeck.voodoodolls.Packets;
import de.ellpeck.voodoodolls.VoodooDollBlockEntity;
import de.ellpeck.voodoodolls.VoodooDolls;
import de.ellpeck.voodoodolls.curses.events.CurseEvent;
import de.ellpeck.voodoodolls.curses.triggers.CurseTrigger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Curse implements INBTSerializable<CompoundNBT> {

    public UUID sourceDoll;
    public String dollName;
    public CurseTrigger trigger;
    public CurseEvent event;
    public UUID playerId;
    public String playerName;
    public boolean isInactive;

    private final World level;

    public Curse(World level, UUID sourceDoll, String dollName, UUID playerId, String playerName, CurseTrigger trigger, CurseEvent event) {
        this.level = level;
        this.sourceDoll = sourceDoll;
        this.dollName = dollName;
        this.playerId = playerId;
        this.playerName = playerName;
        this.trigger = trigger;
        this.event = event;
    }

    public Curse(World level, CompoundNBT nbt) {
        this.level = level;
        this.deserializeNBT(nbt);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
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
    public void deserializeNBT(CompoundNBT nbt) {
        this.trigger = CurseTrigger.TRIGGERS.get(nbt.getString("trigger"));
        this.event = CurseEvent.EVENTS.get(nbt.getString("event"));
        this.sourceDoll = nbt.getUUID("source");
        this.dollName = nbt.getString("doll_name");
        this.playerId = nbt.getUUID("player");
        this.playerName = nbt.getString("player_name");
        this.isInactive = nbt.getBoolean("inactive");
    }

    public void occurRandomly() {
        if (this.isInactive || !this.event.isEnabled())
            return;
        PlayerEntity player = this.getPlayer();
        if (player != null && player.getRandom().nextFloat() <= this.event.chance.get())
            this.forceOccur();
    }

    public void forceOccur() {
        PlayerEntity player = this.getPlayer();
        if (player != null) {
            this.event.occur(player, this);
            if (!player.level.isClientSide) {
                Packets.sendTo(player, new Packets.CurseOccurs(this.sourceDoll));
                player.displayClientMessage(new TranslationTextComponent("info." + VoodooDolls.ID + ".curse_triggered", this.dollName, this.event.getDisplayName()).withStyle(TextFormatting.RED), false);
            }
        }
    }

    public PlayerEntity getPlayer() {
        return this.level.getPlayerByUUID(this.playerId);
    }

    public TranslationTextComponent getDisplayName() {
        return new TranslationTextComponent("info." + VoodooDolls.ID + ".curse", this.event.getDisplayName(), this.trigger.getDisplayName());
    }

    public static Curse create(PlayerEntity player, VoodooDollBlockEntity source) {
        CurseTrigger[] triggers = CurseTrigger.TRIGGERS.values().stream()
                .filter(CurseTrigger::isEnabled)
                .toArray(CurseTrigger[]::new);
        if (triggers.length <= 0)
            return null;
        CurseEvent[] events = CurseEvent.EVENTS.values().stream()
                .filter(CurseEvent::isEnabled)
                .filter(e -> source.getTier().isBadnessAllowed.apply(e.badness.get()))
                .toArray(CurseEvent[]::new);
        if (events.length <= 0)
            return null;

        CurseEvent event = events[player.getRandom().nextInt(events.length)];
        CurseTrigger trigger = triggers[player.getRandom().nextInt(triggers.length)];
        return new Curse(player.level, source.dollId, source.getName().getString(), player.getUUID(), player.getGameProfile().getName(), trigger, event);
    }
}
