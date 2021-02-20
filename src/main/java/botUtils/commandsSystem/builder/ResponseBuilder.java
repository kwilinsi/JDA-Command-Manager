package botUtils.commandsSystem.builder;

import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.tools.Checks;
import botUtils.tools.Colors;
import botUtils.tools.EmbedField;
import botUtils.tools.GenericUtils;
import botUtils.commandsSystem.types.callResponse.MessageType;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ResponseBuilder implements Builder {
    private String[] keys;
    private final int type;

    private String content;

    private String title;
    private String description;
    private String color;
    private String footerText;
    private String footerImgUrl;
    private String link;
    private String imageUrl;
    private JsonObject[] fields;

    private ResponseBuilder(@NotNull String content) {
        this.type = MessageType.TEXT;
        this.content = content;
    }

    private ResponseBuilder(@NotNull String title, @NotNull String description) {
        this.type = MessageType.EMBED_BUILDER;

        this.title = title;
        this.description = description;
    }

    private ResponseBuilder(@NotNull String title, @NotNull String description, String color,
                            String footerText, String footerImgUrl) {
        this.type = MessageType.EMBED_BUILDER;

        this.title = title;
        this.description = description;
        this.color = color;
        this.footerText = footerText;
        this.footerImgUrl = footerImgUrl;
    }

    /**
     * Defines a plain text {@link ResponseBuilder} by specifying the text to send to the user when they request
     * this response with one of its keys.
     * <p>
     * Note that because this constructor does not create an embed builder, it is incompatible with all the
     * {@link ResponseBuilder} methods dealing with embeds, and calling those methods will throw an error.
     *
     * @param content the full message to send to the user
     */
    public static ResponseBuilder of(@NotNull String content) {
        return new ResponseBuilder(content);
    }

    /**
     * Defines an {@link EmbedBuilder} type {@link ResponseBuilder} with a title and description. Use the additional
     * {@link ResponseBuilder} methods to add more parameters to the embed including a color, footer, and
     * field content.
     *
     * @param title       the title of the embed builder sent to the user
     * @param description the description in the embed builder sent to the user
     */
    public static ResponseBuilder of(@NotNull String title, @NotNull String description) {
        return new ResponseBuilder(title, description);
    }

    /**
     * Defines an {@link EmbedBuilder} type {@link ResponseBuilder} with a mandatory title and description along
     * with a number of other optional (but often used) arguments. Use additional methods to add more parameters
     * to the embed such as fields.
     *
     * @param title        the title of the embed builder sent to the user
     * @param description  the description in the embed builder sent to the user
     * @param color        the color of the embed
     * @param footerText   the text in the footer of the embed
     * @param footerImgUrl a url for the footer image
     */
    public static ResponseBuilder of(@NotNull String title, @NotNull String description, String color,
                                     String footerText, String footerImgUrl) {
        return new ResponseBuilder(title, description, color, footerText, footerImgUrl);
    }

    /**
     * Keys are the arguments users can type after a command to return this {@link ResponseBuilder}. You can have
     * as many keys as you want, but there must be at least one and none can be null. When the user provides one
     * of these keys for its associated {@link CallResponseBuilder} command,
     * they will get this response. This method overwrites existing keys and sets them.
     *
     * @param keys one or more valid keys for this response (none may be null)
     * @return this {@link ResponseBuilder} instance for chaining
     * @throws NullPointerException if any of the keys are null or the whole array is null
     */
    public ResponseBuilder setKeys(@NotNull String... keys) {
        Checks.arrayHasNoNulls(keys);
        this.keys = keys;
        return this;
    }

    /**
     * Sets the title of the {@link EmbedBuilder}.
     *
     * @param title the new title
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setTitle(@NotNull String title) {
        assertIsEmbedType();
        this.title = title;
        return this;
    }

    /**
     * Sets the description of the {@link EmbedBuilder}.
     *
     * @param description the new description
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setDescription(@NotNull String description) {
        assertIsEmbedType();
        this.description = description;
        return this;
    }

    /**
     * Sets the color of the {@link EmbedBuilder}.
     *
     * @param color the new color as a hex code string or a color like "RED" or "ORANGE" as specified in {@link Colors}.
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setColor(@NotNull String color) {
        assertIsEmbedType();
        this.color = color;
        return this;
    }

    /**
     * Sets the color of the {@link EmbedBuilder}.
     *
     * @param color the new color, such as one of those predefined in {@link Colors}
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setColor(@NotNull Color color) {
        assertIsEmbedType();
        this.color = Colors.getColorStr(color);
        return this;
    }

    /**
     * Sets the footer text of the {@link EmbedBuilder}.
     *
     * @param text the new footer text
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setFooterText(@NotNull String text) {
        assertIsEmbedType();
        this.footerText = text;
        return this;
    }

    /**
     * Sets the footer image url of the {@link EmbedBuilder}.
     *
     * @param url the url
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setFooterImg(@NotNull String url) {
        assertIsEmbedType();
        this.footerImgUrl = url;
        return this;
    }

    /**
     * Adds an {@link EmbedField} to the {@link EmbedBuilder}
     *
     * @param field the field to add
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     * @throws ClassNotFoundException if there's an error building the Json
     */
    public ResponseBuilder addField(@NotNull EmbedField field) throws ClassNotFoundException {
        assertIsEmbedType();
        if (this.fields == null)
            this.fields = new JsonObject[0];
        this.fields = GenericUtils.mergeArrays(this.fields, new JsonObject[]{field.getJson()});
        return this;
    }

    /**
     * Sets the target url of the hyperlink in the title of the {@link EmbedBuilder}.
     *
     * @param url the url
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setLink(@NotNull String url) {
        assertIsEmbedType();
        this.link = url;
        return this;
    }

    /**
     * Sets the url of the image to appear in the {@link EmbedBuilder}.
     *
     * @param url the url
     * @return this {@link ResponseBuilder} instance for chaining.
     * @throws IllegalStateException if this {@link ResponseBuilder} is a plain text type and not an
     *                               {@link EmbedBuilder} and thus cannot accept embed customization
     */
    public ResponseBuilder setImageUrl(@NotNull String url) {
        assertIsEmbedType();
        this.imageUrl = url;
        return this;
    }

    /**
     * Get an array of all the keys that this {@link ResponseBuilder} responds to.
     *
     * @return the keys array
     */
    public String[] getKeys() {
        return keys;
    }

    /**
     * Get a {@link JsonObject} which contains key-value pairs for the keys used to call this response and the
     * configuration for the response message to send a user.
     */
    public @NotNull JsonObject getJson() {
        try {
            return JsonBuilder.buildJsonObject(JsonMap.of()
                    .add("keys", JsonBuilder.buildJsonArray(keys))
                    .add("type", MessageType.getTypeStr(type))
                    .add("contents", content)
                    .add("title", title)
                    .add("description", description)
                    .add("color", color)
                    .add("footerText", footerText)
                    .add("footerImgUrl", footerImgUrl)
                    .add("link", link)
                    .add("imageUrl", imageUrl)
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }

    /**
     * Asserts that the ResponseBuilder is set up as an {@link EmbedBuilder} and thus can accept config methods. If
     * it's a standard text type message an error will be thrown.
     *
     * @throws IllegalStateException if this {@link ResponseBuilder} is not an {@link EmbedBuilder} type
     */
    private void assertIsEmbedType() {
        if (type != MessageType.EMBED_BUILDER)
            throw new IllegalStateException("This method is restricted to EmbedBuilder type responses. You cannot " +
                    "use it for plain text responses.");
    }
}
