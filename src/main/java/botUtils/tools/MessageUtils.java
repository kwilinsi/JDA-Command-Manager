package botUtils.tools;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.concurrent.TimeUnit;

public class MessageUtils {
    /**
     * Generates a basic {@link EmbedBuilder} with a title, description, and color.
     *
     * @param title       the bold title at the top of the embed
     * @param description the description text under the title (accepts Discord markdown)
     * @param color       the color of the vertical bar highlighting the embed
     * @return the completed {@link EmbedBuilder}
     * @throws IllegalArgumentException possibly thrown by various {@link EmbedBuilder} methods for invalid arguments
     */
    public static @NotNull EmbedBuilder makeEmbedBuilder(
            @Nullable String title,
            @Nullable String description,
            @Nullable Color color) {
        return makeEmbedBuilder(title, null, description, color, null, null, null);
    }

    /**
     * Generates a basic {@link EmbedBuilder} with a title, description, color, and some additional fields.
     *
     * @param title       the bold title at the top of the embed
     * @param description the description text under the title (accepts Discord markdown)
     * @param color       the color of the vertical bar highlighting the embed
     * @param fields      one or more {@link EmbedField} instances to put under the description
     * @return the completed {@link EmbedBuilder}
     * @throws IllegalArgumentException possibly thrown by various {@link EmbedBuilder} methods for invalid arguments
     */
    public static @NotNull EmbedBuilder makeEmbedBuilder(
            @Nullable String title,
            @Nullable String description,
            @Nullable Color color,
            @Nullable EmbedField... fields) {
        return makeEmbedBuilder(title, null, description, color, null, null, fields);
    }

    /**
     * Generates an intermediate {@link EmbedBuilder} with a title, description, color, and a footer.
     *
     * @param title       the bold title at the top of the embed
     * @param description the description text under the title (accepts Discord markdown)
     * @param color       the color of the vertical bar highlighting the embed
     * @param footerText  the text in the footer of the embed (no Discord markdown)
     * @param footerImg   a valid url pointing to an image to put next to the footer text
     * @return the completed {@link EmbedBuilder}
     * @throws IllegalArgumentException possibly thrown by various {@link EmbedBuilder} methods for invalid arguments
     */
    public static @NotNull EmbedBuilder makeEmbedBuilder(
            @Nullable String title,
            @Nullable String description,
            @Nullable Color color,
            @Nullable String footerText,
            @Nullable String footerImg) {
        return makeEmbedBuilder(title, null, description, color, footerText, footerImg, null);
    }

    /**
     * Generates an intermediate {@link EmbedBuilder} with a title, description, color, a footer, and some additional
     * fields.
     *
     * @param title       the bold title at the top of the embed
     * @param description the description text under the title (accepts Discord markdown)
     * @param color       the color of the vertical bar highlighting the embed
     * @param footerText  the text in the footer of the embed (no Discord markdown)
     * @param footerImg   a valid url pointing to an image to put next to the footer text
     * @param fields      one or more {@link EmbedField} instances to put under the description
     * @return the completed {@link EmbedBuilder}
     * @throws IllegalArgumentException possibly thrown by various {@link EmbedBuilder} methods for invalid arguments
     */
    public static @NotNull EmbedBuilder makeEmbedBuilder(
            @Nullable String title,
            @Nullable String description,
            @Nullable Color color,
            @Nullable String footerText,
            @Nullable String footerImg,
            @Nullable EmbedField... fields) {
        return makeEmbedBuilder(title, null, description, color, footerText, footerImg, fields);
    }

