package home.lab.artifactory.license;

import home.lab.jfrog.License;
import home.lab.jfrog.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class ArtifactoryLicenseProvider {

    public static final String type = "org.artifactory.addon.ArtifactoryLicenseProvider";

    public static class getLicenseProduct implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = named(getLicenseProduct.class.getSimpleName()).and(takesArguments(String.class));

        @Advice.OnMethodExit
        static void onExit(
                @Advice.FieldValue(value = "b") Object logger,
                @Advice.Argument(0) String product,
                @Advice.Return Object returned
        ) {
            if (returned == null && License.isReady()) {
                final String message = "License is not found for product: " + product;
                try {
                    logger.getClass()
                            .getDeclaredMethod("warn", String.class)
                            .invoke(logger, message);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    System.out.println("Cannot access logger. " + message);
                }
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
