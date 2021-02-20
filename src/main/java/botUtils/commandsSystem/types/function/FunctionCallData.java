package botUtils.commandsSystem.types.function;

import botUtils.commandsSystem.manager.CommandManager;
import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.types.CommandCallData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This is an extension of {@link CommandCallData}. Not only does it contain the basic information about a message,
 * where it was sent, and who sent it, but it also contains the arguments that were parsed from the message to be used
 * by a function.
 */
public class FunctionCallData extends CommandCallData {
    /**
     * This is the list of {@link Value} instances, each of which contains the raw text of a single argument in the
     * command given by a user. It also holds the matching {@link Argument} assigned to the {@link Function}, which
     * contains the type and all the validation rules.
     */
    private final @NotNull List<Value> values = new ArrayList<>();

    /**
     * This is the first checked {@link Syntax} that matched the user's input successfully (including the input
     * validation step).
     */
    private Syntax matchingSyntax;

    /**
     * Creates a new {@link CommandCallData} instance based on the {@link CommandManager} and {@link Command} associated
     * with it. It is assumed that immediately after creating this command you will set the additional data including
     * the message and user. See {@link CommandCallData#CommandCallData(CommandManager, Command)} documentation for more
     * information.
     *
     * @param manager the {@link CommandManager} operating this command
     * @param command the {@link Command} triggered by the user
     */
    public FunctionCallData(@NotNull CommandManager manager, @NotNull Command command) {
        super(manager, command);
    }

    /**
     * Adds all the {@link Value} objects to the list. This method is intentionally package private so that it is only
     * accessible by the {@link Function} that initially creates it.
     *
     * @param values the list of all the values to add
     * @return this {@link FunctionCallData} instance for chaining
     */
    FunctionCallData setValues(@NotNull List<Value> values) {
        this.values.addAll(values);
        return this;
    }

    /**
     * Sets the {@link Syntax} attached to th {@link Function} that matched what the user entered and was validated.
     *
     * @param syntax the matching {@link #matchingSyntax}
     * @return this {@link FunctionCallData} instance for chaining
     */
    FunctionCallData setMatchingSyntax(@NotNull Syntax syntax) {
        this.matchingSyntax = syntax;
        return this;
    }

    /**
     * Attempts to get the Value with the matching key (case insensitive). If no such value can be found, an exception
     * is thrown.
     *
     * @param key the name of the desired argument/value
     * @return the Value object
     * @throws NoSuchElementException if no Value with the given name was found
     */
    public Value getValue(@NotNull String key) {
        for (Value value : values)
            if (value.matches(key))
                return value;
        throw new NoSuchElementException("Failed to find Value with key '" + key + "'.");
    }

    /**
     * Retrieves the {@link Syntax} that matched the user's Discord message.
     *
     * @return the {@link #matchingSyntax}
     */
    public @NotNull Syntax getSyntax() {
        return matchingSyntax;
    }

    /**
     * This is a convenience method that redirects to {@link Function#getArgument(String)}. You can also just do {@link
     * #getCommand()} and get an argument from the retrieved {@link Function}, but this looks prettier.
     *
     * @param key the name of the desired {@link Argument}
     * @return the matching {@link Argument}, or null if none could be found
     */
    public @Nullable Argument getArgument(@NotNull String key) {
        return getCommand().getArgument(key);
    }

    /**
     * Overrides {@link CommandCallData#getCommand()} by stipulating that the command will always be a {@link Function}
     * type if it's being used inside a {@link FunctionCallData} instance, and thus it can be returned as such.
     *
     * @return {@link CommandCallData#getCommand()} cast to a {@link Function}
     */
    @Override
    public @NotNull Function getCommand() {
        return (Function) super.getCommand();
    }

