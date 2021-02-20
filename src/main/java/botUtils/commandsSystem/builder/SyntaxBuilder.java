package botUtils.commandsSystem.builder;

import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.commandsSystem.json.JsonParser;
import botUtils.commandsSystem.builder.argument.ArgumentBuilder;
import botUtils.tools.GenericUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class provides the framework for syntaxes used in {@link FunctionBuilder} objects. It describes a list of
 * arguments that make up a syntax for a command. The names of arguments used in this class should correspond
 * with the names of {@link ArgumentBuilder} arguments that are also sent to the same {@link FunctionBuilder}.
 */
public class SyntaxBuilder implements Builder {
    private Object[] syntax;

    private SyntaxBuilder(@NotNull String[] arguments) {
        this.syntax = arguments;
    }

    private SyntaxBuilder() {
        syntax = new Object[0];
    }

    /**
     * Creates a new {@link SyntaxBuilder} without adding any arguments immediately
     */
    public static SyntaxBuilder of() {
        return new SyntaxBuilder();
    }

    /**
     * Creates a new {@link SyntaxBuilder} an immediately adds one or more argument names to the SyntaxBuilder.
     * These should all be the exact names of arguments added to the command via ArgumentBuilders. They do not
     * necessarily need to be unique. Note that this is the same as calling
     * <p>
     * {@link #SyntaxBuilder}.{@link SyntaxBuilder#of()}.{@link #addArgument(String...)}
     *
     * @param argumentName the name of the argument(s) to add
     */
    public static SyntaxBuilder of(@NotNull String... argumentName) {
        return new SyntaxBuilder(argumentName);
    }

    /**
     * Adds one or more argument names to the SyntaxBuilder. These should all be the exact names of arguments added
     * to the command via ArgumentBuilders. They do not necessarily need to be unique.
     *
     * @param argumentName the name of the argument(s) to add
     * @return this {@link SyntaxBuilder} instance for chaining
     */
    public SyntaxBuilder addArgument(@NotNull String... argumentName) {
        this.syntax = GenericUtils.mergeArrays(syntax, argumentName);
        return this;
    }

    /**
     * This creates a single argument group, which is a collection of one or more argument names that can be repeated
     * a variable number of times in a syntax. For example, creating an argument group with the names "member" and
     * "nickname" with a maxRepetitions of 30 would mean that when a user called a function with this syntax they
     * could give up to thirty members and associated nicknames, perhaps allowing them to change multiple people's
     * nicknames at once. This will appear as a JsonObject in the syntax array in the json file.
     *
     * @param argumentNames  the name of the argument(s) to add
     * @param maxRepetitions the maximum number of times the arguments can be repeated (minimum is always 1)
     * @return this {@link SyntaxBuilder} instance for chaining
     * @throws ClassNotFoundException if there's an error building Json
     */
    public SyntaxBuilder addArgument(String[] argumentNames, int maxRepetitions) throws ClassNotFoundException {
        JsonObject json = JsonBuilder.buildJsonObject(JsonMap.of()
                .add("args", JsonBuilder.buildJsonArray(argumentNames))
                .add("maxRepetitions", maxRepetitions));
        this.syntax = GenericUtils.mergeArrays(syntax, new JsonObject[]{json});
        return this;
    }

    /**
     * Clears all the arguments that have been added to the syntax array and sets it to a new empty array of type
     * {@link Object}. Not sure why you'd ever want to use this.
     *
     * @return this {@link SyntaxBuilder} instance for chaining
     */
    public SyntaxBuilder clearArguments() {
        this.syntax = new Object[0];
        return this;
    }

    public String[] getUniqueArgumentNames() {
        ArrayList<String> list = new ArrayList<>();

        for (Object o : syntax)
            // Each object will either be a String or a JsonObject containing a JSONArray of Strings
            if (o.getClass() == String.class)
                list.add((String) o);
            else
                try {
                    Collections.addAll(list, JsonParser.getStringArray((JsonObject) o, "args"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

        return list.toArray(new String[0]);
    }

    /**
     * Attempts to convert this {@link SyntaxBuilder} to a {@link JsonObject}.
     *
     * @return the completed Json
     * @throws ClassNotFoundException if there was an error building the Json
     */
    public @NotNull JsonArray getJson() throws ClassNotFoundException {
        return JsonBuilder.buildJsonArray(syntax);
    }
}