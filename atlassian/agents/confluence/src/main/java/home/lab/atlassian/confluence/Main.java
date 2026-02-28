package home.lab.atlassian.confluence;

import home.lab.atlassian.Injector;
import home.lab.atlassian.confluence.license.*;

import java.lang.instrument.Instrumentation;

public class Main {
    public static void premain(String args, Instrumentation inst) {
        new Injector(inst)
                .inject(new Product.equals())
                .inject(new LicenseServiceBootstrapAppConfig.licenseService());
    }
}
