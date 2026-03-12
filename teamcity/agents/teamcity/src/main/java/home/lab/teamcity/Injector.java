package home.lab.teamcity;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class Injector {

    public static final Logger logger = Logger.getLogger(Injector.class.getName());

    private final Instrumentation instrumentation;

    public Injector(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        logger.info("TeamCity Injector initialised.");
    }

    public Injector inject(Transformer transformer) {
        logger.info("Registering transformer for type: " + transformer.getType());
        new AgentBuilder.Default()
                .type(named(transformer.getType()))
                .transform(
                        (builder,
                         typeDescription,
                         classLoader,
                         javaModule,
                         protectionDomain) ->
                                builder.visit(
                                        Advice.to(transformer.getClass())
                                              .on(transformer.getMatcher())))
                .installOn(instrumentation);
        logger.info("Transformer registered for type: " + transformer.getType());
        return this;
    }
}
