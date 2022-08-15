package de.ellpeck.voodoodolls.render;

// TODO voodoo doll item renderer
/*
public class VoodooDollItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void renderByItem(ItemStack item, ItemCameraTransforms.TransformType transform, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        VoodooDollBlock block = (VoodooDollBlock) Block.byItem(item.getItem());
        ITextComponent name = item.hasCustomHoverName() ? item.getHoverName() : null;
        VoodooDollRenderer.render(block.tier, name, Direction.NORTH, stack, buffer, combinedLight, combinedOverlay);

        // render the actual voodoo doll on the item too, which isn't possible by default for some reason
        BlockState state = block.defaultBlockState();
        stack.pushPose();
        BlockModelRenderer.enableCaching();
        for (RenderType layer : RenderType.chunkBufferLayers()) {
            if (!RenderTypeLookup.canRenderInLayer(state, layer))
                continue;
            ForgeHooksClient.setRenderLayer(layer);
            Minecraft.getInstance().getBlockRenderer().renderBlock(state, stack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
        }
        ForgeHooksClient.setRenderLayer(null);
        BlockModelRenderer.clearCache();
        stack.popPose();
    }
}
*/
