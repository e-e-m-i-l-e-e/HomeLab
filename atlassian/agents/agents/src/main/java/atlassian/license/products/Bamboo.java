/*
 * Decompiled with CFR 0.152.
 */
package atlassian.license.products;

import io.zhile.crack.atlassian.license.LicenseEdition;
import io.zhile.crack.atlassian.license.LicenseProperty;

public class Bamboo
extends LicenseProperty {
    public Bamboo(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public void init() {
        super.init();
        this.setLicenseEdition(LicenseEdition.UNLIMITED);
        this.setNumberOfBambooLocalAgents(-1);
        this.setNumberOfBambooRemoteAgents(1000);
        this.setNumberOfBambooPlans(-1);
    }

    @Override
    public String getProductName() {
        return "bamboo";
    }

    @Override
    public void setNumberOfUsers(int numberOfUsers) {
    }

    public void setLicenseEdition(LicenseEdition licenseEdition) {
        this.data.put(this.productProperty("LicenseEdition"), licenseEdition.toString());
    }

    public void setNumberOfBambooLocalAgents(int number) {
        this.data.put(this.productProperty("NumberOfBambooLocalAgents"), String.valueOf(number));
    }

    public void setNumberOfBambooRemoteAgents(int number) {
        this.data.put(this.productProperty("NumberOfBambooRemoteAgents"), String.valueOf(number));
    }

    public void setNumberOfBambooPlans(int number) {
        this.data.put(this.productProperty("NumberOfBambooPlans"), String.valueOf(number));
    }
}

