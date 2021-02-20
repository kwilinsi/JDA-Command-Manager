package botUtils.commandsSystem.builder;

import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.manager.CommandManager;
import botUtils.commandsSystem.builder.argument.ArgumentBuilder;
import botUtils.tools.GenericUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class FunctionBuilder extends CommandBuilder implements Builder {
    private SyntaxBuilder[] syntaxes = new SyntaxBuilder[0];
    private ArgumentBuilder[] arguments = new ArgumentBuilder[0];

    private FunctionBuilder(@NotNull String name, @NotNull String description) {
        super(name, description, "Function");
    }

    /**
     * Create a FunctionBuilder with the required arguments. Call the other configuration methods to add more
     * key-value pairs to the command JSON, and then use the {@link CommandBuilder#build(File)} method to write all the
     * variables to a JSON file that can be read by a {@link CommandManager} later.
     *
     * @param name        the name of the command (what a user types to run it)
     * @param description the description of the command (what a user sees when they retrieve the command help embed)
     */
    public static FunctionBuilder of(@NotNull String name, @NotNull String description) {
        return new FunctionBuilder(name, description);
    }

    /**
     * Adds an argument that can be used by syntaxes in the command. It will be listed in the help embed page for the
     * command underneath the syntaxes.
     * <p>
     * Default state: no arguments
     *
     * @param argument one or more arguments to add
     * @return this {@link FunctionBuilder} instance for chaining.
     */
    public FunctionBuilder addArgument(ArgumentBuilder... argument) {
        arguments = GenericUtils.mergeArrays(arguments, argument);
        return this;
    }

    /**
     * Adds a syntax that uses Arguments in the command. It will be listed in the help embed page for the command.
     * <p>
     * Default state: no syntaxes
     *
     * @param syntax one or more syntaxes to add
     * @return this {@link FunctionBuilder} instance for chaining.
     */
    public FunctionBuilder addSyntax(SyntaxBuilder... syntax) {
        syntaxes = GenericUtils.mergeArrays(syntaxes, syntax);
        return this;
    }

    /**
     * Gets a {@link JsonObject} with all the information for this command, including arguments and syntaxes.
     *
     * @return a finished {@link JsonObject}
     * @throws IllegalStateException if the default key is not specified in any of the responses
     * @throws ClassNotFoundException if there was an error building the Json
     * @throws IllegalArgumentException if an undefined argument was used in a syntax
     */
    @Override
    public @NotNull JsonObject getJson() throws ClassNotFoundException {
        // List all the names of arguments used in SyntaxBuilders and ArgumentBuilders
        List<String> argNames = new ArrayList<>();
        List<String> synArgNames = new ArrayList<>();
        for (ArgumentBuilder argument : arguments)
            argNames.add(argument.getName());
        for (SyntaxBuilder syntax : syntaxes)
            Collections.addAll(synArgNames, syntax.getUniqueArgumentNames());

        // Make sure all the argument names in the Syntaxes are valid Arguments
        for (String arg : synArgNames)
            if (!argNames.contains(arg))
                throw new IllegalArgumentException("Argument " + arg + " was included in a SyntaxBuilder but not " +
                        "defined with an ArgumentBuilder in FunctionBuilder.addArgument()");
        // Issue a warning if a useless ArgumentBuilder was made that's not applied by a Syntax
        for (String arg : argNames)
            if (!synArgNames.contains(arg))
                System.out.println("Unnecessary argument " + arg + " defined with ArgumentBuilder for function " +
                        "but not used by any SyntaxBuilders.");

        // Add Function stuff to the JSON
        Map<String, Object> map = new HashMap<>();

        map.put("syntax", JsonBuilder.buildJsonArray(syntaxes));
        map.put("arguments", JsonBuilder.buildJsonArray(arguments));

        // Must base result JSON off super.getJSON() to include basic command parameters
        return JsonBuilder.appendJsonObject(map, super.getJson());
    }

    public FunctionBuilder setShortDescription(@NotNull String shortDescription) {
        return (FunctionBuilder) super.setShortDescription(shortDescription);
    }

    public FunctionBuilder setLink(@NotNull String linkUrl) {
        return (FunctionBuilder) super.setLink(linkUrl);
    }

    public FunctionBuilder setIncludeInCommandsList(boolean includeInCommandsList) {
        return (FunctionBuilder) super.setIncludeInCommandsList(includeInCommandsList);
    }

    public FunctionBuilder addAliases(@NotNull String... alias) {
        return (FunctionBuilder) super.addAliases(alias);
    }

    public FunctionBuilder addTypoAliases(@NotNull String... alias) {
        return (FunctionBuilder) super.addTypoAliases(alias);
    }
}
