package botUtils.commandsSystem.manager;

import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.types.callResponse.CallResponse;
import botUtils.tools.Checks;
import botUtils.tools.Colors;
import botUtils.tools.Emojis;
import botUtils.tools.GenericUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;

public class ManagerConfig implements Cloneable {

    /**
     * Clone this {@link ManagerConfig} instance (used when building the {@link CommandManager}.
     *
     * @return a cloned version of this {@link ManagerConfig} instance
     * @throws CloneNotSupportedException if there's an error doing the cloning
     */
    @Override
    protected ManagerConfig clone() throws CloneNotSupportedException {
        super.clone();
        return of()
                .setCommandsPerPage(getCommandsPerPage())
                .setCommandInfoColor(getCommandInfoColor())
                .setErrorColor(getErrorColor())
                .setCommandListColor(getCommandListColor())
                .setCommandListFooter(getCommandListFooter(), getCommandListFooterImg())
                .setAllowDirectMessages(doesAllowDirectMessages())
                .setAllowServerMessages(doesAllowServerMessages())
                .setTemporaryErrors(areErrorsTemporary())
                .setCommandInfoTemporary(isCommandInfoTemporary())
                .setPrefixCaseSensitive(isPrefixCaseSensitive())
                .setSendUnknownCommandError(doSendUnknownCommandError())
                .setCommandListPrompts(getCommandListPrompts().clone())
                .setPrefixes(getPrefixes().clone())
                .setRequirePrefixInDM(doRequirePrefixInDM())
                .setRequirePrefixInServer(doRequirePrefixInServer())
                .setJsonReplacements(GenericUtils.cloneHashMap(getJsonReplacements()))
                .setImplementJsonReplacements(doImplementJsonReplacements())
                .setArrowEmojis(getLeftArrowEmoji(), getRightArrowEmoji())
                .setAllowBotEvents(doAllowBotEvents());
    }

    /**
     * When a user requests a list of commands in this {@link CommandManager}, this number controls the maximum number
     * of commands that can be printed at once on a single page in the list.
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    // TODO on building CommandManager, check to see if any command list pages will go over the character limit and if so tell user to lower this number or decrease command shortDescription length
    private int commandsPerPage = 10;

    /**
     * This is the default {@link Color} used when sending an {@link EmbedBuilder} with information on a {@link
     * Command}.
     * <p><br>
     * <b>Default Value: <u>{@link Colors#BLUE}</u></b>
     */
    private @NotNull Color commandInfoColor = Colors.BLUE;

    /**
     * This is the default {@link Color} used when sending an {@link EmbedBuilder} with an error message.
     * <p><br>
     * <b>Default Value: <u>{@link Colors#RED}</u></b>
     */
    private @NotNull Color errorColor = Colors.RED;

    /**
     * This is the default {@link Color} used when sending an {@link EmbedBuilder} with a {@link Command} list.
     * <p><br>
     * <b>Default Value: <u>{@link Colors#BLUE}</u></b>
     */
    private @NotNull Color commandListColor = Colors.BLUE;

    /**
     * This controls the text that goes in the footer of an {@link EmbedBuilder} for a {@link Command} list. The footer
     * will always contain a page number (as this lets the bot know which page the user was on when they switch pages
     * with an emoji). This controls the text that comes after the page number. It is separated from the page number
     * with a vertical pipe: {@code |}. When null, no additional text is added to the footer.
     * <p><br>
     * <b>Default Value: <u>null</u></b>
     */
    private String commandListFooter = null;

    /**
     * This controls the image that goes in the footer of an {@link EmbedBuilder} for a {@link Command} list. When null,
     * no image will be used in the footer. If non-null, this must be a resolvable url.
     * <p><br>
     * <b>Default Value: <u>null</u></b>
     */
    private String commandListFooterImg = null;

