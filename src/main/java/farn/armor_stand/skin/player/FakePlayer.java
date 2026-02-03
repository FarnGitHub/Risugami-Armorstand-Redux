package farn.armor_stand.skin.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.OtherPlayerEntity;

public class FakePlayer extends OtherPlayerEntity {
    public FakePlayer(String name) {
        super(Minecraft.INSTANCE.world, name);
    }
}
