package artifactory.agents;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LicenseValidator {
    public static final Logger logger = Logger.getLogger(LicenseValidator.class.getName());

    public static void premain(String args, Instrumentation inst) {
        logger.log(Level.INFO, "Setting up Agent.");
        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("org.jfrog.license.multiplatform.Signed"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.named("verify"))
                               .intercept(Advice.to(new Object() {
                                   @Advice.OnMethodExit(onThrowable = Throwable.class)
                                   public static void exit(@Advice.Thrown(readOnly = false) Throwable t) {
                                       if (t != null) {
                                           t = null;
                                           logger.log(Level.INFO, "Signed object failed verification, but agent bypassed this.");
                                       }
                                   }
                               }.getClass()))
                ).installOn(inst);
    }
}