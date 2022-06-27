package de.eldritch.turtle.server.util;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.TurtleServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class IOUtil {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveResource(String path) throws IOException {
        InputStream stream = getResource(path);

        // fail silently
        if (stream == null) return;

        File file = new File(TurtleServer.DIR, path);
        if (file.exists()) {
            // fail silently
            return;
        } else {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        stream.transferTo(new FileOutputStream(file, false));
    }

    public static @Nullable InputStream getResource(@NotNull String file) {
        if (file.equalsIgnoreCase(""))
            throw new IllegalArgumentException("File may not be empty string");

        URL url = Main.class.getClassLoader().getResource(file);

        if (url == null) return null;

        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            return urlConnection.getInputStream();
        } catch (IOException e) {
            TurtleServer.LOGGER.log(Level.FINE, "Could not provide resource '" + file + "'", e);
            return null;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static @NotNull File getFile(@NotNull String path) throws IOException {
        try {
            saveResource(path);
        } catch (Exception ignored) { }

        File file = new File(TurtleServer.DIR, path);

        if (file.exists()) {
            if (!file.isFile())
                throw new IOException(file + " is not a file");
        } else {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        return file;
    }
}
