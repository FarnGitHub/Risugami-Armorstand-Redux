package farn.armor_stand.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.List;

@Environment(EnvType.SERVER)
public class ServerUtil {

    public static void sendUpdateToPlayer(BlockEntity entity) {
        Packet packet = entity.createUpdatePacket();
        List<ServerPlayNetworkHandler> list = ServerUtil.server.connections.connections;
        list.forEach(handler -> handler.sendPacket(packet));
    }

    public static MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();

}
