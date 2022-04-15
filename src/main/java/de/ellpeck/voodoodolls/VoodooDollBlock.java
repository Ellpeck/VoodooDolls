package de.ellpeck.voodoodolls;

import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class VoodooDollBlock extends ContainerBlock {

    public final Tier tier;

    protected VoodooDollBlock(Tier tier) {
        super(Properties.copy(Blocks.WHITE_WOOL).strength(1.5F));
        this.tier = tier;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader level) {
        return new VoodooDollBlockEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.defaultBlockState().setValue(HorizontalBlock.FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(HorizontalBlock.FACING, rot.rotate(state.getValue(HorizontalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(HorizontalBlock.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HorizontalBlock.FACING);
    }

    public enum Tier {

        ONE,
        TWO,
        THREE

    }
}
