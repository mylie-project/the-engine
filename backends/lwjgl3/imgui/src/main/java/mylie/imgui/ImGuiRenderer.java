package mylie.imgui;

import java.util.HashMap;
import java.util.Map;
import mylie.engine.core.AppFeature;
import mylie.engine.core.Engine;
import mylie.engine.core.Lifecycle;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputManager;
import mylie.engine.input.listeners.RawInputListener;

public class ImGuiRenderer extends AppFeature.Async
        implements Lifecycle.Update.Timed, Lifecycle.InitDestroy, RawInputListener {
    private final Map<GraphicsContext, ImGuiRenderContext> renderContexts;

    public ImGuiRenderer() {
        super(ImGuiRenderer.class);
        renderContexts = new HashMap<>();
    }

    @Override
    protected void onSetupDependencies() {
        runAfter(Engine.Barriers.ApplicationLogic);
        runBefore(Engine.Barriers.AppRendering);
    }

    public void addRenderContext(GraphicsContext context) {
        renderContexts.put(context, new ImGuiRenderContext(context));
    }

    @Override
    public void onUpdate(Timer.Time time) {
        renderContexts.forEach((context, renderContext) -> {
            renderContext.render(time);
        });
    }

    @Override
    public void onInit() {
        get(InputManager.class).addInputListener(this);
    }

    @Override
    public void onDestroy() {
        get(InputManager.class).removeInputListener(this);
    }

    @Override
    public void onEvent(InputEvent event) {
        ImGuiRenderContext imGuiRenderContext = renderContexts.get(event.graphicsContext());
        if (imGuiRenderContext != null) {
            imGuiRenderContext.inputEvents().add(event);
        }
    }
}
