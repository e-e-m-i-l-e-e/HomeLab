package home.lab.atlassian.jira.license;

import home.lab.atlassian.License;
import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class SetupLicense {

    public static final String type = "com.atlassian.jira.web.action.setup.SetupLicense";

    public static class setSetupLicenseKey implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = named(setSetupLicenseKey.class.getSimpleName());

        @Advice.OnMethodEnter
        public static void onEnter(
                @Advice.Argument(value = 0, readOnly = false) String licenseString
        ) {
            try {
                licenseString = License.generateLicense();
            } catch (Exception e) {
                e.printStackTrace();
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
