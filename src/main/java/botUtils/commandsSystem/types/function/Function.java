package botUtils.commandsSystem.types.function;

import botUtils.commandsSystem.json.JsonParser;
import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.types.CommandCallData;
import botUtils.exceptions.FuncException;
import botUtils.exceptions.InvalidMethodException;
import botUtils.exceptions.JsonParseException;
import botUtils.exceptions.SyntaxException;
import botUtils.tools.*;
import botUtils.commandsSystem.manager.CommandManager;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This class takes the Command object a step further by adding support for custom arguments
 */
public class Function extends Command {
    private final Argument[] arguments;
    private final Syntax[] syntaxes;

    public Function(@NotNull JsonObject json, @NotNull CommandManager manager) throws JsonParseException {
        super(json, manager);
        this.arguments = Argument.ofArray(JsonParser.getJsonObjectArray(json, "arguments"));
        syntaxes = Syntax.ofArray(JsonParser.getJsonArrayArray(json, "syntax"), this);
    }

    /**
     * This represents the class that should be used for storing information about a specific triggering of a {@link
     * Function} by a user in Discord. See {@link Command#getCallDataClass()} for more detailed documentation about what
     * this means.
     *
     * @return the class for storing information about a {@link Function} call and execution
     */
    @Override
    public Class<? extends CommandCallData> getCallDataClass() {
        return FunctionCallData.class;
    }

