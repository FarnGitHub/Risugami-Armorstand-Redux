package farn.armor_stand.skin;

public enum ArmorStandSkins {
    PLAYER("Player", "/mob/char.png"),
    WOOD("Wood", "/assets/armor_stand/armor_stand.png"),
    STEVE("Steve", "/mob/char.png"),
    ZOMBIE("Zombie", "/mob/zombie.png");

    public final String name;
    public final String texture;

    ArmorStandSkins(String name, String textureLocation) {
        this.name = name;
        this.texture = textureLocation;
    }

    public static String getTexture(int ordinal) {
        return values()[ordinal % values().length].texture;
    }

    public static String getName(int ordinal) {
        return values()[ordinal % values().length].name;
    }

    public static boolean isPlayerSkin(int ordinal) {
        return ordinal == ArmorStandSkins.PLAYER.ordinal();
    }
}
