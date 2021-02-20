package botUtils.commandsSystem.json;

import botUtils.exceptions.JsonParseException;
import botUtils.tools.Checks;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.IntStream;

public class JsonParser {

    /**
     * Attempts to get a boolean from the given {@link JsonObject} at the specified key. If that key doesn't exist, the
     * default value is returned instead. If it does exist but there is an error interpreting it (e.g. the value is a
     * different variable type in the json), an error is thrown.
     *
     * @param json         the Json to check
     * @param key          the key to look for
     * @param defaultValue the value to return instead if the key is not present
     * @return the identified value in the json (or defaultValue if key was missing)
     * @throws JsonParseException if the key was present but the matching value is the wrong data type
     */
    public static boolean getBoolean(@NotNull JsonObject json, @NotNull String key, boolean defaultValue)
            throws JsonParseException {
        try {
            return json.has(key) ? json.get(key).getAsBoolean() : defaultValue;
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected boolean.");
        }
    }

    /**
     * Attempts to get a boolean from the given {@link JsonObject} at the specified key. If that key doesn't exist or
     * the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static boolean getBoolean(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            return json.get(key).getAsBoolean();
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected boolean.");
        }
    }

    /**
     * Attempts to get a String from the given {@link JsonObject} at the specified key. If that key doesn't exist, the
     * default value is returned instead. If it does exist but there is an error interpreting it (e.g. the value is a
     * different variable type in the json), an error is thrown.
     *
     * @param json         the Json to check
     * @param key          the key to look for
     * @param defaultValue the value to return instead if the key is not present
     * @return the identified value in the json (or defaultValue if key was missing)
     * @throws JsonParseException if the key was present but the matching value is the wrong data type
     */
    public static String getString(@NotNull JsonObject json, @NotNull String key, String defaultValue)
            throws JsonParseException {
        try {
            return json.has(key) ? json.get(key).getAsString() : defaultValue;
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected String.");
        }
    }

    /**
     * Attempts to get a String from the given {@link JsonObject} at the specified key. If that key doesn't exist or the
     * matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static String getString(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            return json.get(key).getAsString();
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected String.");
        }
    }

    /**
     * Attempts to get a double from the given {@link JsonObject} at the specified key. If that key doesn't exist, the
     * default value is returned instead. If it does exist but there is an error interpreting it (e.g. the value is a
     * different variable type in the json), an error is thrown.
     *
     * @param json         the Json to check
     * @param key          the key to look for
     * @param defaultValue the value to return instead if the key is not present
     * @return the identified value in the json (or defaultValue if key was missing)
     * @throws JsonParseException if the key was present but the matching value is the wrong data type
     */
    public static double getDouble(@NotNull JsonObject json, @NotNull String key, double defaultValue)
            throws JsonParseException {
        try {
            return json.has(key) ? json.get(key).getAsDouble() : defaultValue;
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected double.");
        }
    }

    /**
     * Attempts to get a double from the given {@link JsonObject} at the specified key. If that key doesn't exist or the
     * matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static double getDouble(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            return json.get(key).getAsDouble();
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected double.");
        }
    }

    /**
     * Attempts to get an int from the given {@link JsonObject} at the specified key. If that key doesn't exist, the
     * default value is returned instead. If it does exist but there is an error interpreting it (e.g. the value is a
     * different variable type in the json), an error is thrown.
     *
     * @param json         the Json to check
     * @param key          the key to look for
     * @param defaultValue the value to return instead if the key is not present
     * @return the identified value in the json (or defaultValue if key was missing)
     * @throws JsonParseException if the key was present but the matching value is the wrong data type
     */
    public static int getInteger(@NotNull JsonObject json, @NotNull String key, int defaultValue)
            throws JsonParseException {
        try {
            return json.has(key) ? json.get(key).getAsInt() : defaultValue;
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected integer.");
        }
    }

    /**
     * Attempts to get an int from the given {@link JsonObject} at the specified key. If that key doesn't exist or the
     * matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static int getInteger(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            return json.get(key).getAsInt();
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected integer.");
        }
    }

    /**
     * Attempts to get a {@link JsonObject} inside the given {@link JsonObject} at the specified key. If that key
     * doesn't exist or the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static @NotNull JsonObject getJsonObject(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            return json.get(key).getAsJsonObject();
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected JsonObject.");
        }
    }

    /**
     * Attempts to get a {@link JsonArray} inside the given {@link JsonObject} at the specified key. If that key doesn't
     * exist or the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static @NotNull JsonArray getJsonArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            return json.get(key).getAsJsonArray();
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected JsonArray.");
        }
    }

    /**
     * Attempts to get a boolean array from the given {@link JsonObject} at the specified key. If that key isn't in the
     * json, or if it is but the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static boolean[] getBooleanArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            if (json.get(key).isJsonPrimitive())
                return new boolean[]{json.get(key).getAsBoolean()};
            return primitiveToBooleanArray(jsonElementArrayToPrimitive(parseJsonArray(json.get(key).getAsJsonArray())));
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected array of boolean.");
        }
    }

    /**
     * Convenience method for {@link #getBooleanArray(JsonObject, String)}, but any errors are intercepted. If an error
     * would have been thrown, an empty array is returned instead.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     */
    public static boolean[] getBooleanArrayNoError(@NotNull JsonObject json, @NotNull String key) {
        try {
            return getBooleanArray(json, key);
        } catch (Exception ignore) {
            return new boolean[0];
        }
    }

