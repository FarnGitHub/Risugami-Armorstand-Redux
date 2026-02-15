package farn.armor_stand.screen;

import farn.armor_stand.screen.inventory.ArmorStandScreenHandler;
import farn.armor_stand.skin.ArmorStandSkins;
import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ArmorStandScreen extends HandledScreen {
	public final ArmorStandBlockEntity armorStandEntity;
	public final Inventory inventory;
	private final List<ArmorStandSkinButton> skinButtons = new ArrayList<>();

	public ArmorStandScreen(Inventory inv, ArmorStandBlockEntity entity) {
		super(new ArmorStandScreenHandler(inv, entity));
		this.inventory = inv;
		this.armorStandEntity = entity;
		this.backgroundHeight = 150;
	}

	public void init() {
		super.init();
		this.skinButtons.clear();
		int newX = this.width - this.backgroundWidth >> 1;
		int newY = this.height - this.backgroundHeight >> 1;
		for(ArmorStandSkins skin : ArmorStandSkins.values())
			this.skinButtons.add(new ArmorStandSkinButton(skin.ordinal(),
					newX + 93, newY + 17 + skin.ordinal() * 9,
					this.armorStandEntity));
	}

	protected void drawBackground(float tick) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.textureManager.bindTexture(
				this.minecraft.textureManager.getTextureId(
						"/assets/armor_stand/armor_stand_gui.png"));
		int backX = this.width - this.backgroundWidth >> 1;
		int backY = this.height - this.backgroundHeight >> 1;
		this.drawTexture(backX, backY, 0, 0,
				this.backgroundWidth, this.backgroundHeight);
		for(ArmorStandSkinButton button : this.skinButtons)
			button.render(this.minecraft, 0, 0);
	}

	protected void drawForeground() {
		this.textRenderer.draw(this.armorStandEntity.getName(), 8, 6, 4210752);
		this.textRenderer.draw("Skin", 93, 6, 4210752);
		this.textRenderer.draw(this.inventory.getName(), 8,
				this.backgroundHeight - 96 + 2, 4210752);
	}

	protected void mouseClicked(int mouseX, int mouseY, int button) {
		for(ArmorStandSkinButton theButton : this.skinButtons)
			if(theButton.buttonClicked(this.minecraft, mouseX, mouseY))
				minecraft.soundManager.playSound("random.click", 1.0F, 1.0F);
		super.mouseClicked(mouseX, mouseY, button);
	}
}
