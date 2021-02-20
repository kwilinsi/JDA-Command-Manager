package botUtils.commandsSystem.json;

import botUtils.commandsSystem.builder.Builder;
import botUtils.exceptions.JsonParseException;
import botUtils.exceptions.UnknownColorException;
import botUtils.tools.Colors;
import botUtils.tools.EmbedField;
import botUtils.tools.MessageUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JsonBuilder {
    /**
     * Creates a {@link JsonArray} with all the elements of the given array.
     *
     * @param array the input array
     * @param <T>   type parameter
     * @return the finished {@link JsonArray}
     * @throws ClassNotFoundException if the class type of any items in the array are not recognized by {@link
     *                                #getJsonElement}
     */
    public static <T> JsonArray buildJsonArray(T[] array) throws ClassNotFoundException {
        JsonArray json = new JsonArray();
        if (array == null)
            return json;
        for (T o : array)
            json.add(getJsonElement(o));
        return json;
    }

    /**
     * Appends the key-value pairs from a {@link Map} to an existing {@link JsonObject}. Any values or keys which are
     * null will be omitted and the key will not be added to the Json.
     * <p>
     * All values must be an instance of {@link JsonElement}, {@link String}, {@link Character}, {@link Number}, or
     * {@link Boolean}. Any other class types for non-null values will result in an exception.
     *
     * @param map  a map of key-value pairs
     * @param json the json on which to add the key-value pairs
     * @param <T>  type parameter
     * @return the modified json
     * @throws ClassNotFoundException if the map contains a value with an unknown class type (one not recognized by
     *                                {@link #getJsonElement}).
     */
    public @NotNull
    static <T> JsonObject appendJsonObject(Map<String, T> map, JsonObject json) throws ClassNotFoundException {
        for (String key : map.keySet())
            if (key != null && map.get(key) != null)
                json.add(key, getJsonElement(map.get(key)));
        return json;
    }

    /**
     * Appends the key-value pairs from a {@link JsonMap} to an existing {@link JsonObject}. Any values or keys which
     * are null will be omitted and the key will not be added to the Json.
     * <p>
     * All values must be an instance of {@link JsonElement}, {@link String}, {@link Character}, {@link Number}, or
     * {@link Boolean}. Any other class types for non-null values will result in an exception.
     *
     * @param map  a map of key-value pairs
     * @param json the json on which to add the key-value pairs
     * @return the modified json
     * @throws ClassNotFoundException if the map contains a value with an unknown class type (one not recognized by
     *                                {@link #getJsonElement}).
     */
    public @NotNull
    static JsonObject appendJsonObject(JsonMap map, JsonObject json) throws ClassNotFoundException {
        return appendJsonObject(map.getMap(), json);
    }

    /**
     * Takes any input that is an instance of {@link JsonElement}, {@link String}, {@link Character}, {@link Number}, or
     * {@link Boolean} and returns it as a {@link JsonElement}. Inputs that are already json elements are simply
     * returned unmodified, but other recognized types are converted to a {@link JsonPrimitive} and returned. If the
     * input class is not recognized (meaning it is not in the aforementioned list) an error will be thrown.
     * <p><br>
     * Additionally, any input that extends the {@link Builder} interface will be converted to some {@link JsonElement}
     * via its {@link Builder#getJson()} method and returned.
     *
     * @param input the input to modify and return
     * @param <T>   type parameter
     * @return the input as instance of a {@link JsonElement} subclass
     * @throws ClassNotFoundException if the class type of the input was not recognized
     */
    public static <T> JsonElement getJsonElement(T input) throws ClassNotFoundException {
        // If it's already a JsonElement, return it
        if (input instanceof JsonElement)
            return (JsonElement) input;

        // Otherwise determine which primitive it is
        if (input instanceof String)
            return new JsonPrimitive((String) input);
        if (input instanceof Boolean)
            return new JsonPrimitive((Boolean) input);
        if (input instanceof Number)
            return new JsonPrimitive((Number) input);
        if (input instanceof Character)
            return new JsonPrimitive((Character) input);
        if (input instanceof Builder)
            return ((Builder) input).getJson();

        throw new ClassNotFoundException("Unable to identify value of class '" + input.getClass().getName() + "'. " +
                "Failed to convert it to a JsonElement.");
    }

    /**
     * Builds a {@link JsonObject} from the provided map. Any values or keys which are null will be omitted and the key
     * will not be added to the JsonObject.
     *
     * @param map a {@link Map} of key-value pairs
     * @param <V> type parameter
     * @throws ClassNotFoundException if the class type of any items in the map are not recognized by {@link
     *                                #getJsonElement}
     */
    public @NotNull
    static <V> JsonObject buildJsonObject(Map<String, V> map) throws ClassNotFoundException {
        return appendJsonObject(map, new JsonObject());
    }

    /**
     * Builds a {@link JsonObject} from the provided map. Any values or keys which are null will be omitted and the key
     * will not be added to the JsonObject.
     *
     * @param map a {@link JsonMap} of key-value pairs
     * @throws ClassNotFoundException if the class type of any items in the map are not recognized by {@link
     *                                #getJsonElement}
     */
    public @NotNull
    static JsonObject buildJsonObject(JsonMap map) throws ClassNotFoundException {
        return appendJsonObject(map, new JsonObject());
    }

    /**
     * Creates a new {@link EmbedBuilder} from data in a {@link JsonObject}. All of the parameters for embeds can be set
     * via the Json. The following is a list of recognized keys:<br><br> {@code title, link, description, color,
     * footerText, footerImg, authorText, authorImg, timestamp, mainImg, thumbnailImg, fields}.<br><br> All of these
     * should be strings except for {@code fields}, which should be a {@link JsonArray} of {@link JsonObject} groups
     * that correspond to an {@link EmbedField}. For more information see {@link EmbedField#of(JsonObject)}. For the
     * timestamp, specify a time in '{@code hh:mm:ss MM/dd/yyyy}' format.
     * <br><br>
     * All image entries must be resolvable URLs pointing to images. Otherwise an {@link IllegalArgumentException} will
     * be thrown will adding them to the {@link EmbedBuilder}.
     *
     * @param json the json object to convert to an {@link EmbedBuilder}
     * @return the completed embed
     * @throws JsonParseException if there is an error parsing the Json
     */
    public @NotNull
    static EmbedBuilder makeEmbedBuilder(@NotNull JsonObject json) throws JsonParseException {
        // TODO allow 'now' for the time to set a timestamp to the time when it was sent and 'compile' to use the time the bot reads the json

        String title = JsonParser.getString(json, "title", null);
        String link = JsonParser.getString(json, "link", null);
        String description = JsonParser.getString(json, "description", null);
        Color color;

        try {
            color = Colors.parseColor(JsonParser.getString(json, "color", null));
        } catch (UnknownColorException e) {
            e.printStackTrace();
            color = null;
        }

        String footer = JsonParser.getString(json, "footerText", null);
        String footerImg = JsonParser.getString(json, "footerImg", null);
        String author = JsonParser.getString(json, "authorText", null);
        String authorUrl = JsonParser.getString(json, "authorUrl", null);
        String authorImg = JsonParser.getString(json, "authorImg", null);
        String timeStr = JsonParser.getString(json, "timestamp", null);
        LocalDateTime time = timeStr == null ? null : LocalDateTime.parse(timeStr,
                DateTimeFormatter.ofPattern("hh:mm:ss MM/dd/yyyy"));
        String image = JsonParser.getString(json, "mainImg", null);
        String thumbnail = JsonParser.getString(json, "thumbnailImg", null);

        EmbedField[] fields = json.has("fields") ?
                EmbedField.ofArray(JsonParser.getJsonObjectArray(json, "fields")) :
                null;

        return MessageUtils.makeEmbedBuilder(
                title, link, description, color, footer, footerImg, author,
                authorUrl, authorImg, thumbnail, image, time, fields);
    }
}