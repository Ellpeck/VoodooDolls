package de.ellpeck.voodoodolls.curses;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.ellpeck.voodoodolls.VoodooDolls;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class CurseData extends WorldSavedData {

    private static final String NAME = VoodooDolls.ID + "_curses";
    private static CurseData clientData;

    private final Multimap<UUID, Curse> curses = ArrayListMultimap.create();
    private final World level;

    public CurseData(World level) {
        super(NAME);
        this.level = level;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void load(CompoundNBT nbt) {
        this.curses.clear();
        ListNBT list = nbt.getList("curses", Constants.NBT.TAG_LIST);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT entry = list.getCompound(i);
            UUID player = entry.getUUID("player");
            this.curses.put(player, new Curse(this.level, player, entry));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        for (Map.Entry<UUID, Curse> kv : this.curses.entries()) {
            CompoundNBT entry = kv.getValue().serializeNBT();
            entry.putUUID("player", kv.getKey());
            list.add(entry);
        }
        nbt.put("curses", list);
        return nbt;
    }

    public Collection<Curse> getCurses(UUID player) {
        return this.curses.get(player);
    }

    public static CurseData get(World level) {
        if (level.isClientSide) {
            if (clientData == null || clientData.level != level)
                clientData = new CurseData(level);
            return clientData;
        } else {
            return ((ServerWorld) level).getServer().overworld().getDataStorage().computeIfAbsent(() -> new CurseData(level), NAME);
        }
    }

}
