package botUtils.tools;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonUtils {
    /**
     * Takes a {@link JsonObject} and scours it for usages of the initial key. Each one of these keys is replaced with
     * the new key and stored back into the Json. Note that this method works recursively. It will find keys that are
     * inside other {@link JsonObject} or {@link JsonArray} instances within the outer Json.
     * <p><br>
     * Note that the initial key is case in-sensitive, meaning capitalization does not matter.
     *
     * @param json       the {@link JsonObject} to scan for the initial key
     * @param initialKey the (case insensitive) initial key to look for
     * @param newKey     the new key to replace the initial key with
     * @return the updated {@link JsonObject}
     */
    public static @NotNull JsonObject changeKey(@NotNull JsonObject json,
                                                @NotNull String initialKey, @NotNull String newKey) {
        return changeKeyRecur(json, initialKey, newKey).getAsJsonObject();
    }

    /**
     * Takes a (possibly null) {@link JsonElement} and checks to see if it is a {@link JsonObject}. If it is, it's
     * scanned for instances of the initialKey, all of which are replaced with the new key. This method is called by
     * {@link #changeKey(JsonObject, String, String)} and it is designed to work recursively on any {@link
     * JsonElement}.
     * <p><br>
     * Note that the initial key is case in-sensitive, meaning capitalization does not matter.
     *
     * @param json       the {@link JsonElement} to scan for the initial key
     * @param initialKey the (case insensitive) initial key to look for
     * @param newKey     the new key to replace the initial key with
     * @return the updated {@link JsonElement}
     */
    private static JsonElement changeKeyRecur(@Nullable JsonElement json,
                                              @NotNull String initialKey, @NotNull String newKey) {
        // If the Json is null or a Primitive type that can't contain keys, return it unmodified
        if (json == null)
            return null;
        if (json instanceof JsonPrimitive || json instanceof JsonNull)
            return json;

        // If it is a JsonArray, it doesn't directly contain keys, but could have objects. Change each array element.

        if (json instanceof JsonArray) {
            for (int i = 0; i < json.getAsJsonArray().size(); i++)
                json.getAsJsonArray().set(i, changeKeyRecur(json, initialKey, newKey));
            return json;
        }

        JsonObject jsonObj = json.getAsJsonObject();

        // Only remaining option is that it's a JsonObject. Update all the keys and values
        for (String key : jsonObj.keySet())
            jsonObj.add(key.equalsIgnoreCase(initialKey) ? newKey : key,
                    changeKeyRecur(jsonObj.get(key), initialKey, newKey));

        return json;
    }

    /**
     * Removes all key-value pairs with the matching key from a {@link JsonObject}. This works recursively, meaning it
     * will find instances of the key inside other {@link JsonObject} or {@link JsonArray} instances nested within the
     * outer Json. Also note that the key is case in-sensitive.
     *
     * @param json the object to check
     * @param key  the key to look for and remove (case in-sensitive)
     * @return the modified object with all instances of the key removed
     */
    public static JsonObject removeKey(@NotNull JsonObject json, @NotNull String key) {
        return removeKeyRecur(json, key).getAsJsonObject();
    }

    /**
     * Recursively removes instances of the key from the {@link JsonElement}. This is called exclusively by {@link
     * #removeKey(JsonObject, String)}. Note that the key is case in-sensitive
     *
     * @param json the element to check
     * @param key  the key to look for and remove (case in-sensitive)
     * @return the modified element with all instances of the key removed
     */
    private static JsonElement removeKeyRecur(JsonElement json, @NotNull String key) {
        // If the element is null or primitive, it can't contain keys or other elements. Return it unmodified.
        if (json == null)
            return null;
        if (json instanceof JsonNull || json instanceof JsonPrimitive)
            return json;

        // If it is an array, it can't contain keys, but it can contain other JsonElements. Check those for the key.
        if (json instanceof JsonArray) {
            for (int i = 0; i < json.getAsJsonArray().size(); i++)
                json.getAsJsonArray().set(i, removeKeyRecur(json, key));
            return json;
        }

        JsonObject jsonObj = json.getAsJsonObject();

        // Only remaining option is that it's a JsonObject. Check for instances of the key and remove it.
        for (String curKey : jsonObj.keySet())
            if (key.equalsIgnoreCase(curKey))
                jsonObj.remove(curKey);
            else
                jsonObj.add(curKey, removeKeyRecur(jsonObj.get(curKey), key));

        return json;
    }
}
