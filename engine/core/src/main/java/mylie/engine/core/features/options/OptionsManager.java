package mylie.engine.core.features.options;

import java.util.HashMap;
import java.util.Map;
import mylie.engine.core.BaseFeature;

public class OptionsManager implements BaseFeature.App {
    final Map<String, Option<?>> options;

    public OptionsManager() {
        this.options = new HashMap<>();
    }
}
