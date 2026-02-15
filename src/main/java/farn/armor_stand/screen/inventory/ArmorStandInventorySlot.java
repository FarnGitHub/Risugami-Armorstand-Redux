package farn.armor_stand.screen.inventory;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ArmorStandInventorySlot extends Slot {
	public final int armorSlot;

	public ArmorStandInventorySlot(Inventory inv, int index, int x, int y, int armorSlot) {
		super(inv, index, x, y);
		this.armorSlot = armorSlot;
	}

	public boolean canInsert(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem armor ?
				armor.equipmentSlot == this.armorSlot :
				stack.getItem() instanceof BlockItem blockIt &&
						blockIt.getBlock().id == Block.PUMPKIN.id &&
						this.armorSlot == 0;
	}
}