    /**
     * Generates an {@link EmbedBuilder} with the basic arguments I use most often. This is the main generator using the
     * most common parameters. Another method is available with all the possible parameters recognized by embeds.
     *
     * @param title       the largest text at the top of the embed in bold
     * @param description description text under the title and above fields
     * @param color       color object (usually a FINAL object in AU class)
     * @param footerText  the text to put in the footer. If you don't want a footer use `null`, not ""
     * @param footerImg   a url to an image for the footer. If you don't want a footer image use `null`, not ""
     * @param fields      list of EmbedField objects
     * @return the completed {@link EmbedBuilder}
     * @throws IllegalArgumentException possibly thrown by various {@link EmbedBuilder} methods for invalid arguments
     */
    public static @NotNull EmbedBuilder makeEmbedBuilder(
            @Nullable String title,
            @Nullable String link,
            @Nullable String description,
            @Nullable Color color,
            @Nullable String footerText,
            @Nullable String footerImg,
            @Nullable EmbedField[] fields) {

        return makeEmbedBuilder(
                title, link, description, color, footerText, footerImg,
                null, null, null, null, null, null, fields);
    }

    /**
     * Generates an {@link EmbedBuilder} with every possible parameter recognized by embeds.
     *
     * @param title       the title
     * @param link        the link (what you get when you click the title)
     * @param description the description (goes under the title)
     * @param color       the color of the bar on the side
     * @param footerText  the text in the footer
     * @param footerImg   a url for the image in the footer
     * @param authorText  the author text above the title
     * @param authorImg   a url for the image next to the author text
     * @param thumbnail   a url for the thumbnail image
     * @param image       a url for the main image
     * @param time        the timestamp to put in the footer after the footer text
     * @param fields      an array of fields below the description
     * @return the completed {@link EmbedBuilder}
     * @throws IllegalArgumentException possibly thrown by various {@link EmbedBuilder} methods for invalid arguments
     */
    public static @NotNull EmbedBuilder makeEmbedBuilder(
            @Nullable String title,
            @Nullable String link,
            @Nullable String description,
            @Nullable Color color,
            @Nullable String footerText,
            @Nullable String footerImg,
            @Nullable String authorText,
            @Nullable String authorUrl,
            @Nullable String authorImg,
            @Nullable String thumbnail,
            @Nullable String image,
            @Nullable TemporalAccessor time,
            @Nullable EmbedField[] fields) {
        EmbedBuilder embed = new EmbedBuilder();

        if (title != null)
            embed.setTitle(title, link);
        if (description != null)
            embed.setDescription(description);
        if (color != null)
            embed.setColor(color);

        if (footerImg != null && footerText != null)
            embed.setFooter(footerText, footerImg);
        else if (footerText != null)
            embed.setFooter(footerText);

        embed.setAuthor(authorText, authorUrl, authorImg);

        if (thumbnail != null)
            embed.setThumbnail(thumbnail);
        if (image != null)
            embed.setImage(image);
        if (time != null)
            embed.setTimestamp(time);

        if (fields != null)
            for (EmbedField f : fields)
                if (f != null)
                    embed.addField(f.getTitle(), f.getContent(), f.isInline());

        return embed;
    }

    /**
     * Sends a message by executing {@link MessageAction#queue()} and waits the specified number of seconds before
     * deleting it. If deletion fails an error is not thrown because it is possible someone else deleted the message.
     * However if sending fails an error is thrown to console.
     *
     * @param message the message action to send with {@link MessageAction#queue()}
     * @param delay   the number of seconds to wait before deleting the message
     */
    public static void sendTemp(
            @NotNull RestAction<Message> message, int delay) {
        message.queue(
                m -> m.delete().queueAfter(delay, TimeUnit.SECONDS, success -> {
                }, fail -> {
                }),
                Throwable::printStackTrace);
    }

    /**
     * Similar to {@link #sendTemp(RestAction, int)}, but without sending any errors. Sends a message by executing
     * {@link MessageAction#queue()} and waits the specified number of seconds before deleting it. Regardless of what
     * happens no errors are thrown.
     * <p><br>
     * Typically you don't want to use this method, because it's helpful to throw exceptions if there's an error sending
     * a message to Discord. Most of the time you should use {@link #sendTemp(RestAction, int)} or derivative methods.
     *
     * @param message the message action to send with {@link MessageAction#queue()}
     * @param delay   the number of seconds to wait before deleting the message
     */
    public static void sendTempNoError(
            @NotNull RestAction<Message> message, int delay) {
        message.queue(
                m -> m.delete().queueAfter(delay, TimeUnit.SECONDS, success -> {
                }, fail -> {
                }),
                f -> {
                });
    }

