package botUtils.commandsSystem.manager;

import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.types.CommandCallData;
import botUtils.tools.Checks;
import botUtils.tools.JDAUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

/**
 * This class is simply an extension of a {@link CommandManager}. It is accepts JDA {@link Event} instances and
 * processes them in accordance with the settings of a {@link CommandManager} and the {@link Command} instances attached
 * to it. All the methods here are static, meaning this class is not attached to specific {@link CommandManager}
 * instances.
 */
public class CommandRunner {

    /**
     * This method processes some event passed to a {@link CommandManager} instance.
     * <p><br>
     * <b>Supported Events:</b>
     * <p>{@link GuildMessageReceivedEvent} - redirected to {@link #runGuildMessage(CommandManager,
     * GuildMessageReceivedEvent)}
     * <p>{@link PrivateMessageReceivedEvent} - redirected to {@link #runPrivateMessage(CommandManager,
     * PrivateMessageReceivedEvent)}
     * <p>{@link GuildMessageReactionAddEvent} - redirected to {@link #runGuildReaction(CommandManager,
     * GuildMessageReactionAddEvent)}
     * <p>{@link MessageReactionAddEvent} - redirected to {@link #runPrivateReaction(CommandManager,
     * MessageReactionAddEvent)}
     * <p>All other {@link Event} types are ignored at this time.
     *
     * @param manager the {@link CommandManager} receiving the event
     * @param event   the event received
     * @param <T>     event type parameter
     * @return true if something is done as a result of the event; false otherwise
     * @throws IllegalStateException if the CommandManager has not yet been built with {@link CommandManager#build()}.
     */
    public static <T extends Event> boolean run(@NotNull CommandManager manager, @NotNull T event) {
        Checks.commandManagerBuildState(manager, true);

        if (event instanceof GuildMessageReceivedEvent)
            return runGuildMessage(manager, (GuildMessageReceivedEvent) event);
        else if (event instanceof PrivateMessageReceivedEvent)
            return runPrivateMessage(manager, (PrivateMessageReceivedEvent) event);
        else if (event instanceof GuildMessageReactionAddEvent)
            return runGuildReaction(manager, (GuildMessageReactionAddEvent) event);
        else if (event instanceof MessageReactionAddEvent)
            return runPrivateReaction(manager, (MessageReactionAddEvent) event);

        // Otherwise the event was not recognized; return false
        return false;
    }

    /**
     * Processes {@link GuildMessageReceivedEvent} events from Discord that were passed to a {@link CommandManager}.
     * <p><br>
     * <u>Precondition:</u> this method assumes that the {@link CommandManager} was built with {@link
     * CommandManager#build()}.
     * <p><br>
     * This method redirects to {@link #runMessage(CommandManager, Message, MessageChannel, Member, User)}, which is a
     * generic run method for messages sent in servers and DMs.
     *
     * @param manager the manager handling the event
     * @param event   the event
     * @return true if something is done as a result of the event; false otherwise
     */
    private static boolean runGuildMessage(
            @NotNull CommandManager manager, @NotNull GuildMessageReceivedEvent event) {
        if (manager.getConfig().doesAllowServerMessages())
            return runMessage(manager, event.getMessage(), event.getChannel(), event.getMember(), event.getAuthor());
        return false;
    }

    /**
     * Processes {@link PrivateMessageReceivedEvent} events from Discord that were passed to a {@link CommandManager}.
     * <p><br>
     * <u>Precondition:</u> this method assumes that the {@link CommandManager} was built with {@link
     * CommandManager#build()}.
     * <p><br>
     * This method redirects to {@link #runMessage(CommandManager, Message, MessageChannel, Member, User)}, which is a
     * generic run method for messages sent in servers and DMs.
     *
     * @param manager the manager handling the event
     * @param event   the event
     * @return true if something is done as a result of the event; false otherwise
     */
    private static boolean runPrivateMessage(
            @NotNull CommandManager manager, @NotNull PrivateMessageReceivedEvent event) {
        if (manager.getConfig().doesAllowDirectMessages())
            return runMessage(manager, event.getMessage(), event.getChannel(), null, event.getAuthor());
        return false;
    }

    /**
     * Processes {@link GuildMessageReactionAddEvent} events from Discord that were passed to a {@link CommandManager}.
     * <p><br>
     * <u>Precondition:</u> this method assumes that the {@link CommandManager} was built with {@link
     * CommandManager#build()}.
     * <p><br>
     * This method redirects to {@link #runReaction(CommandManager, User, MessageReaction, boolean)}, which is a generic
     * run method for reactions in servers and DMs.
     *
     * @param manager the manager handling the event
     * @param event   the event
     * @return true if something is done as a result of the event; false otherwise
     */
    private static boolean runGuildReaction(
            @NotNull CommandManager manager, @NotNull GuildMessageReactionAddEvent event) {
        return runReaction(manager, event.getUser(), event.getReaction(), false);
    }

