package mylie.lwjgl3.opengl;

import mylie.engine.graphics.GraphicsApi;

public class Lwjgl3OpenglSettings extends mylie.engine.graphics.apis.opengl.OpenglSettings {
    @Override
    protected GraphicsApi build() {
        return new Lwjgl3OpenglApi();
    }
}