    /**
     * This controls whether the {@link CommandManager} should respond to direct messages. If true, it will respond to
     * them just like normal server messages (unless individual commands are configured to not work in DMs). If false,
     * nothing will happen when direct messages are sent.
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    private boolean allowDirectMessages = true;

    /**
     * This controls whether the {@link CommandManager} should respond to server messages. If true, it will respond to
     * server messages just like normal (unless individual commands are configured to not work in servers). If false,
     * nothing will happen when server messages are sent.
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    private boolean allowServerMessages = true;

    /**
     * This controls whether errors sent by the {@link CommandManager} or {@link Command} instances will be deleted
     * after a short time by default. The time error messages wait before being deleted is based on the length of the
     * error message. If this is false, error messages will not be automatically deleted.
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    private boolean temporaryErrors = true;

    /**
     * This controls whether detailed {@link Command} information panels sent by the {@link CommandManager} will be
     * deleted after a short time by default. If this is false, information messages will remain indefinitely. If true,
     * they will be deleted after a while (they persist for plenty of time to be read and used).
     *
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    private boolean commandInfoTemporary = true;

    /**
     * This controls whether the {@link CommandManager} will require commands sent by the user to match the {@link
     * CommandManager} prefix case. For prefixes that do not use letters and are instead based on symbols like {@code !}
     * or {@code %} that do not have an upper/lower case, this setting has no effect. If this is true, text based
     * prefixes will have to match the exact case. If false, {@link String#toLowerCase(Locale)} will be applied to both
     * the user's message and the {@link CommandManager} prefixes.
     *
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    private boolean prefixCaseSensitive = false;

    /**
     * If a user sends a message with a prefix recognized by the {@link CommandManager} but the command they specified
     * was not recognized, this setting controls whether the bot should reply with an error message or do nothing. If
     * true, an "unknown command" error message will be sent in response. If false, the bot will act as though the
     * message was never sent.
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    private boolean sendUnknownCommandError = true;

    /**
     * The list of commands a user can type to retrieve a {@link Command} list for the {@link CommandManager}. Note that
     * you can't create {@link Command} Json instances with the same name as any of the command list prompts. So by
     * default if you make a {@link Command} called '<u>{@code commands}</u>' the {@link CommandManager} will throw an
     * error when you build it.
     *
     * <p><br>
     * <b>Default Values: <u'>{@code command}</u>' and '<u>{@code commands}</u>'</b>
     */
    private @NotNull String[] commandListPrompts = new String[]{"command", "commands"};

    /**
     * The list of prefixes a user can type before {@link Command} names to call them. If these prefixes contain
     * letters, consider modifying {@link #prefixCaseSensitive} to configure whether the prefix case must match exactly
     * when the user types it.
     * <p><br>
     * Keep in mind that there must always be at least one prefix for the {@link CommandManager}. However, if you're
     * trying to make commands without prefixes, you can use an empty string as the sole prefix for the command.
     * <p><br>
     * Also remember that the first prefix in this list is the default prefix for the {@link CommandManager}. When the
     * bot writes out commands in help panels, it will use the first prefix in this list as the example. Thus, it should
     * be the main prefix you expect people to use most of the time. This is all the prefix used with {@link
     * #prefixJsonReplacement} if that is enabled.
     * <p><br>
     * <b>Default Value: '{@code !}'</b>
     */
    private @NotNull String[] prefixes = new String[]{"!"};

    /**
     * Controls whether the {@link #prefixes} must be used before commands in a DM. If this is false, the {@link
     * CommandManager} will treat all messages sent via DM as commands even if they don't use any of the prefixes. Note
     * that this only takes any effect if {@link #allowDirectMessages} is true.
     * <p><br>
     * <b>Default Value: <u>true</u></b>
     */
    private boolean requirePrefixInDM = true;

    /**
     * Controls whether the {@link #prefixes} must be used before commands in a server. If this is false, the {@link
     * CommandManager} will treat all messages sent in servers (not DMs) as commands even if they don't use any of the
     * prefixes. Note that this only takes any effect if {@link #allowServerMessages} is true.
     * <p><br>
     * <b>Default Value: <u>true</u></b>
     */
    private boolean requirePrefixInServer = true;

    /**
     * The list of text replacements that are applied to all non-name strings found in Json. If any of the keys in this
     * map are found in the Json files for descriptions, shortDescriptions, {@link CallResponse} responses, etc, they
     * will be removed and replaced with the associated value in the map.
     * <p><br>
     * The primary use for these replacements is for constants defined in the Java code that should not be redefined in
     * the Json files in case they need to change at some point. Some commonly used replacements involve bot-specific
     * constants defined elsewhere. For example, replacing {@code $BOT_LOGO_BLUE$} with the hex code for the blue used
     * the bot. Or replacing instances of {@code $FOO$} with {@code $FOOBAR$}. It is not necessary to encase keys in $
     * symbols as in these examples, but it helps to reduce accidental string matches.
     * <p><br>
     * Note that the {@link CommandManager} prefix can be replaced separately by configuring {@link
     * #prefixJsonReplacement}.
     * <p><br>
     * <b>Default Value: <u>N/A</u></b>
     */
    private @NotNull LinkedHashMap<String, String> jsonReplacements = new LinkedHashMap<>();

