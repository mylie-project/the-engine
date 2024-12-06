package mylie.engine.graphics;

import java.util.List;
import mylie.engine.core.BaseFeature;
import mylie.engine.core.features.async.Result;

/**
 * The GraphicsManager interface provides a set of methods for managing graphics displays and contexts.
 * GraphicsManager allows querying available displays, creating graphics contexts, and destroying them.
 */
public interface GraphicsManager extends BaseFeature.App {
    /**
     * Retrieves a list of available graphics displays.
     *
     * @return a list of {@link Graphics.Display} representing all currently available displays.
     */
    List<Graphics.Display> availableDisplays();

    /**
     * Returns the primary display from the list of available displays.
     *
     * @return the primary {@link Graphics.Display} if it exists among the available displays
     * @throws RuntimeException if no primary display is found in the available displays
     */
    default Graphics.Display primaryDisplay() {
        for (Graphics.Display availableDisplay : availableDisplays()) {
            if (availableDisplay.primary()) {
                return availableDisplay;
            }
        }
        throw new RuntimeException("No primary display found");
    }

    /**
     * Creates a new graphics context based on the provided configuration settings.
     *
     * @param contextSettings the configuration settings to use for the new graphics context
     * @param synced if true, the context operations will be synchronized to avoid concurrency issues
     * @return a newly created {@link GraphicsContext} based on the specified configuration
     */
    GraphicsContext createContext(GraphicsContext.Configuration contextSettings, boolean synced);

    /**
     * Destroys the specified graphics context. This method releases any resources associated
     * with the context and makes it unavailable for further rendering operations.
     *
     * @param context the {@link GraphicsContext} to be destroyed
     * @return a {@link Result} containing a Boolean indicating whether the context
     * was successfully destroyed. Returns true if the destruction was successful, or false otherwise.
     */
    Result<Boolean> destroyContext(GraphicsContext context);
}
