package de.ellpeck.voodoodolls.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.ellpeck.voodoodolls.VoodooDollBlock;
import de.ellpeck.voodoodolls.VoodooDollBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;

public class BlockEntityItemRenderer extends BlockEntityWithoutLevelRenderer {

    public BlockEntityItemRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    @Override
    public void renderByItem(ItemStack item, ItemTransforms.TransformType transform, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        var block = Block.byItem(item.getItem());
        if (block instanceof VoodooDollBlock voodooDoll) {
            var name = item.hasCustomHoverName() ? item.getHoverName() : null;
            var profile = VoodooDollBlockEntity.getProfile(name);
            VoodooDollRenderer.render(voodooDoll.tier, profile, Direction.NORTH, stack, buffer, combinedLight, combinedOverlay);

            // render the actual voodoo doll on the item too, which isn't possible by default for some reason
            var state = block.defaultBlockState();
            stack.pushPose();
            ForgeBlockModelRenderer.enableCaching();
            for (var layer : RenderType.chunkBufferLayers()) {
                if (!ItemBlockRenderTypes.canRenderInLayer(state, layer))
                    continue;
                ForgeHooksClient.setRenderType(layer);
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, stack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
            }
            ForgeHooksClient.setRenderType(null);
            ForgeBlockModelRenderer.clearCache();
            stack.popPose();
        }
    }
}
