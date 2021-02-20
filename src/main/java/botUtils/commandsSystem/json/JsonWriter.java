package botUtils.commandsSystem.json;

import botUtils.tools.Checks;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsonWriter {
    /**
     * Writes the specified {@link JsonElement} to the destination {@link File}. Initially checks to make sure the
     * given {@link File} is not a directory and ends with the .json extension. This method will always pretty print
     * the Json, meaning that will look nice in a text editor with appropriate spaces and indentation.
     *
     * @param json the json element to print
     * @param file the file to print it in
     * @throws IllegalArgumentException if the given output file is not a valid json file
     * @throws NullPointerException     if the {@link File} or {@link JsonElement} is null
     * @throws IOException              if there is an unexpected error writing the file
     */
    public static void writeJson(@NotNull JsonElement json, @NotNull File file)
            throws IOException {
        Checks.fileIsJSON(file);

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
        writer.close();
    }
}