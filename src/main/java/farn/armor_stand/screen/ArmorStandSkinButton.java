package farn.armor_stand.screen;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.skin.ArmorStandSkins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class ArmorStandSkinButton extends ButtonWidget {
    public ArmorStandBlockEntity entity;
    public byte skinId;

    public ArmorStandSkinButton(int id, int x, int y, ArmorStandBlockEntity entity) {
        super(id, x, y,7,7, ArmorStandSkins.getName(id));
        this.entity = entity;
        this.skinId = (byte)id;
    }

    public void render() {
        if (this.visible) {
            GL11.glBindTexture(3553, getMinecraft().textureManager.getTextureId("/assets/armor_stand/armor_stand_gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(this.x, this.y, entity.skin != skinId ? 183 : 176, 0, 7, 7);
            getMinecraft().textRenderer.draw(text, this.x + 9, this.y, 4210752);
        }
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY) {
        this.render();
    }

    private Minecraft getMinecraft() {
        return Minecraft.INSTANCE;
    }
}
