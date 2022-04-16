package de.ellpeck.voodoodolls.curses;

import de.ellpeck.voodoodolls.curses.triggers.CurseTrigger;
import de.ellpeck.voodoodolls.curses.events.CurseEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Curse implements INBTSerializable<CompoundNBT> {

    public CurseTrigger trigger;
    public CurseEvent event;

    private final World level;
    private final UUID playerId;

    public Curse(World level, UUID playerId, CurseTrigger trigger, CurseEvent event) {
        this.level = level;
        this.playerId = playerId;
        this.trigger = trigger;
        this.event = event;
    }

    public Curse(World level, UUID playerId, CompoundNBT nbt) {
        this(level, playerId, null, null);
        this.deserializeNBT(nbt);
    }

    public void occur() {
        PlayerEntity player = this.level.getPlayerByUUID(this.playerId);
        if (player != null) {
            if (!this.event.disabled.get())
                this.event.occur(player, this);
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("trigger", this.trigger.id);
        tag.putString("event", this.event.id);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.trigger = CurseTrigger.TRIGGERS.get(nbt.getString("trigger"));
        this.event = CurseEvent.EVENTS.get(nbt.getString("event"));
    }
}
