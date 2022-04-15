package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class CurseEvent {

    public static final Map<String, CurseEvent> EVENTS = new HashMap<>();

    public final String id;

    public CurseEvent(String id) {
        this.id = id;
    }

    public abstract void occur(PlayerEntity player, Curse curse);

    public static void register(CurseEvent event) {
        EVENTS.put(event.id, event);
    }
}
