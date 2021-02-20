package botUtils.commandsSystem.builder;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

/**
 * Implemented by all builders that create Json objects, implementing this method confirms that there is a {@link
 * #getJson()} method that will return some {@link JsonElement} in the class.
 */
public interface Builder {
    @NotNull JsonElement getJson() throws ClassNotFoundException;
}