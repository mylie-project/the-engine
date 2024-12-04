package mylie.engine.core;

import mylie.engine.core.features.timer.Timer;

public interface Feature {

    interface Lifecycle {
        interface InitDestroy {
            void onInit();

            void onDestroy();
        }

        interface Update {
            void onUpdate();

            interface Timed {
                void onUpdate(Timer.Time time);
            }
        }

        interface EnableDisable {
            void onEnable();

            void onDisable();

            default void setEnabled(boolean enabled) {
                if (this instanceof BaseFeature baseFeature) {
                    baseFeature.requestEnabled(enabled);
                }
            }
        }
    }

    interface Settings<T extends Feature> {
        T build();
    }

    interface Core extends Feature {}

    interface App extends Feature {}
}
