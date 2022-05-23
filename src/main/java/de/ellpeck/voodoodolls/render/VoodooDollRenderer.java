package de.ellpeck.voodoodolls.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.ellpeck.voodoodolls.VoodooDollBlock;
import de.ellpeck.voodoodolls.VoodooDollBlockEntity;
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
        render(entity.getTier(), entity.customName, stack, buffer, combinedLight, combinedOverlay);
    }

    public static void render(VoodooDollBlock.Tier tier, ITextComponent name, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        stack.pushPose();
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
            System.out.println("Updating profile for " + name.getString());
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
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTexSize(64, 64);

            switch (tier) {
                case ONE:
                    this.box.addBox(6, 7, 5, 10 - 6, 11 - 7, 9 - 5);
                    this.box.zRot = 22.5F;
                    break;
                case TWO:
                    this.box.addBox(6, 9.8436F, 11.21371F, 10 - 6, 13.8436F - 9.8436F, 15.21371F - 11.21371F);
                    this.box.xRot = 22.5F;
                    break;
                case THREE:
                    this.box.addBox(6, 9.25F, 8.75F, 10 - 6, 13.25F - 9.25F, 12.75F - 8.75F);
                    break;
            }
        }

        @Override
        public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.box.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

    }
}
