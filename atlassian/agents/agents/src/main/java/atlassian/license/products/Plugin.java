/*
 * Decompiled with CFR 0.152.
 */
package atlassian.license.products;

import io.zhile.crack.atlassian.license.LicenseProperty;

public abstract class Plugin
extends LicenseProperty {
    public Plugin(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public void init() {
        super.init();
        this.setLicenseID(null);
        this.setEnterprise(true);
    }

    @Override
    public void setNumberOfUsers(int numberOfUsers) {
        this.data.put("NumberOfUsers", String.valueOf(numberOfUsers));
    }

    public void setEnterprise(boolean enterprise) {
        this.data.put(this.productProperty("enterprise"), String.valueOf(enterprise));
    }
}

