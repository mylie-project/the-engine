package mylie.engine.graphics.apis.opengl;

import java.util.function.BiFunction;
import mylie.engine.graphics.ApiFeature;
import mylie.engine.graphics.Graphics;
import mylie.engine.graphics.GraphicsContext;

public class OpenGl {

    public abstract static class ContextCapabilities {
        public static Graphics.ContextCapability<String> GlApiVersion;
        public static Graphics.ContextCapability<Integer> GlApiMajorVersion;
        public static Graphics.ContextCapability<Integer> GlApiMinorVersion;
    }

    public static class GlContextCapability<T, C extends ApiFeature> extends Graphics.ContextCapability<T> {
        final Class<C> apiFeature;
        final BiFunction<C, Integer, T> getter;
        final int parameter;

        public GlContextCapability(String name, Class<C> apiFeature, BiFunction<C, Integer, T> getter, int parameter) {
            super(name);
            this.apiFeature = apiFeature;
            this.getter = getter;
            this.parameter = parameter;
        }

        @Override
        protected T init(GraphicsContext context) {
            C api = context.getApiFeature(apiFeature);
            return getter.apply(api, parameter);
        }
    }
}
