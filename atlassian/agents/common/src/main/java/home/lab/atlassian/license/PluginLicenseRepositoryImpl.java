package home.lab.atlassian.license;

import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodHandles.privateLookupIn;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class PluginLicenseRepositoryImpl {
    public static final String type = "com.atlassian.upm.license.internal.impl.PluginLicenseRepositoryImpl";

    public static class getPluginLicense implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = named(getPluginLicense.class.getSimpleName()).and(takesArguments(String.class));

        @Advice.OnMethodExit
        public static void onExit(
                @Advice.This Object self,
                @Advice.Origin Method method,
                @Advice.Argument(0) String pluginKey,
                @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned
        ) {
            try {
                Object isDefined = returned.getClass().getMethod("isDefined").invoke(returned);
                if (Boolean.FALSE.equals(isDefined)) {
                    Class<?> licenseManager = Class.forName("home.lab.atlassian.License", false, ClassLoader.getSystemClassLoader());

                    Object logger = self.getClass().getDeclaredField("log").get(null);

                    logger.getClass()
                            .getDeclaredMethod("warn", String.class)
                            .invoke(logger, "Plugin " + pluginKey + " doesn't have license. Installing it now.");

                    Object success = self
                            .getClass()
                            .getMethod("setPluginLicense", String.class, String.class)
                            .invoke(
                                    self,
                                    pluginKey,
                                    String.valueOf(
                                            privateLookupIn(licenseManager, lookup()).findStatic(
                                                    licenseManager,
                                                    "generateLicense",
                                                    MethodType.methodType(String.class)
                                            ).invoke()
                                    ));

                    if (Boolean.TRUE.equals(success.getClass().getMethod("isLeft").invoke(success))) {
                        logger.getClass()
                                .getDeclaredMethod("error", String.class)
                                .invoke(logger, "Failed to install license for " + pluginKey + " plugin");
                    } else returned = method.invoke(self, pluginKey);
                }
            } catch (Throwable t) {
                System.out.println("Reflection failed: " + t);
                t.printStackTrace();
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
