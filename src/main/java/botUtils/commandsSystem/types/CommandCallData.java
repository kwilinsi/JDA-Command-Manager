package botUtils.commandsSystem.types;

import botUtils.commandsSystem.manager.CommandManager;
import botUtils.tools.Checks;
import botUtils.tools.MessageUtils;
import botUtils.tools.TempMsgConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents all the basic information from a command a user typed that is passed to a function. It contains
 * things like the member who sent it, where it was sent, and the contents of the message.
 * <p><br>
 * Either a {@link CommandCallData} or subclass instance is passed as the sole parameter to a function. When that
 * function is called, this instance can be used to retrieve basic information about the user's command request.
 */
public class CommandCallData {
    /**
     * The {@link CommandManager} that operates the {@link Command} using this data
     */
    private final @NotNull CommandManager manager;

    /**
     * The specific {@link Command} that the user triggered.
     */
    private final @NotNull Command command;

    /**
     * The actual {@link Message} a user typed in Discord that triggered a command.
     */
    private Message message;

    /**
     * The channel the {@link #message} was sent in that triggered a command.
     */
    private MessageChannel channel;

    /**
     * The {@link User} who triggered a command by sending a {@link #message}.
     */
    private User user;

    /**
     * The {@link Member} who triggered a command by sending a {@link #message}. If the command was sent in a private
     * DM, this will be null, as a {@link Member} represents a {@link User} as associated with a specific guild.
     */
    private @Nullable Member member;

    /**
     * The contents of the {@link #message} that triggered a command.
     */
    private String messageText;

    /**
     * This is the modified {@link #messageText} where the prefix and the name of the command being triggered are
     * removed. This occurs while setting the {@link #messageArgs} with {@link #setMessageArgs(String[])}.
     */
    private String messageTextMod;

    /**
     * The {@link #messageText} separated into arguments by whitespace.
     */
    private String[] messageArgs;

    /**
     * Creates a new {@link CommandCallData} instance based on the {@link CommandManager} and {@link Command} associated
     * with it. It is assumed that immediately after creating this command you will set the {@link #message}, {@link
     * #channel}, {@link #member}, {@link #user}, {@link #messageText}, and {@link #messageArgs} using their setter
     * methods.
     *
     * @param manager the {@link CommandManager} operating this command
     * @param command the {@link Command} triggered by the user
     */
    public CommandCallData(@NotNull CommandManager manager, @NotNull Command command) {
        this.manager = manager;
        this.command = command;
    }

    /**
     * Sets the {@link Message} sent by a user in Discord that triggered the creation of this call data instance. This
     * is used to set both the {@link #message} and the {@link #messageText}.
     *
     * @param message the {@link #message}
     * @return this {@link CommandCallData} instance for chaining
     */
    public @NotNull CommandCallData setMessage(@NotNull Message message) {
        this.message = message;
        this.messageText = message.getContentRaw();
        return this;
    }

    /**
     * Sets the {@link Member} who triggered the command.
     *
     * @param member the {@link #member}
     * @return this {@link CommandCallData} instance for chaining
     */
    public @NotNull CommandCallData setMember(Member member) {
        this.member = member;
        return this;
    }

    /**
     * Sets the {@link User} who triggered the command.
     *
     * @param user the {@link #user}
     * @return this {@link CommandCallData} instance for chaining
     */
    public @NotNull CommandCallData setUser(@NotNull User user) {
        this.user = user;
        return this;
    }

    /**
     * Sets the arguments in the command sent by the user. This is just the content of the message split by spaces with
     * the name of the command removed. At the same time {@link #messageTextMod} is also created based on the contents
     * of {@link #messageText}.
     * <p><br>
     * <u>Precondition:</u> {@link #setMessage(Message)} must have been called <i>prior</i> to this method due to
     * the text of the message being a prerequisite for generating the {@link #messageTextMod}.
     *
     * @param messageArgs the {@link #messageArgs}
     * @return this {@link CommandCallData} instance for chaining
     */
    public @NotNull CommandCallData setMessageArgs(@NotNull String[] messageArgs) {
        this.messageArgs = messageArgs;
        this.messageTextMod = messageText;

        // Keep removing the stuff at the start of the message text until all that's left is the messageArgs
        while (!Checks.stringArrayStartsWith(messageTextMod.split("\\s+"), messageArgs) || messageTextMod.length() == 0)
            messageTextMod = messageTextMod.replaceAll("^\\s*[^\\s]+\\s+", "");

        return this;
    }

