/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.license.products;

import io.zhile.crack.atlassian.license.LicenseEdition;
import io.zhile.crack.atlassian.license.LicenseProperty;
import io.zhile.crack.atlassian.license.LicenseType;

public class JIRACore
extends LicenseProperty {
    public JIRACore(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public void init() {
        super.init();
        this.setLicenseEdition(LicenseEdition.UNLIMITED);
    }

    @Override
    public String getProductName() {
        return "jira.product.jira-core";
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        this.data.put("jira.active", String.valueOf(active));
    }

    @Override
    public void setNumberOfUsers(int numberOfUsers) {
        super.setNumberOfUsers(numberOfUsers);
        this.data.put("jira.NumberOfUsers", String.valueOf(numberOfUsers));
        this.data.put("NumberOfUsers", String.valueOf(numberOfUsers));
    }

    @Override
    public void setLicenseType(LicenseType licenseType) {
        this.data.put("LicenseTypeName", licenseType.toString());
        this.data.put("jira.LicenseTypeName", licenseType.toString());
    }

    public void setLicenseEdition(LicenseEdition licenseEdition) {
        this.data.put("jira.LicenseEdition", licenseEdition.toString());
    }
}

