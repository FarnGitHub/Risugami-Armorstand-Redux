package farn.armor_stand.skin.player;

import com.google.gson.Gson;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;

public class SkinCache {
    public String skin;
    public BipedEntityModel model;

    public SkinCache(String url, BipedEntityModel model) {
        this.skin = url;
        this.model = model;
    }

    private static BipedEntityModel cloneModel(BipedEntityModel t) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(t), t.getClass());
    }

    public static BipedEntityModel defaultModel() {
        return cloneModel(
                ((PlayerEntityRenderer)render().get(PlayerEntity.class)).bipedModel);
    }

    public static BipedEntityModel cloneModel(FakePlayerEntity plr) {
        return cloneModel(getRender(plr).bipedModel);
    }

    private static PlayerEntityRenderer getRender(PlayerEntity player) {
        return (PlayerEntityRenderer)render().get(player);
    }

    private static EntityRenderDispatcher render() {
        return EntityRenderDispatcher.INSTANCE;
    }
}
