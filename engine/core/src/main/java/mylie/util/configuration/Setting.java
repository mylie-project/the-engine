package mylie.util.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.util.lang.Tuple;

@Slf4j
public class Setting<TARGET, T> {
    @Getter
    final Class<T> type;

    @Getter
    final String name;

    final T defaultValue;
    final boolean serializable;

    public Setting(String name, Class<T> type, boolean serializable, T defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.name = name;
        this.serializable = serializable;
    }

    public Setting(Class<T> type, boolean serializable, T defaultValue) {
        this(type.getSimpleName(), type, serializable, defaultValue);
    }

    protected T get(Configuration<TARGET> configuration) {
        T value;
        Tuple<Setting<TARGET, T>, T> setting = configuration.getSetting(this);
        value = setting.second();
        if (value == null) {
            value = defaultValue;
            setting.second(value);
        }
        return value;
    }

    void set(Configuration<TARGET> configuration, T value) {
        Tuple<Setting<TARGET, T>, T> setting = configuration.getSetting(this);
        setting.second(value);
    }

    void setIfNotExists(Configuration<TARGET> configuration, T value) {
        Tuple<Setting<TARGET, T>, T> setting = configuration.getSetting(this);
        if (setting.second() == null) {
            setting.second(value);
        }
    }
}
