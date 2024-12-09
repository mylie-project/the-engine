package mylie.engine.core;

public abstract sealed class Test permits Test.TestA, Test.TestB, Test.TestC, Test.TestD {

    public static non-sealed class TestA extends Test {}

    public static non-sealed class TestB extends Test {}

    public abstract static non-sealed class TestC extends Test {}

    public abstract static non-sealed class TestD extends Test {}
}