    /**
     * Controls whether instances of {@code $PREFIX$} are automatically replaced with the main prefix for the {@link
     * CommandManager} while reading the Json. This basically acts as a default toggleable {@link #jsonReplacements} for
     * the prefix. If this is set to false, instances of {@code $PREFIX$} in the Json will be ignored and won't get
     * replaced with the {@link CommandManager} main prefix.
     * <p><br>
     * <b>Default Value: <u>true</u></b>
     */
    private boolean prefixJsonReplacement = true;

    /**
     * Controls whether the {@link #jsonReplacements} key-value pairs are used while reading the Json. Setting this to
     * false effectively disables any replacement configuration that was done. See {@link #jsonReplacements} for more
     * information on string replacements in Json files for the {@link CommandManager}.
     * <p><br>
     * <b>Default Value: <u>true</u></b>
     */
    private boolean implementJsonReplacements = true;

    /**
     * This is the emoji that is used to page left in the command list.
     * <p><br>
     * <b>Default Value: <u>{@link Emojis#ARROW_BACKWARD}</u></b>
     */
    private String leftArrowEmoji = Emojis.ARROW_BACKWARD;

    /**
     * This is the emoji that is used to page right in the command list.
     * <p><br>
     * <b>Default Value: <u>{@link Emojis#ARROW_FORWARD}</u></b>
     */
    private String rightArrowEmoji = Emojis.ARROW_FORWARD;

    /**
     * Controls whether the {@link CommandManager} should respond to {@link JDA} events fired because of other bots.
     * Events from this bot will always be ignored, but events from other bots are controlled via this setting.
     * <p><br>
     * <b>Default Value: <u>false</u></b>
     */
    private boolean allowBotEvents = false;

    /**
     * This is the text that goes in the title of an {@link EmbedBuilder} listening the commands for a {@link
     * CommandManager}. The typical format for this title is <b><i>[command-manager-name]</i> Command List</b>.
     * <p><br>
     * For example, you might give the {@link CommandManager} a name of "{@code Admin}". Then, when a user requests to
     * see a list of all the commands (usually by typing "{@code commands}" in Discord), they will be sent an {@link
     * EmbedBuilder} with the list. It will be titled "<b>Admin Command List</b>".
     * <p><br>
     * The title of the embed will <i>always</i> start with the name of the {@link CommandManager}, controllable only
     * during instantiation. This variable merely controls the text that comes <i>after</i> the manager name. So if you
     * don't like the "{@code Command List}" text that comes after the name of the manager, change this variable.
     * <p><br>
     * <b>Default Value: <u>{@value}</u></b>
     */
    private String commandListTitle = "Command List";

    /**
     * Similar to {@link #commandListTitle}, this also controls text that appears in the command list for the {@link
     * CommandManager}. However, this string holds the {@link EmbedBuilder}'s <i>description</i>, which is the text that
     * goes right beneath the title and above all the commands.
     * <p><br>
     * <b>Default Value:</b> {@value}
     */
    private String commandListDescription = "This is a list of commands I recognize. " +
            "For additional information on a command, type `%[command-name] help`.";

    private @NotNull ManagerConfig() {
    }

    public static @NotNull ManagerConfig of() {
        return new ManagerConfig();
    }

