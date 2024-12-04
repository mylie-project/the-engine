package mylie.engine.input.events;

import lombok.*;
import mylie.engine.input.Input;

@ToString
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class MouseButtonEvent extends MouseEvent {
    public enum Type {
        PRESSED,
        RELEASED,
        CLICKED,
        DOUBLE_CLICKED,
    }

    Input.MouseButton button;
    Type type;
    int mods;
}
