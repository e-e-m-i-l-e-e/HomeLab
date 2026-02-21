package home.lab.jfrog;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class Injector {

    private final Instrumentation instrumentation;

    public Injector(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public Injector inject(Transformer transformer) {
        new AgentBuilder.Default()
                .type(named(transformer.getType()))
                .transform(
                        (builder,
                         typeDescription,
                         classLoader,
                         javaModule,
                         protectionDomain) ->
                                builder
                                        .visit(Advice.to(transformer.getClass()).on(transformer.getMatcher())))
                .installOn(instrumentation);
        return this;
    }
}
