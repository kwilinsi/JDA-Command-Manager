package botUtils.tools;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * An {@link ErrorBuilder} handles exceptions that are thrown by the code in the Discord bot at various points. If you
 * catch an exception, you can pass it to an instance of this class and it will be cleanly transmitted to the user in
 * Discord.
 * <p><br>
 * There are two ways to construct {@link ErrorBuilder} instances. The first is by specifying the error title and a
 * description of the error. The second is by passing any {@link Exception}. If you choose to create an {@link
 * ErrorBuilder} with an {@link Exception}, the exception message will be used as the error description, and the name of
 * the exception class will be set as the error title.
 * <p><br>
 * All {@link ErrorBuilder} instances are based on an {@link EmbedBuilder}. Errors sent in Discord are always
 * transmitted in this form, meaning that at minimum a title, description, and color is required. If you don't specify a
 * title and you passed a simple string message to create the error, the title "{@code Error}" will be used. If you
 * don't specify a color either, the default is {@link #DEFAULT_COLOR}.
 * <p><br>
 * Once creating an {@link ErrorBuilder}, you can add all the same features of embeds, including an author, a footer,
 * and various {@link EmbedField} instances.
 */
public class ErrorBuilder extends EmbedBuilder {
    /**
     * This is the default color used by all {@link ErrorBuilder} instances if a color is not specified.
     */
    private static final Color DEFAULT_COLOR = Colors.RED;

    private ErrorBuilder(@NotNull String message, @NotNull String title, @NotNull Color color) {
        setTitle(title);
        setDescription(message);
        setColor(color);
    }

    /**
     * Convenience method to send an {@link Exception} to a channel without instantiating an {@link ErrorBuilder}. The
     * {@link EmbedBuilder} with the error message is sent to the user through {@link
     * MessageUtils#sendTemp(MessageChannel, EmbedBuilder, TempMsgConfig)}, meaning that errors on the initial send to
     * Discord are printed to the console, but errors returned on deletion are blocked.
     *
     * @param channel   the channel to send the error message in
     * @param exception the exception with the name and description of the error
     * @param color     the color of the created embed
     * @param config    the configuration instance to control how long to wait before deleting the temporary error
     * @param <T>       type parameter
     */
    public static <T extends Exception> void handle(@NotNull MessageChannel channel, @NotNull T exception,
                                                    @NotNull Color color, @NotNull TempMsgConfig config) {
        MessageUtils.sendTemp(
                channel,
                MessageUtils.makeEmbedBuilder(
                        GenericUtils.convertCamelCase(exception.getClass().getName()),
                        exception.getMessage(), color),
                config);
    }

    /**
     * Creates a new {@link ErrorBuilder} from the specified message. The title will be set to {@code Error} by default
     * and {@link #DEFAULT_COLOR} will be used for the {@link EmbedBuilder} color.
     *
     * @param message the description of the error
     * @return the new {@link ErrorBuilder} instance
     */
    public static @NotNull ErrorBuilder of(@NotNull String message) {
        return new ErrorBuilder(message, "Error", DEFAULT_COLOR);
    }

    /**
     * Creates a new {@link ErrorBuilder} from the specified message and title. {@link #DEFAULT_COLOR} will be used for
     * the {@link EmbedBuilder} color by default.
     *
     * @param message the description of the error
     * @param title   the title of the error embed
     * @return the new {@link ErrorBuilder} instance
     */
    public static @NotNull ErrorBuilder of(@NotNull String message, @NotNull String title) {
        return new ErrorBuilder(message, title, DEFAULT_COLOR);
    }


    /**
     * Creates a new {@link ErrorBuilder} from the specified message and {@link EmbedBuilder} color. The default title
     * of "{@code Error}" is used as the embed title.
     *
     * @param message the description of the error
     * @param color   the color of the error embed
     * @return the new {@link ErrorBuilder} instance
     */
    public static @NotNull ErrorBuilder of(@NotNull String message, @NotNull Color color) {
        return new ErrorBuilder(message, "Error", color);
    }

