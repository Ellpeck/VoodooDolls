package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
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
        ResourceLocation[] defaultTags = new ResourceLocation[]{
                BlockTags.SAND.getName(),
                Tags.Blocks.DIRT.getName(),
                Tags.Blocks.STONE.getName(),
                Tags.Blocks.COBBLESTONE.getName(),
                Tags.Blocks.SANDSTONE.getName(),
                BlockTags.LOGS.getName(),
                BlockTags.PLANKS.getName()};
        this.allowedTags = config
                .comment("The set of tags for blocks that the " + this.id + " event should destroy.")
                .defineList("allowed_tags", Arrays.stream(defaultTags).map(ResourceLocation::toString).collect(Collectors.toList()), e -> true);
        this.explosionStrength = config
                .comment("The strength of the " + this.id + " curse event's explosion.")
                .define("strength", 4D);
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        if (player.level.isClientSide)
            return;
        float strength = this.explosionStrength.get().floatValue();
        Explosion explosion = new Explosion(player.level, null, null, null, player.getX(), player.getY(), player.getZ(), strength, true, Explosion.Mode.BREAK);
        if (ForgeEventFactory.onExplosionStart(player.level, explosion))
            return;
        explosion.explode();
        explosion.getToBlow().removeIf(p -> player.level.getBlockState(p).getBlock().getTags().stream().noneMatch(r -> this.allowedTags.get().contains(r.toString())));
        explosion.finalizeExplosion(true);

        // copied from ServerWorld.explode, which we can't use because we need to edit toBlow
        for (PlayerEntity other : player.level.players()) {
            if (other.distanceToSqr(player) < 4096 && other instanceof ServerPlayerEntity)
                ((ServerPlayerEntity) other).connection.send(new SExplosionPacket(player.getX(), player.getY(), player.getZ(), strength, explosion.getToBlow(), explosion.getHitPlayers().get(other)));
        }
    }

}
