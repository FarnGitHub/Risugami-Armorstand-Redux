package farn.armor_stand.screen;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.inventory_slot.ArmorStandSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ArmorStandScreenHandler extends ScreenHandler {
	private final Inventory inv;
	public ArmorStandBlockEntity armorStandEntity;
	private final Slot[] armorSlots;

	public ArmorStandScreenHandler(Inventory inv, ArmorStandBlockEntity armorStand) {
		this.inv = inv;
		this.armorStandEntity = armorStand;
		this.armorSlots = new Slot[armorStandEntity.size()];
		int i = 0;
		this.addSlot(armorSlots[4] = new Slot(armorStand, 4, 46, 36));

		int x;
		int y;
		for(y = 0; y < 2; ++y) {
			for(x = 0; x < 2; ++x) {
				this.addSlot(armorSlots[i] = new ArmorStandSlot(armorStand, i, 8 + x * 18, 18 + y * 18, i++));
			}
		}

		for(y = 0; y < 3; ++y) {
			for(x = 0; x < 9; ++x) {
				this.addSlot(new Slot(inv, x + (y + 1) * 9, 8 + x * 18, 68 + y * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inv, i, 8 + i * 18, 126));
		}
	}

	public boolean canUse(PlayerEntity plr) {
		return this.inv.canPlayerUse(plr);
	}

	public ItemStack quickMove(int slot) {
		ItemStack var2 = null;
		Slot var3 = (Slot)this.slots.get(slot);
		if (var3 != null && var3.hasStack()) {
			ItemStack var4 = var3.getStack();
			var2 = var4.copy();
			if(slot >= 5 && slot < 31) {
				int armorSlot = getValidSlotForArmor(var3);
				if(armorSlot >= 0) {
					this.insertItem(var4, armorSlot, armorSlot + 1, false);
				} else {
					this.insertItem(var4, 31, 41, false);
				}
			} else if(slot >= 31 && slot <= 41) {
				int armorSlot = getValidSlotForArmor(var3);
				if(armorSlot >= 0) {
					this.insertItem(var4, armorSlot, armorSlot + 1, false);
				} else {
					this.insertItem(var4, 5, 30, false);
				}
			} else {
				this.insertItem(var4, 5, 41, false);
			}

			if (var4.count == 0) {
				var3.setStack(null);
			} else {
				var3.markDirty();
			}

			if (var4.count == var2.count) {
				return null;
			}

			var3.onTakeItem(var4);
		}

		return var2;
	}

	private int getValidSlotForArmor(Slot slotIndex) {
		ItemStack stack = slotIndex.getStack();
		if(stack != null)
			for(Slot slot : this.armorSlots)
				if(!slot.hasStack() && slot.canInsert(stack))
					return slot.id;
		return -1;
	}
}
