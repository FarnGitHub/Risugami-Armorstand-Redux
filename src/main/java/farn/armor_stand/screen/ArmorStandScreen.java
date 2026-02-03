package farn.armor_stand.screen;

import farn.armor_stand.skin.ArmorStandSkins;
import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.screen.handler.ArmorStandScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ArmorStandScreen extends HandledScreen {
	public final Inventory inv;
	public final ArmorStandBlockEntity armorStandEntity;

	int mouseX;
	int mouseY;
	List<ArmorStandButton> skinButtons = new ArrayList<>();
	private boolean mouseWasDown = false;

	public ArmorStandScreen(Inventory var1, ArmorStandBlockEntity var2) {
		super(new ArmorStandScreenHandler(var1, var2));
		this.inv = var1;
		this.armorStandEntity = var2;
		this.backgroundHeight = 150;
	}

	public void init() {
		super.init();
		int newX = this.width - this.backgroundWidth >> 1;
		int newY = this.height - this.backgroundHeight >> 1;
		for(ArmorStandSkins skin : ArmorStandSkins.values()) {
			this.skinButtons.add(new ArmorStandButton(skin.ordinal(), newX + 93, newY + 17 + skin.ordinal() * 9, this.armorStandEntity));
		}
	}

	public void render(int mouseX, int mouseY, float delta) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		super.render(mouseX, mouseY, delta);
	}

	protected void drawBackground(float var1) {
		int var2 = this.minecraft.textureManager.getTextureId("/assets/armor_stand/armor_stand_gui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.textureManager.bindTexture(var2);
		int var3 = this.width - this.backgroundWidth >> 1;
		int var4 = this.height - this.backgroundHeight >> 1;
		this.drawTexture(var3, var4, 0, 0, this.backgroundWidth, this.backgroundHeight);

		for(ArmorStandButton button : this.skinButtons) {
			button.render(this.minecraft, this.mouseX, this.mouseY);
			if(Mouse.isButtonDown(0) && !this.mouseWasDown) {
				if(button.handleButtonClicked(this.minecraft, mouseX, mouseY)
						|| button.id == skinButtons.size())
					this.mouseWasDown = true;
			} else if(!Mouse.isButtonDown(0)) {
				this.mouseWasDown = false;
			}
		}
	}

	protected void drawForeground() {
		this.textRenderer.draw(this.armorStandEntity.getName(), 8, 6, 4210752);
		this.textRenderer.draw("Skin", 93, 6, 4210752);
		this.textRenderer.draw(this.inv.getName(), 8, this.backgroundHeight - 96 + 2, 4210752);
	}
}
