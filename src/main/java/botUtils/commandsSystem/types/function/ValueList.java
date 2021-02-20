package botUtils.commandsSystem.types.function;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a collection of Value objects being processed. Every time someone sends a function command, an
 * instance of this object is created. It is built upon the function the user is using, and it contains the
 * arguments in the syntax the user chose along with the specific values provided for those arguments.
 */
public class ValueList extends ArrayList<Value> implements List<Value> {
    private final Argument[] arguments;

    /**
     * Instantiates a new ValueCollection object based on `length`, the number of Value objects that will be assigned.
     * This should be the number of arguments that the user provided in their command, as each argument will become
     * a Value object in the array.
     *
     * @param length the number of arguments the user provided
     */
    public ValueList(int length, Argument[] arguments) {
        super(length);
        this.arguments = arguments;
    }

    /**
     * Adds a new Value object to the end of the list and validates it.
     *
     * @param value the Value object to add
     */
    public void addAndValidate(Value value) {
        add(value);
        value.validate();
    }
}