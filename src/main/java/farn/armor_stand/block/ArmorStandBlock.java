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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ArmorStandBlock extends TemplateBlockWithEntity {

	public ArmorStandBlock(Identifier identifier, Material mat) {
		super(identifier, 1, mat);
		this.setBoundingBox(0.1F, 0.0F, 0.1F, 0.9F, 0.1F, 0.9F);
		this.textureId = Block.STONE.textureId;
	}

	public void neighborUpdate(World world, int x, int y, int z, int id) {
		if(!world.method_1783(x, y - 1, z)) {
			this.dropStacks(world, x, y, z, world.getBlockMeta(x, y, z));
			this.onBreak(world,x,y,z);
			world.setBlock(x, y, z, 0);
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

	public void onPlaced(World world, int x, int y, int z, LivingEntity entity) {
		byte var6 = (byte)(MathHelper.floor((double)((entity.yaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15);
		world.setBlockMeta(x, y, z, var6);
	}

	public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
		if(!world.isRemote && world.getBlockEntity(x, y, z) instanceof ArmorStandBlockEntity armorEntity) {
			GuiHelper.openGUI(
					player,
					ArmorStandStationAPI.NAMESPACE.id("armor_stand_gui"),
					armorEntity,
					new ArmorStandScreenHandler(player.inventory, armorEntity),
					new Consumer<MessagePacket>()
			{
				@Override
				public void accept(MessagePacket messagePacket) {
					messagePacket.bytes = new byte[1];
					messagePacket.bytes[0] = armorEntity.skin;
				}
			});
		}
		return true;
	}

	public void onBreak(World world, int x, int y, int z) {
		if(!world.isRemote) {
			Inventory var5 = (Inventory)world.getBlockEntity(x, y, z);

			for(int index = 0; index < var5.size(); ++index) {
				ItemStack stack = var5.getStack(index);
				if(stack != null) {
					float offsetX = world.random.nextFloat() * 0.8F + 0.1F;
					float offsetY = world.random.nextFloat() * 0.8F + 0.1F;
					float offsetZ = world.random.nextFloat() * 0.8F + 0.1F;

					while(stack.count > 0) {
						int count = world.random.nextInt(21) + 10;
						if(count > stack.count) {
							count = stack.count;
						}

						stack.count -= count;
						ItemEntity item = new ItemEntity(world, x + offsetX, y + offsetY, z + offsetZ, new ItemStack(stack.itemId, count, stack.getDamage()));
						float extraOffset = 0.05F;
						item.velocityX = (world.random.nextGaussian() * extraOffset);
						item.velocityY = (world.random.nextGaussian() * extraOffset + 0.2F);
						item.velocityZ = (world.random.nextGaussian() * extraOffset);
						world.spawnEntity(item);
					}
				}
			}
		}

		super.onBreak(world, x, y, z);
	}

	protected BlockEntity createBlockEntity() {
		return new ArmorStandBlockEntity();
	}

}
