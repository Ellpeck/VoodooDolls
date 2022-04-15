package de.ellpeck.voodoodolls.curses.triggers;

import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class CurseTrigger {

    public static final Map<String, CurseTrigger> TRIGGERS = new HashMap<>();

    public final String id;

    public CurseTrigger(String id) {
        this.id = id;
    }

    protected void trigger(PlayerEntity player) {
        CurseData data = CurseData.get(player.level);
        for (Curse curse : data.getCurses(player.getUUID())) {
            // TODO add random chance here
            if (curse.trigger == this)
                curse.occur();
        }
    }

    public static void register(CurseTrigger trigger) {
        TRIGGERS.put(trigger.id, trigger);
    }
}
