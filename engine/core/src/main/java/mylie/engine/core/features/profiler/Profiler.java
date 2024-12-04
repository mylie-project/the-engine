package mylie.engine.core.features.profiler;

import mylie.engine.core.Feature;

public abstract class Profiler implements Feature.Engine {


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
