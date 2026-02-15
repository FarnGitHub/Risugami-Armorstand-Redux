package farn.armor_stand.skin.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class PlayerModelCache {
    public String skinUrl;
    public BipedEntityModel model;

    public PlayerModelCache(String url, BipedEntityModel model) {
        this.skinUrl = url;
        this.model = model;
    }

    private static BipedEntityModel cloneModel(BipedEntityModel t) {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(BipedEntityModel.class, new BipedModelGsonCreator()).
                create();
        return gson.fromJson(gson.toJson(t), t.getClass());
    }

    public static BipedEntityModel clonePlayerModel(FakePlayerEntity plr) {
        return cloneModel(getPlayerRender(plr).bipedModel);
    }

    private static PlayerEntityRenderer getPlayerRender(FakePlayerEntity player) {
        return (PlayerEntityRenderer)EntityRenderDispatcher.INSTANCE.get(player);
    }
}
