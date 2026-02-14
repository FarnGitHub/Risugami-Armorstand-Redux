package farn.armor_stand.network;

import farn.armor_stand.ArmorStandStationAPI;
import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class PacketS2CArmorStandEntityUpdate extends Packet implements ManagedPacket<PacketS2CArmorStandEntityUpdate> {

    public NbtCompound data;
    public int dataSize = 0;
    public static final PacketType<PacketS2CArmorStandEntityUpdate> TYPE = PacketType.builder(true, false, PacketS2CArmorStandEntityUpdate::new).build();

    public PacketS2CArmorStandEntityUpdate() {
        worldPacket = true;
    }

    @Environment(EnvType.SERVER)
    public PacketS2CArmorStandEntityUpdate(ArmorStandBlockEntity te) {
        this();
        data = new NbtCompound();
        te.writeNbt(data);
    }

    @Override
    public void read(DataInputStream stream) {
        data = readNbt(stream);
    }

    public NbtCompound readNbt(DataInputStream dis) {
        try {
            int length = Short.toUnsignedInt(dis.readShort());
            if (length == 0) {
                return null;
            } else {
                byte[] data = new byte[length];
                dis.readFully(data);
                return NbtIo.readCompressed(new ByteArrayInputStream(data));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeNbt(NbtCompound tag, DataOutputStream dos) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NbtIo.writeCompressed(tag, baos);
            byte[] buffer = baos.toByteArray();
            dos.writeShort((short)buffer.length);
            dos.write(buffer);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        int before = stream.size();
        writeNbt(data, stream);
        dataSize = stream.size() - before;
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> handleClient(networkHandler),()->{});
    }

    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler networkHandler) {
        try {
            PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
            int x = data.getInt("x");
            int y = data.getInt("y");
            int z = data.getInt("z");
            if(player.world.getBlockId(x,y,z) == ArmorStandStationAPI.armorStand.id &&
                    player.world.getBlockEntity(x,y,z) instanceof ArmorStandBlockEntity armorStandBlock) {
                armorStandBlock.readNbt(data);
                player.world.setBlockDirty(x,y,z);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int size() {
        return dataSize;
    }

    @Override
    public @NotNull PacketType<PacketS2CArmorStandEntityUpdate> getType() {
        return TYPE;
    }
}
