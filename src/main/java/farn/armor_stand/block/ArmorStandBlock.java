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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;


public class ArmorStandBlock extends TemplateBlockWithEntity {

	public ArmorStandBlock(Identifier identifier) {
		super(identifier, 1, Material.WOOD);
		this.setBoundingBox(0.1F, 0.0F, 0.1F, 0.9F, 0.1F, 0.9F);
		this.textureId = Block.STONE.textureId;
		this.setTranslationKey(ArmorStandStationAPI.NAMESPACE, "armor_stand_block");
		this.setSoundGroup(Block.STONE_SOUND_GROUP);
		this.setHardness(0.1F);
	}

	@Override
	public void neighborUpdate(World world, int x, int y, int z, int id) {
		super.neighborUpdate(world, x, y, z, id);
		if(!world.isAir(x, y + 1, z) ||
					!world.shouldSuffocate(x, y - 1, z)) {
				if(!world.isRemote) this.dropStacks(world, x,y,z, world.getBlockMeta(x,y,z));
				world.setBlock(x, y, z, 0);
		}
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
		this.setBoundingBox(0.1F, 0.0F, 0.1F, 0.9F, 1.95F, 0.9F);
		super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
		this.setBoundingBox(0.1F, 0.0F, 0.1F, 0.9F, 0.1F, 0.9F);
	}

	@Override
	public void onPlaced(World world, int x, int y, int z, LivingEntity entity) {
		super.onPlaced(world, x, y, z, entity);
		byte directionMeta = (byte)(MathHelper.floor((double)((entity.yaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15);
		world.setBlockMeta(x, y, z, directionMeta);
		if(world.getBlockEntity(x, y, z)
				instanceof ArmorStandBlockEntity armorStandBlock
				&& entity instanceof PlayerEntity plr)
			armorStandBlock.placer = plr.name;
	}

	@Override
	public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
		if(!world.isRemote && world.getBlockEntity(x, y, z)
				instanceof ArmorStandBlockEntity armorEntity)
			GuiHelper.openGUI(
					player,
					ArmorStandStationAPI.NAMESPACE.id("armor_stand_gui"),
					armorEntity,
					new ArmorStandScreenHandler(player.inventory, armorEntity),
					(messagePacket) ->
					messagePacket.bytes = new byte[]{armorEntity.skin});
		return true;
	}

	@Override
	public void onBreak(World world, int x, int y, int z) {
		if(!world.isRemote) {
			if(world.getBlockEntity(x, y, z)
					instanceof ArmorStandBlockEntity inv) {
				for(int index = 0; index < inv.size(); ++index) {
					ItemStack stack = inv.getStack(index);
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
		}

		super.onBreak(world, x, y, z);
	}

	@Override
	protected BlockEntity createBlockEntity() {
		return new ArmorStandBlockEntity();
	}

	@Override
	public boolean canPlaceAt(World world, int x, int y, int z) {
		return world.shouldSuffocate(x,y-1,z) &&
			   super.canPlaceAt(world,x,y,z) &&
			   super.canPlaceAt(world,x,y+1,z);
	}
}
