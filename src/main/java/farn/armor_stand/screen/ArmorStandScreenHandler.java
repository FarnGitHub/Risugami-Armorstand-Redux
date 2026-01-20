package farn.armor_stand.screen;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.inventory_slot.ArmorStandSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ArmorStandScreenHandler extends ScreenHandler {
	private Inventory inv;

	public ArmorStandScreenHandler(Inventory var1, ArmorStandBlockEntity var2) {
		this.inv = var1;
		int var5 = 0;
		this.addSlot(new Slot(var2, 4, 46, 36));

		int var3;
		int var4;
		for(var4 = 0; var4 < 2; ++var4) {
			for(var3 = 0; var3 < 2; ++var3) {
				this.addSlot(new ArmorStandSlot(var2, var5, 8 + var3 * 18, 18 + var4 * 18, var5++));
			}
		}

		for(var4 = 0; var4 < 3; ++var4) {
			for(var3 = 0; var3 < 9; ++var3) {
				this.addSlot(new Slot(var1, var3 + (var4 + 1) * 9, 8 + var3 * 18, 68 + var4 * 18));
			}
		}

		for(var5 = 0; var5 < 9; ++var5) {
			this.addSlot(new Slot(var1, var5, 8 + var5 * 18, 126));
		}

	}

	public boolean canUse(PlayerEntity var1) {
		return this.inv.canPlayerUse(var1);
	}

	public ItemStack quickMove(int var1) {
		return null;
	}
}
