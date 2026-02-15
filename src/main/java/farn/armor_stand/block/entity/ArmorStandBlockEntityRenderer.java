package farn.armor_stand.block.entity;

import farn.armor_stand.skin.*;
import farn.armor_stand.skin.player.PlayerModelCache;
import farn.armor_stand.skin.player.FakePlayerEntity;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.item.ArmorTextureProvider;
import net.modificationstation.stationapi.api.client.render.RendererAccess;
import net.modificationstation.stationapi.api.client.render.model.VanillaBakedModel;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
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
	private static final Map<String, PlayerModelCache> plrCache = new Reference2ObjectOpenHashMap<>();
	private LivingEntity dummy;
	private PlayerModelCache defaultCache;
	private String defaultPlayerTexture = "";

	public void render(BlockEntity blockEntity, double x, double y, double z, float tick) {
		if (blockEntity instanceof ArmorStandBlockEntity tileEntityArmor) {
			if(dummy == null)
				dummy = new LivingEntity(Minecraft.INSTANCE.world) {};
			if(dummy.world != tileEntityArmor.world)
				dummy.setWorld(tileEntityArmor.world);
			if(defaultCache == null) {
				FakePlayerEntity fake = new FakePlayerEntity("");
				defaultCache =
						new PlayerModelCache("", PlayerModelCache.clonePlayerModel(fake));
				defaultPlayerTexture = fake.getTexture();
			}
			glPushMatrix();
			float brightness = tileEntityArmor.world.method_1782(
					blockEntity.x, blockEntity.y, blockEntity.z);
			dummy.minBrightness = brightness;
			dummy.setPosition(x,y,z);
			glTranslated(x + 0.5D, y + 1.48D, z + 0.5D);
			glScalef(0.9F, -0.9F, -0.9F);
			float facingRot = tileEntityArmor.getPushedBlockData() * 360.0F / 16F;
			glRotatef(facingRot, 0.0F, 1.0F, 0.0F);
			PlayerModelCache cache = null;
			BipedEntityModel mainModel = body;
			if(ArmorStandSkins.isPlayerSkin(tileEntityArmor.skin)
					&& (cache = getPlayerCache(tileEntityArmor)) != null)
				mainModel = cache.model;

			renderArmorStandEntityModel(cache, tileEntityArmor);
			for (int slot = 0; slot < 5; ++slot) {
				ItemStack stack = tileEntityArmor.getStack(slot);
				if (stack != null) {
					if(slot == 0 && stack.getItem() instanceof BlockItem itemBlock)
						renderPumpkinHead(mainModel, itemBlock, brightness, stack);
					else if(stack.getItem() instanceof ArmorItem armor)
						renderArmor(armor, brightness);
					else if(slot == 4)
						renderHeldItem(mainModel, stack);
				}
			}

			glDepthMask(true);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			glPopMatrix();
		}
	}

	private void renderBipedModel(BipedEntityModel currentModel) {
		currentModel.
				render(0.0F, 0.0F, 0.0F,
						0.0F, 0.0F, 1.0F / 16.0F);
	}

	private PlayerModelCache getPlayerCache(ArmorStandBlockEntity blockEntity) {
		if(blockEntity.placer.isEmpty()) return defaultCache;
		return plrCache.computeIfAbsent(blockEntity.placer, pl -> {
			try {
				FakePlayerEntity fake = new FakePlayerEntity(blockEntity);
				PlayerModelCache finalCache = new PlayerModelCache(
						fake.skinUrl, PlayerModelCache.clonePlayerModel(fake));
				fake.downloadSkin();
				fake.setPlayerCache(finalCache);
				return finalCache;
			} catch (Exception e) {
				return defaultCache;
			}
		});
	}

	private void renderArmorStandEntityModel(@Nullable PlayerModelCache cache, ArmorStandBlockEntity tileEntityArmor) {
		if(cache != null)
			bindSkinTexture(cache.skinUrl);
		else
			this.bindTexture(ArmorStandSkins.getTexture(tileEntityArmor.skin));
		glPushMatrix();
		renderBipedModel(cache != null ? cache.model : body);
		glPopMatrix();
	}

	private void bindSkinTexture(String skin) {
		this.dispatcher.textureManager.bindTexture(
				this.dispatcher.textureManager.downloadTexture(skin
						, defaultPlayerTexture));
	}

	private void renderArmor(ArmorItem armor, float brightness) {
		int equipSlot = armor.equipmentSlot;
		this.bindArmorTexture(armor);
		glPushMatrix();
		BipedEntityModel currentModel =
				equipSlot == 2 ? this.armor_inner : this.armor_outer;
		currentModel.head.visible = equipSlot == 0;
		currentModel.hat.visible = equipSlot == 0;
		currentModel.body.visible = equipSlot == 1 || equipSlot == 2;
		currentModel.rightArm.visible = equipSlot == 1;
		currentModel.leftArm.visible = equipSlot == 1;
		currentModel.rightLeg.visible = equipSlot == 2 || equipSlot == 3;
		currentModel.leftLeg.visible = equipSlot == 2 || equipSlot == 3;
		glColor3f(brightness, brightness, brightness);
		renderBipedModel(currentModel);
		glPopMatrix();
	}

	private void bindArmorTexture(ArmorItem armor) {
		if (armor instanceof ArmorTextureProvider provider) {
			Identifier id = provider.getTexture(armor);
			String[] textures = armorCache.computeIfAbsent(id, k -> new String[4]);
			if (textures[armor.textureIndex] == null) textures[armor.textureIndex]
					= getStationAPIArmor(id, armor.equipmentSlot);
			this.bindTexture(textures[armor.textureIndex]);
		}
		else this.bindTexture(
				"/armor/" + PlayerEntityRenderer.armorTextureNames[armor.textureIndex] +
						(armor.equipmentSlot == 2 ? "_2.png" : "_1.png"));
	}
	private String getStationAPIArmor(Identifier identifier, int slot) {
		return "/assets/" + identifier.namespace +
				"/stationapi/textures/armor/" + identifier.path + (slot == 2 ? "_2.png" : "_1.png");
	}

	private void renderPumpkinHead(BipedEntityModel body, BlockItem itemBlock, float brightness, ItemStack stack) {
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
		EntityRenderDispatcher.INSTANCE.heldItemRenderer.renderItem(dummy, stack);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	public void renderHeldItem(BipedEntityModel body, ItemStack stack) {
		glPushMatrix();
		glDisable(GL_CULL_FACE);
		body.rightArm.transform(0.0625F);
		glTranslatef(-0.0625F, 0.4375F, 0.0625F);
		if (isJsonModel(stack)) GL11.glPushMatrix();
		if (stack.getItem() instanceof BlockItem blockItem &&
				BlockRenderManager.isSideLit(blockItem.getBlock().getRenderType())) {
			float blockScaling = 0.5F;
			glTranslatef(0.0F, 0.1875F, -0.3125F);
			blockScaling *= 0.75F;
			glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
			glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			glScalef(blockScaling, -blockScaling, blockScaling);
		} else if (stack.getItem() instanceof BowItem || stack.getItem().isHandheld()) {
			float heldScaling = 0.625F;
			if (stack.getItem().isHandheldRod()) {
				glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				glTranslatef(0.0F, -0.125F, 0.0F);
			}

			glTranslatef(0.0F, 0.1875F, 0.0F);
			glScalef(heldScaling, -heldScaling, heldScaling);
			glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
			glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			if(stack.getItem() instanceof BowItem) {
				GL11.glTranslatef(0.0F, -0.5F, 0.0F);
			}
		} else {
			float itemScaling = 0.375F;
			glTranslatef(0.25F, 0.1875F, -0.1875F);
			glScalef(itemScaling, itemScaling, itemScaling);
			glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
			glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
		}

		if(isJsonModel(stack)) GL11.glPopMatrix();
		EntityRenderDispatcher.INSTANCE.heldItemRenderer.renderItem(dummy, stack);
		glEnable(GL_CULL_FACE);
		glPopMatrix();
	}

	private boolean isJsonModel(ItemStack stack) {
		return RendererAccess.INSTANCE.hasRenderer() &&
				!(RendererAccess.INSTANCE.getRenderer().bakedModelRenderer().
						getItemModels().getModel(stack) instanceof VanillaBakedModel);
	}

}
