package farn.armor_stand.screen.inventory;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ArmorStandScreenHandler extends ScreenHandler {
	public ArmorStandBlockEntity armorStandEntity;
	public final Slot[] armorStandSlots;

	public ArmorStandScreenHandler(Inventory inv, ArmorStandBlockEntity armorStand) {
		this.armorStandEntity = armorStand;
		this.armorStandSlots = new Slot[armorStandEntity.size()];
		//item slot for armor stand
		this.addSlot(armorStandSlots[4] = new Slot(armorStand, 4, 46, 36));
		//armor slot for armor stand
		int armorSlot = 0;
		for(int y = 0; y < 2; ++y)
			for(int x = 0; x < 2; ++x)
				this.addSlot(armorStandSlots[armorSlot] =
						new ArmorStandInventorySlot(armorStand, armorSlot, 8 + x * 18, 18 + y * 18, armorSlot++));

		//player inventory
		for(int y = 0; y < 3; ++y)
			for(int x = 0; x < 9; ++x)
				this.addSlot(new Slot(inv, x + (y + 1) * 9, 8 + x * 18, 68 + y * 18));

		//player hotbar
		for(int i = 0; i < 9; ++i)
			this.addSlot(new Slot(inv, i, 8 + i * 18, 126));
	}

	public boolean canUse(PlayerEntity plr) {
		return this.armorStandEntity.canPlayerUse(plr);
	}

	public ItemStack quickMove(int slotId) {
		ItemStack newStack = null;
		Slot slot = (Slot)this.slots.get(slotId);
		if (slot != null && slot.hasStack()) {
			ItemStack stack = slot.getStack();
			newStack = stack.copy();
			if(slotId >= 5 && slotId <= 41) {
				int armorSlot = getValidSlotForArmor(slot);
				if (armorSlot >= 0)
					this.insertItem(stack, armorSlot, armorSlot + 1, false);
				else if (slotId <= 31)
					this.insertItem(stack, 32, 41, false);
				else
					this.insertItem(stack, 5, 31, false);
			} else
				this.insertItem(stack, 5, 41, false);

			if (stack.count <= 0)
				slot.setStack(null);
			else
				slot.markDirty();

			if (stack.count == newStack.count) return null;

			slot.onTakeItem(stack);
		}

		return newStack;
	}

	private int getValidSlotForArmor(Slot slotIndex) {
		ItemStack stack = slotIndex.getStack();
		if(stack != null)
			for(Slot slot : this.armorStandSlots)
				if(!slot.hasStack() && slot.canInsert(stack))
					return slot.id;
		return -1;
	}
}
