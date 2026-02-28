/*
 * Decompiled with CFR 0.152.
 */
package atlassian.license.products;

import io.zhile.crack.atlassian.license.LicenseProperty;

public class Crucible
extends LicenseProperty {
    public Crucible(String contactName, String contactEMail, String serverID, String organisation, boolean dataCenter) {
        super(contactName, contactEMail, serverID, organisation, dataCenter);
    }

    @Override
    public String getProductName() {
        return "crucible";
    }
}

