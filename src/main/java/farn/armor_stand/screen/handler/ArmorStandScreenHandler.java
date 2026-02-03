package farn.armor_stand.screen.handler;

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
	private final Slot[] armorStandInvetorySlots;

	public ArmorStandScreenHandler(Inventory inv, ArmorStandBlockEntity armorStand) {
		this.inv = inv;
		this.armorStandEntity = armorStand;
		this.armorStandInvetorySlots = new Slot[armorStandEntity.size()];
		int i = 0;
		//item slot for armor stand
		this.addSlot(armorStandInvetorySlots[4] = new Slot(armorStand, 4, 46, 36));

		int x;
		int y;
		//armor slot for armor stand
		for(y = 0; y < 2; ++y) {
			for(x = 0; x < 2; ++x) {
				this.addSlot(armorStandInvetorySlots[i] = new ArmorStandSlot(armorStand, i, 8 + x * 18, 18 + y * 18, i++));
			}
		}

		//player inventory
		for(y = 0; y < 3; ++y) {
			for(x = 0; x < 9; ++x) {
				this.addSlot(new Slot(inv, x + (y + 1) * 9, 8 + x * 18, 68 + y * 18));
			}
		}

		//player hotbar
		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inv, i, 8 + i * 18, 126));
		}
	}

	public boolean canUse(PlayerEntity plr) {
		return this.inv.canPlayerUse(plr);
	}

	public ItemStack quickMove(int slotId) {
		ItemStack newStack = null;
		Slot slot = (Slot)this.slots.get(slotId);
		if (slot != null && slot.hasStack()) {
			ItemStack stack = slot.getStack();
			newStack = stack.copy();
			//check if the slot is in player inventory, if not then move to player inventory
			if(slotId >= 5 && slotId <= 41) {
				//get the armor stand slot that the item can be put in
				//return -1 if there isn't one
				int armorSlot = getValidSlotForArmor(slot);
				if(armorSlot >= 0) {
					//insert in one of the armor stand slot
					this.insertItem(stack, armorSlot, armorSlot + 1, false);
				} else if(slotId <= 31) {
					//move to hotbar if it inside player external inventory
					this.insertItem(stack, 32, 41, false);
				} else {
					//move to player external inventory if it inside hotbar
					this.insertItem(stack, 5, 31, false);
				}
			} else {
				//move to player inventory if it inside armor stand slot
				this.insertItem(stack, 5, 41, false);
			}

			if (stack.count <= 0) {
				slot.setStack(null);
			} else {
				slot.markDirty();
			}

			if (stack.count == newStack.count) {
				return null;
			}

			slot.onTakeItem(stack);
		}

		return newStack;
	}

	private int getValidSlotForArmor(Slot slotIndex) {
		ItemStack stack = slotIndex.getStack();
		if(stack != null)
			for(Slot slot : this.armorStandInvetorySlots)
				if(!slot.hasStack() && slot.canInsert(stack))
					return slot.id;
		return -1;
	}
}
