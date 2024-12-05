package mylie.engine.input.events;

import lombok.*;
import org.joml.Vector2ic;

@ToString
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
class MouseCursorEvent extends MouseEvent {
    Vector2ic position;
}
