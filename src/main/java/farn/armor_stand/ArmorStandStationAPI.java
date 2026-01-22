package farn.armor_stand;

import farn.armor_stand.block.ArmorStandBlock;
import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.block.entity.ArmorStandBlockEntityRenderer;
import farn.armor_stand.packet.ArmorStandChangeSkinPacket;
import farn.armor_stand.packet.ArmorStandEntityUpdatePacket;
import farn.armor_stand.screen.ArmorStandGuiHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import org.apache.logging.log4j.Logger;

public class ArmorStandStationAPI {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    public static Block armorStand;

    @EventListener
    public void registerArmorStandUI(GuiHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("armor_stand_gui"), new GuiHandler(ArmorStandGuiHandler.screen, ArmorStandGuiHandler.inventory));
    }

    @EventListener
    public void registerBlock(BlockRegistryEvent event) {
        armorStand = new ArmorStandBlock(NAMESPACE.id("armor_stand_block"), Material.WOOD).setTranslationKey(NAMESPACE, "armor_stand").setSoundGroup(Block.STONE_SOUND_GROUP).setHardness(0.1F);;
    }

    @EventListener
    public void registerBlockEntity(BlockEntityRegisterEvent event) {
        event.register(ArmorStandBlockEntity.class, NAMESPACE.id("armor_stand_block_entity").toString());
    }

    @Environment(EnvType.CLIENT)
    @EventListener
    public void registerBlockEntityRenderer(BlockEntityRendererRegisterEvent event) {
        event.renderers.put(ArmorStandBlockEntity.class, new ArmorStandBlockEntityRenderer());
    }

    @EventListener
    public void registerPacket(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE,  NAMESPACE.id("armor_stand_update_packet"), ArmorStandEntityUpdatePacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE,  NAMESPACE.id("armor_stand_skin_packet"), ArmorStandChangeSkinPacket.TYPE);
    }

}