    /**
     * Sends a message using {@link #sendTemp(RestAction, int)}.
     * <p><br>
     * The number of seconds that the message waits before being deleted is determined by the {@link TempMsgConfig}
     * configuration instance. See the {@link TempMsgConfig} documentation for more information.
     *
     * @param channel the channel to send the message in
     * @param message the message to send
     * @param config  the {@link TempMsgConfig} instance controlling how long the message stays in Discord
     */
    public static void sendTemp(
            @NotNull MessageChannel channel, @NotNull MessageBuilder message, @NotNull TempMsgConfig config) {
        sendTemp(channel.sendMessage(message.build()), config.getDelay(message));
    }

    /**
     * Sends a message using {@link #sendTempNoError(RestAction, int)}.
     * <p><br>
     * The number of seconds that the message waits before being deleted is determined by the {@link TempMsgConfig}
     * configuration instance. See the {@link TempMsgConfig} documentation for more information.
     *
     * @param channel the channel to send the message in
     * @param message the message to send
     * @param config  the {@link TempMsgConfig} instance controlling how long the message stays in Discord
     */
    public static void sendTempNoError(
            @NotNull MessageChannel channel, @NotNull MessageBuilder message, @NotNull TempMsgConfig config) {
        sendTemp(channel.sendMessage(message.build()), config.getDelay(message));
    }

    /**
     * Sends a message using {@link #sendTemp(RestAction, int)}.
     * <p><br>
     * The number of seconds that the message waits before being deleted is determined by the {@link TempMsgConfig}
     * configuration instance. See the {@link TempMsgConfig} documentation for more information.
     *
     * @param channel the channel to send the message in
     * @param message the message to send
     * @param config  the {@link TempMsgConfig} instance controlling how long the message stays in Discord
     */
    public static void sendTemp(
            @NotNull MessageChannel channel, @NotNull CharSequence message, @NotNull TempMsgConfig config) {
        sendTemp(channel.sendMessage(message), config.getDelay(message));
    }

    /**
     * Sends a message using {@link #sendTempNoError(RestAction, int)}.
     * <p><br>
     * The number of seconds that the message waits before being deleted is determined by the {@link TempMsgConfig}
     * configuration instance. See the {@link TempMsgConfig} documentation for more information.
     *
     * @param channel the channel to send the message in
     * @param message the message to send
     * @param config  the {@link TempMsgConfig} instance controlling how long the message stays in Discord
     */
    public static void sendTempNoError(
            @NotNull MessageChannel channel, @NotNull CharSequence message, @NotNull TempMsgConfig config) {
        sendTemp(channel.sendMessage(message), config.getDelay(message));
    }

    /**
     * Sends a message using {@link #sendTemp(RestAction, int)}.
     * <p><br>
     * The number of seconds that the message waits before being deleted is determined by the {@link TempMsgConfig}
     * configuration instance. See the {@link TempMsgConfig} documentation for more information.
     *
     * @param channel the channel to send the message in
     * @param message the message to send
     * @param config  the {@link TempMsgConfig} instance controlling how long the message stays in Discord
     */
    public static void sendTemp(
            @NotNull MessageChannel channel, @NotNull EmbedBuilder message, @NotNull TempMsgConfig config) {
        sendTemp(channel.sendMessage(message.build()), config.getDelay(message));
    }

    /**
     * Sends a message using {@link #sendTempNoError(RestAction, int)}.
     * <p><br>
     * The number of seconds that the message waits before being deleted is determined by the {@link TempMsgConfig}
     * configuration instance. See the {@link TempMsgConfig} documentation for more information.
     *
     * @param channel the channel to send the message in
     * @param message the message to send
     * @param config  the {@link TempMsgConfig} instance controlling how long the message stays in Discord
     */
    public static void sendTempNoError(
            @NotNull MessageChannel channel, @NotNull EmbedBuilder message, @NotNull TempMsgConfig config) {
        sendTemp(channel.sendMessage(message.build()), config.getDelay(message));
    }
}