    /**
     * Sets the {@link MessageChannel} where the command was triggered, and where all responses should be directed.
     *
     * @param channel the {@link #channel}
     * @return this {@link CommandCallData} instance for chaining
     */
    public @NotNull CommandCallData setChannel(@NotNull MessageChannel channel) {
        this.channel = channel;
        return this;
    }

    /**
     * Retrieves The {@link CommandManager} that operates the {@link Command} being called.
     *
     * @return the {@link #manager}
     */
    public @NotNull CommandManager getManager() {
        return manager;
    }

    /**
     * Retrieves The {@link Message} sent by a Discord user to trigger the command.
     *
     * @return the {@link #message}
     */
    public @NotNull Message getMessage() {
        return message;
    }

    /**
     * Retrieves The {@link MessageChannel} a Discord user used to trigger the command. Could be a DM channel or a
     * server one.
     *
     * @return the {@link #channel}
     */
    public @NotNull MessageChannel getChannel() {
        return channel;
    }

    /**
     * Retrieves The Discord {@link User} who triggered the command.
     *
     * @return the {@link #user}
     */
    public @NotNull User getUser() {
        return user;
    }

    /**
     * Retrieves The Discord {@link Member} who triggered the command. If the command was triggered in a private DM,
     * this will <b>always</b> be null. However, if it was triggered in a server it will <b>never</b> be null.
     * <p><br>
     * This method is intentionally left unannotated, because while it could be null in theory, you might have
     * background knowledge that it won't be null because the command was disabled in DMs and only works in servers.
     * Similarly if the command is disabled in servers and only works in DMs, this will always be null.
     *
     * @return the {@link #member}
     */
    public @Nullable Member getMember() {
        return member;
    }

    /**
     * Retrieves the contents of the message sent by a Discord user that triggered the command. This is equivalent to
     * {@code getMessage().getContentRaw();}.
     *
     * @return the {@link #messageText}
     */
    public @NotNull String getMessageText() {
        return messageText;
    }

    /**
     * Similar to {@link #getMessageText()}, this method retrieves the contents of the message sent by a Discord user
     * that triggered the command, but the prefix and the name of the command have been removed so it's only the
     * original arguments.
     *
     * @return the {@link #messageTextMod}
     */
    public @NotNull String getMessageTextMod() {
        return messageTextMod;
    }

    /**
     * Retrieves The contents of the message split by whitespaces into arguments. This is equivalent to {@code
     * getMessageContents().split("\\s+");} except that the regex was computed at the creation of this {@link
     * CommandCallData} instance already, meaning it would be inefficient to compute it again now.
     *
     * @return the {@link #messageArgs}
     */
    public @NotNull String[] getMsgArgs() {
        return messageArgs;
    }

    /**
     * Retrieves The {@link Command} that was called by the user when they triggered it.
     *
     * @return the {@link #command}
     */
    public @NotNull Command getCommand() {
        return command;
    }

    /**
     * This is a convenience method to send a message to the channel given in {@link #getChannel()}.
     *
     * @param message the message to send
     */
    public void message(CharSequence message) {
        channel.sendMessage(message).queue();
    }

    /**
     * This is a convenience method to send a message to the channel given in {@link #getChannel()}.
     *
     * @param message the message to send
     */
    public void message(Number message) {
        message(message.toString());
    }

    /**
     * This is a convenience method to send a message to the channel given in {@link #getChannel()}.
     *
     * @param message the message to send
     */
    public void message(MessageEmbed message) {
        channel.sendMessage(message).queue();
    }

    /**
     * This is a convenience method to send a message to the channel given in {@link #getChannel()}.
     *
     * @param message the message to send
     */
    public void message(EmbedBuilder message) {
        channel.sendMessage(message.build()).queue();
    }

    /**
     * This is a convenience method to send a message to the channel given in {@link #getChannel()}.
     *
     * @param message the message to send
     */
    public void message(MessageBuilder message) {
        channel.sendMessage(message.build()).queue();
    }

    /**
     * This is a convenience method to send a message to the channel given in {@link #getChannel()}.
     *
     * @param message the message to send
     */
    public void message(Message message) {
        channel.sendMessage(message).queue();
    }

