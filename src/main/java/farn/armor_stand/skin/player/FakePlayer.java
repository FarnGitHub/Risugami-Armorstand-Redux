package farn.armor_stand.skin.player;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.OtherPlayerEntity;

public class FakePlayer extends OtherPlayerEntity {
    public ArmorStandBlockEntity armorStandBlockEntity;
    private PlayerCache plrCache;

    public FakePlayer(ArmorStandBlockEntity blockEntity) {
        super(Minecraft.INSTANCE.world, blockEntity.placer);
        this.armorStandBlockEntity = blockEntity;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void updateCapeUrl() {
        super.updateCapeUrl();
        if(plrCache != null)
            plrCache.url = this.skinUrl;
    }

    public void setPlayerCache(PlayerCache plrCache) {
        this.plrCache = plrCache;
    }


}
