package de.ellpeck.voodoodolls.curses;

import de.ellpeck.voodoodolls.VoodooDollBlockEntity;
import de.ellpeck.voodoodolls.VoodooDolls;
import de.ellpeck.voodoodolls.curses.events.CurseEvent;
import de.ellpeck.voodoodolls.curses.triggers.CurseTrigger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Curse implements INBTSerializable<CompoundNBT> {

    public UUID sourceDoll;
    public CurseTrigger trigger;
    public CurseEvent event;
    public UUID playerId;

    private final World level;

    public Curse(World level, UUID sourceDoll, UUID playerId, CurseTrigger trigger, CurseEvent event) {
        this.level = level;
        this.sourceDoll = sourceDoll;
        this.playerId = playerId;
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
        nbt.putUUID("player", this.playerId);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.trigger = CurseTrigger.TRIGGERS.get(nbt.getString("trigger"));
        this.event = CurseEvent.EVENTS.get(nbt.getString("event"));
        this.sourceDoll = nbt.getUUID("source");
        this.playerId = nbt.getUUID("player");
    }

    public void occur() {
        PlayerEntity player = this.level.getPlayerByUUID(this.playerId);
        if (player != null) {
            if (!this.event.disabled.get())
                this.event.occur(player, this);
        }
    }

    public TranslationTextComponent getDisplayName() {
        return new TranslationTextComponent("info." + VoodooDolls.ID + ".curse", this.event.getDisplayName(), this.trigger.getDisplayName());
    }

    public static Curse create(PlayerEntity player, VoodooDollBlockEntity source) {
        CurseTrigger[] triggers = CurseTrigger.TRIGGERS.values().toArray(new CurseTrigger[0]);
        if (triggers.length <= 0)
            return null;
        CurseEvent[] events = CurseEvent.EVENTS.values().stream().filter(e -> source.getTier().isBadnessAllowed.apply(e.badness.get())).toArray(CurseEvent[]::new);
        if (events.length <= 0)
            return null;

        CurseEvent event = events[player.getRandom().nextInt(events.length)];
        CurseTrigger trigger = triggers[player.getRandom().nextInt(triggers.length)];
        return new Curse(player.level, source.id, player.getUUID(), trigger, event);
    }
}
