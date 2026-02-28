package home.lab.atlassian;

import home.lab.atlassian.license.Key;
import home.lab.atlassian.license.PluginLicenseRepositoryImpl;
import home.lab.atlassian.license.ProductLicenseProperties;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class Injector {

    private final Instrumentation instrumentation;

    public Injector(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this
                .inject(new Key())
                .inject(new ProductLicenseProperties.getProperty())
                .inject(new PluginLicenseRepositoryImpl.getPluginLicense());
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
