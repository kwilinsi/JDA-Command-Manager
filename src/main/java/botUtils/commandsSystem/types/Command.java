package botUtils.commandsSystem.types;

import botUtils.commandsSystem.manager.CommandManager;
import botUtils.commandsSystem.types.callResponse.CallResponse;
import botUtils.commandsSystem.types.function.Argument;
import botUtils.commandsSystem.types.function.Function;
import botUtils.commandsSystem.types.function.FunctionCallData;
import botUtils.commandsSystem.types.function.Value;
import botUtils.exceptions.JsonParseException;
import botUtils.commandsSystem.json.JsonParser;
import botUtils.tools.*;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * This is the root of the Commands object tree. It contains the basic information required for a command to be
 * implemented in the bot.
 * <p>
 * Instructions for implementing a Command: 1. Create a class that extends this one
 * <p>
 * <p>
 * There are also optional key-value pairs you can include in the JSON that will control the behavior of this command.
 * The following is a list of all the currently supported base Command flags that you can include in the JSON for any
 * Command, along with their DEFAULT values. If you do not include these lines, the default values listed here will be
 * used.
 * <p>
 * "includeInCommandsList": true - If you make this false, the command will not appear in the list of commands for the
 * CommandInitializer and users will have to know
 * <p>
 * "allowNoArgs": false - By default, any command that it sent by itself will be treated the same as `%[that-command]
 * help`, which means that the help argument is unnecessary. Setting this to true will allow the base argument to run
 * code rather than being redirected to the help panel. This is useful for functions or CallResponse commands with a
 * default code execution when you don't pass in any arguments
 */
public abstract class Command {
    // Required
    private final String name;
    private final String description;
    private final String shortDescription;

    // Optional
    private final String[] aliases;
    private final String[] typoAliases;
    // TODO make allowNoArgs work

    // Additional optional configuration
    /**
     * An optional hyperlink to add in the title of the help embed for this command.
     * <p><br>
     * <b>Default Value: <u>null</u></b>
     */
    private final String link;

    /**
     * Controls whether this command should be included in the command list for the {@link CommandManager}. If true, it
     * is included; if false it is not included.
     * <p><br>
     * <b>Default Value: <u>true</u></b>
     */
    private final boolean includeInCommandsList;

    /**
     * Controls whether a command with no arguments should be treated as a request for the help embed. If false, the
     * help embed is sent whenever no arguments are given. If true, missing arguments are allowed and the help embed is
     * not sent. If you have a syntax that takes no arguments, this must be true for that to work.
     * <p><br>
     * <b>Default Value: <u>false</u></b>
     */
    private final boolean allowNoArgs;

    /**
     * The base number of seconds the command help embed sits in Discord before being deleted.
     * <p><br>More precisely, the number of seconds that the help embed lasts for is controlled through a
     * {@link TempMsgConfig} instance. Currently, the default factor is used, but the base value is controlled by this
     * constant. See the documentation for {@link TempMsgConfig#of(int, float)} for more information.
     * <p><br>
     * <b>Default Value: <u>60</u></b>
     */
    private final int helpEmbedTimeout;

    /**
     * This is the list of all the things a user can type after a command name to receive the help embed for the
     * command.
     * <p><br>
     * For example, for a prefix '{@code !}' and command name '{@code command}', if this list contains '{@code help}'
     * and the user types {@code !command help}, they will be sent the help embed for this {@link Command}. Note that
     * the specific help embed sent is controlled through {@link #getInfo()} and {@link #generateInfo()}.
     * <p><br>
     * You cannot make this array empty. Doing so in the Json will result in it simply reverting to the default. If you
     * are trying to remove all the help keys such that the help embed is never sent, assign an empty string {@code ""}
     * as the single key.
     * <p><br>
     * <b>Default Values: <u>{@code help}</u>, <u>{@code info}</u>, and <u>{@code information}</u></b>
     */
    private final String[] helpKeys;

    // Other variables
    private EmbedBuilder infoEmbed;
    protected final CommandManager manager;

