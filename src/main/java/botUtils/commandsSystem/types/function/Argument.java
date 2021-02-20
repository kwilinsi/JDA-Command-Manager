package botUtils.commandsSystem.types.function;

import botUtils.commandsSystem.json.JsonParser;
import botUtils.exceptions.JsonParseException;
import botUtils.tools.Checks;
import botUtils.tools.GenericUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Argument {
    private final String name;
    private final String description;
    private final ArgType type;
    // List of all the possible values for this argument. Null if input shouldn't be constrained to a list
    private final String[] allowedValues;
    // If it's not a required argument then give a default value if it isn't given
    private final String defaultValue;

    // For integers and doubles
    private double floor;
    private boolean floorInclusive;
    private double ceiling;
    private boolean ceilingInclusive;
    private int sigFigs;

    private Argument(JsonObject json) throws JsonParseException {
        // Required arguments (error thrown if not present)
        this.name = Checks.jsonArgNotNull(JsonParser.getString(json, "name"), "name");
        this.description = Checks.jsonArgNotNull(JsonParser.getString(json, "description"), "description");
        this.type = ArgType.getType(Checks.jsonArgNotNull(JsonParser.getString(json, "type"), "type"));

        // Optional arguments
        this.defaultValue = JsonParser.getString(json, "defaultValue", null);

        // TODO make allowed values work for both Strings and integers/doubles properly
        String[] array = JsonParser.getStringArrayNoError(json, "allowedValues");
        this.allowedValues = array.length == 0 ? null : array;

        // If this argument is a number, set its legal bounds
        if (ArgType.isNumber(type)) {
            this.floor = JsonParser.getDouble(json, "floor", ArgType.getMinValue(type));
            this.floorInclusive = JsonParser.getBoolean(json, "floorInclusive", true);
            this.ceiling = JsonParser.getDouble(json, "ceiling", ArgType.getMaxValue(type));
            this.ceilingInclusive = JsonParser.getBoolean(json, "ceilingInclusive", true);

            // User inputs are automatically rounded to these significant figures. This is an optional argument
            this.sigFigs = JsonParser.getInteger(json, "sigFigs", 99);
        }
    }

    /**
     * Builds a new {@link Argument} based on the contents of a {@link JsonObject}, which was read from a Json file for
     * a {@link Function}. The following Json keys are recognized when constructing an {@link Argument}:
     * <br><br>
     * Required keys: {@code name, description, type}<p> Optional keys: {@code defaultValue, allowedValues}<p> Required
     * keys for numbers: {@code floor, floorInclusive, ceiling, ceilingInclusive}<p> Optional keys for numbers: {@code
     * sigFigs}
     *
     * @param json the input Json to parse
     * @return a completed {@link Argument}.
     * @throws JsonParseException if there is an error parsing the Json
     */
    public static @NotNull Argument of(@NotNull JsonObject json) throws JsonParseException {
        return new Argument(json);
    }

    /**
     * Convenience method to convert an array of {@link JsonObject} instances from a Json file for a {@link Function}
     * into an array of {@link Argument} instances. This simply calls {@link Argument}.{@link #of(JsonObject)} for each
     * of the {@link JsonObject} instances and combines them into an {@link Argument} array.
     *
     * @param json the input array of {@link JsonObject} instances
     * @return an array of {@link Argument} instances
     * @throws JsonParseException if there is an error parsing the Json for any of the input objects
     */
    public static @NotNull Argument[] ofArray(@NotNull JsonObject[] json) throws JsonParseException {
        Argument[] args = new Argument[json.length];
        for (int i = 0; i < args.length; i++)
            args[i] = of(json[i]);
        return args;
    }

    /**
     * Returns the name of the {@link Argument}.
     *
     * @return the name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns the {@link ArgType} of the argument as an integer constant. This can be converted to a human-readable
     * String with {@link ArgType#getTypeStr(ArgType)}.
     *
     * @return the type int
     */
    public ArgType getType() {
        return type;
    }

    /**
     * Returns the list of allowed values for this {@link Argument}. If there is no Json-defined list of allowed values,
     * this will be null.
     *
     * @return the list of allowed values
     */
    public String[] getAllowedValues() {
        return allowedValues;
    }

    /**
     * Returns the list of allowed values from {@link #getAllowedValues()} in a single comma separated String (merged
     * with {@link GenericUtils#mergeList(List, String)} with 'or' as the conjunction).
     *
     * @return the list of allowed values in a single delimited String
     */
    public String getAllowedValuesStr() {
        List<String> values = new ArrayList<>();
        for (String value : allowedValues)
            values.add("'" + value + "'");
        return GenericUtils.mergeList(values, "or");
    }

    /**
     * Returns the default value of this argument, if specified in the Json. If none was specified, this will be null.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the minimum allowed value for this argument, assuming it is a number. Related: {@link
     * #isFloorInclusive()}.
     * <p>Note that this only applies if the {@link Argument} is a number.
     *
     * @return the floor
     */
    public double getFloor() {
        return floor;
    }

    /**
     * Returns whether the floor (obtained through {@link #getFloor()} is inclusive). If it is inclusive, the user can
     * return the floor as valid input. If it is exclusive, the user must give a value greater than the floor.
     * <p>Note that this only applies if the {@link Argument} is a number.
     *
     * @return true if the floor is inclusive; false if it is exclusive
     */
    public boolean isFloorInclusive() {
        return floorInclusive;
    }

    /**
     * Returns the maximum allowed value for this argument, assuming it is a number. Related: {@link
     * #isCeilingInclusive()}.
     * <p>Note that this only applies if the {@link Argument} is a number.
     *
     * @return the ceiling
     */
    public double getCeiling() {
        return ceiling;
    }

    /**
     * Returns whether the ceiling (obtained through {@link #getCeiling()} is inclusive). If it is inclusive, the user
     * can return the ceiling as valid input. If it is exclusive, the user must give a value less than the ceiling.
     * <p>Note that this only applies if the {@link Argument} is a number.
     *
     * @return true if the ceiling is inclusive; false if it is exclusive
     */
    public boolean isCeilingInclusive() {
        return ceilingInclusive;
    }

    /**
     * Returns the number of sig-figs used by this {@link Argument}. When a user provides numerical input, it is
     * automatically rounded to this number of significant figures before being parsed by a function.
     * <p>Note that this only applies if the {@link Argument} is a number.
     *
     * @return the number of significant figures used in argument rounding
     */
    public int getSigFigs() {
        return sigFigs;
    }

    /**
     * Determines if the name of this {@link Argument} matches the specified key (case insensitive). If true, the input
     * key is the same as the name of this {@link Argument}. Otherwise it is not the same.
     *
     * @param key the input key to test
     * @return true if the key is the same as this argument's name; false otherwise
     */
    public boolean matches(String key) {
        return name.equalsIgnoreCase(key);
    }

    /**
     * Returns the name and description of this argument formatted nicely like so: [**mean**] - the mean of the normal
     * distribution
     *
     * @return the formatted string
     */
    public String getDescriptionFormat() {
        return "**[" + name.toLowerCase(Locale.ROOT) + "]** - " + description;
    }

    /**
     * Returns the name of this argument in lowercase surrounded by brackets
     *
     * @return the formatted argument name
     */
    @Override
    public String toString() {
        return "[" + name.toLowerCase(Locale.ROOT) + "]";
    }

    /**
     * Attempts to figure out what {@link ArgType} the given argument is (double, String, integer, etc).
     * <p>
     * <br>Note that this is not parsing the literal text "integer" to the enum equivalent. For that behavior, use
     * {@link ArgType#getType(String)}. Rather, this method looks at a user provided input and attempts to determine
     * what argument {@link ArgType} it is and return the appropriate enum.
     *
     * @param arg the input argument
     * @return the type of that argument
     */
    public static ArgType getArgumentType(String arg) {
        try {
            Integer.parseInt(arg);
            return ArgType.INTEGER;
        } catch (Exception ignore) {
        }

        try {
            Double.parseDouble(arg);
            return ArgType.DOUBLE;
        } catch (Exception ignore) {
        }

        if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false"))
            return ArgType.BOOLEAN;
        return ArgType.STRING;
    }

    /**
     * Iterates over each of the arguments in the input {@link String} array, running each of them through {@link
     * Argument}.{@link #getArgumentType(String)} and assembling the resultant integers in an array. This is merely a
     * convenience method for getting the type of more than one argument.
     *
     * @param args what a user typed for an argument, such as "0.1" or "true". The type will be determined.
     * @return an array of {@link ArgType} integers in the same order as the input array
     */
    public static ArgType[] getTypes(@NotNull String... args) {
        return Arrays.stream(args).map(Argument::getArgumentType).toArray(ArgType[]::new);
    }

    /**
     * Determine if two different {@link ArgType} constants match. Specifically, does the {@code givenType} also count
     * as the {@code masterType}?
     * <p><br>
     * For example, if the given type is an integer and the master is a double, this counts as a match because an
     * integer makes a valid double. But the other way around, where the given is a double and the master is an integer
     * would not match.
     *
     * @return true if they match; false if they don't
     */
    public static boolean doesArgumentTypeMatch(ArgType masterType, ArgType givenType) {
        if (masterType == givenType)
            return true;

        // A boolean can be a string if you want it to be
        if (masterType == ArgType.STRING && givenType == ArgType.BOOLEAN)
            return true;

        // An integer is just a double without the decimals
        return masterType == ArgType.DOUBLE && givenType == ArgType.INTEGER;
    }
}
