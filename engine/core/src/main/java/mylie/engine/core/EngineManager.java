package mylie.engine.core;

public interface EngineManager extends BaseFeature.App {
    void shutdown(Engine.ShutdownReason reason);
}
