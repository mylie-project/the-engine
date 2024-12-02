package mylie.util.configuration;

import java.util.HashMap;
import java.util.Map;
import mylie.util.lang.Tuple;

public class Configuration<T> {
    private final Map<String, Tuple<?, ?>> settings;

    public Configuration() {
        this.settings = new HashMap<>();
    }

    public <R> R get(Setting<T, R> setting) {
        return setting.get(this);
    }

    public <R> void set(Setting<T, R> setting, R value) {
        setting.set(this, value);
    }

    public <R> void setIfNotExists(Setting<T, R> setting, R value) {
        setting.setIfNotExists(this, value);
    }

    @SuppressWarnings("unchecked")
    protected <R extends Setting<T, V>, V> Tuple<R, V> getSetting(R setting) {
        return (Tuple<R, V>) settings.computeIfAbsent(setting.name, key -> new Tuple<>(setting, null));
    }
}
