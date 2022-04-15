package de.ellpeck.voodoodolls;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class VoodooDollBlockEntity extends TileEntity implements ITickableTileEntity {

    public VoodooDollBlockEntity() {
        super(VoodooDolls.VOODOO_DOLL_BLOCK_ENTITY.get());
    }

    @Override
    public void tick() {

    }
}
