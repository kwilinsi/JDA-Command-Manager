package botUtils.tools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GenericUtils {
    /**
     * Merge all items in a {@link List}<{@link String}> into a single comma-delimited String. For lists of size three
     * or greater, there will always be a comma between all items (including the Oxford comma before the last item).
     * Additionally, as long as there's at least two items in the list, the last two will be separated by the
     * conjunction String (or by nothing if that string is null). Usually the conjunction string is either "and", "or",
     * or null based on the context, but other conjunctions are possible too.
     * <p>
     * Note that if the list is null or empty, an empty string will be returned. This method will never return null and
     * shouldn't throw errors (unless you give it a ludicrously long list with more then the max String length
     * characters).
     *
     * @param list        the list of items to merge
     * @param conjunction the text to put between the last two items in the list (such as 'and'/'or') or null
     * @return all the strings from the input list merged in a comma delimited list
     */
    @NotNull
    public static String mergeList(List<String> list, @Nullable String conjunction) {
        if (list == null || list.size() == 0)
            return "";
        else if (list.size() == 1)
            return list.get(0);

        StringBuilder merge = new StringBuilder();
        for (int i = 0; i < list.size(); i++)
            merge
                    .append(i > 0 ? list.size() > 2 ? ", " : " " : "")
                    .append(i + 1 == list.size() && conjunction != null ? conjunction + " " : "")
                    .append(list.get(i));

        return merge.toString();
    }

    /**
     * Merges two regular arrays together, similar to {@link Collections}.{@link Collections#addAll} with {@link List}
     * objects and arrays. Note that this method will throw an error if the second array is not a subtype or the same
     * type as the first array.
     *
     * @param array1 the first array of objects
     * @param array2 the second array of objects
     * @param <T>    the type parameter
     * @return an array of the same type as array1 with array2 added to it
     */
    public static <T> T[] mergeArrays(@NotNull T[] array1, @NotNull T[] array2) {
        T[] output = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, output, array1.length, array2.length);
        return output;
    }

    /**
     * Adds an item to an array by encasing it in an array and calling {@link #mergeArrays(Object[], Object[])}.
     *
     * @param array the array to add the item to
     * @param item  the item to add
     * @param <T>   type parameter
     * @return the new merged array
     */
    public static <T> T[] addItemToArray(@NotNull T[] array, T item) {
        // TODO find a way to rewrite this to avoid the unchecked cast warnings
        @SuppressWarnings("unchecked") T[] itemArray = (T[]) new Object[]{item};
        return mergeArrays(array, itemArray);
    }

    /**
     * Makes the first letter of the given input {@link String} capital while making the rest lowercase. If the input is
     * null or empty, it will be returned unmodified. The only way for this method to return null is if the input is
     * null.
     *
     * @param input the input
     * @return the output with modified case
     */
    public static String capitalizeString(@Nullable String input) {
        if (input == null || input.length() == 0)
            return input;

        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1).toLowerCase(Locale.ROOT);
    }

    /**
     * Clones a {@link HashMap} object into a new {@link HashMap} using {@link Object#clone()}.
     *
     * @param map the map to clone
     * @param <K> key type parameter
     * @param <V> value type parameter
     * @return the cloned map
     */
    public static <K, V> LinkedHashMap<K, V> cloneHashMap(@NotNull LinkedHashMap<K, V> map) {
        LinkedHashMap<K, V> clone = new LinkedHashMap<>();
        for (K key : map.keySet())
            clone.put(key, map.get(key));
        return clone;
    }

    /**
     * Converts each {@link String} in an array of strings to lowercase with {@link String#toLowerCase(Locale)} and
     * returns the modified array. This method accepts null inputs, meaning the array itself can be null and items in
     * the array can be null. Null items will simply be returned unmodified. This method preserves the array order.
     *
     * @param array the input array of strings to convert to lowercase
     * @return the modified input array in lowercase form
     */
    public static @Nullable String[] makeStringArrayLowercase(@Nullable String[] array) {
        if (array == null)
            return null;

        // Make a new duplicate array
        String[] lower = new String[array.length];

        // Move items from the input array to the new array while converting them to lowercase
        for (int i = 0; i < array.length; i++)
            // The following line is suppressed due to bug in intelliJ that thinks array[i] could be null when calling
            // .toLowerCase() even though that's not possible due to the ternary statement.
            //noinspection ConstantConditions
            lower[i] = array[i] == null ? null : array[i].toLowerCase(Locale.ROOT);

        return lower;
    }

    /**
     * Converts a {@link String} in camel case to a regular {@link String} with spaces between words. The capitalization
     * is retained. To also make the string lowercase but retain capitalization of the first word, use {@link
     * #capitalizeString(String)}.
     *
     * @param input the input string in camel case
     * @return the output string with spaces between words
     */
    public static @NotNull String convertCamelCase(@NotNull String input) {
        StringBuilder out = new StringBuilder();
        Character c;

        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if (i > 0 && c != c.toString().toLowerCase(Locale.ROOT).charAt(0))
                out.append(" ");
            out.append(c);
        }

        return out.toString();
    }

    /**
     * Rounds the given input to the specified number of places
     *
     * @param num    the input number to round
     * @param places the number of decimals to round to
     * @return the rounded result
     */
    public static double round(double num, int places) {
        return Math.round(num * Math.pow(10, places)) / Math.pow(10, places);
    }

    /**
     * Checks to see if the given string is contained within the array of strings. This check is case insensitive,
     * meaning capitalization does <i>not</i> matter.
     *
     * @param array the array with all the possible values
     * @param str   the string to look for in the array
     * @return true if the array contains the string; false otherwise
     */
    public static boolean stringContains(@NotNull String[] array, @NotNull String str) {
        return Arrays.stream(array).anyMatch(str::equalsIgnoreCase);
    }
}