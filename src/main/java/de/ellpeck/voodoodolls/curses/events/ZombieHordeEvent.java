package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ZombieHordeEvent extends CurseEvent {

    private ForgeConfigSpec.ConfigValue<Integer> verticalRange;
    private ForgeConfigSpec.ConfigValue<Integer> horizontalRange;
    private ForgeConfigSpec.ConfigValue<Integer> amount;

    public ZombieHordeEvent() {
        super("zombie_horde", Badness.WORSE, 0.05F);
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
    public void occur(PlayerEntity player, Curse curse) {
        List<BlockPos> validPositions = new ArrayList<>();
        for (int y = -this.verticalRange.get(); y <= this.verticalRange.get(); y++) {
            for (int x = -this.horizontalRange.get(); x <= this.horizontalRange.get(); x++) {
                for (int z = -this.horizontalRange.get(); z <= this.horizontalRange.get(); z++) {
                    BlockPos pos = new BlockPos(player.getX() + x, player.getY() + y, player.getZ() + z);
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
        int amount = MathHelper.nextInt(player.getRandom(), (int) (this.amount.get() * 0.75F), (int) (this.amount.get() * 1.25F));
        while (amount > 0) {
            BlockPos pos = validPositions.remove(player.getRandom().nextInt(validPositions.size()));
            ZombieEntity zombie = EntityType.ZOMBIE.create(player.level);
            ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
            ((IDyeableArmorItem) helmet.getItem()).setColor(helmet, player.getRandom().nextInt());
            zombie.setItemSlot(EquipmentSlotType.HEAD, helmet);
            zombie.setDropChance(EquipmentSlotType.HEAD, 0);
            zombie.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            zombie.setTarget(player);
            player.level.addFreshEntity(zombie);
            amount--;
        }
    }
}