    protected Command(@NotNull JsonObject json, @NotNull CommandManager manager) throws JsonParseException {
        this.manager = manager;

        // Required Command arguments (will throw an error if not in the JSON)
        this.name = JsonParser.getString(json, "name");
        this.description = JsonParser.getString(json, "description");

        // Optional Command arguments (can be omitted from the JSON)
        this.includeInCommandsList = JsonParser.getBoolean(json, "includeInCommandsList", true);
        this.allowNoArgs = JsonParser.getBoolean(json, "allowNoArgs", false);
        this.link = JsonParser.getString(json, "link", null);
        this.helpEmbedTimeout = JsonParser.getInteger(json, "helpEmbedTimeout", 60);

        String[] help = JsonParser.getStringArrayNoError(json, "helpKeys");
        this.helpKeys = help.length > 0 ? help : new String[]{"help", "info", "information"};

        if (includeInCommandsList)
            // A short description is only required if the command is included in the command list
            this.shortDescription = JsonParser.getString(json, "shortDescription");
        else
            this.shortDescription = JsonParser.getString(json, "shortDescription", "");

        // All Command aliases are optional. typoAliases don't show up in the command help embed.
        this.aliases = JsonParser.getStringArrayNoError(json, "aliases");
        this.typoAliases = JsonParser.getStringArrayNoError(json, "typoAliases");
    }

    /**
     * Get the name of the command with preserved case. Use getNameLower() for lowercase command name. This is mandatory
     * argument in the JSON and will not be null.
     *
     * @return the name of the command
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Retrieves the name of the command with the first letter capitalized and the rest of the letters lowercase.
     * @return the capitalized name of the command
     */
    public @NotNull String getNameCapital() {
        return GenericUtils.capitalizeString(name);
    }

    /**
     * Get the name of the command in all lowercase. Use getName() to preserve the case in the Json. This is mandatory
     * argument in the Json and will not be null.
     *
     * @return the name of the command
     */
    public @NotNull String getNameLower() {
        return name.toLowerCase(Locale.ROOT);
    }

    /**
     * Get the description of the command that appears in the info embed that provides additional command help. This is
     * mandatory argument in the JSON and will not be null. For the short description included in
     *
     * @return the command description
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Get the short description of this command that appears in the Commands list. If this command does not appear in
     * the commands list (check with doIncludeInCommandsList()) this might be null.
     *
     * @return the short description of the command
     */
    public @NotNull String getShortDescription() {
        return shortDescription;
    }

    /**
     * Get the link that goes in the info embed that provides additional help with this command. It is the webpage
     * target if you click on the name of the command at the top of the embed. This may be null if it wasn't set by the
     * user.
     *
     * @return the command link for the help embed
     */
    public @NotNull String getLink() {
        return link;
    }

    /**
     * Get an array with all the main aliases for this command. This does not include the typoAliases, but rather only
     * those that appear in the info embed that provides additional help with this command. This will never be null, but
     * the returned list might be empty.
     *
     * @return the command aliases in an array
     */
    public @NotNull String[] getAliases() {
        return aliases;
    }

    /**
     * Same as getAliases() except the result is in an ArrayList rather than an Array of Strings
     *
     * @return the command aliases in an array
     */
    public @NotNull List<String> getAliasesArray() {
        return Arrays.asList(aliases);
    }

    /**
     * Check whether this command is listed in the commands list. All commands are listed by default, but if the
     * optional flag was included in the JSON this command will not be listed.
     *
     * @return the command aliases in an array
     */
    public boolean doIncludeInCommandsList() {
        return includeInCommandsList;
    }

    /**
     * Returns the name of this command followed by the "help" argument and preceded by the prefix. Note that it is not
     * enclosed in code block tick marks.
     *
     * @return the command a user must type to get help for this command
     */
    public @NotNull String getHelpString() {
        return manager.getMainPrefix() + name.toLowerCase(Locale.ROOT) + " help";
    }