    /**
     * This is a convenience method to send a temporary message to the channel given in {@link #getChannel()}. It uses
     * {@link MessageUtils#sendTemp(MessageChannel, CharSequence, TempMsgConfig)} with {@link
     * TempMsgConfig#DEFAULT_SPEED} to control how long the message persists.
     *
     * @param message the message to send
     */
    public void messageTemp(CharSequence message) {
        MessageUtils.sendTemp(channel, message, TempMsgConfig.DEFAULT_SPEED);
    }

    /**
     * This is a convenience method to send a temporary message to the channel given in {@link #getChannel()}. It uses
     * {@link MessageUtils#sendTemp(MessageChannel, CharSequence, TempMsgConfig)} with {@link
     * TempMsgConfig#DEFAULT_SPEED} to control how long the message persists.
     *
     * @param message the message to send
     */
    public void messageTemp(Number message) {
        messageTemp(message.toString());
    }

    /**
     * This is a convenience method to send a temporary message to the channel given in {@link #getChannel()}. It uses
     * {@link MessageUtils#sendTemp(RestAction, int)} to send the message.
     *
     * @param message the message to send
     * @param seconds the number of seconds to leave the message in Discord before deleting it
     */
    public void messageTemp(CharSequence message, int seconds) {
        MessageUtils.sendTemp(channel.sendMessage(message), seconds);
    }

    /**
     * This is a convenience method to send a temporary message to the channel given in {@link #getChannel()}. It uses
     * {@link MessageUtils#sendTemp(MessageChannel, EmbedBuilder, TempMsgConfig)} with {@link
     * TempMsgConfig#DEFAULT_SPEED} to control how long the message persists.
     *
     * @param message the message to send
     */
    public void messageTemp(EmbedBuilder message) {
        MessageUtils.sendTemp(channel, message, TempMsgConfig.DEFAULT_SPEED);
    }

    /**
     * This is a convenience method to send a temporary message to the channel given in {@link #getChannel()}. It uses
     * {@link MessageUtils#sendTemp(RestAction, int)} to send the message.
     *
     * @param message the message to send
     * @param seconds the number of seconds to leave the message in Discord before deleting it
     */
    public void messageTemp(MessageEmbed message, int seconds) {
        MessageUtils.sendTemp(channel.sendMessage(message), seconds);
    }

    /**
     * This is a convenience method to send a temporary message to the channel given in {@link #getChannel()}. It uses
     * {@link MessageUtils#sendTemp(MessageChannel, MessageBuilder, TempMsgConfig)} with {@link
     * TempMsgConfig#DEFAULT_SPEED} to control how long the message persists.
     *
     * @param message the message to send
     */
    public void messageTemp(MessageBuilder message) {
        MessageUtils.sendTemp(channel, message, TempMsgConfig.DEFAULT_SPEED);
    }

    /**
     * This is a convenience method to send a temporary message to the channel given in {@link #getChannel()}. It uses
     * {@link MessageUtils#sendTemp(RestAction, int)} to send the message.
     *
     * @param message the message to send
     * @param seconds the number of seconds to leave the message in Discord before deleting it
     */
    public void messageTemp(Message message, int seconds) {
        MessageUtils.sendTemp(channel.sendMessage(message), seconds);
    }

    /**
     * This is a convenience method to reply to the message calling a command that triggered this {@link
     * CommandCallData} instance. It uses Discord's reply feature to reply to the {@link Message} retrieved through
     * {@link #getMessage()}.
     *
     * @param message the message to reply with
     */
    public void reply(CharSequence message) {
        this.message.reply(message).queue();
    }

    /**
     * This is a convenience method to reply to the message calling a command that triggered this {@link
     * CommandCallData} instance. It uses Discord's reply feature to reply to the {@link Message} retrieved through
     * {@link #getMessage()}.
     *
     * @param message the message to reply with
     */
    public void reply(MessageEmbed message) {
        this.message.reply(message).queue();
    }

    /**
     * This is a convenience method to reply to the message calling a command that triggered this {@link
     * CommandCallData} instance. It uses Discord's reply feature to reply to the {@link Message} retrieved through
     * {@link #getMessage()}.
     *
     * @param message the message to reply with
     */
    public void reply(Message message) {
        this.message.reply(message).queue();
    }
}
