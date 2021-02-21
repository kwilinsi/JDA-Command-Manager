package botUtils.tools;

import botUtils.commandsSystem.json.JsonParser;
import botUtils.commandsSystem.manager.CommandManager;
import botUtils.commandsSystem.types.Command;
import botUtils.exceptions.InvalidMethodException;
import botUtils.exceptions.JsonParseException;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a miscellaneous library of checks. Some of these checks are general purpose and can be used in regular
 * Discord bot projects. Others are specific to the BotUtilities library and will not make sense on their own. Many of
 * them are only used once, but some are put here in case they need to be reused at some point in the future.
 */
public class Checks {
    /**
     * Confirms that the given method matches the given input types. If it does not, an InvalidMethodException is thrown
     * listing the method in question and the names of the arguments it should have.
     *
     * @param method the method in question
     * @param types  the parameters it should take
     */
    public static void checkMethodParameterTypes(Method method, Class<?>... types) throws InvalidMethodException {
        Class<?>[] mTypes = method.getParameterTypes();

        try {
            for (int i = 0; i < Math.max(mTypes.length, types.length); i++)
                assert mTypes[i] == types[i];

        } catch (Exception ignore) {
            throw new InvalidMethodException("Invalid parameters for method " + method.getName() + "(" +
                    getClassNamesString(mTypes) + "). Expected " + method.getName() + "(" +
                    getClassNamesString(types) + ").");
        }
    }

    /**
     * I have no idea what this method is for or, more specifically, why it is in this class. Future Simon should look
     * into that and write some documentation.
     *
     * @param classes an array of classes
     * @return what appears to be the names of the classes separated by commas
     */
    private static String getClassNamesString(Class<?>[] classes) {
        StringBuilder names = new StringBuilder();
        for (Class<?> c : classes)
            names.append(", ").append(c.getName());
        return names.length() == 0 ? "" : names.substring(2);
    }

    /**
     * Confirms that the given {@link CommandManager} build state matches the given state. If it's not, an {@link
     * IllegalStateException} is thrown. For example, if the requiredState is true and the {@link CommandManager} is
     * <i>not</i> built, an exception is thrown. If the requiredState is false and the {@link CommandManager} <i>is</i>
     * built, an exception is also thrown. The actual state must matched the requiredState.
     *
     * @param manager       the {@link CommandManager} to check
     * @param requiredState the state the {@link CommandManager} must be in to avoid errors
     * @throws IllegalStateException if the CommandManager state is invalid
     */
    public static void commandManagerBuildState(@NotNull CommandManager manager, boolean requiredState) {
        if (manager.isBuilt() != requiredState)
            throw new IllegalStateException("CommandManager must be " + (requiredState ? "built" : "unbuilt") + ".");
    }

    /**
     * Confirms that the given {@link File} object is <i>not</i> null and is not a directory.
     *
     * @param file the file to check
     * @throws NullPointerException     if the file is null
     * @throws IllegalArgumentException if the file is a directory
     */
    public static void fileNotDirectory(File file) {
        if (file == null)
            throw new NullPointerException("File must not be null.");
        if (file.isDirectory())
            throw new IllegalArgumentException("'" + file.getName() + "' cannot be a directory.");
    }

    /**
     * Confirms that the given {@link File} is not null and <i>is</i> a directory.
     *
     * @param file the directory to check
     * @throws NullPointerException     if the file is null
     * @throws IllegalArgumentException if the file is not a directory
     */
    public static void fileIsDirectory(File file) {
        if (file == null)
            throw new NullPointerException("Directory must not be null.");
        if (!file.isDirectory())
            throw new IllegalArgumentException("'" + file.getName() + "' must be a directory.");
    }

