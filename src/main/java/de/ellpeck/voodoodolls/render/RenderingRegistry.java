package de.ellpeck.voodoodolls.render;

import de.ellpeck.voodoodolls.VoodooDolls;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RenderingRegistry {

    public static void setup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(VoodooDolls.VOODOO_DOLL_BLOCK_ENTITY.get(), VoodooDollRenderer::new);
    }

}
