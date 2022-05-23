package de.ellpeck.voodoodolls.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.voodoodolls.VoodooDollBlock;
import de.ellpeck.voodoodolls.VoodooDollBlockEntity;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VoodooDollRenderer extends TileEntityRenderer<VoodooDollBlockEntity> {

    private static final Map<VoodooDollBlock.Tier, HeadModel> MODELS = Arrays.stream(VoodooDollBlock.Tier.values())
            .collect(Collectors.toMap(t -> t, HeadModel::new));
    private static final Map<ITextComponent, GameProfile> PROFILES = new HashMap<>();

    public VoodooDollRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(VoodooDollBlockEntity entity, float f, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        Direction rotation = entity.getBlockState().getValue(HorizontalBlock.FACING);
        render(entity.getTier(), entity.customName, rotation, stack, buffer, combinedLight, combinedOverlay);
    }

    public static void render(VoodooDollBlock.Tier tier, ITextComponent name, Direction rotation, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
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

        HeadModel model = MODELS.get(tier);
        GameProfile profile = getGameProfile(name);
        model.renderToBuffer(stack, buffer.getBuffer(getRenderType(profile)), combinedLight, combinedOverlay, 1, 1, 1, 1);
        stack.popPose();
    }

    public static GameProfile getGameProfile(ITextComponent name) {
        if (name == null)
            return null;
        GameProfile profile = PROFILES.get(name);
        if (profile == null) {
            profile = new GameProfile(null, name.getString());
            profile = SkullTileEntity.updateGameprofile(profile);
            PROFILES.put(name, profile);
        }
        return profile;
    }

    // see SkullTileEntityRenderer
    private static RenderType getRenderType(@Nullable GameProfile profile) {
        if (profile != null) {
            Minecraft minecraft = Minecraft.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(profile);
            return map.containsKey(MinecraftProfileTexture.Type.SKIN) ? RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN)) : RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(PlayerEntity.createPlayerUUID(profile)));
        } else {
            return RenderType.entityCutoutNoCullZOffset(DefaultPlayerSkin.getDefaultSkin());
        }
    }

    private static class HeadModel extends Model {

        private final ModelRenderer box;

        public HeadModel(VoodooDollBlock.Tier tier) {
            super(RenderType::entityTranslucent);
            this.texWidth = 32;
            this.texHeight = 32;
            this.box = new ModelRenderer(this);

            switch (tier) {
                case ONE:
                    this.box.setPos(0, 17, -1);
                    this.setRotation(0, 0, 0.3927F);
                    this.box.texOffs(0, 0).addBox(-2, -4, -2, 4, 4, 4, 0, false);
                    break;
                case TWO:
                    this.box.setPos(0, 14, 5);
                    this.setRotation(-0.3927F, 0, 0);
                    this.box.texOffs(0, 0).addBox(-2, -3.8436F, -1.7863F, 4, 4, 4, 0, false);
                    break;
                case THREE:
                    this.box.setPos(0, 11, 5);
                    this.box.texOffs(0, 0).addBox(-2, -2, -2, 4, 4, 4, 0, false);
                    break;
            }
        }

        @Override
        public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.box.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

        private void setRotation(float x, float y, float z) {
            this.box.xRot = x;
            this.box.yRot = y;
            this.box.zRot = z;
        }
    }
}
