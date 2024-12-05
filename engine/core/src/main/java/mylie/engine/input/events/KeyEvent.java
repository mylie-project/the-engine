package mylie.engine.input.events;

import lombok.*;
import mylie.engine.input.Input;

@ToString
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
class KeyEvent extends KeyboardEvent {
    public enum Type {
        PRESSED,
        RELEASED,
        LONG_PRESSED,
    }

    public enum Modifier {
        SHIFT,
        CONTROL,
        ALT,
        SUPER,
        CAPS_LOCK,
        NUM_LOCK,
    }

    Input.Key key;
    Type type;

    @Getter(AccessLevel.NONE)
    int mods;
}
