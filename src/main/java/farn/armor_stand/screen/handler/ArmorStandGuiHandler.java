package farn.armor_stand.screen.handler;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.screen.ArmorStandScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;

public class ArmorStandGuiHandler {
    public static final ScreenFactory screen = new ScreenFactory();
    public static final InventoryFactory inventory = new InventoryFactory();

    public static class ScreenFactory implements GuiHandler.ScreenFactory {

        @Override
        public Screen create(PlayerEntity player, Inventory inventory, MessagePacket packet) {
            if(inventory instanceof ArmorStandBlockEntity armorStandBlockEntity) {
                armorStandBlockEntity.skin = packet.bytes[0];
                return new ArmorStandScreen(player.inventory, armorStandBlockEntity);
            }
            return null;
        }
    }

    public static class InventoryFactory implements GuiHandler.InventoryFactory {

        @Override
        public Inventory create() {
            return new ArmorStandBlockEntity();
        }
    }

}
