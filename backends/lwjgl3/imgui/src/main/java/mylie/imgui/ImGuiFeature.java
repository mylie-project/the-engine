package mylie.imgui;

import mylie.engine.core.AppFeature;
import mylie.engine.core.Feature;
import mylie.engine.core.Lifecycle;

public abstract class ImGuiFeature extends AppFeature.Sequential implements Lifecycle.InitDestroy {
    public ImGuiFeature(Class<? extends Feature> featureType) {
        super(featureType);
    }

    @Override
    public void onDestroy() {}

    @Override
    public void onInit() {
        ImGuiRenderer imGuiRenderer = get(ImGuiRenderer.class);
        if (imGuiRenderer == null) {
            imGuiRenderer = new ImGuiRenderer();
            add(imGuiRenderer);
        }
        imGuiRenderer.addImGuiFeature(this);
    }

    public abstract void renderImGui();
}
