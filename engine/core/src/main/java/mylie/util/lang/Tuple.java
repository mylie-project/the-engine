package mylie.util.lang;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Tuple<T, V> {
    public T first;
    public V second;
}
