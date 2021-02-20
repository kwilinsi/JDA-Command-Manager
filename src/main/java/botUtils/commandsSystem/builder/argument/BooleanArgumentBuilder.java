package botUtils.commandsSystem.builder.argument;

import botUtils.commandsSystem.builder.Builder;
import botUtils.commandsSystem.builder.SyntaxBuilder;
import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.commandsSystem.types.function.ArgType;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class BooleanArgumentBuilder extends ArgumentBuilder implements Builder {
    /**
     * This is the default value for this argument. If it is not included in a syntax and the user does not give a value
     * for it, this will be substituted.
     */
    private boolean defaultValue;

    private BooleanArgumentBuilder(@NotNull String name, @NotNull String description) {
        super(name, description, ArgType.BOOLEAN);
    }

    /**
     * Creates a new {@link BooleanArgumentBuilder} instance based on a name and description. The argument type is
     * automatically set to {@link ArgType#BOOLEAN}.
     *
     * @param name        the name of the argument (also used when making a {@link SyntaxBuilder} using this argument)
     * @param description a short description explaining what the argument is/does in a command
     */
    public static BooleanArgumentBuilder of(@NotNull String name, @NotNull String description) {
        return new BooleanArgumentBuilder(name, description);
    }

    /**
     * Sets the default value for this argument. This allows code executing a function to call the value of the argument
     * even if it wasn't provided by the user (such as for optional arguments not present in all syntaxes). In that
     * case, the method would simply receive the default value set via this method and stored in the JSON.
     *
     * @param defaultValue the default argument value
     * @return this {@link BooleanArgumentBuilder} instance for chaining
     */
    public BooleanArgumentBuilder setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Get a {@link JsonObject} which contains key-value pairs for the argument parameters. This includes everything
     * instantiated in the {@link ArgumentBuilder} class along with settings specific for boolean types.
     *
     * @return the completed {@link JsonObject}
     * @throws ClassNotFoundException if there's an error building the Json
     */
    @Override
    public @NotNull JsonObject getJson() throws ClassNotFoundException {
        return JsonBuilder.appendJsonObject(
                JsonMap.of().add("defaultValue", defaultValue),
                super.getJson());
    }
}
