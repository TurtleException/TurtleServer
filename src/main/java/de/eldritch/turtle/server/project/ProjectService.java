package de.eldritch.turtle.server.project;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Service responsible for managing all cached {@link Project} objects.
 * <p>This class exists to simplify the general application structure by sacrificing cohesion for clarity.
 */
public class ProjectService {
    private final ConcurrentHashMap<Integer, Project> projectCache = new ConcurrentHashMap<>();

    private final NestedToggleLogger logger = new NestedToggleLogger("ProjectService", TurtleServer.LOGGER);

    public ProjectService() {
        this.reloadProjects();
    }

    public void shutdown() {
        // TODO: save projects to DB
    }

    /* ----- ----- ----- */

    public void reloadProjects() {
        List<Project> projects = Main.singleton.getDataService().getProjects();

        if (projects != null) {
            // invalidate cache
            projectCache.clear();

            // feed user objects to cache
            for (Project project : projects)
                projectCache.put(project.getID(), project);
        } else {
            logger.log(Level.WARNING, "Could not reload projects!");
        }
    }
}
