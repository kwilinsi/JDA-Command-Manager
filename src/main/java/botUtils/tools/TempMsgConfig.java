package botUtils.tools;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * This is a configuration class used by {@link MessageUtils} to control the sending of temporary messages in Discord. When
 * a method is used that sends a temporary message, it requires a {@link TempMsgConfig} instance to
 * know how long to keep the message in Discord before deleting it.
 * <p><br>
 * Configuration instances have two parameters: a {@link #base} and a {@link #factor}. The {@link #base} sets the
 * minimum number of seconds the message must remain before being deleted. No message will be deleted before the base
 * seconds elapse. The {@link #factor} adjusts the time that the message lasts based on how long the message would take
 * the average person to read. For each {@link #factor} number of characters in the message, an additional 1 second is
 * added to the time it lasts.
 * <p><br>
 * For example, if you used {@link #of(int, float)} to create a configuration instance with a {@link #base} of 10 and a
 * {@link #factor} of 12, a message with 100 characters would last for {@code 10 + floor(100 / 12) = 18} seconds.
 * <p><br>
 * If you don't want to manually define the configuration parameters yourself, you can use the predefined constants
 * {@link #SLOW_SPEED}, {@link #DEFAULT_SPEED}, and {@link #FAST_SPEED}, which are set up based on the default reading
 * speed of adults according to <a href="https://en.wikipedia.org/wiki/Words_per_minute#Reading_and_comprehension">
 * Wikipedia</a>.
 */
public class TempMsgConfig {
    /**
     * The minimum number of seconds for a temporary message to remain in Discord before being deleted. A message with
     * zero characters would last exactly this long.
     */
    private final int base;

    /**
     * For every 'factor' characters in a message, one second is added to its time in Discord. This includes all
     * characters in the message, including everything in a {@link EmbedBuilder} except of course for URLs or file names
     * which are not displayed directly.
     */
    private final float factor;

    /**
     * This is the roughly average number of characters read by adults in one second (according to Wikipedia). Used by
     * {@link #DEFAULT_SPEED}.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Words_per_minute#Reading_and_comprehension">Wikipedia Average WPM</a>
     */
    public static final float DEFAULT_FACTOR = 14.38f;

    /**
     * This is the default minimum number of seconds for a message to remain before being deleted. Used by {@link
     * #DEFAULT_SPEED}.
     */
    public static final int DEFAULT_BASE = 8;

    /**
     * This defines a {@link TempMsgConfig} instance with {@link #DEFAULT_BASE} and {@link #DEFAULT_FACTOR}. This means
     * the message will last for a base {@link #DEFAULT_BASE} seconds plus additional seconds for the average reading
     * speed of an adult (863 characters per minute according to wikipedia).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Words_per_minute#Reading_and_comprehension">Wikipedia Average WPM</a>
     */
    public static final TempMsgConfig DEFAULT_SPEED = new TempMsgConfig(DEFAULT_BASE, DEFAULT_FACTOR);

    /**
     * This defines a {@link TempMsgConfig} instance with a {@link #base} of 12 seconds and a {@link #factor} of 10.4.
     * This means the message will last for a base 12 seconds plus additional seconds for a relatively slow reading
     * speed of an adult (629 characters per minute according to wikipedia).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Words_per_minute#Reading_and_comprehension">Wikipedia Average WPM</a>
     */
    public static final TempMsgConfig SLOW_SPEED = new TempMsgConfig(12, 10.4f);

    /**
     * This defines a {@link TempMsgConfig} instance with a {@link #base} of 4 seconds and a {@link #factor} of 18.28.
     * This means the message will last for a base 4 seconds plus additional seconds for a relatively fast reading speed
     * of an adult (1097 characters per minute according to wikipedia).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Words_per_minute#Reading_and_comprehension">Wikipedia Average WPM</a>
     */
    public static final TempMsgConfig FAST_SPEED = new TempMsgConfig(4, 18.28f);

    private TempMsgConfig(int base, float factor) {
        this.base = base;
        this.factor = factor;
    }

    /**
     * Creates a new {@link TempMsgConfig} instance. This is used for controlling how long temporary messages sent
     * through {@link MessageUtils} remain in Discord before being deleted. All messages sent with this config instance will
     * remain for at least 'base' seconds. Then they will last an additional second for each 'factor' number of
     * characters in the entire message.
     * <p><br>
     * For example, if {@code base = 10} and {@code factor = 15}, a message with 200 characters would last for {@code 10
     * + floor(200 / 15) = 23} seconds before being deleted. For character reference, this example is roughly 200
     * characters.
     *
     * @param base   minimum number of seconds for an message to remain in Discord
     * @param factor the number of characters required to add 1 additional second to the message duration
     * @return the newly created configuration instance ready to pass to {@link MessageUtils}
     */
    public static TempMsgConfig of(int base, float factor) {
        return new TempMsgConfig(base, factor);
    }

    /**
     * Creates a new {@link TempMsgConfig} instance with a custom {@link #base} and a {@link #factor} of zero. This
     * means a message will remain in Discord for a fixed amount of time, rather than being determinate on the number of
     * characters in the message. For more information see {@link #of(int, float)}.
     *
     * @param base the number of seconds to wait before deleting the message
     * @return the newly created configuration instance ready to pass to {@link MessageUtils}.
     */
    public static TempMsgConfig of(int base) {
        return new TempMsgConfig(base, 0);
    }

    /**
     * Get the minimum number of seconds the message should remain in Discord.
     *
     * @return {@link #base}
     */
    public int getBase() {
        return base;
    }

    /**
     * Get the number of characters required for one additional second in Discord.
     *
     * @return {@link #factor}
     */
    public float getFactor() {
        return factor;
    }

    /**
     * Returns the number of seconds that the given message should remain in Discord for before being deleted. This is
     * calculated based on the {@link #base} and {@link #factor} for this {@link TempMsgConfig}.
     *
     * @param message the message to check
     * @return the number of seconds to wait after sending the message before deleting it
     */
    public int getDelay(@NotNull CharSequence message) {
        return (int) (base + message.length() / factor);
    }

    /**
     * Returns the number of seconds that the given message should remain in Discord for before being deleted. This is
     * calculated based on the {@link #base} and {@link #factor} for this {@link TempMsgConfig}.
     *
     * @param message the message to check
     * @return the number of seconds to wait after sending the message before deleting it
     */
    public int getDelay(@NotNull EmbedBuilder message) {
        return base + message.build().getLength();
    }

    /**
     * Returns the number of seconds that the given message should remain in Discord for before being deleted. This is
     * calculated based on the {@link #base} and {@link #factor} for this {@link TempMsgConfig}.
     *
     * @param message the message to check
     * @return the number of seconds to wait after sending the message before deleting it
     */
    public int getDelay(@NotNull MessageBuilder message) {
        return base + message.build().getContentStripped().length();
    }
}
