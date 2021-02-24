package botUtils.tools;

import botUtils.commandsSystem.builder.ResponseBuilder;
import botUtils.commandsSystem.json.JsonBuilder;
import botUtils.commandsSystem.json.JsonMap;
import botUtils.commandsSystem.json.JsonParser;
import botUtils.exceptions.JsonParseException;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmbedField {
    private final @NotNull String title;
    private final @NotNull String content;
    private final boolean inline;

    private EmbedField(String title, String content) {
        this(title, content, false);
    }

    private EmbedField(@Nullable String title, @Nullable String content, boolean inline) {
        this.title = title == null ? "" : title;
        this.content = content == null ? "" : content;
        this.inline = inline;
    }

    /**
     * Returns an instance of the {@link EmbedField} class, which represents one of the fields in an EmbedBuilder. The
     * field inline state is automatically set to false when omitted. Note that the title parameter does not accept
     * Discord markdown formatting but the content parameter does.
     * <p>
     * A null title or text will simply be replaced with an empty string
     *
     * @param title   the title of the field
     * @param content the content of the field
     */
    public static @NotNull EmbedField of(@Nullable String title, @Nullable String content) {
        return new EmbedField(title, content);
    }

    /**
     * Returns an instance of the {@link EmbedField} class, which represents one of the fields in an {@link
     * net.dv8tion.jda.api.EmbedBuilder}. Note that the title parameter does not accept Discord markdown formatting but
     * the content parameter does.
     * <p>
     * A null title or text will simply be replaced with an empty string.
     *
     * @param title   the title of the field
     * @param content the content of the field
     * @param inline  true to allow multiple EmbedFields on the same line in an EmbedBuilder; false to force new lines
     * @return the newly created {@link EmbedField}
     */
    public static @NotNull EmbedField of(@Nullable String title, @Nullable String content, boolean inline) {
        return new EmbedField(title, content, inline);
    }

    /**
     * Returns an instance of the {@link EmbedField} class, which represents one of hte fields in an {@link
     * net.dv8tion.jda.api.EmbedBuilder}. The title, text, and inline (optional) parameters will be obtained from the
     * Json. An exception is thrown if there is an error reading the Json, such as missing values for the 'title' or
     * 'text' keys or incorrect data types for any of the keys including 'inline'. Note that the Json does not need to
     * contain an 'inline' value. If this is omitted, false will be used as the default.
     *
     * @param json the Json to read
     * @return the newly created {@link EmbedField}
     * @throws JsonParseException if there is an error reading the Json for the 'title', 'text', or 'inline' keys
     */
    public static @NotNull EmbedField of(@NotNull JsonObject json) throws JsonParseException {
        return new EmbedField(
                JsonParser.getString(json, "title"),
                JsonParser.getString(json, "text"),
                JsonParser.getBoolean(json, "inline", false)
        );
    }

    /**
     * Converts an array of {@link JsonObject} instances to {@link EmbedField} instances by iterating over them and
     * converting via {@link EmbedField}.{@link #of(JsonObject)}. The input array must not be null and must not contain
     * null items.
     *
     * @param array the input array of {@link JsonObject} instances imported from a Json file
     * @return a new array of {@link EmbedField} instances
     * @throws JsonParseException if there is an error while converting Json to an embed for any items
     */
    public static @NotNull EmbedField[] ofArray(@NotNull JsonObject[] array) throws JsonParseException {
        EmbedField[] fields = new EmbedField[array.length];
        for (int i = 0; i < array.length; i++)
            fields[i] = of(array[i]);
        return fields;
    }

    /**
     * Get the title of this field (the bold header before each field that doesn't accept Discord markdown)
     *
     * @return the title
     */
    public @NotNull String getTitle() {
        return title;
    }

    /**
     * Get the content of the field (the text in the body that accepts Discord markdown)
     *
     * @return the content
     */
    public @NotNull String getContent() {
        return content;
    }

    /**
     * Get the inline state of the EmbedField (false by default). If this is true fields are placed side-by-side in the
     * {@link net.dv8tion.jda.api.EmbedBuilder}. Otherwise they're placed vertical above one another.
     *
     * @return true if the field is to be rendered inline; false for its own line
     */
    public boolean isInline() {
        return inline;
    }

    /**
     * Converts my {@link EmbedField} field types into the official JDA {@link MessageEmbed.Field} type. All the data
     * from this field is migrated into one of those and it is returned.
     * <p><br>
     * The field values are also checked at the same time, so if this method runs without errors you can be sure that
     * the fields in the {@link EmbedBuilder} will build without exceptions.
     *
     * @return the {@link MessageEmbed.Field} form of this {@link EmbedField}
     */
    public MessageEmbed.Field getOfficialField() {
        return new MessageEmbed.Field(title, content, inline, true);
    }

    /**
     * Creates a standard {@link JsonObject} with the content of this {@link EmbedField}. This is used for {@link
     * ResponseBuilder} instances of type {@link net.dv8tion.jda.api.EmbedBuilder}
     * that add embed fields to themselves.
     *
     * @return the completed {@link JsonObject}
     * @throws ClassNotFoundException highly unlikely error that could possibly be thrown while building the Json
     */
    public JsonObject getJson() throws ClassNotFoundException {
        return JsonBuilder.buildJsonObject(JsonMap.of()
                .add("title", title)
                .add("content", content)
                .add("inline", inline));
    }
}
