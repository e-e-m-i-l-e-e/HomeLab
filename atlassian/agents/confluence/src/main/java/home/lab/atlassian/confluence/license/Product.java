package home.lab.atlassian.confluence.license;

import home.lab.atlassian.License;
import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class Product {
    public static final Logger logger = Logger.getLogger(Product.class.getName());

    public static final String type = "com.atlassian.extras.api.Product";

    public static class equals implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = isEquals();

        @Advice.OnMethodExit
        public static void onExit(@Advice.This Object product,
                                  @Advice.Argument(0) Object other,
                                  @Advice.Return Boolean returned) {
            if (other.toString().equals("#{T(com.atlassian.extras.api.Product).CONFLUENCE}")) {
                logger.info("Installing product license.");
                try {
                    LicenseServiceBootstrapAppConfig.service
                            .getClass()
                            .getDeclaredMethod("install", String.class)
                            .invoke(LicenseServiceBootstrapAppConfig.service, License.generateLicense());
                } catch (Exception e) {
                    logger.severe("Failed to install license: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                logger.info("Checking product: " + other);
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
