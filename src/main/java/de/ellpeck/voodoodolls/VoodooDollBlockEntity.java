package de.ellpeck.voodoodolls;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class VoodooDollBlockEntity extends TileEntity implements ITickableTileEntity, INameable {

    public ITextComponent customName;

    public VoodooDollBlockEntity() {
        super(VoodooDolls.VOODOO_DOLL_BLOCK_ENTITY.get());
    }

    @Override
    public void tick() {

    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("CustomName", Constants.NBT.TAG_STRING))
            this.customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        if (this.customName != null)
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
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
}
