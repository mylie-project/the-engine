package mylie.engine.core.features.async;

import lombok.AllArgsConstructor;

public abstract class Tasks<R> {
    public R execute() {
        return onExecute();
    }

    public abstract R onExecute();

    @AllArgsConstructor
    public static class T0<R, O> extends Tasks<R> {
        Functions.F0<R, O> function;
        O object;

        @Override
        public R onExecute() {
            return function.run(object);
        }
    }

    @AllArgsConstructor
    public static class T1<R, O, P1> extends Tasks<R> {
        Functions.F1<R, O, P1> function;
        O object;
        P1 param1;

        @Override
        public R onExecute() {
            return function.run(object, param1);
        }
    }

    @AllArgsConstructor
    public static class T2<R, O, P1, P2> extends Tasks<R> {
        Functions.F2<R, O, P1, P2> function;
        O object;
        P1 param1;
        P2 param2;

        @Override
        public R onExecute() {
            return function.run(object, param1, param2);
        }
    }
}
