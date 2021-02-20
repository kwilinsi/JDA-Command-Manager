package botUtils.exceptions;

import botUtils.commandsSystem.manager.CommandManager;
import org.jetbrains.annotations.NotNull;

/**
 * This is a specific exception to be used when an error was thrown while trying to build a {@link CommandManager}
 * with {@link CommandManager#build()}. It encases another {@link Exception} for easier catching outside the
 * {@link CommandManager} method.
 */
public class ManagerBuildException extends Exception {
    /**
     * Defines a new {@link ManagerBuildException} based on an existing {@link Exception} thrown by something else.
     * @param e the exception to wrap
     */
    public ManagerBuildException(@NotNull Exception e) {
        // Todo confirm that this is all I need to do for defining an exception that encases another exception
        super(e);
    }
}
