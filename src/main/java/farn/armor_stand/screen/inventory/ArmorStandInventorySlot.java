package farn.armor_stand.screen.inventory;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ArmorStandInventorySlot extends Slot {
	public final int armorSlot;
	public static final ObjectArrayList<CanInsertFunction> itemsList = new ObjectArrayList<>();

	public ArmorStandInventorySlot(Inventory inv, int index, int x, int y, int armorSlot) {
		super(inv, index, x, y);
		this.armorSlot = armorSlot;
	}

	public boolean canInsert(ItemStack stack) {
		for(CanInsertFunction function : itemsList)
			if(function.canInsert(this, stack)) return true;
		return false;
	}

	static {
		itemsList.add((slot, stack)->
			stack.getItem() instanceof ArmorItem armor && armor.equipmentSlot == slot.armorSlot
		);
		itemsList.add((slot, stack)->
			stack.getItem() instanceof BlockItem blockIt &&
				blockIt.getBlock().id == Block.PUMPKIN.id &&
				slot.armorSlot == 0
		);
	}
}
