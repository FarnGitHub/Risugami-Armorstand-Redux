package farn.armor_stand.network;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.screen.handler.ArmorStandScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketC2SChangeArmorStandSkin extends Packet implements ManagedPacket<PacketC2SChangeArmorStandSkin> {

    public byte skin;

    public static final PacketType<PacketC2SChangeArmorStandSkin> TYPE = PacketType.builder(false, true, PacketC2SChangeArmorStandSkin::new).build();

    public PacketC2SChangeArmorStandSkin() {
    }

    @Environment(EnvType.CLIENT)
    public PacketC2SChangeArmorStandSkin(byte skin) {
        this.skin = skin;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            skin = stream.readByte();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeByte(skin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> {}, () -> {
            PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
            if(player.currentScreenHandler instanceof ArmorStandScreenHandler handler) {
                ArmorStandBlockEntity armorStandBlock = handler.armorStandEntity;
                if(armorStandBlock.canPlayerUse(player)) {
                    armorStandBlock.skin = skin;
                    armorStandBlock.markDirty();
                }
            }
        });
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public PacketType<PacketC2SChangeArmorStandSkin> getType() {
        return TYPE;
    }
}
