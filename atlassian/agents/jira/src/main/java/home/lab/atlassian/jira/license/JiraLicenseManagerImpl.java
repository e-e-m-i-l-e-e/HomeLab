package home.lab.atlassian.jira.license;

import home.lab.atlassian.License;
import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class JiraLicenseManagerImpl implements Transformer {
    public static final String type = "com.atlassian.jira.license.JiraLicenseManagerImpl";

    public static ElementMatcher.Junction<MethodDescription> matcher = isConstructor();

    @Advice.OnMethodEnter
    public static void onEnter(
            @Advice.Argument(2) Object multiLicenseStore
    ) {
        try {
            multiLicenseStore.getClass()
                    .getDeclaredMethod("store", Iterable.class)
                    .invoke(multiLicenseStore, List.of(License.generateLicense()));
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
