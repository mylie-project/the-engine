package mylie.engine.core;

import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Result;
import mylie.util.configuration.Configuration;

public class FeatureBarrier extends CoreFeature implements BaseFeature.App {

    protected FeatureBarrier(Class<? extends Feature> featureType) {
        super(featureType);
        executionMode(Async.Mode.Async);
    }

    public static class FramePreparation extends FeatureBarrier {

        protected FramePreparation() {
            super(FramePreparation.class);
            executionMode(Async.Mode.Direct);
        }

        @Override
        Result<Boolean> update() {
            return super.update();
        }
    }

    public static class AppLogic extends FeatureBarrier {

        protected AppLogic() {
            super(AppLogic.class);
        }

        @Override
        protected void onSetup(
                FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
            super.onSetup(featureManager, engineConfiguration);
            runAfter(FramePreparation.class);
        }
    }

    public static class AppRendering extends FeatureBarrier {
        protected AppRendering() {
            super(AppRendering.class);
        }

        @Override
        protected void onSetup(
                FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
            super.onSetup(featureManager, engineConfiguration);
            runAfter(AppLogic.class);
        }
    }

    public static class FrameComplete extends FeatureBarrier {
        protected FrameComplete() {
            super(AppRendering.class);
        }

        @Override
        protected void onSetup(
                FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
            super.onSetup(featureManager, engineConfiguration);
            runAfter(AppRendering.class);
        }
    }

    public static void initDefaults(FeatureManager featureManager) {
        featureManager.add(new FramePreparation());
        featureManager.add(new AppLogic());
        featureManager.add(new AppRendering());
        featureManager.add(new FrameComplete());
    }
}
