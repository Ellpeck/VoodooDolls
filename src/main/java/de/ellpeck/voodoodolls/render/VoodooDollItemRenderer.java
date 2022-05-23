package de.ellpeck.voodoodolls.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.ellpeck.voodoodolls.VoodooDollBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

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
