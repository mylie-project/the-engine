package mylie.engine.core;

import mylie.engine.core.features.timer.Timer;

public class Lifecycle {
    public interface InitDestroy {
        void onInit();

        void onDestroy();
    }

    public interface Update {
        void onUpdate();

        interface Timed {
            void onUpdate(Timer.Time time);
        }
    }

    public interface EnableDisable {
        void onEnable();

        void onDisable();

        default void setEnabled(boolean enabled) {
            if (this instanceof BaseFeature baseFeature) {
                baseFeature.requestEnabled(enabled);
            }
        }
    }
}
