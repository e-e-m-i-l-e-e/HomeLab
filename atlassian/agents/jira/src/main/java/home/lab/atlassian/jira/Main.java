package home.lab.atlassian.jira;

import home.lab.atlassian.Injector;
import home.lab.atlassian.License;
import home.lab.atlassian.jira.license.JiraLicenseManagerImpl;
import home.lab.atlassian.jira.license.MultiLicenseStoreImpl;
import home.lab.atlassian.jira.license.SetupLicense;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

public class Main {

    static {
        License.customizer = () -> {

            final String prefix = "jira.product.jira-software.";

            Map<String, String> data = new HashMap<>();
            data.put(prefix + "active", "true");
            data.put(prefix + "NumberOfUsers", String.valueOf(-1));
            return data;
        };
    }

    public static void premain(String args, Instrumentation inst) {
        new Injector(inst)
                .inject(new JiraLicenseManagerImpl())
                .inject(new SetupLicense.setSetupLicenseKey())
                .inject(new MultiLicenseStoreImpl.retrieveServerId());
    }
}
