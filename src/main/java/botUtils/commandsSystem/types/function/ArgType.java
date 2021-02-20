package botUtils.commandsSystem.types.function;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ArgType {
    STRING, BOOLEAN, INTEGER, DOUBLE;

    /**
     * Converts a type in {@link String} form to an int based on one of the static constants defined here. Currently the
     * recognized types are: {@code str, string, bool, boolean, int, integer, dbl, double}.<br><br> Note that the type
     * is not case sensitive, as input is trimmed and converted to lowercase. However, the input must not be null, and
     * an unrecognized input will throw an exception.
     *
     * @param type the input type as a string
     * @return the type as an integer constant
     * @throws IllegalArgumentException if the input string was not recognized as a valid type defined in this class
     */
    public static ArgType getType(@NotNull String type) {
        type = type.trim().toLowerCase(Locale.ROOT);

        return switch (type) {
            case "str", "string" -> STRING;
            case "bool", "boolean" -> BOOLEAN;
            case "int", "integer" -> INTEGER;
            case "dbl", "double" -> DOUBLE;
            default -> throw new IllegalArgumentException("Unknown argument type '" + type + "'.");
        };
    }

    /**
     * Converts a type as an integer constant defined here into a readable {@link String} format. The current recognized
     * types and available outputs are: {@code string, boolean, integer, double}. If an unrecognized integer is given,
     * an exception will be thrown.
     *
     * @param type the input type as an integer
     * @return the type as a more readable String
     */
    public @NotNull
    static String getTypeStr(ArgType type) {
        return switch (type) {
            case STRING -> "string";
            case BOOLEAN -> "boolean";
            case INTEGER -> "integer";
            case DOUBLE -> "double";
        };
    }

    /**
     * Checks to see whether the given input type is a number. If it is, true is returned; otherwise, false is returned.
     * The input type is compared against the constants defined in this class.
     *
     * @param type the type to check
     * @return true if it is a number; false if it is not
     */
    public static boolean isNumber(ArgType type) {
        return type == INTEGER || type == DOUBLE;
    }

    /**
     * Checks to see whether the given input type is a number that has decimal precision. Currently this is equivalent
     * to checking {@code (type == ArgType.ARGUMENT_DOUBLE)}, but could conceivably change if more argument types are
     * added.
     *
     * @param type the type to check
     * @return true if is a number type with decimal precision; false otherwise
     */
    public static boolean isDecimal(ArgType type) {
        return type == ArgType.DOUBLE;
    }

    /**
     * Returns the minimum allowed value of the specified argument type in Java. For example, if the given type is
     * {@link #INTEGER}, this method will return {@link Integer#MIN_VALUE}. If the given type is {@link
     * #DOUBLE}, it will return {@link Double#MIN_VALUE}.
     * <p><br>
     * If the given type is not a number as defined by {@link #isNumber(ArgType)}, an exception will be thrown.
     *
     * @param type the given type to check
     * @return the minimum possible value for that data type in Java
     * @throws IllegalArgumentException if the given argument is not a number as defined by {@link #isNumber(ArgType)}
     */
    public static double getMinValue(@NotNull ArgType type) {
        return switch (type) {
            case INTEGER -> Integer.MIN_VALUE;
            case DOUBLE -> Double.MIN_VALUE;
            default -> throw new IllegalArgumentException("Invalid argument type '" + type.name() +
                    "'. No minimum numerical value could be identified.");
        };
    }

    /**
     * Exactly the same functionality as {@link #getMinValue(ArgType)}, except the maximum possible value in Java is
     * returned instead of the minimum. See that method documentation for more information.
     *
     * @param type the given type to check
     * @return the maximum possible value for that data type in Java
     * @throws IllegalArgumentException if the given argument is not a number as defined by {@link #isNumber(ArgType)}
     */
    public static double getMaxValue(@NotNull ArgType type) {
        return switch (type) {
            case INTEGER -> Integer.MAX_VALUE;
            case DOUBLE -> Double.MAX_VALUE;
            default -> throw new IllegalArgumentException("Invalid argument type '" + type.name() +
                    "'. No minimum numerical value could be identified.");
        };
    }
}
