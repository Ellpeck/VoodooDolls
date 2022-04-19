package de.ellpeck.voodoodolls;

import com.mojang.blaze3d.matrix.MatrixStack;
import joptsimple.internal.Strings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class VoodooDollScreen extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(VoodooDolls.ID, "textures/ui/voodoo_doll.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 58;

    private final VoodooDollBlockEntity doll;
    private TextFieldWidget textField;
    private Button okButton;

    public VoodooDollScreen(VoodooDollBlockEntity doll) {
        super(new TranslationTextComponent("screen." + VoodooDolls.ID + ".voodoo_doll"));
        this.doll = doll;
    }

    @Override
    protected void init() {
        int left = (this.width - WIDTH) / 2;
        int top = (this.height - HEIGHT) / 2;
        this.textField = this.addWidget(new TextFieldWidget(this.font, left + 8, top + 20, 161, 9, null, this.title));
        this.textField.setMaxLength(50);
        this.textField.setBordered(false);
        this.textField.setTextColor(16777215);
        this.textField.setValue(this.doll.getName().getString());
        this.okButton = this.addButton(new ExtendedButton(left + (WIDTH - 50) / 2, top + 33, 50, 20, new TranslationTextComponent("gui.ok"), b -> {
            Network.sendToServer(new Network.ChangeVoodooDollName(this.doll.getBlockPos(), this.textField.getValue()));
            this.minecraft.setScreen(null);
        }));
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float pt) {
        this.renderBackground(stack);

        int left = (this.width - WIDTH) / 2;
        int top = (this.height - HEIGHT) / 2;
        this.minecraft.textureManager.bind(TEXTURE);
        this.blit(stack, left, top, 0, 0, WIDTH, HEIGHT);

        this.font.draw(stack, this.title, left + 8, top + 6, 4210752);
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
