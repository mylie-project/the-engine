package mylie.engine.core.features.profiler;

import mylie.engine.core.BaseFeature;

public abstract class Profiler implements BaseFeature.Core {

    public interface Context {}

    public interface Metric {
        interface Cpu extends Metric {
            interface Timer extends Cpu {}

            interface Counter extends Cpu {}
        }

        interface Gpu extends Metric {
            interface Timer extends Gpu {}

            interface Counter extends Gpu {}
        }
    }
}
