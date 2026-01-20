package farn.armor_stand.inventory_slot;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ArmorStandSlot extends Slot {
	private final int armorSlot;

	public ArmorStandSlot(Inventory var1, int var2, int var3, int var4, int var5) {
		super(var1, var2, var3, var4);
		armorSlot = var5;
	}

	public boolean canInsert(ItemStack itemStack1) {
		return itemStack1.getItem() instanceof ArmorItem armor ? armor.equipmentSlot == this.armorSlot : itemStack1.getItem() instanceof BlockItem blockIt && blockIt.getBlock().id == Block.PUMPKIN.id && this.armorSlot == 0;
	}
}
