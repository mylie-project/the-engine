package mylie.imgui;

import imgui.ImDrawData;
import imgui.ImGui;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.ImPlotContext;
import imgui.gl3.ImGuiImplGl3;
import imgui.internal.ImGuiContext;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.graphics.ContextProperties;
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.input.Input;
import mylie.engine.input.InputEvent;
import mylie.util.Versioned;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

@Slf4j
@Getter
@Setter(AccessLevel.PACKAGE)
public class ImGuiRenderContext {
    final GraphicsContext graphicsContext;
    final Queue<InputEvent> inputEvents;
    final Versioned.Reference<Vector2ic> frameBufferSize;
    ImGuiContext imGuiContext;
    ImPlotContext imPlotContext;
    ImGuiImplGl3 imGuiImplGl3;
    final List<ImGuiFeature> imGuiFeatureList;

    public ImGuiRenderContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        this.inputEvents = new LinkedList<>();
        this.imGuiFeatureList = new CopyOnWriteArrayList<>();
        imGuiContext(ImGui.createContext());
        imPlotContext(ImPlot.createContext());
        imGuiImplGl3(new ImGuiImplGl3());
        this.frameBufferSize = ContextProperties.FrameBufferSize.get(graphicsContext);
        Async.async(Async.Mode.Async, Cache.OneFrame, graphicsContext.target(), -1, InitContext, this)
                .get();
    }

    public void addImGuiFeature(ImGuiFeature feature) {
        imGuiFeatureList.add(feature);
    }

    public void removeImGuiFeature(ImGuiFeature feature) {
        imGuiFeatureList.remove(feature);
    }

    protected void render(Timer.Time time) {
        ImGui.setCurrentContext(imGuiContext);
        ImPlot.setCurrentContext(imPlotContext);
        Vector2ic vector2ic = this.frameBufferSize.get();
        ImGui.getIO().setDisplaySize(vector2ic.x(), vector2ic.y());
        while (!inputEvents.isEmpty()) {
            processInputEvent(inputEvents.poll());
        }
        ImGui.newFrame();
        for (ImGuiFeature imGuiFeature : imGuiFeatureList) {
            imGuiFeature.renderImGui();
        }
        // ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();
        ImDrawData drawData = ImGui.getDrawData();
        Async.async(
                Async.Mode.Async, Cache.OneFrame, graphicsContext.target(), time.frameId(), RenderData, this, drawData);
    }

    public static final Functions.F1<Boolean, ImGuiRenderContext, ImDrawData> RenderData =
            new Functions.F1<>("RenderData") {

                @Override
                protected Boolean run(ImGuiRenderContext renderContext, ImDrawData drawData) {
                    GL12.glClearColor(0, 0, 0, 0);
                    GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    renderContext.imGuiImplGl3().renderDrawData(drawData);
                    return true;
                }
            };

    public static final Functions.F0<Boolean, ImGuiRenderContext> InitContext = new Functions.F0<>("InitContext") {
        @Override
        protected Boolean run(ImGuiRenderContext context) {
            context.imGuiImplGl3().init();
            context.imGuiImplGl3().newFrame();
            return true;
        }
    };

    private void processInputEvent(InputEvent event) {
        ImGui.setCurrentContext(imGuiContext);
        if (event instanceof InputEvent.Mouse.Cursor cursorMotionEvent) {
            Vector2ic position = cursorMotionEvent.position();
            ImGui.getIO().addMousePosEvent(position.x(), position.y());
        }
        if (event instanceof InputEvent.Mouse.Button buttonEvent) {
            int buttonId = convertMouseButton(buttonEvent.button());
            if (buttonId != -1) {
                ImGui.getIO().addMouseButtonEvent(buttonId, buttonEvent.type() == InputEvent.Mouse.Button.Type.PRESSED);
            }
        }
        if (event instanceof InputEvent.Mouse.Wheel wheelEvent) {
            if (wheelEvent.axis() == InputEvent.Mouse.Wheel.WheelAxis.Y) {
                ImGui.getIO().addMouseWheelEvent(0, wheelEvent.amount());
            }
        }
        if (event instanceof InputEvent.Keyboard.Text textEvent) {
            ImGui.getIO().addInputCharacter(textEvent.text());
        }
        if (event instanceof InputEvent.Keyboard.Key keyEvent) {
            ImGui.getIO()
                    .addKeyEvent(
                            ImGuiUtil.toImgui(keyEvent.key()), keyEvent.type() == InputEvent.Keyboard.Key.Type.PRESSED);
        }
    }

    private int convertMouseButton(Input.MouseButton button) {
        if (button == Input.MouseButton.LEFT) {
            return 0;
        }
        if (button == Input.MouseButton.RIGHT) {
            return 1;
        }
        if (button == Input.MouseButton.MIDDLE) {
            return 2;
        }
        return -1;
    }
}