    /**
     * Gets the {@link CommandManager} that manages this Command instance. Useful for things like getting the default
     * prefix.
     *
     * @return the associated {@link CommandManager} instance
     */
    public @NotNull CommandManager getManager() {
        return manager;
    }

    /**
     * Returns {@link #infoEmbed}. First, a check is made to confirm that the info panel has been generated already. If
     * so, it is simply returned. But if not, it is first generated with {@link #generateInfo()} and then returned.
     * Generating the info embed only now when it is requested has two advantages.
     * <p><br>
     * First, it means that the generation of the info embed occurs outside of the constructor for this {@link Command}.
     * That means that if the embed depends on variables initialized during the constructor process (as is true for the
     * {@link Function} subclass), errors are not thrown, because the embed is not generated at that time. This is the
     * primary reason the generation was moved to this method.
     * <p>
     * Second, this allows for better memory optimization, as info embeds are not generated in advance for commands, but
     * rather they only enter memory once they have been requested for the first time by a user.
     *
     * @return the generated {@link #infoEmbed}
     */
    public @NotNull EmbedBuilder getInfo() {
        if (infoEmbed == null)
            infoEmbed = generateInfo();
        return infoEmbed;
    }

    /**
     * Checks to see whether the given user message matches this command by comparing the given name against this
     * command's name and aliases. Also checks typo aliases for matches.
     * <p><br>
     * This method assumes the given args come from a message with a matching prefix and that the prefix has been
     * removed. It also assumes that the message was split into args with the regex "{@code \s+}".
     *
     * @param args the message a user sent in Discord with the prefix removed and split by spaces
     * @return the input array sans the command name if the user's message matches this command and null otherwise
     */
    public final @Nullable String[] checkForMatch(String[] args) {
        String[] com = getNameLower().split("\\s+");

        // Check to see if it matches the name of this command
        if (Checks.stringArrayStartsWith(args, com))
            return Arrays.copyOfRange(args, com.length, args.length);

        // If not, check the aliases
        for (String alias : aliases) {
            com = alias.split("\\s+");
            if (Checks.stringArrayStartsWith(args, com))
                return Arrays.copyOfRange(args, com.length, args.length);
        }

        // If still no matches, check the typo aliases
        for (String alias : typoAliases) {
            com = alias.split("\\s+");
            if (Checks.stringArrayStartsWith(args, com))
                return Arrays.copyOfRange(args, com.length, args.length);
        }

        // And return null because there was no matches for this command whatsoever
        return null;
    }

    /**
     * Start by checking to see if the user indicated that they want to see the help panel for this command. If so send
     * it to them. Otherwise, run the command. If a valid {@link Method} was provided, execute that.
     * <p>
     * This method should deal with all exceptions and return them to the user where applicable. It should not throw any
     * exceptions (even runtime ones).
     *
     * @param data   the {@link CommandCallData} instance with all the info about the command message the user called
     * @param method the optional method to execute which allows for custom code in another class to run
     */
    public abstract void process(@NotNull CommandCallData data, Method method);

    /**
     * Subclasses of {@link Command} such as {@link Function} and {@link CallResponse} represent instances of a Json
     * file. They contain the information about a command that was imported from Json and is used to operate the bot and
     * understand commands users type.
     * <p><br>
     * When a user requests a command, all the information about what they typed is stored in a {@link CommandCallData}
     * instance. This includes the {@link Message}, the {@link MessageChannel} it's in, the {@link User} who sent it,
     * etc. Most of the time, this is sufficient information. But for a subtype like {@link Function}, more info is
     * necessary, such as each {@link Value} corresponding to an {@link Argument} given by the user in their message.
     * Thus, there is an extension of the data class, {@link FunctionCallData}.
     * <p><br>
     * This method exists for the sole purpose of returning the {@link CommandCallData} sub class that should be used
     * when dealing with a specific {@link Command} sub class. For a basic {@link Command}, this is simply a {@link
     * CommandCallData} instance. For a {@link Function}, this is a {@link FunctionCallData} instance. Etc.
     *
     * @return the class that should be used for storing the data about a user's attempt to trigger a command
     */
    public abstract Class<? extends CommandCallData> getCallDataClass();

