package farn.armor_stand.skin.player;

import farn.armor_stand.block.entity.ArmorStandBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.OtherPlayerEntity;
import net.minecraft.client.texture.SkinImageProcessor;

public class FakePlayerEntity extends OtherPlayerEntity {
    private PlayerModelCache plrCache;

    public FakePlayerEntity(ArmorStandBlockEntity blockEntity) {
        super(Minecraft.INSTANCE.world, blockEntity.placer);
    }

    public FakePlayerEntity(String name) {
        super(Minecraft.INSTANCE.world, name);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void updateCapeUrl() {
        super.updateCapeUrl();
        if(plrCache != null)
            plrCache.skinUrl = this.skinUrl;
    }

    public void setPlayerCache(PlayerModelCache plrCache) {
        this.plrCache = plrCache;
    }

    public void downloadSkin() {
        if(name != null && !this.name.isEmpty() && this.skinUrl.startsWith("http://s3.amazonaws.com/MinecraftSkins/"))
            Minecraft.INSTANCE.textureManager.
                    downloadImage(this.skinUrl, new SkinImageProcessor());
    }


}