    /**
     * Processes {@link MessageReactionAddEvent} events from Discord that were passed to a {@link CommandManager}.
     * <p><br>
     * <u>Precondition:</u> this method assumes that the {@link CommandManager} was built with {@link
     * CommandManager#build()}.
     * <p><br>
     * This method redirects to {@link #runReaction(CommandManager, User, MessageReaction, boolean)}, which is a generic
     * run method for reactions in servers and DMs.
     *
     * @param manager the manager handling the event
     * @param event   the event
     * @return true if something is done as a result of the event; false otherwise
     */
    private static boolean runPrivateReaction(
            @NotNull CommandManager manager, @NotNull MessageReactionAddEvent event) {
        return runReaction(manager, Objects.requireNonNull(event.getUser()), event.getReaction(), true);
    }

    /**
     * Evaluates the {@link Message} a user sent in a Discord {@link MessageChannel}. Checks to see if it is recognized
     * by the {@link CommandManager} and, if so, sends an appropriate output to the user. If some output is sent
     * (including errors) then true will be returned. Otherwise, if the prefix isn't recognized or the name doesn't
     * match any commands, false is returned.
     * <p><br>
     * <u>Preconditions:</u> it is assumed that the {@link CommandManager} allows messages from the given channel. For
     * example, if it is a private channel, then {@link ManagerConfig#doesAllowDirectMessages()} is presumed true.
     * <p>
     * It is also assumed that the {@link CommandManager} was built with {@link CommandManager#build()}.
     *
     * @param manager the {@link CommandManager} that is evaluating the message
     * @param message the message with the command the user entered
     * @param channel the channel that the user sent the message in and where output should go
     * @param member  the member who sent the message (null if it was sent in a DM)
     * @param user    the user who sent the message
     * @return true if it was a valid command and something happened; false otherwise
     */
    public static boolean runMessage(@NotNull CommandManager manager, @NotNull Message message,
                                     @NotNull MessageChannel channel, Member member, @NotNull User user) {
        // Ignore messages from this bot and other bots if those are disabled too
        if (Checks.eventAuthorIsIgnored(message.getAuthor(), manager.getJda().getSelfUser(),
                manager.getConfig().doAllowBotEvents()))
            return false;

        // Ensure the message starts with the prefix. If not, do nothing and return false.
        String messageStr = removePrefix(manager.getConfig(), message.getContentRaw(), channel.getType());
        if (messageStr == null)
            return false;

        String[] args = messageStr.split("\\s+");

        // Check to see if the command list was requested.
        for (String prompt : manager.getConfig().getCommandListPrompts())
            if (Checks.stringArrayStartsWith(args, prompt.split("\\s+"))) {
                manager.sendCommandList(channel);
                return true;
            }

        // Check to see if the user requested one of the commands associated with the command manager.
        for (Command command : manager.getCommands()) {
            String[] match = command.checkForMatch(args);

            if (match != null)
                try {
                    CommandCallData data = command.getCallDataClass()
                            .getConstructor(CommandManager.class, Command.class)
                            .newInstance(manager, command)
                            .setMessage(message)
                            .setMessageArgs(match)
                            .setChannel(channel)
                            .setMember(member)
                            .setUser(user);
                    // Instantiate both the Command and its associated CommandCallData instance

                    execute(command, data);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        // If this point is reached the user used a proper prefix but the command wasn't recognized.
        // Throw an error unless unknown command errors were disabled.
        if (manager.getConfig().doSendUnknownCommandError()) {
            manager.sendError(channel, "Unknown command.");
            return true;
        }

        return false;
    }

    /**
     * Evaluates a reaction a user added to a {@link Message} in a Discord {@link MessageChannel}. Currently, this just
     * means checking to see if the user was reacting with an arrow emoji to request changing pages in a command list.
     * In the future this may include commands that are executed when reactions are added.
     *
     * <p><br>
     * <u>Precondition:</u> this method assumes that the {@link CommandManager} was built with {@link
     * CommandManager#build()}.
     *
     * @param manager          the manager running this event
     * @param user             the user who triggered the reaction event
     * @param reaction         the reaction
     * @param isPrivateMessage true if the reaction occurred in a DM, in which case it can't be deleted
     * @return true if the reaction is recognized and something happened; false otherwise
     */
    public static boolean runReaction(@NotNull CommandManager manager, @NotNull User user,
                                      @NotNull MessageReaction reaction, boolean isPrivateMessage) {
        // Ignore reactions from this bot and other bots if those are disabled too
        if (Checks.eventAuthorIsIgnored(user, manager.getJda().getSelfUser(), manager.getConfig().doAllowBotEvents()))
            return false;

        String code = reaction.getReactionEmote().getAsReactionCode();
        boolean nextPage = code.equalsIgnoreCase(manager.getConfig().getRightArrowEmoji());

        // If the emoji reaction wasn't recognized give up and do nothing
        if (!code.equalsIgnoreCase(manager.getConfig().getLeftArrowEmoji()) && !nextPage)
            return false;

        // If the command list message was cached update it with the next page and finish
        if (manager.getCommandListMessageCache().containsKey(reaction.getMessageIdLong())) {
            updateCommandList(manager, manager.getCommandListMessageCache().get(reaction.getMessageIdLong()),
                    user, reaction, !isPrivateMessage, nextPage);
            return true;
        }

        // Since the fast detection with the cache didn't work, try the slow method. Check to see if the message
        // was sent by the bot and is a command list for this manager
        Message message = reaction.getChannel().retrieveMessageById(reaction.getMessageIdLong()).complete();
        try {
            assert message.getAuthor().getIdLong() == manager.getJda().getSelfUser().getIdLong();
            assert Objects.equals(message.getEmbeds().get(0).getTitle(), manager.getName() + " Command List");
            updateCommandList(manager, message, user, reaction, !isPrivateMessage, nextPage);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * Replaces a message containing a command list with the next page of commands and stores it in the message cache
     * for the {@link CommandManager}.
     * <p><br>
     * <u>Precondition:</u> this method assumes that the {@link Message} contains an {@link EmbedBuilder} that
     * contains a {@link MessageEmbed.Footer} that contains text in the format "{@code Page x of y}", or any other
     * footer syntax where the second argument (when split by spaces) is the current page number.
     *
     * @param manager  the operating manager
     * @param message  the old command list message
     * @param reaction the reaction a user added to this message
     * @param doRemove true if the reaction should be removed after editing the message
     * @param nextPage true to go to the next page; false to go to the previous page
     */
    private static void updateCommandList(@NotNull CommandManager manager, @NotNull Message message, @NotNull User user,
                                          @NotNull MessageReaction reaction, boolean doRemove, boolean nextPage) {
        int page;
        try {
            String footer = Objects.requireNonNull(message.getEmbeds().get(0).getFooter()).getText();
            assert footer != null;
            page = Integer.parseInt(footer.split("\\s+")[1]) +
                    (nextPage ? 1 : -1);
        } catch (Exception ignore) {
            return;
        }

        message.editMessage(manager.getCommandListPage(page).build()).queue(
                m -> manager.getCommandListMessageCache().put(m.getIdLong(), m));
        if (doRemove)
            JDAUtils.removeReaction(reaction, user, false);
    }

    public static void execute(@NotNull Command command, @NotNull CommandCallData commandData) {
        try {
            command.process(
                    commandData,
                    // This is the method written by bot developer that should be executed (or null if there's
                    // no code, such as for a CallResponse command)
                    commandData.getManager().getCommandCodeMethod(command)
            );
        } catch (Exception e) {
            // If processing the command threw any errors, show it to the end user
            command.sendError(commandData.getChannel(), e.getMessage());
        }
    }

    /**
     * Checks to see if the given message starts with one of the prefixes recognized by the {@link CommandManager} (as
     * defined by its {@link ManagerConfig} instance). If it does, the message sans the prefix is returned. Otherwise,
     * null is returned to indicate that there was no match.
     * <p><br>
     * Note that there are settings for managers that make prefixes unnecessary in private channels or servers. Make
     * sure to specify a {@link ChannelType} corresponding to the type of channel the message was sent in, because if
     * prefixes aren't required in that channel this method will never return null. Note that it will still look for
     * prefixes to see if any need to be removed before returning the string, but if none are found and none were
     * required the original message string will be returned unmodified.
     *
     * @param config  the configuration instance with the prefixes to look for
     * @param message the message to check for prefixes
     * @param type    the type of channel the message was sent in
     * @return the message without the prefix if it started with one; otherwise null
     */
    private static @Nullable String removePrefix(
            @NotNull ManagerConfig config, @NotNull String message, @NotNull ChannelType type) {
        String comp = config.isPrefixCaseSensitive() ? message : message.toLowerCase(Locale.ROOT);

        for (String prefix : config.getPrefixes())
            // If the message starts with the prefix, return true. Otherwise check the next prefix.
            if (comp.startsWith(config.isPrefixCaseSensitive() ? prefix : prefix.toLowerCase(Locale.ROOT)))
                return comp.substring(prefix.length());

        // If a prefix wasn't required to begin with, return the original message
        if (Checks.checkChannelType(type, !config.doRequirePrefixInDM(), !config.doRequirePrefixInServer()))
            return message;

        // Otherwise return null to indicate that a matching prefix was not found in the message string
        return null;
    }
}
