package de.ellpeck.voodoodolls;

import com.mojang.authlib.GameProfile;
import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoodooDollBlockEntity extends BlockEntity implements Nameable {

    private static final Map<Component, GameProfile> PROFILES = new HashMap<>();
    public Component customName;
    public UUID dollId = UUID.randomUUID();

    public VoodooDollBlockEntity(BlockPos pos, BlockState state) {
        super(VoodooDolls.VOODOO_DOLL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("CustomName", Tag.TAG_STRING))
            this.setCustomName(Component.Serializer.fromJson(nbt.getString("CustomName")), false);
        this.dollId = nbt.getUUID("doll_id");
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.customName != null)
            nbt.putString("CustomName", Component.Serializer.toJson(this.customName));
        nbt.putUUID("doll_id", this.dollId);
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public VoodooDollBlock.Tier getTier() {
        return ((VoodooDollBlock) this.getBlockState().getBlock()).tier;
    }

    public Curse getCurse() {
        return CurseData.get(this.level).getCurse(this.dollId);
    }

    public void setCustomName(Component name, boolean updateCurse) {
        this.customName = name;
        if (updateCurse) {
            var curse = this.getCurse();
            if (curse != null)
                curse.dollName = this.getName().getString();
        }
    }

    public static GameProfile getProfile(Component name) {
        if (name == null)
            return null;
        var profile = PROFILES.get(name);
        if (profile == null) {
            profile = new GameProfile(null, name.getString());
            // we put it in before it's updated so that we don't enqueue the update multiple times
            PROFILES.put(name, profile);
            SkullBlockEntity.updateGameprofile(profile, p -> PROFILES.put(name, p));
        }
        return profile;
    }
}
