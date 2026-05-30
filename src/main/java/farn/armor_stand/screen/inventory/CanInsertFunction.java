package farn.armor_stand.screen.inventory;

import net.minecraft.item.ItemStack;

public interface CanInsertFunction {
    boolean canInsert(ArmorStandInventorySlot slot, ItemStack stack);
}
