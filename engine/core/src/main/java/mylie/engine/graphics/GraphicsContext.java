package mylie.engine.graphics;

import lombok.Getter;

@Getter
public abstract class GraphicsContext {
    GraphicsContextSettings settings;

    protected abstract void applySettings(GraphicsContextSettings graphicsContextSettings);
}
