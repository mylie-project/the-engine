package mylie.engine.core.features.options;

import java.util.function.Supplier;
import lombok.Data;

@Data
public class Option<T> {
    Class<T> type;
    T currentValue;
    Supplier<T> onValueChanged;
}
