package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import de.ellpeck.voodoodolls.curses.events.CurseEvent;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Function;

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
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity instanceof VoodooDollBlockEntity) {
            VoodooDollBlockEntity doll = (VoodooDollBlockEntity) entity;

            if (stack.hasCustomHoverName())
                doll.customName = stack.getHoverName();

            if (!placer.level.isClientSide && placer instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) placer;
                Curse curse = Curse.create(player, doll);
                if (curse != null) {
                    CurseData data = CurseData.get(world);
                    data.getCurses(placer.getUUID()).add(curse);
                    player.displayClientMessage(new TranslationTextComponent("info." + VoodooDolls.ID + ".cursed", doll.getDisplayName(), curse.getDisplayName()).withStyle(TextFormatting.RED), false);
                }
            }
        }

        if (!world.isClientSide) {
            LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
            lightning.moveTo(pos.getX(), pos.getY(), pos.getZ());
            lightning.setVisualOnly(true);
            world.addFreshEntity(lightning);
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean bool) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity instanceof VoodooDollBlockEntity) {
            VoodooDollBlockEntity doll = (VoodooDollBlockEntity) entity;
            Curse curse = doll.getCurse();
            if (curse != null) {
                CurseData data = CurseData.get(world);
                data.curses.remove(curse.playerId, curse);

                PlayerEntity player = world.getPlayerByUUID(curse.playerId);
                if (player != null)
                    player.displayClientMessage(new TranslationTextComponent("info." + VoodooDolls.ID + ".curse_removed", doll.getDisplayName(), curse.getDisplayName()).withStyle(TextFormatting.GREEN), false);
            }
        }
        super.onRemove(state, world, pos, newState, bool);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) {
            TileEntity entity = world.getBlockEntity(pos);
            if (entity instanceof VoodooDollBlockEntity) {
                Minecraft.getInstance().setScreen(new VoodooDollScreen((VoodooDollBlockEntity) entity));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HorizontalBlock.FACING);
    }

    public enum Tier {

        ONE(b -> b.allowedInTierOne),
        TWO(b -> b.allowedInTierTwo),
        THREE(b -> b.allowedInTierThree);

        public final Function<CurseEvent.Badness, Boolean> isBadnessAllowed;

        Tier(Function<CurseEvent.Badness, Boolean> isBadnessAllowed) {
            this.isBadnessAllowed = isBadnessAllowed;
        }
    }
}
