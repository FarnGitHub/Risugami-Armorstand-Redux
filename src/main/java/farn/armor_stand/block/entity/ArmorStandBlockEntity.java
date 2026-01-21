package farn.armor_stand.block.entity;

import farn.armor_stand.packet.ArmorStandEntityUpdatePacket;
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
	public byte skin = 3;
	private boolean init = false;

	public int size() {
		return this.items.length;
	}

	public void tick() {
		super.tick();
		if(!init) {
			init = true;
			markDirty();
		}
	}

	public ItemStack getStack(int var1) {
		return this.items[var1];
	}

	public ItemStack removeStack(int var1, int var2) {
		if(this.items[var1] != null) {
			ItemStack var3;
			if(this.items[var1].count <= var2) {
				var3 = this.items[var1];
				this.items[var1] = null;
				this.markDirty();
				return var3;
			} else {
				var3 = this.items[var1].split(var2);
				if(this.items[var1].count == 0) {
					this.items[var1] = null;
				}

				this.markDirty();
				return var3;
			}
		} else {
			return null;
		}
	}

	public void setStack(int var1, ItemStack var2) {
		this.items[var1] = var2;
		if(var2 != null && var2.count > this.getInventoryStackLimit()) {
			var2.count = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	public String getName() {
		return "Armor Stand";
	}

	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		NbtList var2 = nbt.getList("Items");
		this.items = new ItemStack[this.size()];
		this.skin = nbt.getByte("Skin");

		for(int var3 = 0; var3 < var2.size(); ++var3) {
			NbtCompound var4 = (NbtCompound)var2.get(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.items.length) {
				this.items[var5] = new ItemStack(var4);
			}
		}
	}

	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		NbtList var2 = new NbtList();

		for(int var3 = 0; var3 < this.items.length; ++var3) {
			if (this.items[var3] != null) {
				NbtCompound var4 = new NbtCompound();
				var4.putByte("Slot", (byte)var3);
				this.items[var3].writeNbt(var4);
				var2.add(var4);
			}
		}

		nbt.put("Items", var2);
		nbt.putByte("Skin", (byte)this.skin);
	}

	public int getInventoryStackLimit() {
		return 1;
	}

	@Environment(EnvType.SERVER)
	public Packet createUpdatePacket() {
		return new ArmorStandEntityUpdatePacket(this);
	}

	public boolean canPlayerUse(PlayerEntity var1) {
		return var1.getDistance((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 64.0D;
	}
}
