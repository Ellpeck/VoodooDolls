package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.VoodooDollBlock.Tier;
import de.ellpeck.voodoodolls.curses.events.*;
import de.ellpeck.voodoodolls.curses.triggers.*;
import de.ellpeck.voodoodolls.render.RenderingRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Mod(VoodooDolls.ID)
public class VoodooDolls {

    public static final String ID = "voodoodolls";

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(VoodooDolls.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(VoodooDolls.VOODOO_DOLL_BLOCKS.get(0).get());
        }
    };

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VoodooDolls.ID);
    public static final List<RegistryObject<Block>> VOODOO_DOLL_BLOCKS = Arrays.stream(Tier.values())
            .map(t -> VoodooDolls.BLOCKS.<Block>register("voodoo_doll_tier_" + t.name().toLowerCase(Locale.ROOT), () -> new VoodooDollBlock(t)))
            .collect(Collectors.toList());

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VoodooDolls.ID);
    public static final List<RegistryObject<Item>> VOODOO_DOLL_ITEMS = VoodooDolls.VOODOO_DOLL_BLOCKS.stream()
            .map(b -> VoodooDolls.ITEMS.<Item>register(b.getId().getPath(), () ->
                    new BlockItem(b.get(), new Item.Properties().tab(VoodooDolls.CREATIVE_TAB))))
            .collect(Collectors.toList());

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, VoodooDolls.ID);
    public static final RegistryObject<BlockEntityType<VoodooDollBlockEntity>> VOODOO_DOLL_BLOCK_ENTITY = VoodooDolls.BLOCK_ENTITIES.register("voodoo_doll",
            () -> BlockEntityType.Builder.of(VoodooDollBlockEntity::new, VoodooDolls.VOODOO_DOLL_BLOCKS.stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

    public VoodooDolls() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(Packets::setup);
        VoodooDolls.BLOCKS.register(bus);
        VoodooDolls.ITEMS.register(bus);
        VoodooDolls.BLOCK_ENTITIES.register(bus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(RenderingRegistry::setup));

        CurseTrigger.register(new SleepTrigger());
        CurseTrigger.register(new BreakBlockTrigger("chop_tree", s -> s.is(BlockTags.LOGS)));
        CurseTrigger.register(new BreakBlockTrigger("dig_dirt", s -> s.is(BlockTags.DIRT)));
        CurseTrigger.register(new BreakBlockTrigger("mine_stone", s -> s.is(Tags.Blocks.STONE)));
        CurseTrigger.register(new BreakBlockTrigger("mine_ore", s -> s.is(Tags.Blocks.ORES)));
        CurseTrigger.register(new EatTrigger());
        CurseTrigger.register(new KillMobTrigger());
        CurseTrigger.register(new BoneMealTrigger());
        CurseTrigger.register(new JumpTrigger());
        CurseTrigger.register(new SneakTrigger());

        CurseEvent.register(new ShuffleInventoryEvent());
        CurseEvent.register(new SwapHandsEvent());
        CurseEvent.register(new EffectEvent("blindness", CurseEvent.Badness.BAD, 0.05F, MobEffects.BLINDNESS, 0, 1, 3));
        CurseEvent.register(new TeleportToCaveEvent());
        CurseEvent.register(new ZombieHordeEvent());
        CurseEvent.register(new ReverseControlsEvent());
        CurseEvent.register(new TopDownCameraEvent());
        CurseEvent.register(new ExplosionEvent());
        CurseEvent.register(new TeleportRandomlyEvent());

        var config = new ForgeConfigSpec.Builder();
        config.push("triggers");
        for (var trigger : CurseTrigger.TRIGGERS.values()) {
            config.push(trigger.id);
            trigger.setupConfig(config);
            config.pop();
        }
        config.pop();
        config.push("curses");
        for (var event : CurseEvent.EVENTS.values()) {
            config.push(event.id);
            event.setupConfig(config);
            config.pop();
        }
        config.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.build());
    }
}
