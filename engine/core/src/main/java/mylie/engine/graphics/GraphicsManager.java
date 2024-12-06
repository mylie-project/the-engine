package mylie.engine.graphics;

import java.util.List;
import mylie.engine.core.BaseFeature;
import mylie.engine.core.features.async.Result;

public interface GraphicsManager extends BaseFeature.App {
    List<Graphics.Display> availableDisplays();

    default Graphics.Display primaryDisplay() {
        for (Graphics.Display availableDisplay : availableDisplays()) {
            if (availableDisplay.primary()) {
                return availableDisplay;
            }
        }
        throw new RuntimeException("No primary display found");
    }

    GraphicsContext createContext(GraphicsContext.Configuration contextSettings, boolean synced);

    Result<Boolean> destroyContext(GraphicsContext context);
}
