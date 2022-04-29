package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.VoodooDollBlock.Tier;
import de.ellpeck.voodoodolls.curses.events.*;
import de.ellpeck.voodoodolls.curses.triggers.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Mod(VoodooDolls.ID)
public class VoodooDolls {

    public static final String ID = "voodoodolls";

    public static final ItemGroup ITEM_GROUP = new ItemGroup(ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(VOODOO_DOLL_BLOCKS.get(0).get());
        }
    };

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final List<RegistryObject<Block>> VOODOO_DOLL_BLOCKS = Arrays.stream(Tier.values())
            .map(t -> BLOCKS.<Block>register("voodoo_doll_tier_" + t.name().toLowerCase(Locale.ROOT), () -> new VoodooDollBlock(t)))
            .collect(Collectors.toList());

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final List<RegistryObject<Item>> BLOCK_ITEMS = BLOCKS.getEntries().stream()
            .map(b -> ITEMS.<Item>register(b.getId().getPath(), () -> new BlockItem(b.get(), new Item.Properties().tab(ITEM_GROUP))))
            .collect(Collectors.toList());

    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ID);
    public static final RegistryObject<TileEntityType<VoodooDollBlockEntity>> VOODOO_DOLL_BLOCK_ENTITY = BLOCK_ENTITIES.register("voodoo_doll",
            () -> TileEntityType.Builder.of(VoodooDollBlockEntity::new, VOODOO_DOLL_BLOCKS.stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));

    public VoodooDolls() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(Packets::setup);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);

        CurseTrigger.register(new SleepTrigger());
        CurseTrigger.register(new BreakBlockTrigger("chop_tree", s -> s.is(BlockTags.LOGS)));
        CurseTrigger.register(new BreakBlockTrigger("dig_dirt", s -> s.is(Tags.Blocks.DIRT)));
        CurseTrigger.register(new BreakBlockTrigger("mine_stone", s -> s.is(Tags.Blocks.STONE)));
        CurseTrigger.register(new BreakBlockTrigger("mine_ore", s -> s.is(Tags.Blocks.ORES)));
        CurseTrigger.register(new EatTrigger());
        CurseTrigger.register(new KillMobTrigger());
        CurseTrigger.register(new BoneMealTrigger());
        CurseTrigger.register(new JumpTrigger());
        CurseTrigger.register(new SneakTrigger());

        CurseEvent.register(new ShuffleInventoryEvent());
        CurseEvent.register(new SwapHandsEvent());
        CurseEvent.register(new EffectEvent("blindness", CurseEvent.Badness.BAD, 0.05F, Effects.BLINDNESS, 0, 1, 3));
        CurseEvent.register(new TeleportToCaveEvent());
        CurseEvent.register(new ZombieHordeEvent());
        CurseEvent.register(new ReverseControlsEvent());
        CurseEvent.register(new TopDownCameraEvent());
        CurseEvent.register(new ExplosionEvent());
        CurseEvent.register(new TeleportRandomlyEvent());

        ForgeConfigSpec.Builder config = new ForgeConfigSpec.Builder();
        config.push("triggers");
        for (CurseTrigger trigger : CurseTrigger.TRIGGERS.values()) {
            config.push(trigger.id);
            trigger.setupConfig(config);
            config.pop();
        }
        config.pop();
        config.push("curses");
        for (CurseEvent event : CurseEvent.EVENTS.values()) {
            config.push(event.id);
            event.setupConfig(config);
            config.pop();
        }
        config.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config.build());
    }
}
