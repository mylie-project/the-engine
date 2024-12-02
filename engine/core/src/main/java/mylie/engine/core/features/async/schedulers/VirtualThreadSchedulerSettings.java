package mylie.engine.core.features.async.schedulers;

import mylie.engine.core.features.async.Scheduler;

public class VirtualThreadSchedulerSettings implements SchedulerSettings {
    @Override
    public Scheduler build() {
        return new VirtualThreadScheduler();
    }
}
