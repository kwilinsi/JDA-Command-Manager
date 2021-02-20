package botUtils.commandsSystem.manager;

import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.types.callResponse.CallResponse;
import botUtils.commandsSystem.types.function.Function;
import botUtils.commandsSystem.builder.CallResponseBuilder;
import botUtils.commandsSystem.builder.FunctionBuilder;
import botUtils.commandsSystem.builder.ResponseBuilder;
import botUtils.commandsSystem.builder.SyntaxBuilder;
import botUtils.tools.Checks;
import botUtils.tools.Colors;
import botUtils.tools.EmbedField;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CommandUtils {

    /**
     * Creates a default {@link CommandManager} which will grab files from the given folder for all its commands. The
     * default prefix used is an exclamation point, and the default command list prompts are "commands" and "command".
     * These can be configured later.
     *
     * @param jda the {@link JDA} instance for this bot
     * @param directory the folder where all the {@link Command} Json is located
     * @param name      the name of the {@link CommandManager}, used when displaying the command list
     * @return the newly created {@link CommandManager}
     */
    public static @NotNull CommandManager createDefaultManager(
            @NotNull JDA jda, @NotNull File directory, @NotNull String name) {
        return CommandManager.of(jda, directory, name);
    }

    /**
     * Creates a default {@link CallResponse} command via the {@link
     * CallResponseBuilder} class and writes it to the given json file. If the file
     * does not have the .json extension, an error will be thrown.
     * <p>
     * This is a default builder, meaning it adds the typical arguments to the JSON utilized by most commands. It
     * omits the more obscure optional flags that can be added later.
     *
     * @param jsonFile the file to write the Json to (must be a .json file)
     * @param name     the name of the command
     * @return the input file after finished writing
     * @throws NullPointerException     if the file or command name are null
     * @throws IllegalArgumentException if the given file is not a json file or the name is empty
     * @throws IOException              if there was an error printing the file
     */
    public static File createDefaultCallResponseCommand(@NotNull File jsonFile, @NotNull String name)
            throws IOException, ClassNotFoundException {
        Checks.checkStringHasContents(name);

        CallResponseBuilder
                .of(name, "default command description; lorem ipsum dolor sit amet")
                .setShortDescription("short description lorem ipsum dolor")
                .addAliases(name + "-alias-1", name + "-alias-2")
                .setDefaultKey("hello")
                .addResponse(ResponseBuilder.of("Hello world!").setKeys("hi", "hello"))
                .build(jsonFile);
        return jsonFile;
    }

    /**
     * Creates a minimal {@link CallResponse} command via the {@link
     * CallResponseBuilder} class and writes it to the given json file. If the file
     * does not have the .json extension, an error will be thrown.
     * <p>
     * This is a minimal builder, meaning it only adds the arguments to the JSON that are absolutely necessary for a
     * command builder. A minimal command won't do anything when you run it.
     *
     * @param jsonFile the file to write the Json to (must be a .json file)
     * @param name     the name of the command
     * @return the input file after finished writing
     * @throws NullPointerException     if the file or command name are null
     * @throws IllegalArgumentException if the given file is not a json file or the name is empty
     * @throws IOException              if there was an error printing the file
     * @throws ClassNotFoundException   if there was an error building the Json
     */
    public static File createMinimalCallResponseCommand(@NotNull File jsonFile, @NotNull String name)
            throws IOException, ClassNotFoundException {
        Checks.checkStringHasContents(name);

        CallResponseBuilder
                .of(name, "minimal command description; lorem ipsum dolor sit amet")
                .build(jsonFile);
        return jsonFile;
    }

    /**
     * Creates a detailed {@link CallResponse} command via the {@link
     * CallResponseBuilder} class and writes it to the given json file. If the file
     * does not have the .json extension, an error will be thrown.
     * <p>
     * This is a detailed builder, meaning it adds every possible argument to the JSON, including all optional ones.
     *
     * @param jsonFile the file to write the Json to (must be a .json file)
     * @param name     the name of the command
     * @return the input file after finished writing
     * @throws NullPointerException     if the file or command name are null
     * @throws IllegalArgumentException if the given file is not a json file or the name is empty
     * @throws IOException              if there was an error printing the file
     * @throws ClassNotFoundException   if there was an error building the Json
     */
    public static File createDetailedCallResponseCommand(@NotNull File jsonFile, @NotNull String name)
            throws IOException, ClassNotFoundException {
        Checks.checkStringHasContents(name);

        CallResponseBuilder
                .of(name, "default command description; lorem ipsum dolor sit amet")
                .setShortDescription("short description lorem ipsum dolor")
                .addAliases(name + "-alias-1", name + "-alias-2")
                .addTypoAliases(name + "-typoalias-1", name + "-typoalias-2")
                .setDefaultKey("hello")
                .setIncludeInCommandsList(true)
                .setLink("https://xkcd.com")
                .addResponse(ResponseBuilder
                        .of("Hello world!").setKeys("hi", "hello"))
                .addResponse(ResponseBuilder
                        .of("Embed Title", "Pong!")
                        .setKeys("ping", "p")
                        .setColor(Colors.PURPLE)
                        .setLink("https://xkcd.com")
                        .setFooterText("Default footer")
                        .setFooterImg("https://upload.wikimedia.org/wikipedia/commons/c/c6/500_x_500_SMPTE_Color_Bars.png")
                        .setImageUrl("https://xkcd.com/s/0b7742.png")
                        .addField(EmbedField.of("Field 1", "Content"))
                        .addField(EmbedField.of("Field 2", "More content")))
                .build(jsonFile);

        return jsonFile;
    }

    /**
     * Creates a default {@link Function} via the {@link
     * FunctionBuilder} class and writes it to the given json file. If the file does
     * not have the .json extension, an error will be thrown.
     * <p>
     * This is a default builder, meaning it adds the typical arguments to the JSON utilized by most commands. It
     * omits the more obscure optional flags that can be added later.
     *
     * @param jsonFile the file to write the Json to (must be a .json file)
     * @param name     the name of the command
     * @return the input file after finished writing
     * @throws NullPointerException     if the file or command name are null
     * @throws IllegalArgumentException if the given file is not a json file or the name is empty
     * @throws IOException              if there was an error printing the file
     */
    public static File createDefaultFunction(@NotNull File jsonFile, @NotNull String name)
            throws IOException, ClassNotFoundException {
        Checks.checkStringHasContents(name);

        FunctionBuilder
                .of(name, "default command description; lorem ipsum dolor sit amet")
                .setShortDescription("short description lorem ipsum dolor")
                .addAliases(name + "-alias-1", name + "-alias-2")
                .addSyntax(SyntaxBuilder.of())
                .build(jsonFile);
        return jsonFile;
    }
}