    /**
     * Check to see if the user just wants the help embed for this command
     *
     * @param info the info help embed retrieved with getInfo() for this command
     * @param data the data regarding a specific instance of a user calling the command
     * @return true if the user just wanted the info embed and it was sent to them; false otherwise
     */
    protected boolean checkInfoRequest(
            @NotNull EmbedBuilder info, @NotNull CommandCallData data) {
        // If there are no arguments and allowNoArgs was disabled, send the help embed
        if (data.getMsgArgs().length == 0)
            if (allowNoArgs)
                return false;
            else {
                sendInfoEmbed(data.getChannel());
                return true;
            }

        // If the user specifically requested the help embed with 'help' or 'info' or 'information', give it to them
        if (GenericUtils.stringContains(helpKeys, mergeArgs(data.getMsgArgs(), 0))) {
            sendInfoEmbed(data.getChannel());
            return true;
        }

        return false;
    }

    /**
     * Sends the info embed for this {@link Command}, retrieved through {@link #getInfo()}. It is sent as a temporary
     * message controlled by a {@link TempMsgConfig} instance with the default factor and a base value set by {@link
     * #helpEmbedTimeout}.
     *
     * @param channel the channel to send it in
     */
    protected void sendInfoEmbed(@NotNull MessageChannel channel) {
        MessageUtils.sendTemp(channel, getInfo(), TempMsgConfig.of(helpEmbedTimeout, TempMsgConfig.DEFAULT_FACTOR));
    }

    /**
     * Combines the arguments with their descriptions and puts all that plus the command description and aliases in a
     * nice pretty {@link EmbedBuilder} using {@link MessageUtils}.
     *
     * @return a finished EmbedBuilder with all the info about this function
     * @throws IllegalArgumentException if there is an error assembling the {@link EmbedBuilder}, likely due to
     *                                  unresolvable URLs or exceeding character limits
     */
    protected @NotNull EmbedBuilder generateInfo() {
        EmbedBuilder e = MessageUtils.makeEmbedBuilder(
                "Command Info: " + getNameCapital(),
                "",
                manager.getConfig().getCommandInfoColor(),
                EmbedField.of("Description", getDescription()),
                EmbedField.of("Alias" + (aliases.length > 1 ? "es" : ""),
                        GenericUtils.mergeList(getAliasesArray(), "and")));

        if (link != null)
            e.setTitle(getName() + " Info", link);
        return e;
    }

    /**
     * Convenience method to call {@link CommandManager#sendError(MessageChannel, ErrorBuilder)} from the {@link
     * CommandManager} associated with this {@link Command}.
     *
     * @param error   the error to send
     * @param channel the channel to send the error in
     */
    public final void sendError(@NotNull MessageChannel channel, @NotNull ErrorBuilder error) {
        manager.sendError(channel, error);
    }

    /**
     * Convenience method to create an {@link ErrorBuilder} from the given {@link Exception} using {@link
     * ErrorBuilder#of(Exception)} and then pass it to {@link CommandManager#sendError(MessageChannel, ErrorBuilder)}.
     *
     * @param channel the channel to send the error in
     * @param e       the exception to convert to an {@link ErrorBuilder} and then send in Discord
     */
    public final void sendError(@NotNull MessageChannel channel, @NotNull Exception e) {
        manager.sendError(channel, ErrorBuilder.of(e));
    }

    /**
     * Convenience method to call {@link CommandManager#sendError(MessageChannel, String)} from the {@link
     * CommandManager} associated with this {@link Command}.
     *
     * @param error   the error to send
     * @param channel the channel to send the error in
     */
    public final void sendError(@NotNull MessageChannel channel, @NotNull String error) {
        manager.sendError(channel, error);
    }

