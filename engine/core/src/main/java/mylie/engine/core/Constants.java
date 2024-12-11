package mylie.engine.core;

import org.joml.Vector3f;
import org.joml.Vector3fc;

@SuppressWarnings("unused")
public class Constants {
    public static final Vector3fc UnitX = new Vector3f(1, 0, 0);
    public static final Vector3fc UnitY = new Vector3f(0, 1, 0);
    public static final Vector3fc UnitZ = new Vector3f(0, 0, 1);
    public static final Vector3fc Zero = new Vector3f(0, 0, 0);
    public static final Vector3fc One = new Vector3f(1, 1, 1);
    public static final Vector3fc NegativeOne = new Vector3f(-1, -1, -1);
    public static final Vector3fc NegativeUnitX = new Vector3f(-1, 0, 0);
    public static final Vector3fc NegativeUnitY = new Vector3f(0, -1, 0);
    public static final Vector3fc NegativeUnitZ = new Vector3f(0, 0, -1);
}
