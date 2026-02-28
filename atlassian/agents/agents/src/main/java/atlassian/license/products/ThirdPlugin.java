/*
 * Decompiled with CFR 0.152.
 */
package atlassian.license.products;

import io.zhile.crack.atlassian.license.LicenseType;
import io.zhile.crack.atlassian.license.products.Plugin;

public class ThirdPlugin
extends Plugin {
    protected String productName;

    public ThirdPlugin(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public void setLicenseType(LicenseType licenseType) {
        this.data.put("LicenseTypeName", licenseType.toString());
    }
}

