package home.lab.teamcity.license;

import home.lab.teamcity.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class LicenseListImpl {

    public static final String TARGET_TYPE = "jetbrains.buildServer.a.n";

    public static class hasEnterpriseLicense implements Transformer {

        public static final ElementMatcher.Junction<MethodDescription> matcher = named("hasEnterpriseLicense").and(takesArguments(0));

        @Advice.OnMethodExit
        public static void onExit(
                @Advice.Return(readOnly = false) boolean returned
        ) {
            returned = true;
        }

        @Override public String getType() {
            return TARGET_TYPE;
        }

        @Override public ElementMatcher<MethodDescription> getMatcher() {
            return matcher;
        }
    }

    public static class isUnlimitedAgents implements Transformer {

        public static final ElementMatcher.Junction<MethodDescription> matcher = named("isUnlimitedAgents").and(takesArguments(0));

        @Advice.OnMethodExit
        public static void onExit(
                @Advice.Return(readOnly = false) boolean returned
        ) {
            returned = true;
        }

        @Override public String getType() {
            return TARGET_TYPE;
        }

        @Override public ElementMatcher<MethodDescription> getMatcher() {
            return matcher;
        }
    }

    public static class isUnlimitedBuildTypes implements Transformer {

        public static final ElementMatcher.Junction<MethodDescription> matcher = named("isUnlimitedBuildTypes").and(takesArguments(0));

        @Advice.OnMethodExit
        public static void onExit(
                @Advice.Return(readOnly = false) boolean returned
        ) {
            returned = true;
        }

        @Override public String getType() {
            return TARGET_TYPE;
        }

        @Override public ElementMatcher<MethodDescription> getMatcher() {
            return matcher;
        }
    }

    public static class isUnlimitedPipelines implements Transformer {

        public static final ElementMatcher.Junction<MethodDescription> matcher = named("isUnlimitedPipelines").and(takesArguments(0));

        @Advice.OnMethodExit
        public static void onExit(
                @Advice.Return(readOnly = false) boolean returned
        ) {
            returned = true;
        }

        @Override public String getType() {
            return TARGET_TYPE;
        }

        @Override public ElementMatcher<MethodDescription> getMatcher() {
            return matcher;
        }
    }
}
