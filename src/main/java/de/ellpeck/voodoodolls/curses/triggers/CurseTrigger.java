package de.ellpeck.voodoodolls.curses.triggers;

import de.ellpeck.voodoodolls.VoodooDolls;
import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public abstract class CurseTrigger {

    public static final Map<String, CurseTrigger> TRIGGERS = new HashMap<>();

    public final String id;
    public ForgeConfigSpec.ConfigValue<Boolean> disabled;

    public CurseTrigger(String id) {
        this.id = id;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setupConfig(ForgeConfigSpec.Builder config) {
        this.disabled = config
                .comment("Whether the " + this.id + " curse trigger should be disabled.")
                .define("disabled", false);
    }

    public boolean isEnabled() {
        return !this.disabled.get();
    }

    public TranslationTextComponent getDisplayName() {
        return new TranslationTextComponent("curse_trigger." + VoodooDolls.ID + "." + this.id);
    }

    protected void trigger(PlayerEntity player) {
        if (!this.isEnabled())
            return;
        CurseData data = CurseData.get(player.level);
        for (Curse curse : data.getCurses(player.getUUID())) {
            if (curse.trigger == this)
                curse.occur();
        }
    }

    public static void register(CurseTrigger trigger) {
        if (TRIGGERS.put(trigger.id, trigger) != null)
            throw new IllegalArgumentException("A trigger with id " + trigger.id + " is already registered");
    }
}
