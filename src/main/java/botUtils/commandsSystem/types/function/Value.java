package botUtils.commandsSystem.types.function;

import botUtils.tools.Num;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Value {
    private final Argument argument;
    private final String value;

    private Value(Argument argument, @NotNull String value) {
        this.argument = argument;
        this.value = value;
    }

    /**
     * Create a new {@link Value} instance from an {@link Argument} (which contains the required type and validation
     * rules) and the actual value the user gave in Discord for it.
     *
     * @param argument the argument the user's input must conform to
     * @param value    the user input
     * @return the newly created {@link Value} instance
     */
    public static Value of(@Nullable Argument argument, @NotNull String value) {
        return new Value(argument, value);
    }

    /**
     * Retrieves the name of the {@link Argument}. Note that since the {@link #argument} can be null, this could in
     * theory throw a {@link NullPointerException}. However, this is very unlikely and shouldn't ever happen.
     *
     * @return the name of the argument
     */
    public String getName() {
        return argument.getName();
    }

    /**
     * Checks whether the {@link Argument} associated with this {@link Value} instance matches the given key. Note that
     * since the {@link #argument} can be null, this could in theory throw a {@link NullPointerException}. However, this
     * is very unlikely and shouldn't ever happen.
     *
     * @return true if the argument matches the key; false otherwise
     */
    public boolean matches(String key) {
        return argument.matches(key);
    }

    /**
     * Retrieves the string the user gave in Discord for this {@link Value}.
     *
     * @return the user provided string
     */
    public String getValueString() {
        return value;
    }

    /**
     * Returns the value the user gave in Discord parsed into an integer. This could throw errors if the parsing fails.
     *
     * @return the given value as an int
     */
    public int getValueInt() {
        // Have to parse it to a double and then cast to int otherwise some string formats won't work cause
        // parseInt is stupid
        return Num.sigFigs((int) Double.parseDouble(value), argument.getSigFigs());
    }

    /**
     * Returns the value the user gave in Discord parsed into a double. This could throw errors if the parsing fails.
     *
     * @return the given value as a double
     */
    public double getValueDouble() {
        return Num.sigFigs(Double.parseDouble(value), argument.getSigFigs());
    }

    /**
     * Returns the value the user gave in Discord parsed into a boolean. This could throw errors if the parsing fails.
     *
     * @return the given value as a boolean
     */
    public boolean getValueBoolean() {
        return Boolean.parseBoolean(value);
    }

    /**
     * Check to see if this Value object works. In other words, check that the `value` instance variable is compatible
     * with the {@link Argument}. If it's not compatible, throw an error. Otherwise do nothing.
     *
     * @return this {@link Value} instance for chaining
     * @throws IllegalArgumentException if the validation fails
     */
    public Value validate() {
        if (argument == null)
            throw new IllegalArgumentException("Unable to find an Argument instance with the given name.");

        String exceptionMsg = "Failed to parse **" + argument.getName() + "**. ";

        switch (argument.getType()) {
            case INTEGER -> {
                int v;
                try {
                    // Parse it to a double first and then type cast because otherwise scientific notation doesn't work
                    double d = Double.parseDouble(value);
                    v = (int) d;
                    assert d == v;
                } catch (Exception ignore) {
                    throw new IllegalArgumentException(exceptionMsg + "Use a valid integer.");
                }
                checkBounds(exceptionMsg, v);
            }

            case DOUBLE -> {
                double v;
                try {
                    v = Double.parseDouble(value);
                } catch (Exception ignore) {
                    System.out.println("Failed to parse " + value + " into double.");
                    throw new IllegalArgumentException(exceptionMsg + "Use a valid number.");
                }
                checkBounds(exceptionMsg, v);
            }

            case STRING -> {
                // If the string can be anything just return. It's validated
                if (argument.getAllowedValues() == null)
                    return this;

                // Otherwise make sure the user selected one of the legal values
                for (String opt : argument.getAllowedValues())
                    if (value.equals(opt))
                        return this;

                throw new IllegalArgumentException(
                        exceptionMsg + "Must be one of " + argument.getAllowedValuesStr() + ".");
            }
        }

        return this;
    }

    /**
     * Checks to see if a given number is in an acceptable range, and if not an error is thrown
     *
     * @param exceptionMsg the first part of the possible exception, placed before "integer must be greater/less than"
     * @param v            the number to test
     * @throws IllegalArgumentException if the given number is not in the required range
     */
    public void checkBounds(String exceptionMsg, double v) {
        if (v < argument.getFloor() || (!argument.isFloorInclusive() && v == argument.getFloor()))
            throw new IllegalArgumentException(exceptionMsg + "Integer must be greater than " +
                    (argument.isFloorInclusive() ? "or equal to " : "") + argument.getFloor());
        if (v > argument.getCeiling() || (!argument.isCeilingInclusive() && v == argument.getCeiling()))
            throw new IllegalArgumentException(exceptionMsg + "Integer must be less than " +
                    (argument.isCeilingInclusive() ? "or equal to " : "") + argument.getCeiling());
    }
}
