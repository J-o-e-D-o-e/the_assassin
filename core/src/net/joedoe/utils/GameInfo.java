package net.joedoe.utils;

public class GameInfo {
    public static final int WIDTH = 800; // map width in pixel
    public static final int MAP_WIDTH = 40; // map width in tiles
    public static final int HEIGHT = 500; // map height in pixel
    public static final int MAP_HEIGHT = 25; // map height in tiles
    public static final float SCALE = 2.5f; // scaling factor
    public static final int ONE_TILE = 20; // size of one scaled tile
    public static final float[] RED = new float[]{217 / 255f, 100 / 255f, 89 / 255f};
    public static final float[] GREEN = new float[]{151 / 255f, 206 / 255f, 104 / 255f};
    public static final float[] BLUE = new float[]{75 / 255f, 166 / 255f, 224 / 255f};
    public static final float[] GREY = new float[]{105 / 255f, 105 / 255f, 105 / 255f};
    public static final float[] YELLOW = new float[]{255 / 255f, 255 / 255f, 204 / 255f};
    public static final String CONTROLS = "In the city:\nMove: arrow keys or w, a, s, d\n\n"
            + "In action mode:\nMove: arrow keys\nShoot: w, a, s, d\nChange weapon: numbers\n"
            + "Reload: r\nEnd turn: e\n\nPause/Resume: p";
}