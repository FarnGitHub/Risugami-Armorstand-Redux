package farn.armor_stand.skin.player;

import net.minecraft.client.render.entity.model.BipedEntityModel;

public class PlayerCache {
    public String url;
    public BipedEntityModel model;

    public PlayerCache(String url, BipedEntityModel model) {
        this.url = url;
        this.model = model;
    }
}