    /**
     * Creates a new {@link ErrorBuilder} from the specified message, title, and {@link EmbedBuilder} color.
     *
     * @param message the description of the error
     * @param title   the title of the error embed
     * @param color   the color of the error embed
     * @return the new {@link ErrorBuilder} instance
     */
    public static @NotNull ErrorBuilder of(@NotNull String message, @NotNull String title, @NotNull Color color) {
        return new ErrorBuilder(message, title, color);
    }

    /**
     * Creates a new {@link ErrorBuilder} from an exception with the default color of {@link #DEFAULT_COLOR}. For more
     * information see {@link #of(Exception, Color)}.
     *
     * @param exception an exception to send to the user
     * @param <T>       type parameter
     * @return the new {@link ErrorBuilder} instance
     */
    public static <T extends Exception> @NotNull ErrorBuilder of(@NotNull T exception) {
        return new ErrorBuilder(
                exception.getMessage(),
                GenericUtils.convertCamelCase(exception.getClass().getSimpleName()), DEFAULT_COLOR);
    }

    /**
     * Creates a new {@link ErrorBuilder} from the given exception and color. The exception's message will be used as
     * the description of the error, and the name of the exception class will be used as the title of the message.
     * <p><br>
     * Note that class name of the exception is passed through {@link GenericUtils#convertCamelCase(String)}, so it
     * should be reasonably human-readable in Discord.
     *
     * @param exception an exception to send to the user
     * @param color     the color to use in the embed
     * @param <T>       type parameter
     * @return the new {@link ErrorBuilder} instance
     */
    public static <T extends Exception> @NotNull ErrorBuilder of(@NotNull T exception, @NotNull Color color) {
        return new ErrorBuilder(
                exception.getMessage(),
                GenericUtils.convertCamelCase(exception.getClass().getName()), color);
    }

    /**
     * Sends this {@link ErrorBuilder} to the specified channel through a {@link MessageChannel#sendMessage} call with
     * {@link MessageAction#queue()}. This means that errors are still send in the console if there's a problem sending
     * the message.
     * <p><br>
     * Note that the error sent is permanent. To send an error temporarily and have it deleted after a few seconds, use
     * {@link #sendTemp(MessageChannel, TempMsgConfig)}.
     *
     * @param channel the channel to send the message in.
     */
    public void send(@NotNull MessageChannel channel) {
        channel.sendMessage(this.build()).queue();
    }

    /**
     * Sends this {@link ErrorBuilder} to the specified channel through {@link MessageUtils#sendTemp}.
     * <p><br>
     * Note that the message is temporary, meaning it will be deleted from Discord after a short time as determined by
     * the specified {@link TempMsgConfig} instance. To send an error permanently without deleting it, use {@link
     * #send(MessageChannel)}.
     *
     * @param channel the channel to send the message in.
     * @param config  the configuration instance to control how long the error message persists before deletion
     */
    public void sendTemp(@NotNull MessageChannel channel, @NotNull TempMsgConfig config) {
        MessageUtils.sendTemp(channel, this, config);
    }

    /**
     * This sends the error message exactly the same as {@link #sendTemp(MessageChannel, TempMsgConfig)}, except that if
     * Discord returns an error sending the message this error is ignored and not printed in the console.
     *
     * @param channel the channel to send the message in.
     * @param config  the configuration instance to control how long the error message persists before deletion
     */
    public void sendTempNoError(@NotNull MessageChannel channel, @NotNull TempMsgConfig config) {
        MessageUtils.sendTempNoError(channel, this, config);
    }

    /**
     * Adds my own {@link EmbedField} field type conveniently to the {@link ErrorBuilder} and return this {@link
     * ErrorBuilder} instance for chaining. Note that the {@link EmbedField#getOfficialField()} method used here also
     * validates the contents of the {@link EmbedField}, meaning this method will throw an exception if there is invalid
     * content in the field, such as too many characters.
     *
     * @param fields the field to add
     * @return this {@link EmbedField} field for chaining.
     */
    public @NotNull ErrorBuilder addField(@Nullable EmbedField... fields) {
        if (fields != null)
            for (EmbedField field : fields)
                if (field != null)
                    super.addField(field.getOfficialField());
        return this;
    }
}