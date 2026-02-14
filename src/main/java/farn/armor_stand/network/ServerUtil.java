package farn.armor_stand.network;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.SERVER)
public class ServerUtil {

    public static void sendUpdateToPlayer(BlockEntity entity) {
        server.playerManager.sendToAround(
                entity.x, entity.y, entity.z, 64,
                entity.world.dimension.id, entity.createUpdatePacket());
    }

    public static void sendPlacerUpdateToServer(ArmorStandBlockEntity entity) {
        server.playerManager.sendToAround(
                entity.x, entity.y, entity.z, 64,
                entity.world.dimension.id, new PacketS2CUpdatePlacer(entity));
    }

    public static MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();

}
