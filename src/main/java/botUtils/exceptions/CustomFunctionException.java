package botUtils.exceptions;

import botUtils.commandsSystem.types.function.Function;
import botUtils.tools.ErrorBuilder;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * This class represents intentional errors that were thrown by methods written for the Discord bot and implemented
 * through functions. When you're writing a method that is called by a {@link Function}, it is advised to throw a {@link
 * CustomFunctionException} because these are easily understood by {@link Function} error handlers and they produce the
 * most elegant output in Discord.
 * <p><br>
 * This error is simply an extension of {@link Exception} that wraps an {@link ErrorBuilder} instance. In fact, you can
 * even create a {@link CustomFunctionException} by simply passing an {@link ErrorBuilder}.
 */
public class CustomFunctionException extends Exception {
    private final ErrorBuilder error;

    /**
     * Creates a {@link CustomFunctionException} based on the contents of another {@link Exception} using {@link
     * ErrorBuilder#of(Exception)}.
     *
     * @param error the exception that triggered this
     */
    public CustomFunctionException(Exception error) {
        this.error = ErrorBuilder.of(error);
    }

    /**
     * Wraps an {@link ErrorBuilder} with a {@link CustomFunctionException}.
     *
     * @param error the error
     */
    public CustomFunctionException(ErrorBuilder error) {
        this.error = error;
    }

    /**
     * This works just like {@link CustomFunctionException#CustomFunctionException(ErrorBuilder)}, except it allows you
     * to encase an {@link ErrorBuilder} that got converted to an {@link EmbedBuilder} because of chaining.
     * <p>Be warned: This method will immediately recast it back to an {@link ErrorBuilder}. Only use it if you know
     * what you're doing.
     *
     * @param error the error
     */
    public CustomFunctionException(EmbedBuilder error) {
        this.error = (ErrorBuilder) error;
    }

    /**
     * Retrieves the {@link #error}, which was either passed directly to this {@link CustomFunctionException} or created
     * based on a regular {@link Exception}.
     *
     * @return the {@link #error}
     */
    public ErrorBuilder getError() {
        return error;
    }
}
