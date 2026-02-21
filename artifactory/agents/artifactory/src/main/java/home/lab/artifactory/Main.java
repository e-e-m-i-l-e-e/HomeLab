package home.lab.artifactory;

import home.lab.artifactory.license.ArtifactoryLicenseProvider;
import home.lab.artifactory.license.BaseLicensePool;
import home.lab.jfrog.Injector;
import home.lab.jfrog.JFrogLicenseManager;

import java.lang.instrument.Instrumentation;

public class Main {

    static {
        JFrogLicenseManager.load.configure("a", "a", "a");
    }

    public static void premain(String args, Instrumentation inst) {
        System.out.println("Artifactory agent is up and running");

        new Injector(inst)
                .inject(new BaseLicensePool.read())
                .inject(new JFrogLicenseManager.load())
                .inject(new ArtifactoryLicenseProvider.getLicenseProduct());
    }
}