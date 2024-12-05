package mylie.engine.graphics;

import java.util.List;
import mylie.engine.core.BaseFeature;

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
}