    /**
     * Get the default value of an Argument for this function by specifying its key. If no Argument with that name can
     * be found, null is returned.
     *
     * @param key the name of the Argument to get the default value of.
     * @return the Argument's default value, or null if no Argument with a matching name found.
     */
    public String getDefaultValue(@NotNull String key) {
        try {
            return Objects.requireNonNull(getArgument(key)).getDefaultValue();
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * Check to see if there's any Value with the specified key name. If any Value is found with the matching name, true
     * is returned; otherwise false is returned after checking every Value.
     *
     * @param key the name of the argument to look for (as written in the json, but case insensitive)
     * @return true if any Value with a matching name is found; false otherwise
     */
    public boolean hasValue(@NotNull String key) {
        for (Value value : values)
            if (value.matches(key))
                return true;
        return false;
    }

    /**
     * Get a Value by specifying it's name. Though this is case insensitive, it must be written exactly the same as the
     * name of the Argument in the json file for the function. If no Value with a matching key is found, the default
     * value of the Argument is returned. If no Argument with the right name can be found either, or no default value
     * was set for that Argument, 0 is returned.
     *
     * @param argument the name of the Value
     * @return the integer of the Value or the default value for the argument (or 0 if all else fails)
     */
    public int getInt(@NotNull String argument) {
        try {
            return getValue(argument).getValueInt();
        } catch (Exception ignore) {
            String def = getDefaultValue(argument);
            return def == null ? 0 : (int) Double.parseDouble(def);
        }
    }

    /**
     * Get a Value by specifying it's name. Though this is case insensitive, it must be written exactly the same as the
     * name of the Argument in the json file for the function. If no Value with a matching key is found, the default
     * value of the Argument is returned. If no Argument with the right name can be found either, or no default value
     * was set for that Argument, 0 is returned.
     *
     * @param argument the name of the Value
     * @return the double of the Value or the default value for the argument (or 0 if all else fails)
     */
    public double getDouble(@NotNull String argument) {
        try {
            return getValue(argument).getValueDouble();
        } catch (Exception ignore) {
            String def = getDefaultValue(argument);
            return def == null ? 0 : Double.parseDouble(def);
        }
    }

    /**
     * Get a Value by specifying it's name. Though this is case insensitive, it must be written exactly the same as the
     * name of the Argument in the json file for the function. If no Value with a matching key is found, the default
     * value of the Argument is returned. If no Argument with the right name can be found either, or no default value
     * was set for that Argument, null is returned.
     *
     * @param argument the name of the Value
     * @return the String of the Value or the default value for the argument (or null if all else fails)
     */
    public String getString(@NotNull String argument) {
        try {
            return getValue(argument).getValueString();
        } catch (Exception ignore) {
            return getDefaultValue(argument);
        }
    }

    /**
     * Get a Value by specifying it's name. Though this is case insensitive, it must be written exactly the same as the
     * name of the Argument in the json file for the function. If no Value with a matching key is found, the default
     * value of the Argument is returned. If no Argument with the right name can be found either, or no default value
     * was set for that Argument, false is returned (as this is the result of Boolean.parseBoolean()).
     *
     * @param argument the name of the Value
     * @return the boolean of the Value or the default value for the argument (or false if all else fails)
     */
    public boolean getBoolean(@NotNull String argument) {
        try {
            return getValue(argument).getValueBoolean();
        } catch (Exception ignore) {
            String def = getDefaultValue(argument);
            return Boolean.parseBoolean(def);
        }
    }

    /**
     * Get an array of all Values that match the specified name. Though this is case insensitive, it must be written
     * exactly the same as the name of the Argument in the json file for the function. If no Value with a matching key
     * is found, an empty array is returned. The default value for the argument is never used
     *
     * @param argument the name of the Value
     * @return an array of matching double Values, or if none match then an empty array
     */
    public double[] getArrayDouble(@NotNull String argument) {
        List<Double> result = new ArrayList<>();
        for (Value value : values)
            if (value.matches(argument))
                result.add(value.getValueDouble());

        return result.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * Get an array of all Values that match the specified name. Though this is case insensitive, it must be written
     * exactly the same as the name of the Argument in the json file for the function. If no Value with a matching key
     * is found, an empty array is returned. The default value for the argument is never used
     *
     * @param argument the name of the Value
     * @return an array of matching integer Values, or if none match then an empty array
     */
    public int[] getArrayInteger(@NotNull String argument) {
        List<Integer> result = new ArrayList<>();
        for (Value value : values)
            if (value.matches(argument))
                result.add(value.getValueInt());

        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Get an array of all Values that match the specified name. Though this is case insensitive, it must be written
     * exactly the same as the name of the Argument in the json file for the function. If no Value with a matching key
     * is found, an empty array is returned. The default value for the argument is never used
     *
     * @param argument the name of the Value
     * @return an array of matching String Values, or if none match then an empty array
     */
    public String[] getArrayString(@NotNull String argument) {
        ArrayList<String> result = new ArrayList<>();
        for (Value value : values)
            if (value.matches(argument))
                result.add(value.getValueString());

        return result.toArray(new String[0]);
    }

    /**
     * Get an array of all Values that match the specified name. Though this is case insensitive, it must be written
     * exactly the same as the name of the Argument in the json file for the function. If no Value with a matching key
     * is found, an empty array is returned. The default value for the argument is never used
     *
     * @param argument the name of the Value
     * @return an array of matching boolean Values, or if none match then an empty array
     */
    public boolean[] getArrayBoolean(@NotNull String argument) {
        ArrayList<Boolean> result = new ArrayList<>();
        for (Value value : values)
            if (value.matches(argument))
                result.add(value.getValueBoolean());

        // There's no convenient method for using a stream to convert ArrayList<Boolean> to boolean[] so for loop used
        boolean[] r = new boolean[result.size()];
        for (int i = 0; i < result.size(); i++)
            r[i] = result.get(i);
        return r;
    }
}
