package farn.armor_stand.screen;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class ArmorStandScreen extends HandledScreen {
	public static final int GRIDX = 8;
	public static final int GRIDY = 18;
	public static final int BLOCKX = 46;
	public static final int BLOCKY = 36;
	public static final int INVENTORYX = 8;
	public static final int INVENTORYY = 68;
	public static final int BUTTONSIZE = 7;
	public static final int BUTTONDOWNX = 176;
	public static final int BUTTONDOWNY = 0;
	public static final int BUTTONUPX = 183;
	public static final int BUTTONUPY = 0;
	public static final int STALKERX = 45;
	public static final int STALKERY = 17;
	public static final int LISTX = 93;
	public static final int LISTY = 17;
	public static final int LISTOFFSET = 9;
	public final Inventory inv;
	public final ArmorStandBlockEntity tile;
	private boolean mouseWasDown = false;
	private ArmorStandBlockEntity ArmorInventory;

	public ArmorStandScreen(Inventory var1, ArmorStandBlockEntity var2) {
		super(new ArmorStandScreenHandler(var1, var2));
		this.inv = var1;
		this.tile = var2;
		this.backgroundHeight = 150;
	}

	protected void drawBackground(float var1) {
		int var2 = this.minecraft.textureManager.getTextureId("/assets/armor_stand/armor_stand_gui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.textureManager.bindTexture(var2);
		int var3 = this.width - this.backgroundWidth >> 1;
		int var4 = this.height - this.backgroundHeight >> 1;
		this.drawTexture(var3, var4, 0, 0, this.backgroundWidth, this.backgroundHeight);

		for(int l = 1; l < 5; ++l) {
			this.drawTexture(var3 + 93, var4 + 17 + l * 9, this.tile.skin != l ? 183 : 176, 0, 7, 7);
		}
	}

	public void render(int i, int j, float f) {
		super.render(i, j, f);
		if(Mouse.isButtonDown(0) && !this.mouseWasDown) {
			int k = this.width - this.backgroundWidth >> 1;
			int l = this.height - this.backgroundHeight >> 1;
			i -= k;
			j -= l;

			for(int i1 = 1; i1 < 5; ++i1) {
				if(i > 93 && j > 17 + i1 * 9 && i <= 100 && j <= 24 + i1 * 9) {
					this.tile.skin = (byte)i1;
				}
			}

			this.mouseWasDown = true;
		} else if(!Mouse.isButtonDown(0)) {
			this.mouseWasDown = false;
		}

	}

	protected void drawForeground() {
		this.textRenderer.draw(this.tile.getName(), 8, 6, 4210752);
		this.textRenderer.draw("Skin", 93, 6, 4210752);
		this.textRenderer.draw(this.inv.getName(), 8, this.backgroundHeight - 96 + 2, 4210752);
		this.textRenderer.draw("Black", 102, 26, 4210752);
		this.textRenderer.draw("Zombie", 102, 35, 4210752);
		this.textRenderer.draw("Wood", 102, 44, 4210752);
		this.textRenderer.draw("Steve", 102, 53, 4210752);
	}
}
