package home.lab.access;

import home.lab.jfrog.Injector;
import home.lab.jfrog.JFrogLicenseManager;

import java.lang.instrument.Instrumentation;

public class Main {

    static {
        JFrogLicenseManager.load.configure("JFrogLicenseManager", "load", "createLicenseKey");
    }

    public static void premain(String args, Instrumentation inst) {
        System.out.println("Access Server agent is up and running");

        new Injector(inst)
                .inject(new JFrogLicenseManager.load());
    }
}