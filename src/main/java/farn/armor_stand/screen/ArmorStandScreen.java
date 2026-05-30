package farn.armor_stand.screen;

import farn.armor_stand.ArmorStandStationAPI;
import farn.armor_stand.network.packet.ArmorStandSkinPacket;
import farn.armor_stand.screen.inventory.ArmorStandScreenHandler;
import farn.armor_stand.skin.ArmorStandSkins;
import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ArmorStandScreen extends HandledScreen {
	public final ArmorStandBlockEntity armorStandEntity;
	public final Inventory inventory;
	private final List<ArmorStandSkinButton> skinButtons = new ArrayList<>();
	private final ArmorStandScreenHandler armorScreenHandler;

	public ArmorStandScreen(Inventory inv, ArmorStandBlockEntity entity) {
		super(new ArmorStandScreenHandler(inv, entity));
		this.inventory = inv;
		this.armorStandEntity = entity;
		this.backgroundHeight = 150;
		this.armorScreenHandler = (ArmorStandScreenHandler)this.handler;
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
			button.render();
	}

	protected void drawForeground() {
		this.textRenderer.draw(this.armorStandEntity.getName(), 8, 6, 4210752);
		this.textRenderer.draw("Skin", 93, 6, 4210752);
		this.textRenderer.draw(this.inventory.getName(), 8,
				this.backgroundHeight - 96 + 2, 4210752);
		for(Slot slot : armorScreenHandler.armorStandSlots)
			if(slot != null && !slot.hasStack())
				renderBgIcon(slot.x, slot.y, slot.index);
	}

	protected void mouseClicked(int mouseX, int mouseY, int button) {
		for(ArmorStandSkinButton theButton : this.skinButtons) {
			if(theButton.isMouseOver(this.minecraft, mouseX, mouseY)) {
				theButton.entity.skin = theButton.skinId;
				if(minecraft.world.isRemote)
					PacketHelper.send(new ArmorStandSkinPacket(theButton.entity.skin));
				break;
			}
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	public void renderBgIcon(int x, int y, int index) {
		StationRenderAPI.getBakedModelManager().
				getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
		Sprite sprite = ArmorStandStationAPI.armorStandIcon[index].getSprite();
		float startU = sprite.getMinU();
		float startV = sprite.getMinV();
		float endU = sprite.getMaxU();
		float endV = sprite.getMaxV();
		Tessellator tess = Tessellator.INSTANCE;
		tess.startQuads();
		tess.vertex(x, y + 16, 0, startU, endV);
		tess.vertex(x + 16, y + 16, 0, endU, endV);
		tess.vertex(x + 16, y, 0, endU, startV);
		tess.vertex(x, y, 0, startU, startV);
		tess.draw();
	}
}
