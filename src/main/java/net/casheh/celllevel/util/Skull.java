package net.casheh.celllevel.util;

public enum Skull {
    EMPTY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19"),
    LEVEL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJjZGUwNjhlOTlhNGY5OGMzMWY4N2I0Y2MwNmJlMTRiMjI5YWNhNGY3MjgxYTQxNmM3ZTJmNTUzMjIzZGI3NCJ9fX0=");

    private String texture;

    private Skull(String texture) {
        this.texture = texture;
    }

    public String getTexture() {
        return this.texture;
    }
}
