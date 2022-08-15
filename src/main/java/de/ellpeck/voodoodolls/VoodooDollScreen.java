package de.ellpeck.voodoodolls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.ellpeck.voodoodolls.curses.Curse;
import joptsimple.internal.Strings;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.List;

public class VoodooDollScreen extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(VoodooDolls.ID, "textures/ui/voodoo_doll.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 93;

    private final VoodooDollBlockEntity doll;
    private EditBox textField;
    private Button okButton;

    public VoodooDollScreen(VoodooDollBlockEntity doll) {
        super(new TranslatableComponent("screen." + VoodooDolls.ID + ".voodoo_doll"));
        this.doll = doll;
    }

    @Override
    protected void init() {
        var left = (this.width - VoodooDollScreen.WIDTH) / 2;
        var top = (this.height - VoodooDollScreen.HEIGHT) / 2;
        this.textField = this.addWidget(new EditBox(this.font, left + 8, top + 55, 161, 9, null, this.title));
        this.textField.setMaxLength(50);
        this.textField.setBordered(false);
        this.textField.setTextColor(16777215);
        this.textField.setValue(this.doll.getName().getString());
        this.okButton = this.addWidget(new ExtendedButton(left + (VoodooDollScreen.WIDTH - 50) / 2, top + 68, 50, 20, new TranslatableComponent("gui.ok"), b -> {
            Packets.sendToServer(new Packets.VoodooDollName(this.doll.getBlockPos(), this.textField.getValue()));
            this.minecraft.setScreen(null);
        }));
    }

    @Override
    public void render(PoseStack stack, int x, int y, float pt) {
        this.renderBackground(stack);

        var left = (this.width - VoodooDollScreen.WIDTH) / 2;
        var top = (this.height - VoodooDollScreen.HEIGHT) / 2;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, VoodooDollScreen.TEXTURE);
        this.blit(stack, left, top, 0, 0, VoodooDollScreen.WIDTH, VoodooDollScreen.HEIGHT);

        var curse = this.doll.getCurse();
        if (curse != null) {
            Component text = new TranslatableComponent("info." + VoodooDolls.ID + ".cursed_player", curse.playerName, curse.getDisplayName());
            var split = this.font.split(text, VoodooDollScreen.WIDTH - 16);
            for (var i = 0; i < split.size(); i++)
                this.font.draw(stack, split.get(i), left + 8, top + 8 + i * 10, 4210752);
        }

        this.font.draw(stack, this.title, left + 8, top + 43, 4210752);
        this.textField.render(stack, x, y, pt);
        super.render(stack, x, y, pt);
    }

    @Override
    public void tick() {
        this.textField.tick();
        this.okButton.active = !Strings.isNullOrEmpty(this.textField.getValue().trim());
    }

    @Override
    public boolean mouseClicked(double x, double y, int i) {
        return this.textField.mouseClicked(x, y, i) || super.mouseClicked(x, y, i);
    }

    @Override
    public boolean keyPressed(int x, int y, int z) {
        return this.textField.keyPressed(x, y, z) || super.keyPressed(x, y, z);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.textField.charTyped(c, i) || super.charTyped(c, i);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
