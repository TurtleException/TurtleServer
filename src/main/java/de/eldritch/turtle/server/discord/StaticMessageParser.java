package de.eldritch.turtle.server.discord;

import de.eldritch.turtle.server.TurtleServer;
import net.dv8tion.jda.api.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StaticMessageParser {
    public record Content(MessageBuilder messageBuilder, String[] actionRowKeys, File[] files) { }

    /* ----- ----- ----- */

    public static @NotNull List<Content> parseContent(@NotNull String key) throws IOException, NullPointerException {
        ArrayList<Content> builders = new ArrayList<>();

        for (String instruction : getInstructions(key)) {
            // case: text
            if (instruction.startsWith("T#")) {
                builders.add(new Content(new MessageBuilder(readAsText(key, instruction)), null, null));
                continue;
            }

            // case: file (without message)
            if (instruction.startsWith("F#")) {
                builders.add(new Content(new MessageBuilder(), null, new File[]{readAsFile(key, instruction)}));
                continue;
            }

            // skip if line is not indented (cannot be processed after this)
            if (!(instruction.stripLeading().length() < instruction.length())) continue;

            String instructionWithoutIndent = instruction.stripLeading();

            // case: file (attachment to previous message)
            if (instructionWithoutIndent.startsWith("F#")) {
                File file = readAsFile(key, instructionWithoutIndent);

                // add file to last content obj

                int index = builders.size() - 1;
                Content lastContent = builders.get(index);

                if (lastContent == null)
                    throw new NullPointerException("Could not add attachment without leading message.");

                builders.set(index, new Content(
                        lastContent.messageBuilder(),
                        lastContent.actionRowKeys(),
                        joinFileArr(lastContent.files(), file)
                ));

                continue;
            }

            // case: key to saved action row
            if (instructionWithoutIndent.startsWith("A#")) {
                // add ar key to last content obj

                int index = builders.size() - 1;
                Content lastContent = builders.get(index);

                if (lastContent == null)
                    throw new NullPointerException("Could not add action row key without leading message.");

                String newActionRowKey = stripPrefix(instructionWithoutIndent);

                builders.set(index, new Content(
                        lastContent.messageBuilder(),
                        joinStringArr(lastContent.actionRowKeys(), newActionRowKey),
                        lastContent.files()
                ));
            }
        }

        return builders;
    }

    private static File[] joinFileArr(@Nullable File[] arr, File file) {
        if (arr == null)
            return new File[]{file};
        else {
            File[] newArr = new File[arr.length + 1];
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            newArr[newArr.length - 1] = file;
            return newArr;
        }
    }

    private static String[] joinStringArr(@Nullable String[] arr, String str) {
        if (arr == null)
            return new String[]{str};
        else {
            String[] newArr = new String[arr.length + 1];
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            newArr[newArr.length - 1] = str;
            return newArr;
        }
    }

    private static String readAsText(@NotNull String key, @NotNull String filename) throws IOException {
        File file = new File(getDir(key), stripPrefix(filename));
        return Files.readString(file.toPath());
    }

    private static File readAsFile(@NotNull String key, @NotNull String filename) {
        return new File(getDir(key), stripPrefix(filename));
    }

    private static String stripPrefix(@NotNull String str) {
        if (str.length() < 2)
            return str;

        if (str.charAt(1) != '#')
            return str;
        return str.substring(2);
    }

    private static List<String> getInstructions(@NotNull String key) throws IOException {
        File file = new File(getDir(key), ".index.txt");
        return Files.readAllLines(file.toPath());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File getDir(@NotNull String key) {
        File dir = new File(TurtleServer.DIR, "statics/" + key);

        dir.mkdirs();

        return dir;
    }
}
