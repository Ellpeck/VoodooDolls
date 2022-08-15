package de.ellpeck.voodoodolls.render;

import de.ellpeck.voodoodolls.VoodooDolls;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RenderingRegistry {

    public static void setup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(VoodooDolls.VOODOO_DOLL_BLOCK_ENTITY.get(), VoodooDollRenderer::new);
    }

}
