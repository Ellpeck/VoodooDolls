package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfigSpec;

public class TeleportRandomlyEvent extends CurseEvent {

    private ForgeConfigSpec.ConfigValue<Integer> distance;

    public TeleportRandomlyEvent() {
        super("teleport_randomly", Badness.WORST, 0.05F, false);
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide)
            return;
        var angle = player.getRandom().nextFloat() * (float) Math.PI * 2;
        var goalPos = player.blockPosition().offset(Math.cos(angle) * this.distance.get(), 0, Math.sin(angle) * this.distance.get());

        var checkRadius = 16;
        for (var x = -checkRadius; x <= checkRadius; x++) {
            for (var z = -checkRadius; z <= checkRadius; z++) {
                var offset = goalPos.offset(x, 0, z);
                // load the chunk at the location because getHeightmapPos doesn't force-load it
                player.level.getChunk(offset);
                var pos = player.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, offset);
                var below = pos.below();
                if (!player.level.getBlockState(below).entityCanStandOn(player.level, below, player))
                    continue;
                var box = player.getType().getDimensions().makeBoundingBox(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                for (var collision : player.level.getBlockCollisions(player, box)) {
                    if (!collision.isEmpty())
                        continue;
                }
                CurseEvent.teleportFancy(player, pos);
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
