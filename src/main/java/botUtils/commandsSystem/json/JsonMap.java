package botUtils.commandsSystem.json;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is purely for convenience methods. All it does it make it easy to quickly create a temporary
 * HashMap of key-value pairs to go into a Json builder using dot notation.
 */
public class JsonMap {
    private final Map<String, Object> map = new HashMap<>();

    private JsonMap() {
    }

    public static JsonMap of() {
        return new JsonMap();
    }

    /**
     * Add another key-value pair to the Json.
     *
     * @param key   the key (exact String that will appear in the Json file)
     * @param value the matching value
     * @return this {@link JsonMap} instance for chaining
     */
    public JsonMap add(String key, Object value) {
        map.put(key, value);
        return this;
    }

    /**
     * Get the full map with all the key-value pairs.
     *
     * @return the map
     */
    public Map<String, Object> getMap() {
        return map;
    }
}
