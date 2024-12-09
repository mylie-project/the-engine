package mylie.engine.graphics.apis.opengl.api;

import mylie.engine.graphics.api.Base;
import mylie.engine.graphics.apis.opengl.GlApiFeature;

public interface GlBase extends GlApiFeature, Base {
    int getInteger(int parameter);

    double getDouble(int parameter);

    String getString(int parameter);
}
