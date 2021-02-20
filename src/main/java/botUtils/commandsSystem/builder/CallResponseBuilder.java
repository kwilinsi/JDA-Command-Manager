package botUtils.commandsSystem.builder;

import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.tools.Checks;
import botUtils.commandsSystem.manager.CommandManager;
import botUtils.tools.GenericUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class CallResponseBuilder extends CommandBuilder implements Builder {
    private ResponseBuilder[] responses = new ResponseBuilder[0];
    private String defaultKey;

    private CallResponseBuilder(@NotNull String name, @NotNull String description) {
        super(name, description, "CallResponse");
    }

    /**
     * Create a CallResponseBuilder with the two required arguments for all Commands: a name and a description. Call the
     * other configuration methods to add more key-value pairs to the command JSON. Then call the {@link
     * CommandBuilder#build(File)} method to write all the variables to a JSON file that can be read by a {@link
     * CommandManager} later.
     *
     * @param name        the name of the command (what a user types to run it)
     * @param description the description of the command (what a user sees when they retrieve the command help embed)
     */
    public static CallResponseBuilder of(@NotNull String name, @NotNull String description) {
        return new CallResponseBuilder(name, description);
    }

    /**
     * Sets the default key called when the user runs this command without specifying a key. This key should be
     * associated with one of the {@link ResponseBuilder} responses added to the command. If no default key is given, if
     * the user does not specify a key they will get the help embed.
     *
     * @param defaultKey the name of the key to use by default if one is not specified by the user
     * @return this {@link CallResponseBuilder} instance for chaining
     * @throws IllegalArgumentException if the key is null or empty
     */
    public CallResponseBuilder setDefaultKey(@NotNull String defaultKey) {
        Checks.checkStringHasContents(defaultKey);
        this.defaultKey = defaultKey;
        return this;
    }

    /**
     * Adds a {@link ResponseBuilder} response to the command, complete with a list of keys to call it and a message to
     * send to the user when it is called.
     *
     * @param response the finished {@link ResponseBuilder}
     * @return this {@link CallResponseBuilder} instance for chaining
     */
    public CallResponseBuilder addResponse(@NotNull ResponseBuilder response) {
        this.responses = GenericUtils.mergeArrays(this.responses, new ResponseBuilder[]{response});
        return this;
    }

    /**
     * Gets a {@link JsonObject} with all the information for this command, including an array of all the {@link
     * ResponseBuilder} responses.
     *
     * @return a finished {@link JsonObject}
     * @throws IllegalStateException    if the default key is not specified in any of the responses
     * @throws ClassNotFoundException   if there was an error building the Json
     * @throws IllegalArgumentException if a response key is used by multiple builders or a default key is not used by
     *                                  any builders
     */
    @Override
    public @NotNull JsonObject getJson() throws ClassNotFoundException {
        // Get a list of all the keys used by all responses
        List<String> keys = new ArrayList<>();
        for (ResponseBuilder response : responses)
            Collections.addAll(keys, response.getKeys());

        // Check for any duplicate keys
        if (Checks.containsDuplicates(keys))
            throw new IllegalArgumentException("Builder contains duplicate response key(s). Keys cannot be reused " +
                    "by multiple ResponseBuilders in a single command.");

        // Confirm that the default key is recognized by one of the responses or that there are no defined responses
        // This ensures that if a response was defined in the builder it will be called if the user doesn't give
        // any keys when calling it in Discord. But if the builder is running without any responses (meaning they
        // will be added later in the Json) then this check is unnecessary.
        if (keys.size() != 0 && !keys.contains(defaultKey))
            throw new IllegalArgumentException("Default key `" + defaultKey + "` is not listed as a valid key for " +
                    "any ResponseBuilders.");

        // Must base result JSON off super.getJSON() to include basic command parameters
        return JsonBuilder.appendJsonObject(
                JsonMap.of()
                        .add("defaultResponseKey", defaultKey)
                        .add("responses", JsonBuilder.buildJsonArray(Arrays.stream(responses)
                                .map(ResponseBuilder::getJson).toArray(JsonObject[]::new))),
                super.getJson());
    }

    public CallResponseBuilder setShortDescription(@NotNull String shortDescription) {
        return (CallResponseBuilder) super.setShortDescription(shortDescription);
    }

    public CallResponseBuilder setLink(@NotNull String linkUrl) {
        return (CallResponseBuilder) super.setLink(linkUrl);
    }

    public CallResponseBuilder setIncludeInCommandsList(boolean includeInCommandsList) {
        return (CallResponseBuilder) super.setIncludeInCommandsList(includeInCommandsList);
    }

    public CallResponseBuilder addAliases(@NotNull String... alias) {
        return (CallResponseBuilder) super.addAliases(alias);
    }

    public CallResponseBuilder addTypoAliases(@NotNull String... alias) {
        return (CallResponseBuilder) super.addTypoAliases(alias);
    }
}
