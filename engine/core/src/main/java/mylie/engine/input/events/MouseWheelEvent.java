package mylie.engine.input.events;

import lombok.*;

@ToString
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class MouseWheelEvent extends MouseEvent {
    public enum WheelAxis {
        X,
        Y,
    }

    WheelAxis axis;
    int amount;
}
