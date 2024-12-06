package mylie.examples.utils;

import mylie.engine.graphics.GraphicsContext;

public class IconFactory {
    public static GraphicsContext.Icons getDefaultIcons() {
        String basePath = "icons/default/";
        String ending = ".png";
        String[] variance = new String[] {"16", "24", "48", "128"};
        String[] paths = new String[variance.length];
        for (int i = 0; i < variance.length; i++) {
            paths[i] = basePath + variance[i] + ending;
        }
        return new GraphicsContext.Icons(paths);
    }
}
