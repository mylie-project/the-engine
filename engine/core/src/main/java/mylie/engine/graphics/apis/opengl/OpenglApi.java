package mylie.engine.graphics.apis.opengl;

import mylie.engine.graphics.Graphics;
import mylie.engine.graphics.GraphicsApi;
import mylie.engine.graphics.apis.opengl.api.GlBase;

public abstract class OpenglApi extends GraphicsApi {
    public OpenglApi() {
        OpenGl.ContextCapabilities.GlApiVersion =
                new OpenGl.GlContextCapability<>("ApiVersion", GlBase.class, GlBase::getString, 7938);
        OpenGl.ContextCapabilities.GlApiMajorVersion =
                new OpenGl.GlContextCapability<>("ApiVersionMajor", GlBase.class, GlBase::getInteger, 33307);
        OpenGl.ContextCapabilities.GlApiMinorVersion =
                new OpenGl.GlContextCapability<>("ApiVersionMinor", GlBase.class, GlBase::getInteger, 33308);
        Graphics.ContextCapabilities.MaxTextureSize =
                new OpenGl.GlContextCapability<>("MaxTextureSize", GlBase.class, GlBase::getInteger, 3379);
        Graphics.ContextCapabilities.Max3dTextureSize =
                new OpenGl.GlContextCapability<>("Max3DTextureSize", GlBase.class, GlBase::getInteger, 32883);
    }
}
