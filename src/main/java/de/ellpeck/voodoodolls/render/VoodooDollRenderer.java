package de.ellpeck.voodoodolls.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import de.ellpeck.voodoodolls.VoodooDollBlock;
import de.ellpeck.voodoodolls.VoodooDollBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class VoodooDollRenderer implements BlockEntityRenderer<VoodooDollBlockEntity> {

    private static final Map<VoodooDollBlock.Tier, HeadModel> MODELS = Arrays.stream(VoodooDollBlock.Tier.values())
            .collect(Collectors.toMap(t -> t, HeadModel::new));

    public VoodooDollRenderer(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(VoodooDollBlockEntity entity, float f, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        var rotation = entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        var profile = VoodooDollBlockEntity.getProfile(entity.getCustomName());
        VoodooDollRenderer.render(entity.getTier(), profile, rotation, stack, buffer, combinedLight, combinedOverlay);
    }

    public static void render(VoodooDollBlock.Tier tier, GameProfile profile, Direction rotation, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        stack.pushPose();
        stack.translate(0.5, 1.5, 0.5);
        stack.mulPose(Vector3f.ZP.rotationDegrees(180));

        switch (rotation) {
            case SOUTH:
                stack.mulPose(Vector3f.YP.rotationDegrees(180));
                break;
            case WEST:
                stack.mulPose(Vector3f.YP.rotationDegrees(270));
                break;
            case EAST:
                stack.mulPose(Vector3f.YP.rotationDegrees(90));
                break;
        }

        VoodooDollRenderer.MODELS.get(tier).renderToBuffer(stack, buffer.getBuffer(VoodooDollRenderer.getRenderType(profile)), combinedLight, combinedOverlay, 1, 1, 1, 1);
        stack.popPose();
    }

    // see SkullTileEntityRenderer
    private static RenderType getRenderType(@Nullable GameProfile profile) {
        if (profile != null) {
            var minecraft = Minecraft.getInstance();
            var map = minecraft.getSkinManager().getInsecureSkinInformation(profile);
            return map.containsKey(MinecraftProfileTexture.Type.SKIN) ? RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN)) : RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(profile)));
        } else {
            return RenderType.entityCutoutNoCullZOffset(DefaultPlayerSkin.getDefaultSkin());
        }
    }

    private static class HeadModel extends Model {

        private final ModelPart model;

        public HeadModel(VoodooDollBlock.Tier tier) {
            super(RenderType::entityTranslucent);
            var mesh = new MeshDefinition();
            var builder = new CubeListBuilder();
            switch (tier) {
                case ONE -> builder.addBox(-2, -4, -2, 4, 4, 4, false);
                case TWO -> builder.addBox(-2, -3.8436F, -1.7863F, 4, 4, 4, false);
                case THREE -> builder.addBox(-2, -2, -2, 4, 4, 4, false);
            }
            mesh.getRoot().addOrReplaceChild("main", builder, PartPose.ZERO);
            this.model = LayerDefinition.create(mesh, 32, 32).bakeRoot();

            switch (tier) {
                case ONE -> {
                    this.model.setPos(0, 17, -1);
                    this.setRotation(0, 0, 0.3927F);
                }
                case TWO -> {
                    this.model.setPos(0, 14, 5);
                    this.setRotation(-0.3927F, 0, 0);
                }
                case THREE -> this.model.setPos(0, 11, 5);
            }
        }

        @Override
        public void renderToBuffer(PoseStack matrixStack, VertexConsumer iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.model.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

        private void setRotation(float x, float y, float z) {
            this.model.xRot = x;
            this.model.yRot = y;
            this.model.zRot = z;
        }
    }
}
