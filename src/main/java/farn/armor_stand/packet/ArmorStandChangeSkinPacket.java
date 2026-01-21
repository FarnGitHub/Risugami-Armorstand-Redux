package farn.armor_stand.packet;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.screen.ArmorStandScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ArmorStandChangeSkinPacket extends Packet implements ManagedPacket<ArmorStandChangeSkinPacket> {

    public byte skin;

    public static final PacketType<ArmorStandChangeSkinPacket> TYPE = PacketType.builder(false, true, ArmorStandChangeSkinPacket::new).build();

    public ArmorStandChangeSkinPacket() {
    }

    @Environment(EnvType.CLIENT)
    public ArmorStandChangeSkinPacket(byte skin) {
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
        return 5;
    }

    @Override
    public PacketType<ArmorStandChangeSkinPacket> getType() {
        return TYPE;
    }
}
