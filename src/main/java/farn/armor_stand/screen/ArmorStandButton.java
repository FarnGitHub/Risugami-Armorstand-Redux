package farn.armor_stand.screen;

import farn.armor_stand.skin.ArmorStandSkins;
import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.network.PacketC2SChangeArmorStandSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.opengl.GL11;

public class ArmorStandButton extends ButtonWidget {
    ArmorStandBlockEntity entity;

    public ArmorStandButton(int id, int x, int y, ArmorStandBlockEntity entity) {
        super(id, x, y, 7, 7, ArmorStandSkins.getName(id));
        this.entity = entity;
    }

    public void render(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            GL11.glBindTexture(3553, minecraft.textureManager.getTextureId("/assets/armor_stand/armor_stand_gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(this.x, this.y, entity.skin != id ? 183 : 176, 0, 7, 7);
            minecraft.textRenderer.draw(text, this.x + 9, this.y, 4210752);
        }
    }

    public boolean handleButtonClicked(Minecraft minecraft, int mouseX, int mouseY) {
        if(isMouseOver(minecraft, mouseX, mouseY)) {
            entity.skin = (byte)this.id;
            if(minecraft.world.isRemote) {
                PacketHelper.send(new PacketC2SChangeArmorStandSkin(entity.skin));
            }
            return true;
        }
        return false;
    }
}
