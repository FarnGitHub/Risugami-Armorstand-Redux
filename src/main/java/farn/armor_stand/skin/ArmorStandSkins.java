package farn.armor_stand.skin;

public enum ArmorStandSkins {
    PLAYER("Player", "/mob/char.png"),
    WOOD("Wood", "/assets/armor_stand/armor_stand.png"),
    STEVE("Steve", "/mob/char.png"),
    ZOMBIE("Zombie", "/mob/zombie.png");

    private final String name;
    private final String texture;

    ArmorStandSkins(String name, String textureLocation) {
        this.name = name;
        this.texture = textureLocation;
    }

    private static boolean boundCheck(int ordinal) {
        return ordinal >= 0 && ordinal < values().length;
    }

    public static String getTexture(int ordinal) {
        return boundCheck(ordinal) ? values()[ordinal].texture : "/mob/char.png";
    }

    public static String getName(int ordinal) {
        return boundCheck(ordinal) ? values()[ordinal].name : "Invalid Skin";
    }

    public static boolean isPlayerSkin(int ordinal) {
        return ordinal == ArmorStandSkins.PLAYER.ordinal();
    }
}
