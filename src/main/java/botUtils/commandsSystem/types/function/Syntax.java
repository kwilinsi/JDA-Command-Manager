package botUtils.commandsSystem.types.function;

import botUtils.commandsSystem.json.JsonParser;
import botUtils.exceptions.JsonParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Syntax {
    private final ArrayList<ArgumentGroup> arguments = new ArrayList<>();
    private final Function function;
    /**
     * This is the index of the {@link Syntax} in the Json. It represents the position of this syntax in the list of
     * syntaxes in the Json file for the {@link Function}. That list is 1-indexed, meaning an index of 1 represents the
     * first syntax.
     */
    private final int index;

    private Syntax(@NotNull JsonArray array, @NotNull Function function, int index)
            throws JsonParseException {
        this.function = function;
        for (JsonElement j : array)
            arguments.add(new ArgumentGroup(j, function));
        this.index = index;
    }

    /**
     * Creates a new {@link Syntax} instance based on a {@link JsonArray} and then {@link Function} that this syntax
     * belongs to.
     *
     * @param array    the {@link JsonArray} with the Json data for this syntax
     * @param function the {@link Function} this syntax belongs to, which contains the {@link Argument} array
     * @param index    the {@link #index} of this {@link Syntax} in the list of syntaxes in the Json file
     * @return a newly created {@link Syntax}
     * @throws JsonParseException if there is an error parsing the Json
     */
    public static @NotNull Syntax of(@NotNull JsonArray array, @NotNull Function function, int index)
            throws JsonParseException {
        return new Syntax(array, function, index);
    }

    /**
     * Converts multiple {@link JsonArray} instances into an array of {@link Syntax} instances by iterating over each of
     * the arrays and converting them with {@link Syntax}.{@link #of(JsonArray, Function, int)}. All of the {@link
     * Syntax} instances will be assigned the same {@link Function} parent.
     *
     * @param arrays   the input array of {@link JsonArray} instances from a {@link Function} Json file
     * @param function the {@link Function} that owns all of these syntaxes
     * @return an array of newly created {@link Syntax} instances
     * @throws JsonParseException if there is an error parsing the Json for any of the syntaxes
     */
    public static @NotNull Syntax[] ofArray(@NotNull JsonArray[] arrays, @NotNull Function function)
            throws JsonParseException {
        Syntax[] array = new Syntax[arrays.length];
        for (int i = 0; i < arrays.length; i++)
            array[i] = of(arrays[i], function, i + 1);
        return array;
    }

    /**
     * Retrieves the names of the arguments in this {@link Syntax} in a nicely formatted {@link String}.
     *
     * @return the formatted syntax
     */
    @Override
    public @NotNull String toString() {
        StringBuilder s = new StringBuilder(function.getManager().getMainPrefix() + function.getNameLower());
        for (ArgumentGroup g : arguments)
            s.append(" ").append(g.toString());
        return s.toString();
    }

    /**
     * Retrieves the {@link #index} of this {@link Syntax} element in the Json that was used to create the parent {@link
     * Function}. This value is 1-indexed, meaning the first syntax is assigned an index of 1 and it goes up from
     * there.
     *
     * @return the {@link #index}
     */
    public int getIndex() {
        return index;
    }

    /**
     * Checks to see whether this {@link Syntax} matches all the data types given by a user in the proper order. If it
     * does match, an array containing the names of all the arguments matched in correct order is returned. Otherwise
     * null is returned.
     *
     * @param inputTypes an array of the types of variables the user used
     * @return an array of the names of the arguments that match what the user provided, or null if nothing matched
     */
    public @Nullable String[] matches(ArgType[] inputTypes) {
        ArrayList<String> argNames = new ArrayList<>();

        // If the user didn't give any arguments and this syntax takes no arguments, that's a successful match.
        if (inputTypes.length == 0 && arguments.size() == 0)
            return new String[]{};
        // But if only one of those two things has no arguments, it can't be a match.
        if (inputTypes.length == 0 || arguments.size() == 0)
            return null;

        int argumentIndex = 0;
        int repetitions = 0;
        // Represents the argumentIndex when repetitions were last invoked
        int indexOfLastRepetition = -1;

        // Go through each of the types given by the user and see if it can match this syntax
        try {

            while (inputTypes != null && inputTypes.length > 0) {

                boolean canRepeat =
                        argumentIndex > 0 && arguments.get(argumentIndex - 1).getRepetitions() > repetitions + 1;

                // If argumentIndex is out of bounds...
                if (argumentIndex >= arguments.size())

                    if (canRepeat) {
                        // Check to see if we can just repeat the last argument
                        argumentIndex--;
                        repetitions++;
                        indexOfLastRepetition = argumentIndex;
                        continue;
                    } else {
                        // If that's not an option, check to see if the last matched argument was a String. If so,
                        // all the rest of the user's arguments can be considered part of that String and we can
                        // cease checking arguments
                        if (arguments.get(argumentIndex - 1).getLastType() == ArgType.STRING) {
                            inputTypes = new ArgType[0];
                            continue;
                        }
                    }

                ArgumentGroup arg = arguments.get(argumentIndex);

                // Check to see if all the arguments in this group match the upcoming ones from inputTypes
                boolean matches = true;
                for (int subIndex = 0; subIndex < arg.groupSize(); subIndex++)
                    // If a single one doesn't work, make matches false and stop checking
                    if (!Argument.doesArgumentTypeMatch(arg.getType(subIndex), inputTypes[subIndex])) {
                        matches = false;
                        break;
                    }

                // If all args in the group matched remove them from the queue and move on to the next group
                if (matches) {
                    // If the current repetition counter isn't referencing this argument group, reset it
                    // Otherwise leave the counter going because we might be coming back to this argumentIndex
                    if (indexOfLastRepetition != argumentIndex)
                        repetitions = 0;

                    inputTypes = removeFirstItems(inputTypes, arg.groupSize());
                    argNames.addAll(Arrays.asList(arg.getNames()));
                    argumentIndex++;
                    continue;
                }

                // Otherwise check to see if the previous argument can do repetitions and if so try it
                if (canRepeat) {
                    argumentIndex--;
                    repetitions++;
                    indexOfLastRepetition = argumentIndex;
                    continue;
                }

                // If we can't do repetitions of the previous group and this group didn't match, the Syntax doesn't
                // work. Return false to indicate that it's not a match.
                return null;
            }
        } catch (Exception ignore) {
            // There's tons of potential for index out of bounds errors and other runtime stuff in this while loop.
            // If any of that happens though, it means this Syntax doesn't work so just return false.
            return null;
        }

        // If we made it through the entire Syntax without anything breaking, it works!
        return argNames.toArray(new String[0]);
    }

    /**
     * Takes an array of integers as input and removes the first numToRemove of them. Then returns an output array
     * identical to the first but with those first few items removed.
     *
     * @param array       the input array
     * @param numToRemove the number of items to remove from the start of the input array
     * @return the output array with the specified number of items removed
     */
    private static @NotNull ArgType[] removeFirstItems(ArgType[] array, int numToRemove) {
        ArgType[] newArray = new ArgType[array.length - numToRemove];
        if (newArray.length >= 0) System.arraycopy(array, numToRemove, newArray, 0, newArray.length);
        return newArray;
    }

    private static class ArgumentGroup {
        private final String[] names;
        private final ArgType[] types;
        private final int repetitions;

        /**
         * Creates an {@link ArgumentGroup} based on an element retrieved from Json and an associated {@link Function}.
         * The element j can either be a String represented as a {@link JsonPrimitive} or, if multiple arguments are
         * included in a single group, a {@link JsonObject}
         *
         * @param j        the element retrieved from the Json
         * @param function the {@link Function} controlling this {@link Syntax} and {@link ArgumentGroup}
         * @throws JsonParseException if the element is not a {@link JsonPrimitive} or {@link JsonObject} or there is
         *                            some sort of error parsing the Json
         */
        public ArgumentGroup(JsonElement j, Function function) throws JsonParseException {
            JsonObject json;

            if (j instanceof JsonPrimitive) {
                this.names = new String[]{j.getAsString()};
                this.repetitions = 1;
                try {
                    this.types = new ArgType[]{Objects.requireNonNull(function.getArgument(names[0])).getType()};
                } catch (Exception ignore2) {
                    throw new JsonParseException("There should have been an argument assigned to this function " +
                            "called '" + names[0] + "' because it was referenced in a syntax, but no matching " +
                            "argument could be found.");
                }
                return;
            }

            if (j instanceof JsonObject) {
                this.names = JsonParser.getStringArray((JsonObject) j, "args");
                this.types = new ArgType[this.names.length];
                for (int i = 0; i < this.names.length; i++)
                    try {
                        types[i] = Objects.requireNonNull(function.getArgument(names[i])).getType();
                    } catch (Exception ignore) {
                        throw new JsonParseException("There should have been an argument assigned to this function " +
                                "called '" + names[i] + "' because it was referenced in a syntax, but no matching " +
                                "argument could be found.");
                    }

                this.repetitions = JsonParser.getInteger((JsonObject) j, "maxRepetitions");
                return;
            }

            throw new JsonParseException("Syntax element " + j + " must be one of JsonPrimitive String or JsonObject.");
        }

        /**
         * Retrieves the name(s) of the arguments in the {@link ArgumentGroup}.
         *
         * @return the {@link #names}
         */
        public String[] getNames() {
            return names;
        }

        /**
         * Retrieves the name of an argument at the specified index
         *
         * @param index the index of the argument to retrieve (0 indexed)
         * @return one of the {@link #names}
         */
        public String getName(int index) {
            return names[index];
        }

        /**
         * Similar to {@link #getName(int)}, this retrieves the name at index 0.
         *
         * @return the first element in {@link #names}
         */
        public String getName() {
            return getName(0);
        }

        /**
         * Retrieves the maximum number of repetitions of the arguments.
         *
         * @return the max {@link #repetitions}
         */
        public int getRepetitions() {
            return repetitions;
        }

        /**
         * Retrieves the type of an argument at the specified index
         *
         * @param index the index of the argument to retrieve (0 indexed)
         * @return one of the {@link #types}
         */
        public ArgType getType(int index) {
            return types[index];
        }

        /**
         * Similar to {@link #getType(int)}, this retrieves the type at index 0.
         *
         * @return the first element in {@link #types}
         */
        public ArgType getType() {
            return getType(0);
        }

        /**
         * Similar to {@link #getType(int)}, this retrieves the type at the last index.
         *
         * @return the last element in {@link #types}
         */
        public ArgType getLastType() {
            return getType(names.length - 1);
        }

        /**
         * Retrieves the number of arguments in this {@link ArgumentGroup}, as specified by the length of {@link
         * #names}.
         *
         * @return the length of {@link #names}, which is the number of arguments in this group
         */
        public int groupSize() {
            return names.length;
        }

        /**
         * Expresses all the argument(s) in this {@link ArgumentGroup} as a {@link String}, designed to be presented to
         * a user as part of a {@link Syntax}.
         *
         * @return the pretty string representation of all the argument(s).
         */
        @Override
        public @NotNull String toString() {
            StringBuilder s = new StringBuilder();

            for (String name : names)
                s.append(" [").append(name).append("]");

            // If there's multiple allowed repetitions enclose the arguments in a curly braces with a factor
            if (repetitions > 1)
                return "{" + s.substring(1) + " x" + repetitions + "}";
            else
                return s.substring(1);
        }
    }
}
