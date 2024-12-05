package mylie.engine.core;

public interface Feature {

    interface Settings<T extends Feature> {
        T build();
    }
}
