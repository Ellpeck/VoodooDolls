package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;

public class TeleportRandomlyEvent extends CurseEvent {

    private ForgeConfigSpec.ConfigValue<Integer> distance;

    public TeleportRandomlyEvent() {
        super("teleport_randomly", Badness.WORST, 0.05F, false);
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        if (player.level.isClientSide)
            return;
        float angle = player.getRandom().nextFloat() * (float) Math.PI * 2;
        BlockPos goalPos = player.blockPosition().offset(Math.cos(angle) * this.distance.get(), 0, Math.sin(angle) * this.distance.get());

        int checkRadius = 16;
        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int z = -checkRadius; z <= checkRadius; z++) {
                BlockPos offset = goalPos.offset(x, 0, z);
                // load the chunk at the location because getHeightmapPos doesn't force-load it
                player.level.getChunk(offset);
                BlockPos pos = player.level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, offset);
                BlockPos below = pos.below();
                if (!player.level.getBlockState(below).entityCanStandOn(player.level, below, player))
                    continue;
                AxisAlignedBB box = player.getType().getDimensions().makeBoundingBox(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                if (!player.level.getBlockCollisions(player, box).allMatch(VoxelShape::isEmpty))
                    continue;
                teleportFancy(player, pos);
                return;
            }
        }
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder config) {
        super.setupConfig(config);
        this.distance = config
                .comment("The distance of the " + this.id + " curse event's teleportation.")
                .define("distance", 1000);
    }
}
