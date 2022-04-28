package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class TeleportToCaveEvent extends CurseEvent {

    private ForgeConfigSpec.ConfigValue<Integer> range;

    public TeleportToCaveEvent() {
        super("teleport_to_cave", Badness.WORSE, 0.05F, false);
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder config) {
        super.setupConfig(config);
        this.range = config
                .comment("The radius of the " + this.id + " event's teleportation area. This only applies to horizontal coordinates.")
                .define("range", 16);
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        if (player.level.isClientSide)
            return;
        List<BlockPos> validPositions = new ArrayList<>();
        for (int y = player.level.getSeaLevel(); y > 0; y--) {
            for (int x = -this.range.get(); x <= this.range.get(); x++) {
                for (int z = -this.range.get(); z <= this.range.get(); z++) {
                    BlockPos pos = new BlockPos(player.getX() + x, y, player.getZ() + z);
                    if (player.level.canSeeSky(pos))
                        continue;
                    BlockPos below = pos.below();
                    if (!player.level.getBlockState(below).entityCanStandOn(player.level, below, player))
                        continue;
                    AxisAlignedBB box = player.getType().getDimensions().makeBoundingBox(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (!player.level.getBlockCollisions(player, box).allMatch(VoxelShape::isEmpty))
                        continue;
                    validPositions.add(pos);
                }
            }
        }
        if (validPositions.isEmpty())
            return;

        BlockPos pos = validPositions.get(player.getRandom().nextInt(validPositions.size()));
        player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        player.level.broadcastEntityEvent(player, (byte) 46);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
        player.level.playSound(null, player.xOld, player.yOld, player.zOld, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
    }
}
