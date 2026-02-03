package farn.armor_stand.skin.player;

import com.google.gson.InstanceCreator;
import net.minecraft.client.render.entity.model.BipedEntityModel;

import java.lang.reflect.Type;

public class BipedModelCreator implements InstanceCreator<BipedEntityModel> {
    @Override
    public BipedEntityModel createInstance(Type type) {
        return new BipedEntityModel();
    }
}
