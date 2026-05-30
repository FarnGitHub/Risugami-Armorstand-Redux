package farn.armor_stand.network.packet;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import farn.armor_stand.screen.inventory.ArmorStandScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ArmorStandSkinPacket extends Packet implements ManagedPacket<ArmorStandSkinPacket> {

    public byte skin;
    public static final PacketType<ArmorStandSkinPacket>
            TYPE = PacketType.builder(false, true,
            ArmorStandSkinPacket::new).build();

    public ArmorStandSkinPacket() {
        this.worldPacket = true;
    }

    @Environment(EnvType.CLIENT)
    public ArmorStandSkinPacket(byte skin) {
        this();
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
        if(FabricLoader.getInstance().
                getEnvironmentType() == EnvType.SERVER)
            applyServer(networkHandler);
    }

    @Environment(EnvType.SERVER)
    public void applyServer(NetworkHandler networkHandler) {
        PlayerEntity player =
                PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.currentScreenHandler instanceof ArmorStandScreenHandler handler) {
            ArmorStandBlockEntity armorStandBlock = handler.armorStandEntity;
            if(armorStandBlock.canPlayerUse(player)
                    && player.currentScreenHandler instanceof ArmorStandScreenHandler) {
                armorStandBlock.skin = skin;
                armorStandBlock.markDirty();
            }
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    @NotNull
    public PacketType<ArmorStandSkinPacket> getType() {
        return TYPE;
    }
}
