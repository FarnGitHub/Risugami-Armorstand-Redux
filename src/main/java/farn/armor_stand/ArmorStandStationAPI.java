package farn.armor_stand;

import farn.armor_stand.block.ArmorStandBlock;
import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.block.entity.ArmorStandBlockEntityRenderer;
import farn.armor_stand.network.PacketC2SChangeArmorStandSkin;
import farn.armor_stand.network.PacketS2CArmorStandEntityUpdate;
import farn.armor_stand.screen.ArmorStandScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mine_diver.unsafeevents.listener.EventListener;
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

@SuppressWarnings("unused")
public class ArmorStandStationAPI {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static Logger LOGGER = Null.get();

    public static ArmorStandBlock armorStand;

    @Environment(EnvType.CLIENT)
    @EventListener
    public void registerGuiHandler(GuiHandlerRegistryEvent event) {
        event.register(
            NAMESPACE.id("armor_stand_gui"),
             new GuiHandler(new ArmorStandScreenFactory(), ArmorStandBlockEntity::new));
    }

    @Environment(EnvType.CLIENT)
    @EventListener
    public void registerBlockEntityRenderer(BlockEntityRendererRegisterEvent event) {
        event.renderers.put(
                ArmorStandBlockEntity.class,
                new ArmorStandBlockEntityRenderer());
    }

    @EventListener
    public void registerBlock(BlockRegistryEvent event) {
        armorStand = new ArmorStandBlock(NAMESPACE.id("armor_stand_block"));
    }

    @EventListener
    public void registerBlockEntity(BlockEntityRegisterEvent event) {
        event.register(
                ArmorStandBlockEntity.class,
                NAMESPACE.id("armor_stand_block_entity").toString());
    }

    @EventListener
    public void registerPacket(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE,
                NAMESPACE.id("armor_stand_update_packet"),
                PacketS2CArmorStandEntityUpdate.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE,
                NAMESPACE.id("armor_stand_skin_packet"),
                PacketC2SChangeArmorStandSkin.TYPE);
    }

}