    /**
     * Convenience method to call {@link CommandManager#sendError(MessageChannel, ErrorBuilder)} from the {@link
     * CommandManager} associated with this {@link Command}.
     *
     * @param error   the error to send
     * @param channel the channel to send the error in
     * @param fields  any fields to add to the error, such as syntax
     */
    public final void sendError(@NotNull MessageChannel channel, @NotNull String error, @Nullable EmbedField... fields) {
        manager.sendError(channel, ErrorBuilder.of(error, manager.getConfig().getErrorColor()).addField(fields));
    }

    /**
     * Convince method for using {@link #respond(MessageBuilder, MessageChannel)} with an {@link Number}.
     *
     * @param output  the message to send
     * @param channel the channel to send it in
     */
    @Deprecated
    protected final void respond(Number output, MessageChannel channel) {
        respond(String.valueOf(output), channel);
    }

    /**
     * Convince method for using {@link #respond(MessageBuilder, MessageChannel)} with a {@link String}.
     *
     * @param output  the message to send
     * @param channel the channel to send it in
     */
    @Deprecated
    protected final void respond(String output, MessageChannel channel) {
        respond(new MessageBuilder(output), channel);
    }

    /**
     * Convince method for using {@link #respond(MessageBuilder, MessageChannel)} with an {@link EmbedBuilder}.
     *
     * @param output  the message to send
     * @param channel the channel to send it in
     */
    @Deprecated
    protected final void respond(EmbedBuilder output, MessageChannel channel) {
        respond(new MessageBuilder(output), channel);
    }

    /**
     * Send a response to the user in the specified channel. If there's an error, it is printed to the console and an
     * attempt is made to notify the user of the error in Discord.
     *
     * @param output  the message to send
     * @param channel the channel to send it in
     */
    @Deprecated
    protected final void respond(MessageBuilder output, MessageChannel channel) {
        channel.sendMessage(output.build()).queue(s -> {
                },
                f -> {
                    f.printStackTrace();
                    sendError(channel, "Encountered fatal exception. Try again later. Reason: " + f.toString());
                });
    }

    /**
     * Merge all the arguments from the start index onwards into a single String separated by spaces.
     *
     * @param strArgs the array of arguments given by the user
     * @param start   the first index to merge from
     * @return the merged string, or an empty string if there was an index out of bounds error
     */
    public static @NotNull String mergeArgs(@NotNull String[] strArgs, int start) {
        if (strArgs.length < start || strArgs.length == 0)
            return "";

        StringBuilder s = new StringBuilder();
        for (int i = start; i < strArgs.length; i++)
            s.append(" ").append(strArgs[i]);

        return s.substring(1);
    }

    /**
     * This method works very similarly to {@link #mergeArgs(String[], int)}. However, that method joins the arguments
     * together with spaces as a delimiter. But when a user's message was split into arguments, <i>any</i> whitespace
     * was treated as spaces. This means that a linebreak or double space would be lost while converting to arguments
     * and back to a joined string. They would all get replaced with a single space.
     * <p><br>
     * This method solves that problem by going back to the original message. It uses the {@link CommandCallData}
     * instance to grab {@link CommandCallData#getMessageTextMod()}, and then removes all the arguments up to the
     * specified {@code index}. This is roughly equivalent to a fancy substring based on words instead of characters.
     * <p><br>
     * Note that the {@code index} parameter should correspond to {@link CommandCallData#getMsgArgs()}{@code [index]}.
     *
     * @param data  the command data, from which the message text and arguments are derived
     * @param index the index of the argument to start extracting from (0 indexed)
     * @return the contents of the original message starting at the specified argument
     */
    public static @NotNull String getOrigMessageFromArg(@NotNull CommandCallData data, int index) {
        String text = data.getMessageTextMod();

        // Remove each argument one at a time from the beginning of the original text until it's just the desired args.
        for (int i = 0; i < index; i++)
            text = text.replaceAll("^\\s*[^\\s]+\\s+", "");

        return text;
    }
}