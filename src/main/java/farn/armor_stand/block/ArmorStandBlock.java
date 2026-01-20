package farn.armor_stand.block;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.screen.ArmorStandScreenHandler;
import farn.armor_stand.ArmorStandStationAPI;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;

public class ArmorStandBlock extends TemplateBlockWithEntity {

	public ArmorStandBlock(Identifier identifier, Material var2) {
		super(identifier, 1, var2);
		this.setBoundingBox(0.1F, 0.0F, 0.1F, 0.9F, 0.1F, 0.9F);
		this.textureId = Block.STONE.textureId;
	}

	public void neighborUpdate(World var1, int var2, int var3, int var4, int var5) {
		if(!var1.method_1783(var2, var3 - 1, var4)) {
			this.dropStacks(var1, var2, var3, var4, var1.getBlockMeta(var2, var3, var4));
			var1.setBlock(var2, var3, var4, 0);
		}

	}

	public boolean isOpaque() {
		return false;
	}

	public boolean isFullCube() {
		return false;
	}

	public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
		this.setBoundingBox(0.1F, 0.0F, 0.1F, 0.9F, 1.95F, 0.9F);
		super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
		this.setBoundingBox(0.1F, 0.0F, 0.1F, 0.9F, 0.1F, 0.9F);
	}

	public void onPlaced(World var1, int var2, int var3, int var4, LivingEntity var5) {
		byte var6 = (byte)(MathHelper.floor((double)((var5.yaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15);
		var1.setBlockMeta(var2, var3, var4, var6);
	}

	public boolean onUse(World var1, int var2, int var3, int var4, PlayerEntity var5) {
		if(!var1.isRemote && var1.getBlockEntity(var2, var3, var4) instanceof ArmorStandBlockEntity var7) {
			GuiHelper.openGUI(var5, ArmorStandStationAPI.NAMESPACE.id("armor_stand_gui"), var7, new ArmorStandScreenHandler(var5.inventory, var7));
		}
		return true;
	}

	public void onBreak(World var1, int var2, int var3, int var4) {
		if(!var1.isRemote) {
			Inventory var5 = (Inventory)var1.getBlockEntity(var2, var3, var4);

			for(int var6 = 0; var6 < var5.size(); ++var6) {
				ItemStack var7 = var5.getStack(var6);
				if(var7 != null) {
					float var8 = var1.random.nextFloat() * 0.8F + 0.1F;
					float var9 = var1.random.nextFloat() * 0.8F + 0.1F;
					float var10 = var1.random.nextFloat() * 0.8F + 0.1F;

					while(var7.count > 0) {
						int var11 = var1.random.nextInt(21) + 10;
						if(var11 > var7.count) {
							var11 = var7.count;
						}

						var7.count -= var11;
						ItemEntity var12 = new ItemEntity(var1, (double)((float)var2 + var8), (double)((float)var3 + var9), (double)((float)var4 + var10), new ItemStack(var7.itemId, var11, var7.getDamage()));
						float var13 = 0.05F;
						var12.velocityX = (double)((float)var1.random.nextGaussian() * var13);
						var12.velocityY = (double)((float)var1.random.nextGaussian() * var13 + 0.2F);
						var12.velocityZ = (double)((float)var1.random.nextGaussian() * var13);
						var1.spawnEntity(var12);
					}
				}
			}
		}

		super.onBreak(var1, var2, var3, var4);
	}

	protected BlockEntity createBlockEntity() {
		return new ArmorStandBlockEntity();
	}
}
