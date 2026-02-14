package farn.armor_stand.screen;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;

public class ArmorStandScreenFactory implements GuiHandler.ScreenFactory {
    @Override
    public Screen create(PlayerEntity player, Inventory inventory, MessagePacket packet) {
        if(inventory instanceof ArmorStandBlockEntity armorStandBlockEntity) {
            armorStandBlockEntity.skin = packet.bytes[0];
            return new ArmorStandScreen(player.inventory, armorStandBlockEntity);
        }
        return null;
    }
}
