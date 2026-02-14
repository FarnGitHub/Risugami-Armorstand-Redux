package farn.armor_stand.network;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketS2CUpdatePlacer extends Packet implements ManagedPacket<PacketS2CUpdatePlacer> {
    public static final PacketType<PacketS2CUpdatePlacer> TYPE = PacketType.builder(true, false, PacketS2CUpdatePlacer::new).build();
    int x;
    int y;
    int z;
    public String placer;

    @Environment(EnvType.SERVER)
    public PacketS2CUpdatePlacer(ArmorStandBlockEntity entity) {
        this();
        this.placer = entity.placer;
        this.x = entity.x;
        this.y = entity.y;
        this.z = entity.z;
    }

    public PacketS2CUpdatePlacer() {
        this.worldPacket = true;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.x = stream.readInt();
            this.y = stream.readInt();
            this.z = stream.readInt();
            this.placer = stream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(this.x);
            stream.writeInt(this.y);
            stream.writeInt(this.z);
            stream.writeUTF(this.placer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> {
            this.applyClient(PlayerHelper.getPlayerFromGame().world);
        }, () -> {});
    }

    @Environment(EnvType.CLIENT)
    private void applyClient(World world) {
        if(world.getBlockEntity(x,y,z) instanceof ArmorStandBlockEntity entity) {
            entity.placer = placer;
        }
    }

    @Override
    public int size() {
        return 12 + placer.length();
    }

    @Override
    public @NotNull PacketType<PacketS2CUpdatePlacer> getType() {
        return TYPE;
    }
}
