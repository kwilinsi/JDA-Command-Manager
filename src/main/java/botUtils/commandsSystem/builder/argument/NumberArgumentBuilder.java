package botUtils.commandsSystem.builder.argument;

import botUtils.commandsSystem.builder.Builder;
import botUtils.commandsSystem.builder.SyntaxBuilder;
import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.commandsSystem.types.function.ArgType;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Expands on {@link ArgumentBuilder} objects by adding additional parameters specific to doubles, like a required floor
 * and ceiling for input validation.
 */
public class NumberArgumentBuilder extends ArgumentBuilder implements Builder {
    /**
     * This is the smallest legal value for input to this argument.
     */
    private Double floor = null;

    /**
     * This is the largest legal value for input to this argument.
     */
    private Double ceiling = null;

    /**
     * This is the default value of the number if it's omitted from a syntax and not specified by the user in Discord.
     */
    private Double defaultValue = null;

    /**
     * This determines whether the smallest legal value is actually {@link #floor}, or if instead it is any value
     * greater than {@link #floor}. For integers, this is nearly always true. For doubles, it depends.
     */
    private Boolean floorInclusive = true;

    /**
     * This determines whether the largest legal value is actually {@link #ceiling}, or if instead it is any value less
     * than {@link #ceiling}. For integers, this is nearly always true. For doubles, it depends.
     */
    private Boolean ceilingInclusive = true;

    /**
     * This is the amount of precision that a user can specify on a number in Discord for this argument. User input will
     * be rounded to this number of sig-figs, if present.
     */
    private Integer sigFigs = null;

    private NumberArgumentBuilder(@NotNull String name, @NotNull String description, @NotNull ArgType type) {
        super(name, description, type);
    }

    /**
     * Creates a new {@link NumberArgumentBuilder} instance based on a name, description, and {@link ArgType}. Note
     * that the argument type must be a number as defined by {@link ArgType#isNumber(ArgType)}, or else an excpetion
     * will be thrown.
     *
     * @param name        the name of the argument (also used when making a {@link SyntaxBuilder} using this argument)
     * @param description a short description explaining what the argument is/does in a command
     * @param type        the type of the argument (must be a number type)
     * @throws IllegalArgumentException if the type parameter does not represent a number
     */
    public static NumberArgumentBuilder of(@NotNull String name, @NotNull String description, @NotNull ArgType type)
            throws IllegalArgumentException {
        if (!ArgType.isNumber(type))
            throw new IllegalArgumentException("The ArgType for a NumberArgumentBuilder must be a number.");
        return new NumberArgumentBuilder(name, description, type);
    }

    /**
     * Sets the minimum number a user can input for this argument. By default this is inclusive, meaning the given floor
     * can be used by the user as valid input. If this is the desired result, you can use {@link #setFloor(double)} as
     * shorthand. Otherwise, you can specify a value for isInclusive. If false, the user will be required to give a
     * number greater than the floor.
     *
     * @param floor       the minimum allowed value
     * @param isInclusive true if the floor is valid input; false if the input must be greater than the floor
     * @return this {@link NumberArgumentBuilder} instance for chaining
     */
    public NumberArgumentBuilder setFloor(double floor, boolean isInclusive) {
        this.floor = floor;
        this.floorInclusive = isInclusive;
        return this;
    }

    /**
     * Sets the minimum number a user can input for this argument. By default this in inclusive, meaning the given floor
     * can be used by the user as valid input. If you need the floor to be exclusive, use {@link #setFloor(double,
     * boolean)} instead.
     *
     * @param floor the minimum allowed value
     * @return this {@link NumberArgumentBuilder} instance for chaining
     */
    public NumberArgumentBuilder setFloor(double floor) {
        this.floor = floor;
        return this;
    }

    /**
     * Sets the maximum number a user can input for this argument. By default this is inclusive, meaning the given
     * ceiling can be used by the user as valid input. If this is the desired result, you can use {@link
     * #setCeiling(double)} as shorthand. Otherwise, you can specify a value for isInclusive. If false, the user will be
     * required to give a number less than the ceiling.
     *
     * @param ceiling     the maximum allowed value
     * @param isInclusive true if the ceiling is valid input; false if the input must be less than the ceiling
     * @return this {@link NumberArgumentBuilder} instance for chaining
     */
    public NumberArgumentBuilder setCeiling(double ceiling, boolean isInclusive) {
        this.ceiling = ceiling;
        this.ceilingInclusive = isInclusive;
        return this;
    }

    /**
     * Sets the maximum number a user can input for this argument. By default this in inclusive, meaning the given
     * ceiling can be used by the user as valid input. If you need the ceiling to be exclusive, use {@link
     * #setCeiling(double, boolean)} instead.
     *
     * @param ceiling the maximum allowed value
     * @return this {@link NumberArgumentBuilder} instance for chaining
     */
    public NumberArgumentBuilder setCeiling(double ceiling) {
        this.ceiling = ceiling;
        return this;
    }

    /**
     * Sets the maximum number of significant figures a user can input for this argument. Any input they give will
     * automatically be rounded to this number of sig-figs before being evaluated to see if it matches the floor/ceiling
     * constraints (if applicable).
     *
     * @param sigFigs the maximum significant figures on input for this argument
     * @return this {@link NumberArgumentBuilder} instance for chaining
     */
    public NumberArgumentBuilder setSigFigs(int sigFigs) {
        this.sigFigs = sigFigs;
        return this;
    }

    /**
     * Sets the default value for this argument. This allows code executing a function to call the value of the argument
     * even if it wasn't provided by the user (such as for optional arguments not present in all syntaxes). In that
     * case, the method would simply receive the default value set via this method and stored in the JSON.
     *
     * @param defaultValue the default argument value
     * @return this {@link BooleanArgumentBuilder} instance for chaining
     */
    public NumberArgumentBuilder setDefaultValue(double defaultValue) {
        this.defaultValue = ArgType.isDecimal(super.getType()) ? defaultValue : Math.round(defaultValue);
        return this;
    }

    /**
     * Get a {@link JsonObject} which contains key-value pairs for the argument parameters. This includes everything
     * instantiated in the {@link ArgumentBuilder} class along with settings specific for number types.
     *
     * @return the completed {@link JsonObject}
     * @throws ClassNotFoundException if there's an error building the Json
     */
    @Override
    public @NotNull JsonObject getJson() throws ClassNotFoundException {
        return JsonBuilder.appendJsonObject(JsonMap.of()
                        .add("floor", floor)
                        .add("floorInclusive", floorInclusive)
                        .add("ceiling", ceiling)
                        .add("ceilingInclusive", ceilingInclusive)
                        .add("sigFigs", sigFigs)
                        .add("defaultValue", defaultValue),
                super.getJson());
    }
}