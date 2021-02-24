package botUtils.commandsSystem.manager;

import botUtils.commandsSystem.json.JsonParser;
import botUtils.commandsSystem.types.Command;
import botUtils.commandsSystem.types.CommandCallData;
import botUtils.commandsSystem.types.function.Function;
import botUtils.exceptions.JsonParseException;
import botUtils.exceptions.ManagerBuildException;
import botUtils.exceptions.CommandClassException;
import botUtils.exceptions.DuplicateMethodsException;
import botUtils.tools.*;
import botUtils.commandsSystem.types.callResponse.CallResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandManager {
    private final File folder;
    /**
     * This is the name of the {@link CommandManager}. It is shown to the user when they request the command list.
     * <p><br>
     * The name should make sense as a title for a list of commands, as in "{@code [name] Command List}". For example,
     * if this is the only {@link CommandManager} used by the bot you could name it "{@code All}" so that the command
     * list will be titled "{@code All Commands}".
     */
    private final String name;

    /**
     * The {@link JDA} instance for this bot that is used by this {@link CommandManager}.
     */
    private final JDA jda;

    /**
     * This is a list of all the {@link Command} instances created from Json files when building the {@link
     * CommandManager}.
     */
    private final ArrayList<Command> commands = new ArrayList<>();

    /**
     * These are the classes that contain code that a {@link Function} instance can call, as specified in the Json.
     */
    private final List<Class<?>> commandCodeClasses = new ArrayList<>();

    /**
     * This is a list of types of {@link Command} subtypes that can be used by Json files. They must be hardcoded before
     * building as classes that extend {@link Command}. Currently, the two default options are {@link CallResponse} and
     * {@link Function}.
     */
    private final Map<String, Class<? extends Command>> commandTypes = new HashMap<>();

    /**
     * These are all the methods found in the classes in {@link #commandCodeClasses} that are eligible for {@link
     * Function} use.
     */
    private final Map<String, Method> commandCodeMethods = new HashMap<>();

    /**
     * This is a cache of messages the bot sent in Discord that contain a list of commands for this {@link
     * CommandManager}. When a user reacts with emojis to change the page and see more commands, having this message
     * cached is very helpful because it improves the bot's reaction time by not requiring it to retrieve the message
     * from Discord to check to see if it's a command lis or not.
     */
    private final Map<Long, Message> commandListMessageCache = new HashMap<>();

    /**
     * This is updated every time the {@link CommandManager} is built. It stores the number of commands attached to this
     * manager that are eligible for inclusion in the commands list. This is used to determine how many pages are
     * necessary to list all the commands.
     * <p><br>
     * A {@link Command} is determined to be list eligible based on {@link Command#doIncludeInCommandsList()}.
     */
    private int listEligibleCommands;
    // TODO actually set these values when building

    /**
     * Just like {@link #listEligibleCommands}, this is updated every time the {@link CommandManager} is built. It
     * stores the number of pages required to print all the commands in this manager, given {@link
     * ManagerConfig#getCommandsPerPage()}.
     * <p>
     * Only list eligible commands are included in the count for the commands list. A {@link Command} is determined to
     * be list eligible based on {@link Command#doIncludeInCommandsList()}.
     */
    private int totalCommandListPages;

    /**
     * This is the {@link ManagerConfig} configuration class that contains all the settings for this {@link
     * CommandManager}. If you would like to change the settings for this manager, use this configuration. Unlike the
     * {@link #builtConfig}, this configuration does not necessarily reflect the current configuration of the {@link
     * CommandManager}. To update the current configuration to this one, run {@link #build()}.
     */
    private @NotNull ManagerConfig workingConfig = ManagerConfig.of();

    /**
     * This is the list of all the Json files found in the {@link #folder} given to this {@link CommandManager}. It is
     * set when calling {@link #setJsonFiles(File)} in {@link #rawBuild()}.
     */
    private @NotNull
    final ArrayList<File> jsonFiles = new ArrayList<>();

    /**
     * This is a saved non-modifiable version of the {@link ManagerConfig} used by this {@link CommandManager}. It
     * should never be modified with setter methods, and should only be used for the purpose of retrieving existing
     * settings. This configuration instance is only modified with {@link #build()} is called. If you would like to make
     * changes to the configuration for this {@link CommandManager}, retrieve {@link #workingConfig}.
     */
    private ManagerConfig builtConfig;

    private boolean built = false;

    /**
     * @param jda       the {@link JDABuilder} for the bot
     * @param directory the directory to scan for json files
     * @param name      the name of the manager (shown to user when they request a commands list)
     * @param prefixes  the prefixes recognized by this manager
     * @throws IllegalArgumentException if the given file is null or not a directory
     */
    private CommandManager(@NotNull JDA jda, @NotNull File directory, @NotNull String name, @NotNull String[] prefixes) {
        this(jda, directory, name);
        getConfig().setPrefixes(prefixes);
    }

    /**
     * @param jda       the {@link JDABuilder} for the bot
     * @param directory the directory to scan for json files
     * @param name      the name of the manager (shown to user when they request a commands list)
     * @throws IllegalArgumentException if the given file is null or not a directory
     */
    private CommandManager(@NotNull JDA jda, @NotNull File directory, @NotNull String name) {
        Checks.fileIsDirectory(directory);
        this.jda = jda;
        this.folder = directory;
        this.name = name;

        // All the builtin in library classes that extend Command
        commandTypes.put("Function", Function.class);
        commandTypes.put("CallResponse", CallResponse.class);
    }

    /**
     * Create a new {@link CommandManager} instance by specifying the folder where it can find all the Json files with
     * {@link Command} information and the list of recognized prefixes.
     *
     * @param jda       the {@link JDA} instance for this bot
     * @param directory the folder in the project root directory with all the commands to import
     * @param name      the {@link #name} of the {@link CommandManager}
     * @param prefixes  a list of the prefixes this manager should accept
     * @throws IllegalArgumentException if the given {@link File} is null or not a directory
     */
    public static CommandManager of(
            @NotNull JDA jda, @NotNull File directory, @NotNull String name, @NotNull String[] prefixes) {
        return new CommandManager(jda, directory, name, prefixes);
    }

    /**
     * Create a new {@link CommandManager} instance by specifying the folder where it can find all the Json files with
     * {@link Command} information.
     *
     * @param jda       the {@link JDA} instance for this bot
     * @param directory the folder in the project root directory with all the commands to import
     * @param name      the {@link #name} of the {@link CommandManager}
     * @throws IllegalArgumentException if the given {@link File} is null or not a directory
     */
    public static CommandManager of(@NotNull JDA jda, @NotNull File directory, @NotNull String name) {
        return new CommandManager(jda, directory, name);
    }

    /**
     * Get the {@link ManagerConfig} configuration class where you can change all the settings for this {@link
     * CommandManager}. Unless you specified a set of prefixes while creating this {@link CommandManager}, the {@link
     * ManagerConfig} will contain entirely default settings when you first call it after making a new manager.
     *
     * @return the {@link ManagerConfig} associated with this {@link CommandManager}
     */
    public @NotNull ManagerConfig getConfigManager() {
        return this.workingConfig;
    }

    /**
     * Gets either the {@link #builtConfig} if the bot has been built or the {@link #workingConfig} if it hasn't been
     * built yet.
     * <p><br>
     * <b>WARNING: Only use this method if you need the currently active {@link ManagerConfig} instance.</b>
     * You should almost certainly be using {@link #getConfigManager()} instead of this method. Only use this if you
     * know exactly what you're doing. If you use this method it should only be for the purpose of
     * <i>retrieving</i> current bot settings. Under no circumstances should you <i>modify</i> settings in the
     * {@link ManagerConfig} returned by this method.
     *
     * @return the correct {@link ManagerConfig} to use right now
     */
    @NotNull
    public ManagerConfig getConfig() {
        return built ? builtConfig : workingConfig;
    }

    /**
     * Returns the {@link #name} of this {@link CommandManager}.
     *
     * @return the name
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Returns the text that is placed in the title of the {@link Command} list for this {@link CommandManager}. It is
     * based on the {@link #name} of the manager and the title setting from {@link ManagerConfig#getCommandListTitle()}.
     * <p><br>
     * The default title, assuming it wasn't changed by the user, will be the name of the manager followed by "Command
     * List".
     * <p><b>{@code [name] Command List}</b>
     *
     * @return the title of the command list
     */
    public @NotNull String getCommandListTitle() {
        return name + " " + getConfig().getCommandListTitle();
    }

    /**
     * Returns the {@link #jda} of this {@link CommandManager}.
     *
     * @return the bot jda
     */
    public @NotNull JDA getJda() {
        return jda;
    }

    /**
     * Get a method that has code for a {@link Command} based on its name (which should be the name of the command
     * itself). This is simply grabbing a method from the {@link #commandCodeMethods} {@link HashMap}, which was defined
     * through {@link #build()}. It contains methods extracted from classes added to this {@link CommandManager} with
     * {@link #addCommandCodeClass}.
     *
     * @param name the name of the method
     * @return the method with a matching name, or null if no matching method was found
     */
    public @Nullable Method getCommandCodeMethod(@NotNull String name) {
        return commandCodeMethods.get(name);
    }

    /**
     * Convenience method that called {@link #getCommandCodeMethod(String)} based on the name of the given {@link
     * Command} (converted to lowercase). This simply finds a method that was given to this {@link CommandManager} that
     * has the same name as the specified command.
     *
     * @param command the command to look for
     * @return the method with a matching name, or null if no matching method was found
     */
    public @Nullable Method getCommandCodeMethod(@NotNull Command command) {
        return getCommandCodeMethod(command.getName().toLowerCase(Locale.ROOT));
    }

    /**
     * Overwrites the {@link ManagerConfig} associated with this {@link CommandManager} with a new configuration object
     * defined elsewhere. All original settings will be lost, including prefixes if they were set with {@link #of(JDA,
     * File, String, String[])} while creating this {@link CommandManager}.
     *
     * @param workingConfig the new configuration settings instance
     * @return this {@link CommandManager} instance for chaining
     */
    public @NotNull CommandManager setConfigManager(@NotNull ManagerConfig workingConfig) {
        this.workingConfig = workingConfig;
        return this;
    }

    /**
     * Returns the first and main prefix accepted by this {@link CommandManager}
     *
     * @return the main command prefix
     */
    public @NotNull String getMainPrefix() {
        return getConfig().getPrefixes()[0];
    }

    /**
     * Get the list of {@link Command} objects imported from Json files that are in this {@link CommandManager}. This
     * will not work if the manager has not yet been built with {@link #build()}.
     *
     * @return the list of commands.
     * @throws IllegalStateException if the {@link CommandManager} is not yet built
     */
    public @NotNull ArrayList<Command> getCommands() {
        Checks.commandManagerBuildState(this, true);
        return commands;
    }

    /**
     * Returns the {@link #commandListMessageCache}.
     *
     * @return the cache of command list messages
     */
    @NotNull Map<Long, Message> getCommandListMessageCache() {
        return commandListMessageCache;
    }

    /**
     * Checks whether the given command string uses one of the prefixes supported by this {@link CommandManager}. If it
     * does, a new String is returned with that prefix removed. If it doesn't, null is returned. Note that prefix checks
     * are case in/sensitive depending on the setting in {@link ManagerConfig}.
     *
     * @param command the command to check for a prefix
     * @return the command sans the prefix if there was one; null if there was no prefix
     * @throws IllegalStateException if the {@link CommandManager} is not yet built
     */
    @Deprecated
    private String usesPrefix(@NotNull String command) {
        Checks.commandManagerBuildState(this, true);
        String commandMatch = getConfig().isPrefixCaseSensitive() ? command : command.toLowerCase(Locale.ROOT);

        for (String prefix : getConfig().getPrefixes())
            if (commandMatch.startsWith(getConfig().isPrefixCaseSensitive() ? prefix : prefix.toLowerCase(Locale.ROOT)))
                return command.substring(prefix.length());

        return null;
    }

    /**
     * Add a class with code for commands. When {@link #build()} is called all the methods from the code classes will be
     * identified. Whenever a user requests a command, the bot will run the method with the same name as the requested
     * command (as defined by the "name" key in the Json). The input types of the method must be the same as required by
     * the Command subclass it uses.
     *
     * @return this {@link CommandManager} instance for chaining
     */
    public CommandManager addCommandCodeClass(@NotNull Class<?> c) {
        this.commandCodeClasses.add(c);
        return this;
    }

    /**
     * Same as {@link #addCommandCodeClass(Class)} except that an instantiated {@link Object} is taken as input rather
     * than the object's class. This simply obtains the class and redirects the method call.
     *
     * @param o the object type
     * @return this {@link CommandManager} instance for chaining
     */
    public CommandManager addCommandCodeClass(@NotNull Object o) {
        this.commandCodeClasses.add(o.getClass());
        return this;
    }

    /**
     * Removes all added code classes. This will not take affect until the {@link CommandManager} is built.
     *
     * @return this {@link CommandManager} instance for chaining
     */
    public CommandManager clearCommandCodeClasses() {
        this.commandCodeClasses.clear();
        return this;
    }

    /**
     * Add a Command subclass that can be instantiated by a Json file with a "type" argument that matches the Class's
     * name. This class must extend the Command class and not be null. Do not add the Function or CallResponse classes
     * as they are included by default. This will take effect the next time the CommandManager is built with {@link
     * #build()}.
     *
     * @param command the Command subclass to add
     * @return this {@link CommandManager} instance for chaining
     */
    public CommandManager addCommandType(Class<? extends Command> command) {
        commandTypes.put(command.getName(), command);
        return this;
    }

    /**
     * Each {@link CommandManager} has a set of keys and values corresponding to strings that are replaced in Json. Give
     * this method a raw {@link String} that was just read from Json and it will search for all instances of keys in the
     * string, replace them with their corresponding values, and return the finished modified {@link String}. If there
     * are no key value pairs for this {@link CommandManager}, the input string is returned unmodified.
     * <p>
     * Since the replacement pairs are stored as a {@link LinkedHashMap}, replacements are executed in the order they
     * were added. It is possible for the replacement of one pair to affect whether another pair would find a match. For
     * example, if the key 'foobar' and value 'hello' were added, followed by the key 'hello world' and value 'hi', an
     * input string of 'foobar world' would become 'hello world' and then 'hi' as each pair was evaluated.
     *
     * @param input the input {@link String} to modify and check for matching keys (case sensitive)
     * @return the modified output {@link String}
     */
    public String runStringReplacement(String input) {
        if (getConfig().doPrefixJsonReplacement())
            input = input.replace("$PREFIX$", getMainPrefix());
        for (String key : getConfig().getJsonReplacements().keySet())
            input = input.replace(key, getConfig().getJsonReplacements().get(key));
        return input;
    }

    /**
     * Gets all the methods from each of the commandCodeClasses and puts it in the methods {@link HashMap}. The methods
     * map is cleared beforehand to ensure that doubly building the CommandManager will not add double the methods. Note
     * that method names are not case sensitive. They are converted to lowercase for matching.
     */
    private void setCodeMethods() throws DuplicateMethodsException {
        commandCodeMethods.clear();
        for (Class<?> c : commandCodeClasses)
            for (Method m : c.getDeclaredMethods())
                if (isMethodStatic(m) && isMethodParamCorrect(m))
                    if (commandCodeMethods.containsKey(m.getName()))
                        throw new DuplicateMethodsException(
                                "More than one command method with same name: " + m.getName());
                    else
                        commandCodeMethods.put(m.getName().toLowerCase(Locale.ROOT), m);
    }

    /**
     * Checks to see if the given object is both public and static, and thus accessible by any object.
     *
     * @param method the method to check
     * @return true if it is public and static; false otherwise
     */
    private boolean isMethodStatic(@NotNull Method method) {
        try {
            return method.canAccess(null);
        } catch (IllegalArgumentException ignore) {
            return false;
        }
    }

    /**
     * Checks to see if the given method takes a single parameter, that being a {@link CommandCallData} or subclass
     * thereof.
     *
     * @param method the method to check
     * @return true if it takes the proper argument; false otherwise
     */
    private boolean isMethodParamCorrect(@NotNull Method method) {
        Class<?>[] params = method.getParameterTypes();
        return params.length == 1 && CommandCallData.class.isAssignableFrom(params[0]);
    }

    /**
     * Grabs all the Json files in the given root folder and adds them to {@link #jsonFiles}. This method runs
     * recursively on all sub folders as well.
     *
     * @param root the root folder to begin searching from
     */
    private void setJsonFiles(@NotNull File root) {
        File[] files = root.listFiles();

        for (File file : files)
            if (file.isDirectory())
                setJsonFiles(file);
            else if (Checks.fileIsJsonBool(file))
                jsonFiles.add(file);
    }

    /**
     * Finishes creating this {@link CommandManager} instance from all the settings. All the Commands are read from Json
     * files in the commands folder (and sub-folders) belonging to this {@link CommandManager} and they are assigned to
     * their respective classes.
     * <p>
     * This method can be called multiple times to reload the {@link CommandManager} and reimport all the commands from
     * the Json during a bot reload.
     * <p>
     * Note that because calling this method requires the {@link ManagerConfig} to be cloned to create a static copy of
     * settings, this should be called sparingly.
     *
     * @return the newly built {@link CommandManager} instance for chaining
     * @throws ManagerBuildException if there is any error building the manager. This exception is designed to wrap all
     *                               the possible errors that can be thrown, and it should be printed to the console as
     *                               it means the {@link CommandManager} was not built.
     */
    public CommandManager build() throws ManagerBuildException {
        try {
            return rawBuild();
        } catch (IllegalStateException | CloneNotSupportedException | DuplicateMethodsException e) {
            throw new ManagerBuildException(e);
        }
    }

    /**
     * Does the actual building of a {@link CommandManager}. Errors thrown here are caught by the surrounding {@link
     * #build()} method and returned encased in a {@link ManagerBuildException}.
     *
     * @return the newly built {@link CommandManager} instance for chaining
     * @throws IllegalStateException      if the CommandManager is already built
     * @throws DuplicateMethodsException  if multiple methods in classes added through {@link #addCommandCodeClass} have
     *                                    the same name
     * @throws CloneNotSupportedException if there is an error cloning the {@link ManagerConfig} config instance
     */
    private CommandManager rawBuild() throws CloneNotSupportedException, DuplicateMethodsException {
        Checks.commandManagerBuildState(this, false);
        System.out.println("Building CommandManager from folder '" + folder.getPath() + "'...");

        // TODO check to see if there are duplicate aliases or typo aliases across all commands
        builtConfig = getConfig().clone();

        // Reset the list of command list messages
        commandListMessageCache.clear();
        listEligibleCommands = 0;

        // Get a list of all the Methods that are eligible to be called by Commands
        setCodeMethods();

        // Clear the Commands and get a list of all the command Json files in the folder for this manager
        commands.clear();
        jsonFiles.clear();
        setJsonFiles(folder);

        ArrayList<String> errors = new ArrayList<>();
        FileReader r = null;
        Gson gson = new Gson();

        // Iterate through each of the Json files
        for (File f : jsonFiles)

            // Try to parse it
            try {
                r = new FileReader(f);
                JsonObject json = gson.fromJson(r, JsonObject.class);
                commands.add(buildCommand(json));

                // Check if the command is eligible for being in the commands list and if so update the count
                if (commands.get(commands.size() - 1).doIncludeInCommandsList())
                    listEligibleCommands++;

            } catch (Exception e) {
                // Record any errors for the summary report sent later
                errors.add(e.getClass().getName() + " in " + f.getName());
                e.printStackTrace();
            } finally {
                // Close the FileReader before moving on to the next Json file
                try {
                    assert r != null;
                    r.close();
                } catch (Exception ignore) {
                }
            }

        // Send the summary of errors report
        StringBuilder errorResult = new StringBuilder("Loaded " + commands.size() + " commands with " +
                errors.size() + " errors" + (errors.size() == 0 ? "" : ":"));
        for (String error : errors)
            errorResult.append("\n   ").append(error);

        System.out.println(errorResult);

        totalCommandListPages = (int) Math.ceil(listEligibleCommands / (double) builtConfig.getCommandsPerPage());

        // The CommandManager is now built
        built = true;
        return this;
    }

    /**
     * Gets the current build state of this {@link CommandManager}. When this manager is built, it does not accept
     * updates to the settings. They will be ignored until the manager is rebuilt.
     *
     * @return true if it is build and false otherwise
     */
    public boolean isBuilt() {
        return built;
    }

    /**
     * Unbuilds this {@link CommandManager}. This prevents it from executing any Discord commands but allows its
     * settings to be modified through the {@link ManagerConfig}. You can adjust these settings by getting the
     * configuration object with {@link #getConfigManager()}.
     *
     * @return this {@link CommandManager} instance for chaining
     */
    public CommandManager unbuild() {
        built = false;
        return this;
    }

    /**
     * Takes a {@link JsonObject} read from a Json file and uses it to build a {@link Command}. It grabs the Class type
     * from the Json and finds a {@link Command} subclass with a matching name from {@link #commandTypes} (case
     * sensitive) to instantiate. If there is an error getting the required type from the Json or a class with the right
     * name is not found, an exception is thrown.
     *
     * @param json the JsonObject imported from a file
     * @return the newly created {@link Command} subclass instance
     * @throws JsonParseException    if there is an error getting the command type from the Json
     * @throws CommandClassException if the type in the Json is not a valid {@link Command} subclass
     * @throws Exception             if there is some other problem instantiating the new {@link Command} object
     */
    private Command buildCommand(JsonObject json) throws Exception {
        String type = JsonParser.getString(json, "type");
        Class<? extends Command> commandType = commandTypes.get(type);

        if (commandType == null)
            throw new CommandClassException("   Type '" + type + "' possibly not registered with CommandManager.\n" +
                    "   Try registering it with addCommandType(Class<? extends Command>).");

        try {
            Constructor<? extends Command> constructor = commandType.getConstructor(
                    JsonObject.class, CommandManager.class);
            return constructor.newInstance(json, this);
        } catch (NoSuchMethodException e) {
            throw new CommandClassException("   '" + type + "' command class does not have a valid constructor.\n" +
                    "   It must accept a JsonObject and CommandManager as parameters in that order.");
        } catch (InvocationTargetException e) {
            // If a console error brought you here, it most likely means there is something wrong with the Json file.
            // Check the error message for assistance, or follow the stack trace to wherever the error was thrown
            // before this location. If the error occurred in the try-catch immediately beneath this comment, then that
            // probably means somehow initializing a constructor threw an invalid InvocableTargetException. That
            // shouldn't have even been possible. ¯\_(ツ)_/¯
            Exception f;
            try {
                f = (Exception) e.getCause();
            } catch (Exception ignore) {
                throw e;
            }
            throw f;
        }
    }

    /**
     * Sends a temporary error message to a user via an {@link ErrorBuilder} passed to {@link #sendError(MessageChannel,
     * ErrorBuilder)}.
     *
     * @param channel the channel to send the error in
     * @param error   the message to send the user (accepts Discord formatting)
     */
    public void sendError(@NotNull MessageChannel channel, @NotNull String error) {
        sendError(channel, ErrorBuilder.of(error, builtConfig.getErrorColor()));
    }

    /**
     * Sends an {@link ErrorBuilder} error message to the specified {@link MessageChannel}. The color and possibly
     * temporary state of the error is determined by the configuration for this {@link CommandManager}.
     * <p><br>
     * Note that errors sent through this method are sent as temporary/permanent errors depending on the configuration
     * setting in {@link ManagerConfig#areErrorsTemporary()}. To override this you'll have to make your own send method
     * call through an {@link ErrorBuilder}.
     *
     * @param channel the channel to send the error in
     * @param error   the error to send
     */
    public void sendError(@NotNull MessageChannel channel, @NotNull ErrorBuilder error) {
        if (builtConfig.areErrorsTemporary())
            error.sendTemp(channel, TempMsgConfig.DEFAULT_SPEED);
        else
            error.send(channel);
    }

    /**
     * Get a list of {@link Command} instances corresponding to the given page. This is called when printing command
     * lists in Discord.
     * <p><br>
     * <u>Precondition:</u> because this method is called mainly by {@link #getCommandListPage(int)}, it is
     * assumed that the given page number is valid, and thus it is not checked to see if it's out of bounds. It is also
     * assumed that the {@link CommandManager} has been built with {@link #build()}.
     *
     * @param pageNum the number of the page to grab (1 indexed)
     * @return the list of commands on the given page
     */
    private @NotNull ArrayList<Command> getCommandsList(int pageNum) {
        int pageLen = builtConfig.getCommandsPerPage();

        int first = pageLen * (pageNum - 1);
        int i = 0;
        ArrayList<Command> list = new ArrayList<>();

        while (i < commands.size()) {
            if (i >= first && commands.get(i).doIncludeInCommandsList())
                list.add(commands.get(i));
            if (list.size() == pageLen)
                break;
            i++;
        }

        return list;
    }

    /**
     * Returns an {@link EmbedBuilder} containing a list of commands for this {@link CommandManager} at the specified
     * page number. This method simply retrieves the list of commands from {@link #getCommandsList(int)} and formats
     * them nicely in an {@link EmbedBuilder}.
     * <p><br>
     * If the given page number is less than 1, the last page will be returned. If the given page number is greater than
     * the number of pages, the first page will be returned. The number of commands on a page is determined by {@link
     * ManagerConfig#getCommandsPerPage()}.
     *
     * @param curPage the page of commands (1 indexed) that is passed to {@link #getCommandsList(int)}
     * @return an EmbedBuilder with the commands listed out
     * @throws IllegalStateException if the CommandManager has not yet been built with {@link #build()}.
     */
    public @NotNull EmbedBuilder getCommandListPage(int curPage) {
        Checks.commandManagerBuildState(this, true);

        // If the pageNum is out of bounds, put it in bounds (with wrapping)
        curPage = curPage < 1 ? totalCommandListPages : curPage > totalCommandListPages ? 1 : curPage;

        EmbedField[] fields = getCommandsList(curPage)
                .stream()
                .map(command -> EmbedField.of(command.getName(), command.getShortDescription()))
                .toArray(EmbedField[]::new);

        String footer = builtConfig.getCommandListFooter() == null ? "" : " | " + builtConfig.getCommandListFooter();

        if (fields.length == 0)
            return MessageUtils.makeEmbedBuilder(
                    getCommandListTitle(),
                    "",
                    "There are no commands to list.",
                    builtConfig.getCommandListColor(),
                    "Page 1 of 1" + footer,
                    builtConfig.getCommandListFooterImg(),
                    fields);
        else
            return MessageUtils.makeEmbedBuilder(
                    getCommandListTitle(),
                    "",
                    getConfig().getCommandListDescription(),
                    builtConfig.getCommandListColor(),
                    "Page " + curPage + " of " + totalCommandListPages + footer,
                    builtConfig.getCommandListFooterImg(),
                    fields);
    }

    /**
     * This is a convenience method to call {@link #getCommandListPage(int)} for the first page, build it, and send it
     * in the specified channel. The resulting message is also added to the {@link #commandListMessageCache} for this
     * {@link CommandManager}, and the appropriate arrow emojis for changing pages are added (but only if there's more
     * than one page for the command list).
     * <p><br>
     * <u>Precondition:</u> this method assumes that the {@link CommandManager} was built with {@link
     * CommandManager#build()}.
     *
     * @param channel the channel to send the command list in
     */
    public void sendCommandList(@NotNull MessageChannel channel) {
        channel.sendMessage(getCommandListPage(1).build()).queue(
                m -> {
                    commandListMessageCache.put(m.getIdLong(), m);
                    if (totalCommandListPages > 1) {
                        JDAUtils.react(m, builtConfig.getLeftArrowEmoji());
                        JDAUtils.react(m, builtConfig.getRightArrowEmoji());
                    }
                });
    }

    /**
     * Process a JDA event with this {@link CommandManager}. If the event type is recognized and the bot does something
     * in Discord in response, {@code true} is returned. Otherwise, {@code false} is returned to indicate that the event
     * wasn't recognized.
     * <p><br>
     * Currently {@link CommandManager} instances support {@link GuildMessageReceivedEvent}, {@link
     * PrivateMessageReceivedEvent}, {@link GuildMessageReactionAddEvent}, and {@link MessageReactionAddEvent}. All
     * other event types are ignored.
     * <p><br>
     * This method is simply a redirect to {@link CommandRunner#run(CommandManager, Event)}. See that method for
     * additional documentation.
     *
     * @param event the event being passed to this {@link CommandManager}
     * @param <T>   type parameter
     * @return true if something is done as a result of the event; false otherwise
     * @throws IllegalStateException if the CommandManager has not yet been built with {@link CommandManager#build()}.
     */
    public <T extends Event> boolean run(@NotNull T event) {
        return CommandRunner.run(this, event);
    }
}