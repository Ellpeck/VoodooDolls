package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
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
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide)
            return;
        List<BlockPos> validPositions = new ArrayList<>();
        for (var y = player.level.getSeaLevel(); y > 0; y--) {
            for (var x = -this.range.get(); x <= this.range.get(); x++) {
                z:
                for (var z = -this.range.get(); z <= this.range.get(); z++) {
                    var pos = new BlockPos(player.getX() + x, y, player.getZ() + z);
                    if (player.level.canSeeSky(pos))
                        continue;
                    var below = pos.below();
                    if (!player.level.getBlockState(below).entityCanStandOn(player.level, below, player))
                        continue;
                    var box = player.getType().getDimensions().makeBoundingBox(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    for (var collision : player.level.getBlockCollisions(player, box)) {
                        if (!collision.isEmpty())
                            continue z;
                    }
                    validPositions.add(pos);
                }
            }
        }
        if (validPositions.isEmpty())
            return;
        CurseEvent.teleportFancy(player, validPositions.get(player.getRandom().nextInt(validPositions.size())));
    }
}
