package de.ellpeck.voodoodolls.render;

import de.ellpeck.voodoodolls.VoodooDolls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RenderingRegistry {

    public static BlockEntityItemRenderer blockEntityItemRenderer;

    public static void setup(FMLClientSetupEvent event) {
        var mc = Minecraft.getInstance();
        RenderingRegistry.blockEntityItemRenderer = new BlockEntityItemRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
        BlockEntityRenderers.register(VoodooDolls.VOODOO_DOLL_BLOCK_ENTITY.get(), VoodooDollRenderer::new);
    }

}
