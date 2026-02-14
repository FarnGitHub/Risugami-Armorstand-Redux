package farn.armor_stand.block.entity;

import farn.armor_stand.ArmorStandStationAPI;
import farn.armor_stand.skin.*;
import farn.armor_stand.skin.player.PlayerCache;
import farn.armor_stand.skin.player.FakePlayer;
import farn.armor_stand.skin.player.PlayerCacheHandler;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.SkinImageProcessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.ArmorTextureProvider;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;

@Environment(EnvType.CLIENT)
public class ArmorStandBlockEntityRenderer extends BlockEntityRenderer {
	private final BipedEntityModel armor_outer = new BipedEntityModel(1.0F);
	private final BipedEntityModel armor_inner = new BipedEntityModel(0.5F);
	private final BipedEntityModel body = new BipedEntityModel(0.0F);
	private static final Map<Identifier, String[]> armorCache = new Reference2ObjectOpenHashMap<>();
	private static final Map<String, PlayerCache> plrCache = new Reference2ObjectOpenHashMap<>();
	private LivingEntity dummy;
	private final PlayerCache defaultCache = new PlayerCache("", body);

	public void render(BlockEntity blockEntity, double x, double y, double z, float var8) {
		if (blockEntity instanceof ArmorStandBlockEntity tileEntityArmor) {
			if(dummy == null) {
				dummy = new LivingEntity(Minecraft.INSTANCE.world) {
				};
			}
			if(dummy.world != tileEntityArmor.world) {
				dummy.setWorld(tileEntityArmor.world);
			}
			glPushMatrix();
			float brightness = tileEntityArmor.world.method_1782(blockEntity.x, blockEntity.y, blockEntity.z);
			dummy.minBrightness = brightness;
			dummy.setPosition(x,y,z);
			glTranslatef((float) x + 0.5F, (float) y + 1.48F, (float) z + 0.5F);
			glScalef(0.9F, -0.9F, -0.9F);
			float var13 = (float) (tileEntityArmor.getPushedBlockData() * 360 / 16);
			glRotatef(var13, 0.0F, 1.0F, 0.0F);

			for (int index = -1; index < 5; ++index) {
				if (index == -1) {
					renderArmorStandEntityModel(tileEntityArmor);
				} else {
					ItemStack stack = tileEntityArmor.getStack(index);
					if (stack != null) {
						if(index == 0 && stack.getItem() instanceof BlockItem itemBlock) {
							renderPumpkinHead(itemBlock, brightness, stack, dummy);
						} else if(stack.getItem() instanceof ArmorItem armor) {
							renderArmor(armor, brightness);
						} else if(index == 4) {
							renderHeldItem(body, stack, dummy);
						}
					}
				}
			}

			glDepthMask(true);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			glPopMatrix();
		}
	}

