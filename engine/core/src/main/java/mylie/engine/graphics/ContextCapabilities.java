package mylie.engine.graphics;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

public class ContextCapabilities {
    public static ContextCapability<Integer> MaxTextureSize;
    public static ContextCapability<Integer> Max3dTextureSize;

    @Slf4j
    public abstract static class ContextCapability<T> {
        @Getter(AccessLevel.PACKAGE)
        private static final List<ContextCapability<?>> allCapabilities = new ArrayList<>();

        private final String name;

        public ContextCapability(String name) {
            this.name = name;
            allCapabilities.add(this);
        }

        @SuppressWarnings("unchecked")
        public T get(GraphicsContext context) {
            return (T) context.capabilities().get(this);
        }

        protected abstract T init(GraphicsContext context);

        static void initAll(GraphicsContext context) {
            context.queue().add(new Runnable() {
                @Override
                public void run() {
                    for (ContextCapability<?> capability : allCapabilities) {
                        Object init = capability.init(context);
                        context.capabilities().put(capability, init);
                        log.trace("Capability<{}> = {}", capability.name, init);
                    }
                }
            });
        }
    }
}
