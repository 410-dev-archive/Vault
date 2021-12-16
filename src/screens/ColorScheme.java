package screens;

import java.awt.Color;

public class ColorScheme {
    public static Color background = Color.WHITE;
    public static Color text = Color.BLACK;
    public static Color button = Color.GRAY;

    public static String ID_BLACK = "black";
    public static String ID_WHITE = "white";

    public static void setColor(String id) {
        if (id.equals(ID_BLACK)) {
            background = Color.BLACK;
            text = Color.WHITE;
            button = Color.GRAY;
        } else if (id.equals(ID_WHITE)) {
            background = Color.WHITE;
            text = Color.BLACK;
            button = Color.GRAY;
        }

        repaintAllWindows();
    }

    private static void repaintAllWindows() {

    }
}
