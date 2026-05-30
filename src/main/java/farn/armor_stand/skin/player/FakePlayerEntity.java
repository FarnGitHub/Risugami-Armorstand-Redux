package farn.armor_stand.skin.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.OtherPlayerEntity;
import net.minecraft.client.texture.SkinImageProcessor;

public class FakePlayerEntity extends OtherPlayerEntity {
    public SkinCache plrCache;

    public FakePlayerEntity(String name, boolean downloadSkin) {
        super(Minecraft.INSTANCE.world, name);
        if(downloadSkin) this.downloadSkin();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void updateCapeUrl() {
        super.updateCapeUrl();
        if(plrCache != null) {
            plrCache.skin = this.skinUrl;
            plrCache.model = SkinCache.cloneModel(this);
        }
    }

    public void setPlayerCache(SkinCache plrCache) {
        this.plrCache = plrCache;
    }

    private void downloadSkin() {
        if(name != null && !this.name.isEmpty())
            Minecraft.INSTANCE.textureManager.
                    downloadImage(this.skinUrl, new SkinImageProcessor());
    }


}
