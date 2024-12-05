package mylie.engine.input.events;

import lombok.*;

@ToString
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
class MouseWheelEvent extends MouseEvent {
    public enum WheelAxis {
        X,
        Y,
    }

    WheelAxis axis;
    int amount;
}
