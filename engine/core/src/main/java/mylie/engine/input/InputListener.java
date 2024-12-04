package mylie.engine.input;

public sealed interface InputListener permits InputListener.Raw {
    void onEvent(InputEvent event);

    non-sealed interface Raw extends InputListener {}
}
