package botUtils.commandsSystem.types.callResponse;

import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonParser;
import botUtils.exceptions.JsonParseException;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.MessageBuilder;
import org.jetbrains.annotations.NotNull;

public class Response {
    private final String[] keys;
    private final MessageBuilder message;

    private Response(JsonObject json) throws JsonParseException {
        keys = JsonParser.getStringArray(json, "keys");
        String type = JsonParser.getString(json, "type");

        switch (type) {
            // TODO implement the replacement strings doing substitutions like ?PREFIX? becoming %
            case "text" -> this.message = new MessageBuilder(JsonParser.getString(json, "contents"));
            case "embed" -> message = new MessageBuilder(JsonBuilder.makeEmbedBuilder(json));

            default -> throw new JsonParseException("Unknown message type '" + type + "'. " +
                    "Expected 'text' or 'embed'.");
        }
    }

    /**
     * Create a new {@link Response} from a {@link JsonObject} read from a file
     *
     * @param json the Json from the file
     * @return the newly created response
     * @throws JsonParseException if there is an error parsing the Json
     */
    public static Response of(JsonObject json) throws JsonParseException {
        return new Response(json);
    }

    public MessageBuilder getMessage() {
        return message;
    }

    /**
     * Returns the first and main key assigned to this {@link Response}. If there are no keys, an empty string is
     * returned.
     * @return the main key
     */
    public @NotNull String getMainKey() {
        return keys.length == 0 ? "" : keys[0];
    }

    /**
     * Determine if the given input key matches this Response object. If this response object was assigned a
     * set of keys from the json, these are compared to the input key and if any of them match it returns true.
     * If a set of keys was not provided in the json then this method will always return true.
     *
     * @param inputKey the key to check against this Response object
     * @return true if this Response object has a key that matches the inputKey, or it doesn't have any keys at all
     */
    public boolean matches(String inputKey) {
        if (keys.length == 0)
            return true;

        for (String key : keys)
            if (key.equalsIgnoreCase(inputKey))
                return true;
        return false;
    }
}
