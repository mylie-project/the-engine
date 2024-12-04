package mylie.engine.core.features.async;

import lombok.AccessLevel;
import lombok.Getter;

public interface Functions {

    @Getter(AccessLevel.PACKAGE)
    abstract class Function {
        private final String name;

        protected Function(String name) {
            this.name = name;
        }
    }

    abstract class F0<R, O> extends Function {
        protected F0(String name) {
            super(name);
        }

        protected abstract R run(O o);
    }

    abstract class F1<R, O, P1> extends Function {
        protected F1(String name) {
            super(name);
        }

        protected abstract R run(O o, P1 p1);
    }
}
