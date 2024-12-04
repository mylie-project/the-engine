package mylie.engine.core;

public interface EngineManager extends Feature.App {
    void shutdown(Engine.ShutdownReason reason);
}
