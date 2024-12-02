package mylie.examples.tests;

import mylie.engine.core.Engine;
import mylie.engine.platform.PlatformDesktop;
import mylie.util.configuration.Configuration;

public class A0_HelloEngine {

    public static void main(String[] args) {
        PlatformDesktop platform = new PlatformDesktop();
        Configuration<Engine> initialize = platform.initialize();
        Engine.ShutdownReason start = Engine.start(initialize, true, false);
        System.out.println(start.toString());
    }
}
