package farn.armor_stand.block.entity;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
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
	private BipedEntityModel outer = getBipedModel(1.0F);
	private BipedEntityModel inner = getBipedModel(0.5F);
	private BipedEntityModel body = getBipedModel(0.5F);
	private static String[] armorArray = PlayerEntityRenderer.armorTextureNames;
	private static final Map<Identifier, String[]> STATIONAPI$ARMOR_CACHE = new Reference2ObjectOpenHashMap<>();
	private LivingEntity dummyEntity;

	public void render(BlockEntity blockEntity, double x, double y, double z, float var8) {
		if (blockEntity instanceof ArmorStandBlockEntity tileEntityArmor) {
			if (armorArray != null) {
				if(dummyEntity == null || tileEntityArmor.world != dummyEntity.world) {
					dummyEntity = new LivingEntityDummy(tileEntityArmor.world);
				}
				glPushMatrix();
				float brightness = tileEntityArmor.world.method_1782(blockEntity.x, blockEntity.y, blockEntity.z);
				dummyEntity.minBrightness = brightness;
				dummyEntity.setPosition(x,y,z);
				glTranslatef((float) x + 0.5F, (float) y + 1.48F, (float) z + 0.5F);
				glScalef(0.9F, -0.9F, -0.9F);
				float var13 = (float) (tileEntityArmor.getPushedBlockData() * 360 / 16);
				glRotatef(var13, 0.0F, 1.0F, 0.0F);

				for (int var25 = -1; var25 < 5; ++var25) {
					if (var25 == -1) {
						bindSkin(tileEntityArmor);
						glPushMatrix();
						renderBipedEntityModel(body);
						glPopMatrix();
					} else {
						ItemStack stack = tileEntityArmor.getStack(var25);
						if (stack != null) {
							if(var25 == 0 && stack.getItem() instanceof BlockItem itemBlock) {
								GL11.glPushMatrix();
								this.body.head.transform(0.0625F);
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
							} else if(stack.getItem() instanceof ArmorItem armor) {
								int equipSlot = armor.equipmentSlot;
								this.bindArmorTexture(armor, armor.textureIndex, equipSlot);
								glPushMatrix();
								BipedEntityModel currentModel = equipSlot == 2 ? this.inner : this.outer;
								currentModel.head.visible = equipSlot == 0;
								currentModel.hat.visible = equipSlot == 0;
								currentModel.body.visible = equipSlot == 1 || equipSlot == 2;
								currentModel.rightArm.visible = equipSlot == 1;
								currentModel.leftArm.visible = equipSlot == 1;
								currentModel.rightLeg.visible = equipSlot == 2 || equipSlot == 3;
								currentModel.leftLeg.visible = equipSlot == 2 || equipSlot == 3;
								glColor3f(brightness, brightness, brightness);
								renderBipedEntityModel(currentModel);
								glPopMatrix();
							} else if(var25 == 4) {
								aresenicRenderHeldItem(stack);
							}
						}
					}
				}

				glDepthMask(true);
				glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				glPopMatrix();
			}
		}
	}

	public void bindSkin(ArmorStandBlockEntity entity) {
		switch(entity.skin) {
			case 1:
				this.bindTexture("/title/black.png");
				break;
			case 2:
				this.bindTexture("/mob/zombie.png");
				break;
			case 3:
				this.bindTexture("/assets/armor_stand/armor_stand.png");
				break;
			default:
				this.bindTexture("/mob/char.png");
		}
	}

	public BipedEntityModel getBipedModel(float scale) {
		return new BipedEntityModel(scale);
	}

	private void renderBipedEntityModel(BipedEntityModel currentModel) {
		currentModel.body.pivotZ = 0.01F;
		currentModel.rightLeg.roll = 0.1F;
		currentModel.leftLeg.roll = -0.1F;
		currentModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / 16.0F);
	}

	private void bindArmorTexture(ArmorItem armor, int textureIndex, int slot) {
		if (armor instanceof ArmorTextureProvider provider) {
			Identifier id = provider.getTexture(armor);
			String[] textures = STATIONAPI$ARMOR_CACHE.computeIfAbsent(id, k -> new String[4]);
			if (textures[textureIndex] == null) textures[textureIndex] = stationapi_getTexturePath(id, textureIndex);
			this.bindTexture(textures[textureIndex]);
		}
		else this.bindTexture("/armor/" + armorArray[textureIndex] + (slot == 2 ? "_2.png" : "_1.png"));
	}
	private String stationapi_getTexturePath(Identifier identifier, int armorIndex) {
		return "/assets/" + identifier.namespace + "/stationapi/textures/armor/" + identifier.path + (armorIndex == 2 ? "_2.png" : "_1.png");
	}

	public void aresenicRenderHeldItem(ItemStack stack) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		this.body.rightArm.transform(0.0625F);
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
