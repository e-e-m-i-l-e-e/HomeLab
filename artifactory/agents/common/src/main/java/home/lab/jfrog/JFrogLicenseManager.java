package home.lab.jfrog;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class JFrogLicenseManager {

    public static String type;

    public static class load implements Transformer {
        public static String methodName;
        public static String createLicenseKey;
        public static ElementMatcher.Junction<MethodDescription> matcher;

        public static void configure(String className, String methodName, String createLicenseKey) {
            load.methodName = methodName;
            load.createLicenseKey = createLicenseKey;
            JFrogLicenseManager.type = "org.jfrog.license.multiplatform." + className;
            matcher = named(methodName).and(takesArguments(String.class, PublicKey.class));
        }

        @Advice.OnMethodEnter
        static void onEnter(
                @Advice.This Object jFrogLicenseManager,
                @Advice.Origin Method method,
                @Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object license,
                @Advice.Argument(value = 1, readOnly = false) PublicKey key
        ) {
            if (!License.isReady()) {
                Logger logger = Logger.getLogger(jFrogLicenseManager.getClass().getName());
                logger.info("License is not installed. Installing it now.");
                try {
                    home.lab.jfrog.License.generateKeyPair((Provider) Class.forName("org.jfrog.security.util.BCProviderFactory").getMethod("getProvider").invoke(null));
                    generateLicense(
                            jFrogLicenseManager,
                            createLicenseKey
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }

            key = License.publicKey;
            license = License.licenseKey;
        }

        public static void generateLicense(Object jFrogLicenseManager,
                                           String createLicenseKeyMethodName) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            Method createLicenseKey = jFrogLicenseManager.getClass()
                    .getDeclaredMethod(createLicenseKeyMethodName, PrivateKey.class, Map.class, boolean.class);

            Object licenseKey = createLicenseKey.invoke(jFrogLicenseManager, home.lab.jfrog.License.privateKey, Map.of(
                    "artifactory", getProduct(),
                    "xray", getProduct()
            ), false);
            License.licenseKey = licenseKey.toString();
        }

        public static Object getProduct() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> Product = Class.forName("org.jfrog.license.api.Product", false, cl);
            Class<?> ProductType = Class.forName("org.jfrog.license.api.Product$Type", false, cl);

            Object product = Product.getDeclaredConstructor().newInstance();
            Product.getMethod("setOwner", String.class).invoke(product, "admin");
            Product.getMethod("setValidFrom", Date.class).invoke(product, Date.from(Instant.now()));
            Product.getMethod("setExpires", Date.class).invoke(product, Date.from(LocalDateTime.now().plusYears(5).toInstant(ZoneOffset.UTC)));
            Product.getMethod("setType", ProductType).invoke(product, Enum.valueOf(ProductType.asSubclass(Enum.class), "ENTERPRISE_PLUS"));
            Product.getMethod("setTrial", boolean.class).invoke(product, false);
            return product;
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
