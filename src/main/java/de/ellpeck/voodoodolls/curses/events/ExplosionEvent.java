package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExplosionEvent extends CurseEvent {

    private ForgeConfigSpec.ConfigValue<List<? extends String>> allowedTags;
    private ForgeConfigSpec.ConfigValue<Double> explosionStrength;

    public ExplosionEvent() {
        super("explosion", Badness.WORST, 0.05F, false);
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder config) {
        super.setupConfig(config);
        var defaultTags = new ResourceLocation[]{
                BlockTags.SAND.location(),
                BlockTags.DIRT.location(),
                Tags.Blocks.STONE.location(),
                Tags.Blocks.COBBLESTONE.location(),
                Tags.Blocks.SANDSTONE.location(),
                BlockTags.LOGS.location(),
                BlockTags.PLANKS.location()};
        this.allowedTags = config
                .comment("The set of tags for blocks that the " + this.id + " event should destroy.")
                .defineList("allowed_tags", Arrays.stream(defaultTags).map(ResourceLocation::toString).collect(Collectors.toList()), e -> true);
        this.explosionStrength = config
                .comment("The strength of the " + this.id + " curse event's explosion.")
                .define("strength", 4D);
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide)
            return;
        var strength = this.explosionStrength.get().floatValue();
        var explosion = new Explosion(player.level, null, null, null, player.getX(), player.getY(), player.getZ(), strength, true, Explosion.BlockInteraction.BREAK);
        if (ForgeEventFactory.onExplosionStart(player.level, explosion))
            return;
        explosion.explode();
        explosion.getToBlow().removeIf(p -> player.level.getBlockState(p).getTags().noneMatch(r -> this.allowedTags.get().contains(r.toString())));
        explosion.finalizeExplosion(true);

        // copied from ServerWorld.explode, which we can't use because we need to edit toBlow
        for (Player other : player.level.players()) {
            if (other.distanceToSqr(player) < 4096 && other instanceof ServerPlayer server)
                server.connection.send(new ClientboundExplodePacket(player.getX(), player.getY(), player.getZ(), strength, explosion.getToBlow(), explosion.getHitPlayers().get(other)));
        }
    }

}
