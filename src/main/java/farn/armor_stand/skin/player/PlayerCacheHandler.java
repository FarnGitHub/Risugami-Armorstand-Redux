package farn.armor_stand.skin.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;

import java.lang.reflect.Type;

public class PlayerCacheHandler implements InstanceCreator<BipedEntityModel> {
    @Override
    public BipedEntityModel createInstance(Type type) {
        return new BipedEntityModel();
    }

    public static BipedEntityModel cloneBipedEntity(BipedEntityModel t) {
        Gson gson = new GsonBuilder().registerTypeAdapter(BipedEntityModel.class, new PlayerCacheHandler()).create();
        String json = gson.toJson(t);
        return gson.fromJson(json, t.getClass());
    }

    public static BipedEntityModel cloneBipedEntity(FakePlayer plr) {
        PlayerEntityRenderer renderer = getPlayerRender(plr);
        return cloneBipedEntity(renderer.bipedModel);
    }

    private static PlayerEntityRenderer getPlayerRender(FakePlayer player) {
        return (PlayerEntityRenderer) EntityRenderDispatcher.INSTANCE.get(player);
    }
}
