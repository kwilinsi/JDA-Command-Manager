package botUtils.tools;

import botUtils.exceptions.UnknownColorException;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Locale;

public class Colors {
    public static final Color RED = new Color(0xD64949);
    public static final Color ORANGE = new Color(0xD1AC6B);
    public static final Color BROWN = new Color(0x6E5E47);
    public static final Color YELLOW = new Color(0xCCD175);
    public static final Color GREEN = new Color(0x85AB7E);
    public static final Color TEAL = new Color(0x68AFB0);
    public static final Color BLUE = new Color(0x86A4BF);
    public static final Color PURPLE = new Color(0xB584C2);
    public static final Color GREY = new Color(0x8A8A8A);
    public static final Color BLACK = new Color(0x17191A);
    public static final Color WHITE = new Color(0xF2F4F7);

    /**
     * Gets a {@link Color} object from a {@link String} with the name of the color. First, the given color name is
     * checked against the names of the predefined colors in this class. If there aren't any matches, the string is
     * checked to see if it's a valid hex code (it starts with 0x or # followed by six hexadecimal numbers), and if so
     * that color is returned. Otherwise an error is thrown.
     * <p>
     * Note that the string is not case sensitive. It is initially passed through {@link String#trim()} and {@link
     * String#toLowerCase()}. Null strings are also accepted, and will simply result in a null {@link Color} being
     * returned.
     *
     * @param color the name of a color supported in this class (or a hex code prefixed with '0x' or '#')
     * @return a matching {@link Color} object
     */
    public @Nullable
    static Color parseColor(@Nullable String color) throws UnknownColorException {
        if (color == null)
            return null;

        switch (color.trim().toLowerCase(Locale.ROOT)) {
            case "red" -> {
                return RED;
            }
            case "orange" -> {
                return ORANGE;
            }
            case "brown" -> {
                return BROWN;
            }
            case "yellow" -> {
                return YELLOW;
            }
            case "green" -> {
                return GREEN;
            }
            case "teal" -> {
                return TEAL;
            }
            case "blue" -> {
                return BLUE;
            }
            case "purple" -> {
                return PURPLE;
            }
            case "grey" -> {
                return GREY;
            }
            case "black" -> {
                return BLACK;
            }
            case "white" -> {
                return WHITE;
            }
            default -> {
                try {
                    if (color.startsWith("#") && color.length() == 7)
                        return Color.decode(color);
                    else if (color.startsWith("0x") && color.length() == 8)
                        return Color.decode("#" + color.substring(2));
                } catch (Exception ignore) {
                }
                throw new UnknownColorException("Failed to interpret color '" + color + "'.");
            }
        }
    }

    /**
     * Converts a {@link Color} to a hexadecimal {@link String} with a '{@code #}' prefix. This will always be a seven
     * character {@link String} in the format '{@code #000000}'. The output will be null <i>if and only if</i> the input
     * {@link Color} object is null.
     * <p><br>
     * Note that the alpha value in the color object is ignored and won't be reflected in the hex string. The output
     * assumes full opacity. Also, keep in mind that the returned string will user lowercase letters in the hex. You'll
     * need to append {@link String#toUpperCase(Locale)} if you want to make the letters uppercase.
     *
     * @param color the input {@link Color} object to convert to a hex code string (may be null)
     * @return the converted hex string (null if and only if the input is null)
     */
    public static String getColorStr(@Nullable Color color) {
        if (color == null)
            return null;
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