    /**
     * Parses a command from a user, determines the answer, and sends it to them. Starts by validating all their
     * arguments to be in the required range and the right number of args.
     *
     * @param commandData the {@link CommandCallData} instance with all the information on the command the user called
     * @param method      the method to call when running this function
     */
    public void process(@NotNull CommandCallData commandData, Method method) {
        FunctionCallData data = (FunctionCallData) commandData;

        try {
            // Parse the arguments from the command. If something is sent to the user (help embed or error message)
            // true is returned and processing terminates.
            if (parseArgsForErrors(data))
                return;

            // Confirm that the method associated with this function takes the right parameters and run it
            Checks.checkMethodParameterTypes(method, FunctionCallData.class);
            method.invoke(this, data);

        } catch (SyntaxException e) {
            // Catches SyntaxExceptions and ArgumentExceptions, sending the user the proper syntax in the error
            sendSyntaxError(e.getMessage(), data.getChannel(), e.getSyntax());

        } catch (InvalidMethodException e) {
            sendError(data.getChannel(), e.getMessage());

        } catch (InvocationTargetException e) {
            // An InvocationTargetException encases another exception that was thrown while invoking a method.
            // Figure out what kind of exception that was.
            Exception error = (Exception) e.getCause();

            if (error instanceof FuncException)
                sendError(data.getChannel(), ((FuncException) error).getError());
            else
                sendError(data.getChannel(), error);

        } catch (Exception e) {
            // Unknown/unanticipated exceptions are also printed to the console
            sendError(data.getChannel(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Attempts to parse the arguments provided by the user based on the syntax of this command. If that fails, an error
     * message is sent to the user (or simply the help panel if that's what they wanted) and the method returns true to
     * indicate that the arguments were not parsed and the command processing should terminate. If the arguments are
     * parsed successfully, false is returned to indicate that there were no errors, and the {@link FunctionCallData}
     * instance will contain all the parsed {@link Value} instances.
     *
     * @param data the {@link FunctionCallData} containing the needed info about the command processing
     * @return true if there were errors or something was sent in Discord and the processing should exit; false if no
     * errors were encountered and processing should continue
     */
    protected boolean parseArgsForErrors(@NotNull FunctionCallData data) throws Exception {
        // Check if the user merely wants the info/help panel for this command
        if (checkInfoRequest(getInfo(), data))
            return true;

        // Determine the data type of each of the arguments the user provided
        ArgType[] inputTypes = Argument.getTypes(data.getMsgArgs());

        Exception error = null;

        for (Syntax syntax : syntaxes) {

            // For each syntax, check if it matches. If it does, exit the loop. If not, try again.
            try {
                if (checkSyntax(syntax, inputTypes, data))
                    // If the syntax checked with no errors, exit this method by returning false to indicate the
                    // lack of errors during parsing.
                    return false;

            } catch (Exception e) {
                // If there's an error during validation, cache it for later (unless an error is already cached)
                error = error == null ? e : error;
            }
        }

        // If the syntax checking loop ends without finding a matching syntax, throw an exception.

        if (error != null)
            // Throw the cached error if a syntax matched argument types but failed validation
            throw error;
        else
            // Otherwise none of the syntaxes matched at all, even their data types. Send an error for that.
            throw new SyntaxException(
                    "Syntax error. The given argument types do not match " + (
                            syntaxes.length == 1 ? "the command syntax." : (syntaxes.length == 2 ? "either" : "any")
                                    + " of the syntaxes for this command."));
    }

    /**
     * Check to see if the given syntax matches the command a user typed, stored in the {@link FunctionCallData}. This
     * method is called exclusively by {@link #parseArgsForErrors(FunctionCallData)} as it iterates over each of the
     * syntaxes associated with this {@link Function}. First it checks to see if the data types of the arguments sent by
     * the user match the data types of the syntax. If they do, it validates each of those arguments sent by the user
     * against the constraints of the {@link Argument} instances attached to this {@link Function}.
     * <p><br>
     * If the syntax is found to match the necessary data types and validates correctly, a {@link Value} instance is
     * created for each of the arguments the user sent in Discord, and each of these values are added to the {@link
     * FunctionCallData} instance. Then true is returned to indicate that the syntax was checked successfully.
     * <p><br>
     * If the syntax's data types are found to <i>not</i> match the data types of the arguments in the user's message,
     * false is returned to indicate that the next syntax should be tested.
     * <p><br>
     * If the data types <i>is</i> found to match the user's input but the input fails the validation process for those
     * arguments, an exception is thrown and the method terminates. This exception is cached and possibly sent to the
     * user in Discord later after checking to make sure none of the other syntaxes work.
     *
     * @param syntax     the syntax to check
     * @param inputTypes the data types of each of the arguments the user sent in Discord
     * @param data       all the data associated with the command the user sent
     * @return true if the syntax data types match; false if they do not match
     */
    private boolean checkSyntax(@NotNull Syntax syntax, @NotNull ArgType[] inputTypes, @NotNull FunctionCallData data) {
        // Check to see if it matches the arguments the user entered. If not, try the next syntax
        String[] syntaxArgs = syntax.matches(inputTypes);
        String[] msgArgs = data.getMsgArgs();
        if (syntaxArgs == null)
            return false;

        // Syntax matches by data types. Now validate each of the user's arguments.

        List<Value> values = new ArrayList<>(syntaxArgs.length);

        for (int i = 0; i < syntaxArgs.length; i++)
            // Add a value to the list while validating it - validation errors are thrown as runtime exceptions
            // and caught outside this method
            values.add(Value
                    .of(
                            getArgument(syntaxArgs[i]),
                            // If this is the last syntax arg but there's more real args in Discord, merge those real
                            // args together and treat them like one
                            i == syntaxArgs.length - 1 && i < msgArgs.length - 1 ? getOrigMessageFromArg(data, i) : msgArgs[i])
                    .validate());

        // At this point all of the arguments have been successfully parsed and validated. Transfer them to the
        // FunctionCallData instance and return true to indicate that the syntax matches.
        data.setValues(values).setMatchingSyntax(syntax);
        return true;
    }

    /**
     * Get an {@link Argument} object from its name (case insensitive)
     *
     * @param key the name of the {@link Argument}
     * @return the matching {@link Argument}, or null if none could be found
     */
    protected @Nullable Argument getArgument(@NotNull String key) {
        for (Argument arg : arguments)
            if (arg.getName().equalsIgnoreCase(key))
                return arg;
        return null;
    }

    /**
     * Combines the arguments with their descriptions and puts all that plus the command description and aliases in a
     * nice pretty {@link EmbedBuilder} using {@link MessageUtils}.
     *
     * @return a finished EmbedBuilder with all the info about this function
     * @throws IllegalArgumentException if there is an error assembling the {@link EmbedBuilder}, likely due to
     *                                  unresolvable URLs or exceeding character limits
     */
    @Override
    protected @NotNull EmbedBuilder generateInfo() {
        StringBuilder s = new StringBuilder();
        for (Argument arg : getArguments())
            s.append("\n").append(arg.getDescriptionFormat());
        String syntaxDesc = s.substring(1);

        return MessageUtils.makeEmbedBuilder(
                "Command Info: " + getNameCapital(),
                getLink(),
                "",
                manager.getConfig().getCommandInfoColor(),
                null, null, null, null,
                null, null, null, null,
                new EmbedField[]{
                        EmbedField.of("Description", getDescription()),
                        EmbedField.of("Syntax" + (syntaxes.length > 1 ? "es" : ""), getSyntaxes()),
                        EmbedField.of("Arguments", syntaxDesc),
                        EmbedField.of("Alias" + (getAliases().length > 1 ? "es" : ""),
                                GenericUtils.mergeList(getAliasesArray(), "and"))
                });
    }

    /**
     * Send an error message to the user along with the proper syntax for the command they tried to use. If a Syntax
     * object is provided, only that Syntax is listed in the embed. Otherwise, if null is passed for the Syntax then all
     * the Syntaxes associated with the function are displayed.
     *
     * @param error   the error message to display at the top of the embed
     * @param channel the channel to send the error in
     * @param syntax  the syntax to display under the error (or null to list all syntaxes)
     */
    protected void sendSyntaxError(String error, MessageChannel channel, Syntax syntax) {
        // Set the Syntax part to either all the syntaxes (if syntax is null) or only the one provided
        EmbedField syntaxField = EmbedField.of(
                "Syntax" + (syntaxes.length > 1 && syntax == null ? "es" : ""),
                syntax == null ? getSyntaxes() : "```\n" + syntax.toString() + "```");

        sendError(channel, error, syntaxField);
    }

    /**
     * Gets a list of all the valid Syntaxes for this command each in their own code block appended to one another. If
     * there's only one syntax for the command, this will simply look like the syntax enclosed in a code block.
     *
     * @return the list of syntaxes
     */
    protected String getSyntaxes() {
        StringBuilder s = new StringBuilder();
        for (Syntax syntax : this.syntaxes)
            s.append("```\n").append(syntax.toString()).append("```");

        return s.toString();
    }

    /**
     * Returns a list of arguments associated with this {@link Function}. Note that while this method is annotated not
     * null, There is a possibility that it will return null if it is called before the constructor finishes. But if
     * you're using this well after creating the {@link Function} instance, it'll be fine. Just don't depend on this
     * method while initializing the {@link Function}.
     *
     * @return the list of associated {@link Argument} instances, possibly empty
     */
    protected @NotNull Argument[] getArguments() {
        return arguments;
    }
}