    /**
     * Retrieves the current state of {@link #commandsPerPage} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public int getCommandsPerPage() {
        return commandsPerPage;
    }

    /**
     * Sets the new value for {@link #commandsPerPage} <i>(click for more info on the setting)</i>.
     *
     * @param commandsPerPage the new setting value
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandsPerPage(int commandsPerPage) {
        this.commandsPerPage = commandsPerPage;
        return this;
    }

    /**
     * Retrieves the current state of {@link #commandInfoColor} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public @NotNull Color getCommandInfoColor() {
        return commandInfoColor;
    }

    /**
     * Sets the new value for {@link #commandInfoColor} <i>(click for more info on the setting)</i>.
     *
     * @param color the new setting value
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandInfoColor(@NotNull Color color) {
        this.commandInfoColor = color;
        return this;
    }

    /**
     * Retrieves the current state of {@link #errorColor} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public @NotNull Color getErrorColor() {
        return errorColor;
    }

    /**
     * Sets the new value for {@link #errorColor} <i>(click for more info on the setting)</i>.
     *
     * @param color the new setting value
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setErrorColor(@NotNull Color color) {
        this.errorColor = color;
        return this;
    }

    /**
     * Retrieves the current state of {@link #commandListColor} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public @NotNull Color getCommandListColor() {
        return commandListColor;
    }

    /**
     * Sets the new value for {@link #commandListColor} <i>(click for more info on the setting)</i>.
     *
     * @param color the new setting value
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandListColor(@NotNull Color color) {
        this.commandListColor = color;
        return this;
    }

    /**
     * Retrieves the current state of {@link #commandListFooter} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public String getCommandListFooter() {
        return commandListFooter;
    }

    /**
     * Retrieves the current state of {@link #commandListFooterImg} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public String getCommandListFooterImg() {
        return commandListFooterImg;
    }

    /**
     * Sets the new value for {@link #commandListFooter} and {@link #commandListFooterImg}
     * <i>(click for more info on the setting)</i>.
     *
     * @param text the new text setting value
     * @param url  the new url setting value
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandListFooter(String text, String url) {
        this.commandListFooter = text;
        this.commandListFooterImg = url;
        return this;
    }

    /**
     * Retrieves the current state of {@link #allowDirectMessages} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doesAllowDirectMessages() {
        return allowDirectMessages;
    }

    /**
     * Sets the new state for {@link #allowDirectMessages} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setAllowDirectMessages(boolean state) {
        this.allowDirectMessages = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #allowServerMessages} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doesAllowServerMessages() {
        return allowServerMessages;
    }

    /**
     * Sets the new state for {@link #allowServerMessages} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setAllowServerMessages(boolean state) {
        this.allowServerMessages = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #temporaryErrors} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean areErrorsTemporary() {
        return temporaryErrors;
    }

    /**
     * Sets the new state for {@link #temporaryErrors} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setTemporaryErrors(boolean state) {
        this.temporaryErrors = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #commandInfoTemporary} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean isCommandInfoTemporary() {
        return commandInfoTemporary;
    }

    /**
     * Sets the new state for {@link #commandInfoTemporary} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandInfoTemporary(boolean state) {
        this.commandInfoTemporary = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #prefixCaseSensitive} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean isPrefixCaseSensitive() {
        return prefixCaseSensitive;
    }

    /**
     * Sets the new state for {@link #prefixCaseSensitive} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setPrefixCaseSensitive(boolean state) {
        this.prefixCaseSensitive = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #sendUnknownCommandError} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doSendUnknownCommandError() {
        return sendUnknownCommandError;
    }

    /**
     * Sets the new state for {@link #sendUnknownCommandError} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setSendUnknownCommandError(boolean state) {
        this.sendUnknownCommandError = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #implementJsonReplacements} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doImplementJsonReplacements() {
        return implementJsonReplacements;
    }

    /**
     * Sets the new state for {@link #implementJsonReplacements} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setImplementJsonReplacements(boolean state) {
        this.implementJsonReplacements = state;
        return this;
    }

    /**
     * Retrieves the current list of Json string replacements in {@link #jsonReplacements}.
     * <i>(click for more info on the setting)</i>.
     *
     * @return the current setting list
     */
    public @NotNull LinkedHashMap<String, String> getJsonReplacements() {
        return jsonReplacements;
    }

    /**
     * Sets the new list of {@link #jsonReplacements} <i>(click for more info on the setting)</i>.
     *
     * @param map the new map
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setJsonReplacements(@NotNull LinkedHashMap<String, String> map) {
        this.jsonReplacements = map;
        return this;
    }

    /**
     * Adds an item to the list of {@link #jsonReplacements} <i>(click for more info on the setting)</i>.
     *
     * @param key   the key to replace in the Json
     * @param value the text to replace the key with.
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig addJsonReplacements(@NotNull String key, @NotNull String value) {
        this.jsonReplacements.put(key, value);
        return this;
    }

    /**
     * Clears the list of {@link #jsonReplacements} <i>(click for more info on the setting)</i>.
     *
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig clearJsonReplacements() {
        this.jsonReplacements.clear();
        return this;
    }

    /**
     * Retrieves the current list of {@link #prefixes} <i>(click for more info on the setting)</i>.
     *
     * @return the current prefixes list
     */
    public @NotNull String[] getPrefixes() {
        return prefixes;
    }

