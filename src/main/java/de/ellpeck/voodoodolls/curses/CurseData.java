package de.ellpeck.voodoodolls.curses;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.ellpeck.voodoodolls.Packets;
import de.ellpeck.voodoodolls.VoodooDolls;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Collection;
import java.util.UUID;

public class CurseData extends SavedData {

    private static final String NAME = VoodooDolls.ID + "_curses";
    private static CurseData clientData;

    private final Multimap<UUID, Curse> curses = ArrayListMultimap.create();

    public CurseData() {

    }

    public CurseData(CompoundTag nbt) {
        this.load(nbt);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public void load(CompoundTag nbt) {
        this.curses.clear();
        var list = nbt.getList("curses", Tag.TAG_COMPOUND);
        for (var i = 0; i < list.size(); i++) {
            var curse = new Curse(list.getCompound(i));
            this.curses.put(curse.playerId, curse);
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        var list = new ListTag();
        for (var curse : this.curses.values())
            list.add(curse.serializeNBT());
        nbt.put("curses", list);
        return nbt;
    }

    public Collection<Curse> getCurses(UUID player) {
        return this.curses.get(player);
    }

    public Curse getCurse(UUID dollId) {
        for (var curse : this.curses.values()) {
            if (curse.sourceDoll.equals(dollId))
                return curse;
        }
        return null;
    }

    public Packets.Curses getPacket() {
        return new Packets.Curses(this.save(new CompoundTag()));
    }

    public static CurseData get(Level level) {
        if (level.isClientSide) {
            if (CurseData.clientData == null)
                CurseData.clientData = new CurseData();
            return CurseData.clientData;
        } else {
            return ((ServerLevel) level).getServer().overworld().getDataStorage().computeIfAbsent(CurseData::new, CurseData::new, CurseData.NAME);
        }
    }
}
