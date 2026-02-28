/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.license.products;

import io.zhile.crack.atlassian.license.LicenseProperty;
import io.zhile.crack.atlassian.license.LicenseType;

public class JIRAServiceDesk
extends LicenseProperty {
    public JIRAServiceDesk(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public void init() {
        super.init();
        this.setEnterprise(true);
        this.setNumRoleCount(-1);
    }

    @Override
    public String getProductName() {
        return "jira.product.jira-servicedesk";
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        this.data.put("com.atlassian.servicedesk.active", String.valueOf(active));
    }

    @Override
    public void setNumberOfUsers(int numberOfUsers) {
        super.setNumberOfUsers(numberOfUsers);
        this.data.put("NumberOfUsers", String.valueOf(numberOfUsers));
    }

    @Override
    public void setLicenseType(LicenseType licenseType) {
        this.data.put("LicenseTypeName", licenseType.toString());
        this.data.put("com.atlassian.servicedesk.LicenseTypeName", licenseType.toString());
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

    public void setEnterprise(boolean enterprise) {
        this.data.put("com.atlassian.servicedesk.enterprise", String.valueOf(enterprise));
    }

    public void setNumRoleCount(int roleCount) {
        this.data.put("com.atlassian.servicedesk.numRoleCount", String.valueOf(roleCount));
    }
}