    /**
     * Overwrites the list of {@link #prefixes} <i>(click for more info on the setting)</i>.
     *
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setPrefixes(@NotNull String... prefix) {
        this.prefixes = prefix;
        return this;
    }

    /**
     * Adds a prefix to the list of {@link #prefixes} <i>(click for more info on the setting)</i>.
     *
     * @return this {@link CommandManager} instance for chaining
     * @throws IllegalArgumentException if the prefix being added is already in the prefixes list
     */
    public @NotNull ManagerConfig addPrefix(@NotNull String prefix) {
        if (Checks.listContainsItem(prefixes, prefix))
            throw new IllegalArgumentException(
                    "Prefix '" + prefix + "' is already in the prefixes list " + Arrays.toString(prefixes) + ".");
        this.prefixes = GenericUtils.addItemToArray(prefixes, prefix);
        return this;
    }

    /**
     * Retrieves the current state of {@link #requirePrefixInDM} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doRequirePrefixInDM() {
        return requirePrefixInDM;
    }

    /**
     * Sets the new state for {@link #requirePrefixInDM} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setRequirePrefixInDM(boolean state) {
        this.requirePrefixInDM = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #requirePrefixInServer} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doRequirePrefixInServer() {
        return requirePrefixInServer;
    }

    /**
     * Sets the new state for {@link #requirePrefixInServer} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setRequirePrefixInServer(boolean state) {
        this.requirePrefixInServer = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #prefixJsonReplacement} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doPrefixJsonReplacement() {
        return prefixJsonReplacement;
    }

    /**
     * Sets the new state for {@link #prefixJsonReplacement} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setPrefixJsonReplacement(boolean state) {
        this.prefixJsonReplacement = state;
        return this;
    }

    /**
     * Retrieves the current list of {@link #commandListPrompts} <i>(click for more info on the setting)</i>.
     *
     * @return the current prefixes list
     */
    public @NotNull String[] getCommandListPrompts() {
        return commandListPrompts;
    }

    /**
     * Overwrites the list of {@link #commandListPrompts} <i>(click for more info on the setting)</i>.
     *
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandListPrompts(@NotNull String... command) {
        this.commandListPrompts = command;
        return this;
    }

    /**
     * Adds a command to the list of {@link #commandListPrompts} <i>(click for more info on the setting)</i>.
     *
     * @return this {@link CommandManager} instance for chaining
     * @throws IllegalArgumentException if the command being added is already in the command list prompts list
     */
    public @NotNull ManagerConfig addCommandListPrompt(@NotNull String command) {
        if (Checks.listContainsItem(commandListPrompts, command))
            throw new IllegalArgumentException("Command '" + command + "' is already in the command prompts " +
                    Arrays.toString(commandListPrompts) + ".");
        this.commandListPrompts = GenericUtils.addItemToArray(commandListPrompts, command);
        return this;
    }

    /**
     * Retrieves the current {@link #leftArrowEmoji}. <i>(click for more info on the setting)</i>.
     *
     * @return the current left arrow emoji
     */
    public @NotNull String getLeftArrowEmoji() {
        return leftArrowEmoji;
    }

    /**
     * Retrieves the current {@link #rightArrowEmoji}. <i>(click for more info on the setting)</i>.
     *
     * @return the current right arrow emoji
     */
    public @NotNull String getRightArrowEmoji() {
        return rightArrowEmoji;
    }

    /**
     * Sets new values for {@link #leftArrowEmoji} and {@link #rightArrowEmoji} to change the emojis the bot uses to
     * change pages in a command list.
     *
     * @param left  the new left arrow emoji
     * @param right the new right arrow emoji
     * @return this {@link CommandManager} instance for chaining
     */
    public ManagerConfig setArrowEmojis(@NotNull String left, @NotNull String right) {
        this.leftArrowEmoji = left;
        this.rightArrowEmoji = right;
        return this;
    }

    /**
     * Retrieves the current state of {@link #allowBotEvents} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public boolean doAllowBotEvents() {
        return allowBotEvents;
    }

    /**
     * Sets the new state for {@link #allowBotEvents} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setAllowBotEvents(boolean state) {
        this.allowBotEvents = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #commandListTitle} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public @NotNull String getCommandListTitle() {
        return commandListTitle;
    }

    /**
     * Sets the new state for {@link #commandListTitle} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandListTitle(@NotNull String state) {
        this.commandListTitle = state;
        return this;
    }

    /**
     * Retrieves the current state of {@link #commandListDescription} <i>(click for more info on the setting)</i>.
     *
     * @return the current setting state
     */
    public @NotNull String getCommandListDescription() {
        return commandListDescription;
    }

    /**
     * Sets the new state for {@link #commandListDescription} <i>(click for more info on the setting)</i>.
     *
     * @param state the new state
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull ManagerConfig setCommandListDescription(@NotNull String state) {
        this.commandListDescription = state;
        return this;
    }
}