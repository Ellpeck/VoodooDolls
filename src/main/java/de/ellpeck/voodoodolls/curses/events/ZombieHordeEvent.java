package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ZombieHordeEvent extends CurseEvent {

    private ForgeConfigSpec.ConfigValue<Integer> verticalRange;
    private ForgeConfigSpec.ConfigValue<Integer> horizontalRange;
    private ForgeConfigSpec.ConfigValue<Integer> amount;

    public ZombieHordeEvent() {
        super("zombie_horde", Badness.WORSE, 0.05F, false);
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder config) {
        super.setupConfig(config);
        this.verticalRange = config
                .comment("The vertical radius of the " + this.id + " event's spawn area.")
                .define("vertical_range", 10);
        this.horizontalRange = config
                .comment("The horizontal radius of the " + this.id + " event's spawn area.")
                .define("horizontal_range", 32);
        this.amount = config
                .comment("The average amount of zombies that the " + this.id + " should spawn.")
                .define("amount", 15);
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide)
            return;
        List<BlockPos> validPositions = new ArrayList<>();
        for (var y = -this.verticalRange.get(); y <= this.verticalRange.get(); y++) {
            for (var x = -this.horizontalRange.get(); x <= this.horizontalRange.get(); x++) {
                for (var z = -this.horizontalRange.get(); z <= this.horizontalRange.get(); z++) {
                    var pos = new BlockPos(player.getX() + x, player.getY() + y, player.getZ() + z);
                    if (!player.level.getBlockState(pos.below()).isCollisionShapeFullBlock(player.level, pos.below()))
                        continue;
                    if (!player.level.isEmptyBlock(pos) || !player.level.isEmptyBlock(pos.above()))
                        continue;
                    validPositions.add(pos);
                }
            }
        }
        if (validPositions.isEmpty())
            return;
        var amount = Mth.nextInt(player.getRandom(), (int) (this.amount.get() * 0.75F), (int) (this.amount.get() * 1.25F));
        while (amount > 0) {
            var pos = validPositions.remove(player.getRandom().nextInt(validPositions.size()));
            var zombie = EntityType.ZOMBIE.create(player.level);
            var helmet = new ItemStack(Items.LEATHER_HELMET);
            ((DyeableArmorItem) helmet.getItem()).setColor(helmet, player.getRandom().nextInt());
            zombie.setItemSlot(EquipmentSlot.HEAD, helmet);
            zombie.setDropChance(EquipmentSlot.HEAD, 0);
            zombie.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            zombie.setTarget(player);
            player.level.addFreshEntity(zombie);
            amount--;
        }
    }
}