	private void renderNormalModel(BipedEntityModel currentModel) {
		currentModel.body.pivotZ = 0.01F;
		currentModel.rightLeg.roll = 0.1F;
		currentModel.leftLeg.roll = -0.1F;
		currentModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / 16.0F);
	}

	private PlayerCache getPlayerCache(ArmorStandBlockEntity blockEntity) {
		if(blockEntity.placer.isEmpty()) return defaultCache;
		PlayerCache cache = plrCache.computeIfAbsent(blockEntity.placer, pl -> {
			try {
				FakePlayer fake = new FakePlayer(blockEntity);
				PlayerCache cache2 = new PlayerCache(fake.skinUrl, PlayerCacheHandler.cloneBipedEntity(fake));
				fake.setPlayerCache(cache2);
				return cache2;
			} catch (Exception e) {
				return null;
			}
		});
		return cache != null ? cache : defaultCache;
	}

	private void renderArmorStandEntityModel(ArmorStandBlockEntity tileEntityArmor) {
		if(ArmorStandSkins.isPlayerSkin(tileEntityArmor.skin)) {
			PlayerCache cache = getPlayerCache(tileEntityArmor);
			if(cache != null) {
				bindSkinTexture(cache.url);
				glPushMatrix();
				renderNormalModel(cache.model);
				glPopMatrix();
			}
		} else {
			this.bindTexture(ArmorStandSkins.getTexture(tileEntityArmor.skin));
			glPushMatrix();
			renderNormalModel(body);
			glPopMatrix();
		}
	}

	private void bindSkinTexture(String skin) {
		this.dispatcher.textureManager.bindTexture(
				this.dispatcher.textureManager.downloadTexture(skin
						, "/mob/char.png"));
	}

	private void renderArmor(ArmorItem armor, float brightness) {
		int equipSlot = armor.equipmentSlot;
		this.bindArmorTexture(armor, armor.textureIndex, equipSlot);
		glPushMatrix();
		BipedEntityModel currentModel = equipSlot == 2 ? this.armor_inner : this.armor_outer;
		currentModel.head.visible = equipSlot == 0;
		currentModel.hat.visible = equipSlot == 0;
		currentModel.body.visible = equipSlot == 1 || equipSlot == 2;
		currentModel.rightArm.visible = equipSlot == 1;
		currentModel.leftArm.visible = equipSlot == 1;
		currentModel.rightLeg.visible = equipSlot == 2 || equipSlot == 3;
		currentModel.leftLeg.visible = equipSlot == 2 || equipSlot == 3;
		glColor3f(brightness, brightness, brightness);
		renderNormalModel(currentModel);
		glPopMatrix();
	}

	private void bindArmorTexture(ArmorItem armor, int textureIndex, int slot) {
		if (armor instanceof ArmorTextureProvider provider) {
			Identifier id = provider.getTexture(armor);
			String[] textures = armorCache.computeIfAbsent(id, k -> new String[4]);
			if (textures[textureIndex] == null) textures[textureIndex] = getStationAPIArmor(id, slot);
			this.bindTexture(textures[textureIndex]);
		}
		else this.bindTexture("/armor/" + PlayerEntityRenderer.armorTextureNames[textureIndex] + (slot == 2 ? "_2.png" : "_1.png"));
	}
	private String getStationAPIArmor(Identifier identifier, int slot) {
		return "/assets/" + identifier.namespace + "/stationapi/textures/armor/" + identifier.path + (slot == 2 ? "_2.png" : "_1.png");
	}

	private void renderPumpkinHead(BlockItem itemBlock, float brightness, ItemStack stack, LivingEntity dummyEntity) {
		GL11.glPushMatrix();
		body.head.transform(0.0625F);
		if (BlockRenderManager.isSideLit(itemBlock.getBlock().getRenderType())) {
			float offset = 0.625F;
			GL11.glTranslatef(0.0F, -0.25F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(offset, -offset, offset);
		}

		GL11.glDisable(GL11.GL_CULL_FACE);
		glColor3f(brightness, brightness, brightness);
		EntityRenderDispatcher.INSTANCE.heldItemRenderer.renderItem(dummyEntity, stack);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	public void renderHeldItem(BipedEntityModel body, ItemStack stack, LivingEntity dummyEntity) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		body.rightArm.transform(0.0625F);
		GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

		if (stack.getItem() instanceof BlockItem blockItem && BlockRenderManager.isSideLit(blockItem.getBlock().getRenderType())) {
			float var24 = 0.5F;
			GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
			var24 *= 0.75F;
			GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(var24, -var24, var24);
		} else if (stack.getItem().isHandheld()) {
			float var22 = 0.625F;
			if (stack.getItem().isHandheldRod()) {
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GL11.glTranslatef(0.0F, -0.125F, 0.0F);
			}

			GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
			GL11.glScalef(var22, -var22, var22);
			GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
		} else {
			float var23 = 0.375F;
			GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
			GL11.glScalef(var23, var23, var23);
			GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
		}

		EntityRenderDispatcher.INSTANCE.heldItemRenderer.renderItem(dummyEntity, stack);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

}
