package home.lab.artifactory.license;

import home.lab.jfrog.License;
import home.lab.jfrog.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;
import java.io.File;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class BaseLicensePool {
    public static final String type = "org.artifactory.addon.BaseLicensePool";

    public static class read implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = named("a").and(takesArguments(3).and(takesArgument(0, File.class)));

        @Advice.OnMethodExit
        static void onExit(
                @Advice.Argument(1) Object logger,
                @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned
        ) {
            if (returned == null) {
                final String message = "License file appears to be empty. Injecting license placeholder.";
                try {
                    logger.getClass()
                            .getDeclaredMethod("info", String.class)
                            .invoke(logger, message);
                } catch (Exception e) {
                    System.out.println("Cannot access logger. " + message);
                }

                returned = License.licenseKey;
            }
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public ElementMatcher<MethodDescription> getMatcher() {
            return matcher;
        }
    }
}
