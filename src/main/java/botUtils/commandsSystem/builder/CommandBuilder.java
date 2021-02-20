package botUtils.commandsSystem.builder;

import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.commandsSystem.json.JsonWriter;
import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.manager.CommandManager;
import botUtils.tools.GenericUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class CommandBuilder implements Builder {
    private final String name;
    private final String description;
    private final String type;
    private String shortDescription;
    private String link;
    private String[] aliases = new String[0];
    private String[] typoAliases;
    private boolean includeInCommandsList = true;

    /**
     * Create a CommandBuilder with the required arguments for all Commands: a name, a description, and a type. Call the
     * other configuration methods to add more key-value pairs to the command JSON, and then use the {@link
     * #build(File)} method to write all the variables to a JSON file that can be read by a {@link
     * CommandManager} later.
     * <p><br>
     * Beware that this does not add a {@link #shortDescription}. It is strongly suggested to set one via {@link
     * #setShortDescription(String)}, even if it's just an empty string, because otherwise the user will see "<i>{@code
     * <No description>}</i>" as the description in the command list.
     *
     * @param name        the name of the command (what a user types to run it)
     * @param description the description of the command (what a user sees when they retrieve the command help embed)
     * @param type        the name of the {@link Command} subclass that should be instantiated when the JSON is parsed
     *                    (case sensitive)
     */
    protected CommandBuilder(@NotNull String name, @NotNull String description, @NotNull String type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.shortDescription = "*<No description>*";
    }

    /**
     * Sets a short description for the command that shows up in the commands list for the {@link
     * CommandManager}.
     * <p>
     * Default state: empty string with no information. It is strongly recommended to set a shortDescription.
     *
     * @param shortDescription concise description of this command
     * @return this {@link CommandBuilder} instance for chaining.
     */
    protected CommandBuilder setShortDescription(@NotNull String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    /**
     * Sets a destination url for the link in the help embed of the command. Users can click the command name to view a
     * website with more information.
     * <p>
     * Default state: no link
     *
     * @param linkUrl concise description of this command
     * @return this {@link CommandBuilder} instance for chaining.
     */
    protected CommandBuilder setLink(@NotNull String linkUrl) {
        this.link = linkUrl;
        return this;
    }

    /**
     * Determines whether this command should appear in the commands list when users request to view all the commands in
     * a {@link CommandManager}.
     * <p>
     * Default state: true
     *
     * @param includeInCommandsList true to appear in command list; false to not appear
     * @return this {@link CommandBuilder} instance for chaining.
     */
    protected CommandBuilder setIncludeInCommandsList(boolean includeInCommandsList) {
        this.includeInCommandsList = includeInCommandsList;
        return this;
    }

    /**
     * Adds an alias that a user can use to call this command. It will be listed in the help embed page for the command,
     * unlike aliases added with {@link #addTypoAliases}.
     * <p>
     * Default state: no aliases
     *
     * @param alias one or more aliases to add
     * @return this {@link CommandBuilder} instance for chaining.
     */
    protected CommandBuilder addAliases(@NotNull String... alias) {
        // If there aren't any aliases yet create a new empty array to add some
        aliases = GenericUtils.mergeArrays(aliases == null ? new String[0] : aliases, alias);
        return this;
    }

    /**
     * Adds a typo alias that a user can use to call this command. This works exactly the same as {@link #addAliases}
     * except that anything added here will not be listed as an alias in the command's help embed. It is intended for
     * adding slight command typos that the bot can still recognize, but could be used for other aliases that you simply
     * don't want listed in the help embed.
     * <p>
     * Default state: no typo aliases
     *
     * @param alias one or more aliases to add
     * @return this {@link CommandBuilder} instance for chaining.
     */
    protected CommandBuilder addTypoAliases(@NotNull String... alias) {
        // If there aren't any typo aliases yet create a new empty array to add some
        typoAliases = GenericUtils.mergeArrays(typoAliases == null ? new String[0] : typoAliases, alias);
        return this;
    }

    /**
     * Compiles all the settings from this builder into a specified Json file.
     *
     * @param file the {@link File} to put all the JSON in
     * @throws NullPointerException     if the destination file is null
     * @throws IllegalArgumentException if the destination file is not a json file
     * @throws IOException              if there was an error printing the file
     * @throws ClassNotFoundException   if there was an error building the Json
     */
    public void build(@NotNull File file)
            throws IOException, ClassNotFoundException {
        JsonWriter.writeJson(getJson(), file);
    }

    /**
     * Converts all the instance variables into key-value pairs in the JSONObject. Subclasses of {@link CommandBuilder}
     * should override this method to add new instance variables should still call this method to add the basic instance
     * variables first.
     *
     * @return the completed JSONObject with all the instance variables added
     */
    public @NotNull JsonObject getJson() throws ClassNotFoundException {
        return JsonBuilder.buildJsonObject(JsonMap.of()
                .add("name", name)
                .add("description", description)
                .add("type", type)
                .add("shortDescription", shortDescription)
                .add("link", link)
                .add("aliases", JsonBuilder.buildJsonArray(aliases))
                .add("typoAliases", JsonBuilder.buildJsonArray(typoAliases))
                .add("includeInCommandsList", includeInCommandsList));
    }
}