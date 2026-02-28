package home.lab.atlassian.confluence.license;

import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class LicenseServiceBootstrapAppConfig {
    public static final Logger logger = Logger.getLogger(LicenseServiceBootstrapAppConfig.class.getName());

    public static final String type = "com.atlassian.confluence.impl.setup.LicenseServiceBootstrapAppConfig";

    public static Object service;

    public static class licenseService implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = isMethod().and(named(licenseService.class.getSimpleName()));

        @Advice.OnMethodExit
        public static void onExit(@Advice.Return Object licenseService) {
            service = licenseService;
            logger.info("License service is ready.");
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
