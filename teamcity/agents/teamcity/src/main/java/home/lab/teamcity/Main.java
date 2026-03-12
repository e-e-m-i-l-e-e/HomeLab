package home.lab.teamcity;

import home.lab.teamcity.license.LicenseListImpl;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void premain(String args, Instrumentation inst) {
        System.out.println("SUCCESS");
        logger.info("TeamCity agent starting up...");

        new Injector(inst)
                .inject(new LicenseListImpl.hasEnterpriseLicense())
                .inject(new LicenseListImpl.isUnlimitedAgents())
                .inject(new LicenseListImpl.isUnlimitedPipelines())
                .inject(new LicenseListImpl.isUnlimitedBuildTypes());

        logger.info("TeamCity agent hooks installed successfully.");
    }
}
