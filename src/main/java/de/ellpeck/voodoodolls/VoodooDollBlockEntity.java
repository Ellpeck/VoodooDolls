package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public class VoodooDollBlockEntity extends TileEntity implements INameable {

    public ITextComponent customName;
    public UUID dollId = UUID.randomUUID();

    public VoodooDollBlockEntity() {
        super(VoodooDolls.VOODOO_DOLL_BLOCK_ENTITY.get());
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("CustomName", Constants.NBT.TAG_STRING))
            this.customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
        this.dollId = nbt.getUUID("doll_id");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        if (this.customName != null)
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        nbt.putUUID("doll_id", this.dollId);
        return nbt;
    }

    @Override
    public ITextComponent getName() {
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getName();
    }

    @Override
    @Nullable
    public ITextComponent getCustomName() {
        return this.customName;
    }

    public VoodooDollBlock.Tier getTier() {
        return ((VoodooDollBlock) this.getBlockState().getBlock()).tier;
    }

    public Curse getCurse() {
        CurseData data = CurseData.get(this.level);
        for (Curse curse : data.curses.values()) {
            if (curse.sourceDoll.equals(this.dollId))
                return curse;
        }
        return null;
    }
}