    /**
     * Attempts to get a String array from the given {@link JsonObject} at the specified key. If that key isn't in the
     * json, or if it is but the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static @NotNull String[] getStringArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            if (json.get(key).isJsonPrimitive())
                return new String[]{json.get(key).getAsString()};
            return primitiveToStringArray(jsonElementArrayToPrimitive(parseJsonArray(json.get(key).getAsJsonArray())));
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected array of Strings.");
        }
    }

    /**
     * Convenience method for {@link #getStringArray(JsonObject, String)}, but any errors are intercepted. If an error
     * would have been thrown, an empty array is returned instead.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     */
    public static @NotNull String[] getStringArrayNoError(@NotNull JsonObject json, @NotNull String key) {
        try {
            return getStringArray(json, key);
        } catch (Exception ignore) {
            return new String[0];
        }
    }

    /**
     * Attempts to get a double array from the given {@link JsonObject} at the specified key. If that key isn't in the
     * json, or if it is but the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static double[] getDoubleArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            if (json.get(key).isJsonPrimitive())
                return new double[]{json.get(key).getAsDouble()};
            return primitiveToDoubleArray(jsonElementArrayToPrimitive(parseJsonArray(json.get(key).getAsJsonArray())));
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected array of doubles.");
        }
    }

    /**
     * Convenience method for {@link #getDoubleArray(JsonObject, String)}, but any errors are intercepted. If an error
     * would have been thrown, an empty array is returned instead.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     */
    public static double[] getDoubleArrayNoError(@NotNull JsonObject json, @NotNull String key) {
        try {
            return getDoubleArray(json, key);
        } catch (Exception ignore) {
            return new double[0];
        }
    }

    /**
     * Attempts to get an integer array from the given {@link JsonObject} at the specified key.  If that key isn't in
     * the json, or if it is but the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static int[] getIntArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            if (json.get(key).isJsonPrimitive())
                return new int[]{json.get(key).getAsInt()};
            return primitiveToIntegerArray(jsonElementArrayToPrimitive(parseJsonArray(json.get(key).getAsJsonArray())));
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected array of integers.");
        }
    }

    /**
     * Convenience method for {@link #getIntArray(JsonObject, String)}, but any errors are intercepted. If an error
     * would have been thrown, an empty array is returned instead.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     */
    public static int[] getIntArrayNoError(@NotNull JsonObject json, @NotNull String key) {
        try {
            return getIntArray(json, key);
        } catch (Exception ignore) {
            return new int[0];
        }
    }

    /**
     * Attempts to get an array of {@link JsonArray} instances from the given {@link JsonObject} at the specified key.
     * If that key isn't in the json, or if it is but the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static @NotNull JsonArray[] getJsonArrayArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        return jsonElementArrayToArray(getJsonElementArray(json, key));
    }

    /**
     * Convenience method for {@link #getJsonArrayArray(JsonObject, String)}, but any errors are intercepted. If an
     * error would have been thrown, an empty array is returned instead.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     */
    public static @NotNull JsonArray[] getJsonArrayArrayNoError(@NotNull JsonObject json, @NotNull String key) {
        try {
            return getJsonArrayArray(json, key);
        } catch (Exception ignore) {
            return new JsonArray[0];
        }
    }

    /**
     * Attempts to get an array of {@link JsonObject} instances from the given {@link JsonObject} at the specified key.
     * If that key isn't in the json, or if it is but the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static @NotNull JsonObject[] getJsonObjectArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        return jsonElementArrayToObject(getJsonElementArray(json, key));
    }

    /**
     * Convenience method for {@link #getJsonObjectArray(JsonObject, String)}, but any errors are intercepted. If an
     * error would have been thrown, an empty array is returned instead.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     */
    public static @NotNull JsonObject[] getJsonObjectArrayNoError(@NotNull JsonObject json, @NotNull String key) {
        try {
            return getJsonObjectArray(json, key);
        } catch (Exception ignore) {
            return new JsonObject[0];
        }
    }

