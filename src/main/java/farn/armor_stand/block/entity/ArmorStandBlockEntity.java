package farn.armor_stand.block.entity;

import farn.armor_stand.network.PacketS2CArmorStandEntityUpdate;
import farn.armor_stand.network.ServerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;

public class ArmorStandBlockEntity extends BlockEntity implements Inventory {
	public ItemStack[] items = new ItemStack[5];
	public byte skin = 0;
	public String placer = "";

	@Override
	public int size() {
		return this.items.length;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.items[slot];
	}

	@Override
	public ItemStack removeStack(int slot, int stack) {
		if(this.items[slot] != null) {
			ItemStack removedStack;
			if(this.items[slot].count <= stack) {
				removedStack = this.items[slot];
				this.items[slot] = null;
			} else {
				removedStack = this.items[slot].split(stack);
				if(this.items[slot].count == 0) {
					this.items[slot] = null;
				}
			}
			this.markDirty();
			return removedStack;
		} else {
			return null;
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.items[slot] = stack;
		if(stack != null && stack.count > this.getMaxCountPerStack()) {
			stack.count = this.getMaxCountPerStack();
		}
		this.markDirty();
	}

	@Override
	public String getName() {
		return "Armor Stand";
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		NbtList itemsList = nbt.getList("Items");
		this.items = new ItemStack[this.size()];
		this.skin = nbt.getByte("Skin");
		this.placer = nbt.getString("Placer");

		for(int index = 0; index < itemsList.size(); ++index) {
			NbtCompound itemData = (NbtCompound)itemsList.get(index);
			byte targetSlot = itemData.getByte("Slot");
			if (targetSlot >= 0 && targetSlot < this.items.length) {
				this.items[targetSlot] = new ItemStack(itemData);
			}
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		NbtList list = new NbtList();

		for(int index = 0; index < this.items.length; ++index) {
			if (this.items[index] != null) {
				NbtCompound itemData = new NbtCompound();
				itemData.putByte("Slot", (byte)index);
				this.items[index].writeNbt(itemData);
				list.add(itemData);
			}
		}

		nbt.put("Items", list);
		nbt.putByte("Skin", this.skin);
		if(!this.placer.isEmpty()) {
			nbt.putString("Placer", this.placer);
		}
	}

	@Environment(EnvType.SERVER)
	@Override
	public Packet createUpdatePacket() {
		return new PacketS2CArmorStandEntityUpdate(this);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity var1) {
		return this.world.getBlockEntity(this.x, this.y, this.z) == this && var1.getDistance((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 64.0D;
	}

	@Environment(EnvType.SERVER)
	@Override
	public void markDirty() {
		super.markDirty();
		if(world != null)
			ServerUtil.sendUpdateAroundTrackedPlayer(this);
	}

}
