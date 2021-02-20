package botUtils.exceptions;

import botUtils.commandsSystem.types.function.Argument;
import botUtils.commandsSystem.types.function.Syntax;

/**
 * Reports a problem with a specific Argument in a specified Syntax being parsed
 */
public class ArgumentException extends SyntaxException {
    /**
     * Creates an exception with a message formatting "**Argument Exception:** Failed to parse [arg name].
     * Input [input] [message]."
     *
     * @param message the text to put after the preface
     */
    public ArgumentException(Argument argument, String input, Syntax syntax, String message) {
        super(
                "**Argument Exception:** Failed to parse __" + argument.getName() +
                        "__. Input " + input + " " + message + ".",
                syntax);
    }
}