    /**
     * Attempts to get an array of {@link JsonElement} instances from the given {@link JsonObject} at the specified key.
     * If that key isn't in the json, or if it is but the matching value is the wrong data type, an error is thrown.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     * @throws JsonParseException if the key was missing or the matching value is the wrong data type
     */
    public static @NotNull JsonElement[] getJsonElementArray(@NotNull JsonObject json, @NotNull String key)
            throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        try {
            return parseJsonArray(json.get(key).getAsJsonArray());
        } catch (Exception ignore) {
            throw new JsonParseException("Unexpected type at key '" + key + "'. Expected array of JsonElements.");
        }
    }

    /**
     * Convenience method for {@link #getJsonElementArray(JsonObject, String)}, but any errors are intercepted. If an
     * error would have been thrown, an empty array is returned instead.
     *
     * @param json the Json to check
     * @param key  the key to look for
     * @return the identified value in the json
     */
    public static @NotNull JsonElement[] getJsonElementArrayNoError(@NotNull JsonObject json, @NotNull String key) {
        try {
            return getJsonElementArray(json, key);
        } catch (Exception ignore) {
            return new JsonElement[0];
        }
    }

    public static Object getElement(@NotNull JsonObject json, @NotNull String key) throws JsonParseException {
        Checks.jsonContainsKey(json, key);
        JsonElement element = json.get(key);

        if (!(element instanceof JsonPrimitive))
            return element;

        JsonPrimitive primitive = element.getAsJsonPrimitive();

        if (primitive.isBoolean())
            return primitive.getAsBoolean();
        if (primitive.isNumber())
            return primitive.getAsNumber();
        if (primitive.isString())
            return primitive.getAsString();

        throw new JsonParseException("Unknown JsonPrimitive type.");
    }

    /**
     * Converts a {@link JsonArray} to a Java array of its component {@link JsonElement} objects.
     *
     * @param json the input Json array
     * @return the output Java array of Json objects
     */
    public @NotNull
    static JsonElement[] parseJsonArray(@NotNull JsonArray json) {
        JsonElement[] array = new JsonElement[json.size()];
        for (int i = 0; i < json.size(); i++) {
            array[i] = json.get(i);
        }
        return array;
    }

    /**
     * Converts a Java array of {@link JsonElement} instances to an array of {@link JsonPrimitive} instances.
     *
     * @param array the input array
     * @return the output (casted) array
     */
    public static JsonPrimitive[] jsonElementArrayToPrimitive(@NotNull JsonElement[] array) {
        return Arrays.stream(array).map(JsonElement::getAsJsonPrimitive).toArray(JsonPrimitive[]::new);
    }

    /**
     * Converts a Java array of {@link JsonElement} instances to an array of {@link JsonObject} instances.
     *
     * @param array the input array
     * @return the output (casted) array
     */
    public static JsonObject[] jsonElementArrayToObject(@NotNull JsonElement[] array) {
        return Arrays.stream(array).map(JsonElement::getAsJsonObject).toArray(JsonObject[]::new);
    }

    /**
     * Converts a Java array of {@link JsonElement} instances to an array of {@link JsonArray} instances. This is
     * basically type conversion of the first dimension in a 2D array.
     *
     * @param array the input array
     * @return the output (casted) array
     */
    public static JsonArray[] jsonElementArrayToArray(@NotNull JsonElement[] array) {
        return Arrays.stream(array).map(JsonElement::getAsJsonArray).toArray(JsonArray[]::new);
    }

    /**
     * Converts one ore more {@link JsonPrimitive} objects to booleans.
     *
     * @param primitive one or more primitives
     * @return the array of converted primitives
     * @throws JsonParseException if a primitive fails to convert to the proper data type
     */
    public static boolean[] primitiveToBooleanArray(@NotNull JsonPrimitive... primitive) throws JsonParseException {
        try {
            boolean[] array = new boolean[primitive.length];
            IntStream.range(0, primitive.length).forEach(i -> array[i] = primitive[i].getAsBoolean());
            return array;
        } catch (Exception ignore) {
            throw new JsonParseException("Failed to read primitive as boolean(s).");
        }
    }

    /**
     * Converts one ore more {@link JsonPrimitive} objects to Strings.
     *
     * @param primitive one or more primitives
     * @return the array of converted primitives
     * @throws JsonParseException if a primitive fails to convert to the proper data type
     */
    public static String[] primitiveToStringArray(@NotNull JsonPrimitive... primitive) throws JsonParseException {
        try {
            return Arrays.stream(primitive).map(JsonPrimitive::getAsString).toArray(String[]::new);
        } catch (Exception ignore) {
            throw new JsonParseException("Failed to read primitive as String(s).");
        }
    }

    /**
     * Converts one ore more {@link JsonPrimitive} objects to doubles.
     *
     * @param primitive one or more primitives
     * @return the array of converted primitives
     * @throws JsonParseException if a primitive fails to convert to the proper data type
     */
    public static double[] primitiveToDoubleArray(@NotNull JsonPrimitive... primitive) throws JsonParseException {
        try {
            return Arrays.stream(primitive).map(JsonPrimitive::getAsDouble).mapToDouble(Double::doubleValue).toArray();
        } catch (Exception ignore) {
            throw new JsonParseException("Failed to read primitive as String(s).");
        }
    }

    /**
     * Converts one ore more {@link JsonPrimitive} objects to integers.
     *
     * @param primitive one or more primitives
     * @return the array of converted primitives
     * @throws JsonParseException if a primitive fails to convert to the proper data type
     */
    public static int[] primitiveToIntegerArray(@NotNull JsonPrimitive... primitive) throws JsonParseException {
        try {
            return Arrays.stream(primitive).map(JsonPrimitive::getAsInt).mapToInt(Integer::intValue).toArray();
        } catch (Exception ignore) {
            throw new JsonParseException("Failed to read primitive as integer(s).");
        }
    }
}