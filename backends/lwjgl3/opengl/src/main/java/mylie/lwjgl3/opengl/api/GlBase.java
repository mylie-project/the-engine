package mylie.lwjgl3.opengl.api;

import org.lwjgl.opengl.GL11;

public class GlBase implements mylie.engine.graphics.apis.opengl.api.GlBase {

    @Override
    public int getInteger(int parameter) {
        return GL11.glGetInteger(parameter);
    }

    @Override
    public double getDouble(int parameter) {
        return GL11.glGetDouble(parameter);
    }

    @Override
    public String getString(int parameter) {
        return GL11.glGetString(parameter);
    }
}
