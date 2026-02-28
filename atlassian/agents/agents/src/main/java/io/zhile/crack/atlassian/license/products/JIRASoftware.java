/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.license.products;

import io.zhile.crack.atlassian.license.LicenseEdition;
import io.zhile.crack.atlassian.license.LicenseProperty;
import io.zhile.crack.atlassian.license.LicenseType;

public class JIRASoftware
extends LicenseProperty {
    public JIRASoftware(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public void init() {
        super.init();
        this.setLicenseEdition(LicenseEdition.UNLIMITED);
        this.setEnterprise(true);
    }

    @Override
    public String getProductName() {
        return "jira.product.jira-software";
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        this.data.put("greenhopper.active", String.valueOf(active));
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
        this.data.put("greenhopper.LicenseTypeName", licenseType.toString());
        this.data.put("jira.LicenseTypeName", licenseType.toString());
    }

    @Override
    public void setDataCenter(boolean dataCenter) {
        super.setDataCenter(dataCenter);
        if (dataCenter) {
            this.data.put("jira.DataCenter", "true");
        } else {
            this.data.remove("jira.DataCenter");
        }
    }

    public void setLicenseEdition(LicenseEdition licenseEdition) {
        this.data.put("greenhopper.LicenseEdition", licenseEdition.toString());
        this.data.put("jira.LicenseEdition", licenseEdition.toString());
    }

    public void setEnterprise(boolean enterprise) {
        this.data.put("greenhopper.enterprise", String.valueOf(enterprise));
    }
}

