package botUtils.commandsSystem.builder.argument;

import botUtils.commandsSystem.builder.Builder;
import botUtils.commandsSystem.builder.SyntaxBuilder;
import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.commandsSystem.types.function.ArgType;
import botUtils.commandsSystem.types.function.Function;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * This is the base class for all {@link ArgumentBuilder} types. To create an argument, use the static {@code of()}
 * method available in {@link BooleanArgumentBuilder}, {@link NumberArgumentBuilder}, and {@link
 * StringArgumentBuilder}.
 */
public abstract class ArgumentBuilder implements Builder {
    /**
     * The name of the argument. All argument names must be unique for a single {@link Function}. This is the name
     * referenced by a {@link SyntaxBuilder} for arguments.
     */
    private final String name;

    /**
     * The description of the argument. This is printed in the help embed for the {@link Function}.
     */
    private final String description;

    /**
     * The data type expected of input for this argument, represented as an {@link ArgType} enum. When printed in the
     * Json, this will be represented as a string obtained through {@link ArgType#getTypeStr(ArgType)}.
     */
    private final ArgType type;

    protected ArgumentBuilder(@NotNull String name, @NotNull String description, @NotNull String type) {
        this(name, description, ArgType.getType(type));
    }

    protected ArgumentBuilder(@NotNull String name, @NotNull String description, @NotNull ArgType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    /**
     * Returns the name of the argument, useful for checking against a {@link SyntaxBuilder}.
     *
     * @return the argument {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link ArgType} representing the data type required by this argument.
     *
     * @return the {@link #type}
     */
    public ArgType getType() {
        return type;
    }

    /**
     * Returns the string representing the type of this argument. This is the same as calling {@link #getType()} and
     * passing it through {@link ArgType#getTypeStr(ArgType)}.
     *
     * @return the {@link #type} as a {@link String}
     */
    public String getTypeStr() {
        return ArgType.getTypeStr(type);
    }

    /**
     * Get a {@link JsonObject} which contains key-value pairs for the basic argument parameters: name, description, and
     * type. Extend this class to add more parameters.
     *
     * @return the completed {@link JsonObject}
     * @throws ClassNotFoundException if there's an error building the Json
     */
    public @NotNull JsonObject getJson() throws ClassNotFoundException {
        return JsonBuilder.buildJsonObject(JsonMap.of()
                .add("name", name)
                .add("description", description)
                .add("type", ArgType.getTypeStr(type)));
    }
}
