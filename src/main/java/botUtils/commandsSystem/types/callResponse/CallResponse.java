package botUtils.commandsSystem.types.callResponse;

import botUtils.commandsSystem.json.JsonParser;
import botUtils.commandsSystem.manager.CommandManager;
import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.types.CommandCallData;
import botUtils.exceptions.JsonParseException;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * This is a less intensive Command extension than Functions. It doesn't allow for arguments, but simply sends a
 * pre-programmed response when the user sends a certain command.
 * <p>
 * When creating EmbedBuilders in the json, the following parameters are required: type, title, description, color. You
 * can also add footerText, footerImg (a url), and fields (which is a json array containing objects that each must have
 * a `title`, `text`, and `inline` parameter.)
 */
public class CallResponse extends Command {
    private final Response[] responses;
    private final String defaultResponseKey;

    //                            |
    // TODO implement this stuff \|/
    private final String[] replaceKeys = {
            "PREFIX"};
    private final String[] replaceValues = {
            super.manager.getMainPrefix()};

    public CallResponse(@NotNull JsonObject json, @NotNull CommandManager manager) throws JsonParseException {
        super(json, manager);

        JsonObject[] responseObjects = JsonParser.getJsonObjectArray(json, "responses");
        this.responses = new Response[responseObjects.length];
        for (int i = 0; i < this.responses.length; i++)
            this.responses[i] = Response.of(responseObjects[i]);

        this.defaultResponseKey = JsonParser
                .getString(json, "defaultResponseKey", responses.length == 0 ? "" : responses[0].getMainKey());
    }

    /**
     * This represents the class that should be used for storing information about a specific triggering of a {@link
     * CallResponse} by a user in Discord. See {@link Command#getCallDataClass()} for more detailed documentation
     * about what this means.
     *
     * @return the class for storing information about a {@link CallResponse} call and execution
     */
    @Override
    public Class<? extends CommandCallData> getCallDataClass() {
        return CommandCallData.class;
    }

    public void process(@NotNull CommandCallData data, Method method) {
        String key;

        if (data.getMsgArgs().length == 1)
            key = defaultResponseKey;
        else
            key = mergeArgs(data.getMsgArgs(), 1);

        for (Response response : responses)
            if (response.matches(key)) {
                data.message(response.getMessage());
                return;
            }

        // If no response was sent it means a matching key wasn't found
        sendError(
                data.getChannel(),
                "Error loading response (unknown term). Try `" + getHelpString() + "` for more information.");
    }

}
