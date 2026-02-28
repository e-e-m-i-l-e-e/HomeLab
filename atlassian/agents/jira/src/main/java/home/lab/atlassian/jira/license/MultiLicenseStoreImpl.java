package home.lab.atlassian.jira.license;

import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class MultiLicenseStoreImpl {

    public static final String type = "com.atlassian.jira.license.MultiLicenseStoreImpl";

    public static class retrieveServerId implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = named(MultiLicenseStoreImpl.retrieveServerId.class.getSimpleName());

        @Advice.OnMethodExit
        public static void onExit(
                @Advice.This Object self,
                @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned
        ) {
            try {
                if (returned == null) {
                    returned = "Default";
                    self.getClass()
                            .getMethod("storeServerId", String.class)
                            .invoke(self, returned.toString());
                }
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