    /**
     * Confirms that the give {@link File} is not null and is a JSON file type
     *
     * @param file the file to check
     * @throws NullPointerException     if the file is null
     * @throws IllegalArgumentException if the file is not JSON
     */
    public static void fileIsJson(File file) {
        fileNotDirectory(file);
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".json"))
            throw new IllegalArgumentException("'" + file.getName() + "' must be of type JSON.");
    }

    /**
     * Convenience method to call {@link #fileIsJson(File)} on the given file, but with a boolean output instead of
     * throwing errors on a non Json file. A try catch is used, and if any exception is thrown false is returned.
     *
     * @param file the file to test
     * @return true if the file is a Json file; false otherwise
     */
    public static boolean fileIsJsonBool(File file) {
        try {
            fileIsJson(file);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * Confirms that a given input matches one of the options. Toggle the ignoreCase flag to change whether or not the
     * input must match the exact case of one of the options. If none of the options match, an error is thrown. Note
     * that a null input and an array containing a null String will count as a match. However, a null options array will
     * throw an exception.
     *
     * @param input      the input to test
     * @param options    the list of possibilities it could be (must not be null, but can contain nulls)
     * @param ignoreCase true to allow mismatched case (case insensitive); false to force exact case (case sensitive)
     * @throws NullPointerException     if the options array is null
     * @throws IllegalArgumentException if the given input cannot be found in the options array
     */
    public static void inputMatches(String input, String[] options, boolean ignoreCase) {
        if (options == null)
            throw new NullPointerException("List of options must not be null. To allow null input, use an array " +
                    "containing a null entry.");

        for (String o : options)
            if (input != null && (input.equals(o) || (ignoreCase && input.equalsIgnoreCase(o))))
                return;
            else if (input == null && o == null)
                return;

        throw new IllegalArgumentException("Input `" + input + "` must be one of " + Arrays.toString(options) + ".");
    }

    /**
     * Confirms that the given input is between the given upper and lower bounds, and if not throws an exception. Note
     * that both bounds are inclusive, meaning an input of 5 and a lower bound of 5 will count as a success.
     *
     * @param input the int input to text
     * @param lower the lower bound (inclusive)
     * @param upper the upper bound (inclusive)
     * @throws IllegalArgumentException if the given input is not in the range
     */
    public static void intInRange(int input, int lower, int upper) {
        if (input < lower || input > upper)
            throw new IllegalArgumentException(
                    "Input `" + input + "` must be between " + lower + " and " + upper + " (inclusive).");
    }

    /**
     * Checks to make sure that two input arrays are both not null and contain the same number of items.
     *
     * @param array1 the first array
     * @param array2 the second array
     * @param <T>    type parameter
     * @throws IllegalArgumentException if the check fails
     */
    public static <T> void checkMatchingArrays(T[] array1, T[] array2) {
        if (array1 == null || array2 == null || array1.length != array2.length)
            throw new IllegalArgumentException("Arrays do not match (either null or of different lengths.)");
    }

    /**
     * Confirms that the input string is not null and not empty.
     *
     * @param str the input string to test
     * @throws IllegalArgumentException if it is null or empty
     */
    public static void checkStringHasContents(String str) {
        if (str == null || str.length() == 0)
            throw new IllegalArgumentException("String must not be null or empty.");
    }

    /**
     * @param list the list to check
     * @param <T>  type parameter
     * @return true if it contains duplicates; false if it does not
     */
    public static <T> boolean containsDuplicates(Collection<T> list) {
        // Check to see if original list and list of distinct items are the same
        return !list.stream().distinct().collect(Collectors.toList()).equals(list);
    }

    /**
     * Confirms that the given array is not null and does not contain any null items
     *
     * @param array the input array to check
     * @param <T>   type parameter
     * @throws NullPointerException if the array is null or contains null item(s)
     */
    public static <T> void arrayHasNoNulls(T[] array) {
        if (array == null)
            throw new NullPointerException("Array may not be null.");
        for (T a : array)
            if (a == null)
                throw new NullPointerException("Array must not contain null objects.");
    }

    /**
     * Confirms that the given {@link JsonObject} contains the specified key. If it doesn't, an exception is thrown.
     *
     * @param json the json to check
     * @param key  the key to look for
     * @throws JsonParseException if the key is not in the json
     */
    public static void jsonContainsKey(@NotNull JsonObject json, @NotNull String key) throws JsonParseException {
        if (!json.has(key))
            throw new JsonParseException("Missing value for key '" + key + "'.");
    }

    /**
     * Confirms that the given input (which was presumably just retrieved from a {@link JsonObject} through a {@link
     * JsonParser} method) is not null. If it's not null, it is immediately returned as though nothing happened. If it
     * is null, an error is thrown.
     *
     * @param arg the input argument
     * @param key the name of the argument in the Json
     * @param <T> type parameter
     * @return the input argument if it's not null
     * @throws JsonParseException if the input is null
     */
    public static <T> T jsonArgNotNull(T arg, String key) throws JsonParseException {
        if (arg == null)
            throw new JsonParseException("Argument at key '" + key + "' must not be null.");
        return arg;
    }

    /**
     * This is a wrapper method that checks to make sure the given input string will meet character length restrictions
     * for an {@link net.dv8tion.jda.api.EmbedBuilder}. If it will, the string is returned unaltered. Otherwise, a
     * {@link JsonParseException} is thrown to indicate that the input is too long and the Json must be revised. The
     * error will mention that that the [term] was too long. For example, if this is being called to check an embed
     * description, use "description" for the term parameter.<br>
     * <p>
     * If the input {@link String} is null, no error will be thrown and null will be returned.
     *
     * @param input the String to test
     * @return the original unmodified input String
     * @throws JsonParseException if the String is more than 1024 characters long
     */
    @Deprecated
    public static @Nullable String validateEmbedString(@Nullable String input)
            throws JsonParseException {
        if (input != null && input.length() > 1024)
            throw new JsonParseException("'" + GenericUtils.capitalizeString(input) +
                    "' is too long for an EmbedBuilder. Must not be longer than 1024 characters.");
        return input;
    }

    /**
     * Checks to see whether the given list contains the item. If it does, true is returned. If it doesn't, false is
     * returned. This method shouldn't throw errors. If the list is null, false will be returned. Should work for null
     * items and a list containing null items (if both occur true would be returned).
     * <p>
     * If you're using this method to check a list of {@link String} types for a {@link String} item, the check will be
     * <i>case sensitive</i>.
     */
    public static <T> boolean listContainsItem(T[] list, T item) {
        if (list == null)
            return false;
        for (T t : list)
            if (t == null && item == null)
                return true;
            else if (t != null && t.equals(item))
                return true;
        return false;
    }

    /**
     * Checks to see if the given {@link MessageChannel} is of an acceptable type based on its {@link ChannelType} and
     * the private/server flags. If it is a {@link PrivateChannel} and allowPrivate is true, or if it is a {@link
     * TextChannel} and allowText is true, true is returned. If neither of those conditions are met, false is returned.
     * As long as the input channel is not null, this method will not return errors.
     *
     * @param type         the type of the channel to test
     * @param allowPrivate if true, an input channel type of Private will be accepted and true will be returned
     * @param allowText    if true, an input channel type of Text will be accepted and true will be returned
     * @return true if the the channel type is accepted; false if it does not match
     */
    public static boolean checkChannelType(@NotNull ChannelType type, boolean allowPrivate, boolean allowText) {
        if (type.equals(ChannelType.PRIVATE) && allowPrivate)
            return true;
        else return type.equals(ChannelType.TEXT) && allowText;
    }

    /**
     * Checks to see if the given list is null or empty. If it is null or doesn't contain any objects, true is returned.
     * Otherwise, false is returned.
     *
     * @param list the input array to test
     * @param <T>  type parameter
     * @return true if it is null or empty; false otherwise
     */
    public static <T> boolean listEmpty(T[] list) {
        return list == null || list.length == 0;
    }

    /**
     * Checks to see if array1 starts with the elements in array2. All of the items in array2 must be found in array1 in
     * order at the start for this method to return true.
     * <p><br>
     * For example, if array1 is a {@link String} array of {@code "a", "b", "c", "d", "e"} and array2 is {@code "a",
     * "b"}, this would return true. If array1 was {@code "a", "b", "c"} and array2 was {@code "a", "b", "c"}, it would
     * also return true. But if array1 was {@code "a", "b", "c", "d"} and array2 was {@code "b", "a", "c"} it would
     * return false.
     * <p><br>
     * Sometimes this method is desirable when comparing full strings, such as with {@link
     * Command#checkForMatch(String[])}. Admittedly it is slower to split the strings being compared with regex and then
     * compare them with this method, but the advantage has to do with users requesting commands in Discord. If they do
     * abnormal things like separating arguments of commands with double spaces or line breaks, this method will still
     * result in a match, because it compares each element of the strings separated by whitespace.
     * <p><br>
     * Note that if either array is null, false is returned. (However null items within the array are perfectly fine).
     * Additionally, if array2 is longer than array1 it will always return false. The elements in the array are compared
     * via {@link Object#equals(Object)} (or the appropriate overridden method if applicable).
     *
     * @param array1 the main array to check
     * @param array2 the elements to look for in the start of array1
     * @param <T>    type parameter
     * @return true if array1 starts with array2; false if it does not (or if either array is null)
     */
    public static <T> boolean arrayStartsWith(@Nullable T[] array1, @Nullable T[] array2) {
        if (array1 == null || array2 == null || array2.length > array1.length)
            return false;
        for (int i = 0; i < array2.length; i++)
            if (!Objects.equals(array1[i], array2[i]))
                return false;
        return true;
    }

    /**
     * Convenience method for using {@link #arrayStartsWith(Object[], Object[])} with strings that you wish to be
     * compared regardless of case.
     *
     * @param array1 the main array to check
     * @param array2 the elements to look for in the start of array1
     * @return true if array1 starts with array2; false if it does not (or if either array is null)
     */
    public static boolean stringArrayStartsWith(@Nullable String[] array1, @Nullable String[] array2) {
        return arrayStartsWith(
                GenericUtils.makeStringArrayLowercase(array1),
                GenericUtils.makeStringArrayLowercase(array2)
        );
    }

    /**
     * Checks to see if a {@link JDA} event is ignored, meaning it shouldn't do anything.
     * <p><br>
     * Any event triggered by this Discord bot is automatically ignored (meaning true will be returned). Any event
     * triggered by a generic Discord user will be accepted, returning false. And any event triggered by another bot
     * besides this one is up to the state of allowBots.
     *
     * @param user      the user who triggered the event in question
     * @param botUser   this bot
     * @param allowBots true if other bots are allowed to fire this event
     * @return true if the event author is ignored and the event should not be processed; false if the event should be
     * accepted
     */
    public static boolean eventAuthorIsIgnored(@NotNull User user, @NotNull User botUser, boolean allowBots) {
        if (user.getIdLong() == botUser.getIdLong())
            return true;
        if (user.isBot())
            return !allowBots;
        return false;
    }
}