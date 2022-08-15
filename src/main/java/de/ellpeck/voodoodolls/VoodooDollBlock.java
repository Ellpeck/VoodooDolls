package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import de.ellpeck.voodoodolls.curses.events.CurseEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class VoodooDollBlock extends BaseEntityBlock {

    public final Tier tier;

    protected VoodooDollBlock(Tier tier) {
        super(Properties.copy(Blocks.WHITE_WOOL).strength(1.5F).noOcclusion());
        this.tier = tier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new VoodooDollBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(HorizontalDirectionalBlock.FACING, rot.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        var entity = world.getBlockEntity(pos);
        if (entity instanceof VoodooDollBlockEntity doll) {
            if (stack.hasCustomHoverName())
                doll.setCustomName(stack.getHoverName(), true);

            if (!placer.level.isClientSide && placer instanceof Player player) {
                var data = CurseData.get(world);
                Curse curse = null;
                // get curse from saved doll id if it exists
                if (stack.hasTag()) {
                    var tag = stack.getTag();
                    if (tag.hasUUID("doll_id")) {
                        doll.dollId = tag.getUUID("doll_id");
                        curse = data.getCurse(doll.dollId);
                        curse.isInactive = false;
                    }
                }
                // otherwise, create a new curse
                if (curse == null) {
                    curse = Curse.create(player, doll);
                    data.getCurses(placer.getUUID()).add(curse);
                }
                if (curse != null) {
                    Packets.sendToAll(data.getPacket());
                    player.displayClientMessage(new TranslatableComponent("info." + VoodooDolls.ID + ".cursed", doll.getDisplayName(), curse.getDisplayName()).withStyle(ChatFormatting.RED), false);
                }
            }
        }

        if (!world.isClientSide) {
            var lightning = EntityType.LIGHTNING_BOLT.create(world);
            lightning.moveTo(pos.getX(), pos.getY(), pos.getZ());
            lightning.setVisualOnly(true);
            world.addFreshEntity(lightning);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean bool) {
        var entity = world.getBlockEntity(pos);
        if (entity instanceof VoodooDollBlockEntity doll) {
            var curse = doll.getCurse();
            if (curse != null) {
                curse.isInactive = true;
                Packets.sendToAll(CurseData.get(world).getPacket());

                var player = curse.getPlayer(world);
                if (player != null)
                    player.displayClientMessage(new TranslatableComponent("info." + VoodooDolls.ID + ".curse_removed", doll.getDisplayName(), curse.getDisplayName()).withStyle(ChatFormatting.GREEN), false);
            }
        }
        super.onRemove(state, world, pos, newState, bool);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            var entity = world.getBlockEntity(pos);
            if (entity instanceof VoodooDollBlockEntity voodoo) {
                Minecraft.getInstance().setScreen(new VoodooDollScreen(voodoo));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return this.tier.shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING);

    }

    public enum Tier {

        ONE(b -> b.allowedInTierOne, Block.box(1, 0, 1, 15, 12, 15)),
        TWO(b -> b.allowedInTierTwo, Block.box(1, 0, 1, 15, 14, 15)),
        THREE(b -> b.allowedInTierThree, Block.box(0, 0, 0, 16, 15, 16));

        public final Function<CurseEvent.Badness, Boolean> isBadnessAllowed;
        public final VoxelShape shape;

        Tier(Function<CurseEvent.Badness, Boolean> isBadnessAllowed, VoxelShape shape) {
            this.isBadnessAllowed = isBadnessAllowed;
            this.shape = shape;
        }
    }
}
